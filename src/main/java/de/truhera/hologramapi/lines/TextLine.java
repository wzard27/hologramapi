package de.truhera.hologramapi.lines;

import com.comphenix.protocol.events.PacketContainer;
import de.truhera.hologramapi.HologramAPI;
import de.truhera.hologramapi.lines.Line;
import de.truhera.hologramapi.lines.helpers.PacketHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TextLine
		implements Line {
	private final int entityId;
	private String text;

	public TextLine(int entityId, String text) {
		this.entityId = entityId;
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public double getHeight() {
		return 0.23;
	}

	@Override
	public void sendSpawnPackets(Player p, double x, double y, double z) {
		PacketContainer packetSpawn = PacketHelper.armorStandSpawn(this.entityId, x, y - 0.25, z);
		PacketContainer packetMeta1 = PacketHelper.armorStandMeta1(this.entityId);
		PacketContainer packetMeta2 = PacketHelper.armorStandMeta2(this.entityId, this.text);
		PacketHelper.sendPackets(p, packetSpawn, packetMeta1);
		Bukkit.getScheduler().runTaskLater((Plugin)HologramAPI.getApi().getPlugin(), () -> PacketHelper.sendPackets(p, packetMeta2), 1L);
	}

	@Override
	public PacketContainer[] getDespawnPackets() {
		return new PacketContainer[]{PacketHelper.entityDestroy(this.entityId)};
	}

	@Override
	public PacketContainer[] getTeleportPackets(Player p, double oldX, double oldY, double oldZ, double newX, double newY, double newZ) {
		return new PacketContainer[]{PacketHelper.entityTeleport(this.entityId, newX, newY - 0.25, newZ)};
	}

	@Override
	public PacketContainer[] getUpdatePackets() {
		return new PacketContainer[]{PacketHelper.armorStandMeta(this.entityId, this.text)};
	}
}