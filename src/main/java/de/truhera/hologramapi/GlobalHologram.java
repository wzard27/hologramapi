package de.truhera.hologramapi;

import com.comphenix.protocol.events.PacketContainer;
import de.truhera.hologramapi.HologramAPI;
import de.truhera.hologramapi.HologramAlignment;
import de.truhera.hologramapi.lines.Line;
import de.truhera.hologramapi.lines.helpers.PacketHelper;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GlobalHologram {
	private final List<Player> loadedPlayers = new ArrayList<Player>();
	private Location loc;
	private final List<Line> lines = new ArrayList<Line>();
	private final List<Line> clientSynchronized = new ArrayList<Line>();
	private final HologramAlignment alignment;

	public GlobalHologram(Location loc) {
		this.loc = loc;
		this.alignment = HologramAlignment.STACK_UP;
	}

	public GlobalHologram(Location loc, HologramAlignment alignment) {
		this.loc = loc;
		this.alignment = alignment;
	}

	private void broadcastPackets(PacketContainer[] packets) {
		for (Player player : this.loadedPlayers) {
			PacketHelper.sendPackets(player, packets);
		}
	}

	public void load(Player player) {
		if (this.loadedPlayers.contains(player) || player.getWorld() != this.loc.getWorld()) {
			return;
		}
		this.loadedPlayers.add(player);
		double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		for (Line line : this.lines) {
			currentY -= line.getHeight();
			line.sendSpawnPackets(player, this.loc.getX(), currentY -= 0.05, this.loc.getZ());
		}
	}

	public void unload(Player player) {
		if (!this.loadedPlayers.contains(player) || player.getWorld() != this.loc.getWorld()) {
			return;
		}
		for (Line line : this.lines) {
			PacketContainer[] packets = line.getDespawnPackets();
			PacketHelper.sendPackets(player, packets);
		}
		this.loadedPlayers.remove(player);
	}

	public void unloadAll() {
		for (Player loaded : new ArrayList<Player>(this.loadedPlayers)) {
			this.unload(loaded);
		}
	}

	public void teleport(Location loc) {
		Location oldLoc = this.loc;
		this.loc = loc;
		for (Player player : this.loadedPlayers) {
			if (HologramAPI.getApi().isInRange(player, this)) continue;
			this.unload(player);
			return;
		}
		double oldCurrentY = oldLoc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		for (Line line : this.lines) {
			currentY -= line.getHeight();
			currentY -= 0.05;
			oldCurrentY -= line.getHeight();
			oldCurrentY -= 0.05;
			for (Player player : this.loadedPlayers) {
				PacketContainer[] packets = line.getTeleportPackets(player, oldLoc.getX(), oldCurrentY, oldLoc.getZ(), this.loc.getX(), currentY, this.loc.getZ());
				PacketHelper.sendPackets(player, packets);
			}
		}
		HologramAPI.getApi().checkLoadableHologram(this);
	}

	private double getFullHologramHeight() {
		double height = 0.0;
		for (Line line : this.lines) {
			height += 0.05;
			height += line.getHeight();
		}
		return height;
	}

	private double getAlignmentAdjustmentHeight() {
		double height = this.getFullHologramHeight();
		if (this.alignment == HologramAlignment.STACK_UP) {
			return 0.0;
		}
		if (this.alignment == HologramAlignment.STACK_DOWN) {
			return -height;
		}
		return -height / 2.0;
	}

	public List<Line> getLines() {
		return this.lines;
	}

	public void addLine(Line line) {
		this.lines.add(line);
		this.synchronizeLines();
	}

	public void removeLine(Line line) {
		this.lines.remove(line);
		this.broadcastPackets(line.getDespawnPackets());
		this.synchronizeLines();
	}

	public void removeLines() {
		for (Line line : new ArrayList<Line>(this.lines)) {
			this.lines.remove(line);
			this.broadcastPackets(line.getDespawnPackets());
		}
	}

	public void updateLine(Line line) {
		this.broadcastPackets(line.getUpdatePackets());
	}

	public void updateLines() {
		for (Line line : this.lines) {
			this.updateLine(line);
		}
	}

	@Deprecated
	public void refreshLines() {
		for (Line line : this.lines) {
			PacketContainer[] despawnPackets = line.getDespawnPackets();
			for (Player loaded : this.loadedPlayers) {
				PacketHelper.sendPackets(loaded, despawnPackets);
			}
		}
		double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		for (Line line : this.lines) {
			currentY -= line.getHeight();
			currentY -= 0.05;
			for (Player loaded : this.loadedPlayers) {
				line.sendSpawnPackets(loaded, this.loc.getX(), currentY, this.loc.getZ());
			}
		}
	}

	public void synchronizeLines() {
		double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		for (int lineIndex = 0; lineIndex < this.lines.size(); ++lineIndex) {
			Line line2 = this.lines.get(lineIndex);
			currentY -= line2.getHeight();
			currentY -= 0.05;
			int syncedBefore = this.clientSynchronized.indexOf(line2);
			if (syncedBefore == -1) {
				for (Player loaded : this.loadedPlayers) {
					line2.sendSpawnPackets(loaded, this.loc.getX(), currentY, this.loc.getZ());
				}
				continue;
			}
			if (syncedBefore == lineIndex) continue;
			for (Player loaded : this.loadedPlayers) {
				PacketContainer[] packets = line2.getTeleportPackets(loaded, 0.0, 0.0, 0.0, this.loc.getX(), currentY, this.loc.getZ());
				PacketHelper.sendPackets(loaded, packets);
			}
		}
		this.clientSynchronized.stream().filter(l -> !this.lines.contains(l)).forEach(line -> {
			PacketContainer[] despawnPackets = line.getDespawnPackets();
			for (Player loaded : this.loadedPlayers) {
				PacketHelper.sendPackets(loaded, despawnPackets);
			}
		});
		this.clientSynchronized.clear();
		this.clientSynchronized.addAll(this.lines);
	}

	public boolean isLoaded(Player player) {
		return this.loadedPlayers.contains(player);
	}

	public List<Player> getLoadedPlayers() {
		return this.loadedPlayers;
	}

	public Location getLocation() {
		return this.loc;
	}
}