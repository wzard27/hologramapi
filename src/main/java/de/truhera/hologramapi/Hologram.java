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

public class Hologram {
	private final Player player;
	private Location loc;
	private final List<Line> lines = new ArrayList<Line>();
	private final List<Line> clientSynchronized = new ArrayList<Line>();
	private final HologramAlignment alignment;
	private boolean loaded = false;

	public Hologram(Player player, Location loc) {
		this.player = player;
		this.loc = loc;
		this.alignment = HologramAlignment.STACK_UP;
	}

	public Hologram(Player player, Location loc, HologramAlignment alignment) {
		this.player = player;
		this.loc = loc;
		this.alignment = alignment;
	}

	public void load() {
		if (this.loaded || this.player.getWorld() != this.loc.getWorld()) {
			return;
		}
		this.loaded = true;
		double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		for (Line line : this.lines) {
			currentY -= line.getHeight();
			line.sendSpawnPackets(this.player, this.loc.getX(), currentY -= 0.05, this.loc.getZ());
		}
	}

	public void unload() {
		if (!this.loaded || this.player.getWorld() != this.loc.getWorld()) {
			return;
		}
		for (Line line : this.lines) {
			PacketContainer[] packets = line.getDespawnPackets();
			PacketHelper.sendPackets(this.player, packets);
		}
		this.loaded = false;
	}

	public void teleport(Location loc) {
		Location oldLoc = this.loc;
		this.loc = loc;
		if (this.loaded) {
			if (!HologramAPI.getApi().isInRange(this.player, this)) {
				this.unload();
				return;
			}
			double oldCurrentY = oldLoc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
			double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
			for (Line line : this.lines) {
				currentY -= line.getHeight();
				oldCurrentY -= line.getHeight();
				PacketContainer[] packets = line.getTeleportPackets(this.player, oldLoc.getX(), oldCurrentY -= 0.05, oldLoc.getZ(), this.loc.getX(), currentY -= 0.05, this.loc.getZ());
				PacketHelper.sendPackets(this.player, packets);
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
		if (this.loaded) {
			this.synchronizeLines();
		}
	}

	public void removeLine(Line line) {
		this.lines.remove(line);
		if (this.loaded) {
			PacketHelper.sendPackets(this.player, line.getDespawnPackets());
			this.synchronizeLines();
		}
	}

	public void removeLines() {
		for (Line line : new ArrayList<Line>(this.lines)) {
			this.lines.remove(line);
			if (!this.loaded) continue;
			PacketHelper.sendPackets(this.player, line.getDespawnPackets());
		}
	}

	public void updateLine(Line line) {
		PacketHelper.sendPackets(this.player, line.getUpdatePackets());
	}

	@Deprecated
	public void refreshLines() {
		if (!this.loaded || this.player.getWorld() != this.loc.getWorld()) {
			return;
		}
		for (Line line : this.lines) {
			PacketHelper.sendPackets(this.player, line.getDespawnPackets());
		}
		double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		for (Line line2 : this.lines) {
			currentY -= line2.getHeight();
			line2.sendSpawnPackets(this.player, this.loc.getX(), currentY -= 0.05, this.loc.getZ());
		}
	}

	public void synchronizeLines() {
		if (!this.loaded || this.player.getWorld() != this.loc.getWorld()) {
			return;
		}
		double currentY = this.loc.getY() + this.getFullHologramHeight() + this.getAlignmentAdjustmentHeight();
		for (int lineIndex = 0; lineIndex < this.lines.size(); ++lineIndex) {
			Line line2 = this.lines.get(lineIndex);
			currentY -= line2.getHeight();
			currentY -= 0.05;
			int syncedBefore = this.clientSynchronized.indexOf(line2);
			if (syncedBefore == -1) {
				line2.sendSpawnPackets(this.player, this.loc.getX(), currentY, this.loc.getZ());
				continue;
			}
			if (syncedBefore == lineIndex) continue;
			PacketContainer[] packets = line2.getTeleportPackets(this.player, 0.0, 0.0, 0.0, this.loc.getX(), currentY, this.loc.getZ());
			PacketHelper.sendPackets(this.player, packets);
		}
		this.clientSynchronized.stream().filter(l -> !this.lines.contains(l)).forEach(line -> PacketHelper.sendPackets(this.player, line.getDespawnPackets()));
		this.clientSynchronized.clear();
		this.clientSynchronized.addAll(this.lines);
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Location getLocation() {
		return this.loc;
	}
}