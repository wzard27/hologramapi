package de.truhera.hologramapi.lines;

import com.comphenix.protocol.events.PacketContainer;
import de.truhera.hologramapi.lines.helpers.PacketHelper;
import org.bukkit.entity.Player;

public class EmptyLine implements Line {

	private double height;

	public EmptyLine() {
		this.height = 0.23D;
	}

	public EmptyLine(double height) {
		this.height = height;
		if (height <= 0.0D)
			this.height = 0.23D;
	}
	@Override
	public double getHeight() {
		return 0.23D;
	}
	@Override
	public void sendSpawnPackets(Player p, double x, double y, double z) {
		PacketHelper.sendPackets(p, new PacketContainer[0]);
	}

	public PacketContainer[] getSpawnPackets(Player p, double x, double y, double z) {
		return new PacketContainer[0];
	}

	@Override
	public PacketContainer[] getDespawnPackets() {
		return new PacketContainer[0];
	}

	@Override
	public PacketContainer[] getTeleportPackets(Player p, double oldX, double oldY, double oldZ, double newX, double newY, double newZ) {
		return new PacketContainer[0];
	}

	@Override
	public PacketContainer[] getUpdatePackets() {
		return new PacketContainer[0];
	}

}
