package de.truhera.hologramapi;

import com.comphenix.protocol.events.PacketContainer;
import de.truhera.hologramapi.HologramAPI;
import de.truhera.hologramapi.lines.Line;
import de.truhera.hologramapi.lines.helpers.PacketHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerDefinedHologram {
    private List<Player> loadedPlayers = new ArrayList<Player>();
    private Location loc;
    private final Map<Player, List<Line>> playerLines = new HashMap<Player, List<Line>>();

    public PlayerDefinedHologram(Location loc) {
        this.loc = loc;
    }

    public void load(Player player) {
        if (this.loadedPlayers.contains(player) || player.getWorld() != this.loc.getWorld()) {
            return;
        }
        this.loadedPlayers.add(player);
        List<Line> loadLines = this.onLoad(player);
        this.changeLines(player, loadLines);
    }

    public void unload(Player player) {
        if (!this.loadedPlayers.contains(player) || player.getWorld() != this.loc.getWorld()) {
            return;
        }
        this.onUnload(player);
        this.removeLines(player);
        this.loadedPlayers.remove(player);
    }

    public void unloadAll() {
        for (Player loaded : new ArrayList<Player>(this.loadedPlayers)) {
            this.unload(loaded);
        }
    }

    public void updateLines(Player player) {
        for (Line line : this.playerLines.get(player)) {
            PacketContainer[] packets = line.getUpdatePackets();
            PacketHelper.sendPackets(player, packets);
        }
    }

    public void removeLines(Player player) {
        if (!this.playerLines.containsKey(player)) {
            return;
        }
        for (Line line : this.playerLines.get(player)) {
            PacketContainer[] packets = line.getDespawnPackets();
            PacketHelper.sendPackets(player, packets);
        }
        this.playerLines.remove(player);
    }

    public void changeLines(Player player, List<Line> newLines) {
        if (newLines == null || newLines.isEmpty()) {
            return;
        }
        if (this.playerLines.containsKey(player) && !this.playerLines.get(player).isEmpty()) {
            this.removeLines(player);
        }
        this.playerLines.put(player, newLines);
        double currentY = this.loc.getY();
        for (int i = 0; i < newLines.size(); ++i) {
            Line line = newLines.get(i);
            if (i != 0) {
                currentY -= line.getHeight();
                currentY -= 0.02;
            }
            line.sendSpawnPackets(player, this.loc.getX(), currentY, this.loc.getZ());
        }
    }

    public List<Line> getLines(Player player) {
        if (!this.playerLines.containsKey(player)) {
            return null;
        }
        return this.playerLines.get(player);
    }

    public void teleport(Location loc) {
        Location oldLoc = this.loc;
        this.loc = loc;
        for (Player player : this.loadedPlayers) {
            if (HologramAPI.getApi().isInRange(player, this)) continue;
            this.unload(player);
            return;
        }
        for (Map.Entry entry : this.playerLines.entrySet()) {
            Player player = (Player)entry.getKey();
            List lines = (List)entry.getValue();
            double oldCurrentY = oldLoc.getY();
            double currentY = this.loc.getY();
            for (int i = 0; i < lines.size(); ++i) {
                Line line = (Line)lines.get(i);
                if (i != 0) {
                    currentY -= line.getHeight();
                    currentY -= 0.02;
                    oldCurrentY -= line.getHeight();
                    oldCurrentY -= 0.02;
                }
                PacketContainer[] packets = line.getTeleportPackets(player, oldLoc.getX(), oldCurrentY, oldLoc.getZ(), this.loc.getX(), currentY, this.loc.getZ());
                PacketHelper.sendPackets(player, packets);
            }
            HologramAPI.getApi().checkLoadableHologram(this);
        }
    }

    public Location getLocation() {
        return this.loc;
    }

    public boolean isLoaded(Player player) {
        return this.loadedPlayers.contains(player);
    }

    public List<Player> getLoadedPlayers() {
        return this.loadedPlayers;
    }

    public List<Line> onLoad(Player player) {
        return new ArrayList<Line>();
    }

    public void onUnload(Player player) {
    }
}