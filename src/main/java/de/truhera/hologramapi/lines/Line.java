package de.truhera.hologramapi.lines;

import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public interface Line {
	public double getHeight();

	public void sendSpawnPackets(Player var1, double var2, double var4, double var6);

	public PacketContainer[] getDespawnPackets();

	public PacketContainer[] getTeleportPackets(Player var1, double var2, double var4, double var6, double var8, double var10, double var12);

	public PacketContainer[] getUpdatePackets();
}
