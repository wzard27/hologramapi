package de.truhera.hologramapi.lines;

import com.comphenix.protocol.events.PacketContainer;
import de.truhera.hologramapi.HologramOffsets;
import de.truhera.hologramapi.lines.helpers.PacketHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemLine implements Line {
	private final int entityId;
	private final int armorStandId;
	private ItemStack item;

	public ItemLine(int entityId, int armorStandId, ItemStack item) {
		this.entityId = entityId;
		this.armorStandId = armorStandId;
		this.item = item;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public double getHeight() {
		return 0.7D;
	}


	public PacketContainer[] getSpawnPackets(Player p, double x, double y, double z) {
		double offset = HologramOffsets.getOffset(p);
		y += offset;
		return new PacketContainer[] {
			PacketHelper.itemSpawn(entityId, x, y, z),
			PacketHelper.itemMeta(entityId, item),
			PacketHelper.armorStandSpawn(this.armorStandId, x, y - 1.48D, z),
			PacketHelper.itemArmorStandMeta(this.armorStandId),
			PacketHelper.itemArmorStandAttach(entityId, armorStandId)
		};
	}

	@Override
	public PacketContainer[] getDespawnPackets() {
		return new PacketContainer[] { PacketHelper.entityDestroy(this.entityId), PacketHelper.entityDestroy(this.armorStandId) };
	}

	public void sendSpawnPackets(Player p, double x, double y, double z) {
		PacketContainer[] packets = { PacketHelper.itemSpawn(this.entityId, x, y, z), PacketHelper.itemMeta(this.entityId, this.item), PacketHelper.armorStandSpawn(this.armorStandId, x, y + 0.03D, z), PacketHelper.itemArmorStandMeta(this.armorStandId), PacketHelper.itemArmorStandAttach(this.entityId, this.armorStandId) };
		PacketHelper.sendPackets(p, packets);
	}

	@Override
	public PacketContainer[] getTeleportPackets(Player p, double oldX, double oldY, double oldZ, double newX, double newY, double newZ) {
		double offset = HologramOffsets.getOffset(p);
//		oldY += offset;
		newY += offset;
//		int distX = (int) (Math.floor(newX * 32.0D) - Math.floor(oldX * 32.0D));
//		int distY = (int) (Math.floor((newY - 1.48D) * 32.0D) - Math.floor((oldY - 1.48D) * 32.0D));
//		int distZ = (int) (Math.floor(newZ * 32.0D) - Math.floor(oldZ * 32.0D));

		return new PacketContainer[] {
				PacketHelper.entityTeleport(this.armorStandId, newX, newY - 1.48D, newZ)
		};

//		if ((distX >= -128) && (distX < 128) && (distY >= -128) && (distY < 128) && (distZ >= -128) && (distZ < 128)) {
//			// Send teleport
//			return new PacketContainer[] {
//				PacketHelper.entityTeleport(this.armorStandId, newX, newY - 1.48D, newZ)
//			};
//		} else {
//			// Send move
//			return new PacketContainer[] {
//				PacketHelper.entityMove(this.armorStandId, (byte) distX, (byte) distY, (byte) distZ)
//			};
//		}
	}

	@Override
	public PacketContainer[] getUpdatePackets() {
		return new PacketContainer[] { PacketHelper.itemMeta(entityId, item) };
	}

}
