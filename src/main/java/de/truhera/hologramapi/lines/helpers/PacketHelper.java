package de.truhera.hologramapi.lines.helpers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class PacketHelper {


	public static PacketContainer itemArmorStandAttach(int itemEntityId, int armorStandEntityId) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ATTACH_ENTITY);
		packet.getIntegers().write(0, itemEntityId);
		packet.getIntegerArrays().write(0, new int[]{armorStandEntityId});
		return packet;
	}

	public static PacketContainer itemArmorStandMeta(int entityId) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

		List<WrappedDataValue> values = Arrays.asList(
			new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x20)),
			new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), false),
				new WrappedDataValue(15, WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x01 | 0x08 | 0x10))

		);

		packet.getIntegers().write(0, entityId);
		packet.getDataValueCollectionModifier().writeSafely(0, values);
		return packet;
	}

	public static PacketContainer itemSpawn(int entityId, double x, double y, double z) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		packet.getIntegers().write(0, entityId);
		packet.getUUIDs().writeSafely(0, UUID.randomUUID());
		packet.getDoubles().write(0, x);
		packet.getDoubles().write(1, y);
		packet.getDoubles().write(2, z);
		packet.getEntityTypeModifier().writeSafely(0, EntityType.DROPPED_ITEM);

		return packet;
	}

	public static PacketContainer itemMeta(int entityId, ItemStack stack) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

		List<WrappedDataValue> values = Collections.singletonList(
			new WrappedDataValue(8, WrappedDataWatcher.Registry.getItemStackSerializer(false), stack)
		);

		packet.getIntegers().write(0, entityId);
		packet.getDataValueCollectionModifier().writeSafely(0, values);
		return packet;
	}

	public static PacketContainer armorStandSpawn(int entityId, double x, double y, double z) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		packet.getIntegers().write(0, entityId);
		packet.getUUIDs().writeSafely(0, UUID.randomUUID());
		packet.getDoubles().write(0, x);
		packet.getDoubles().write(1, y);
		packet.getDoubles().write(2, z);
		packet.getEntityTypeModifier().writeSafely(0, EntityType.ARMOR_STAND);
		return packet;
	}

	public static PacketContainer armorStandMeta(int entityId, String text) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

		List<WrappedDataValue> values = Arrays.asList(
			new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0x20),
			new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), Optional.of(WrappedChatComponent.fromText(text).getHandle())),
				new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), true),
				new WrappedDataValue(5, WrappedDataWatcher.Registry.get(Boolean.class), true), // No grav
				new WrappedDataValue(15, WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x01 | 0x08 | 0x10))
		);

		packet.getIntegers().write(0, entityId);
		packet.getDataValueCollectionModifier().writeSafely(0, values);
		return packet;
	}

	public static PacketContainer armorStandMeta1(int entityId) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

		List<WrappedDataValue> values = Arrays.asList(
				new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte)0x20),
				new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), true), // No grav
		new WrappedDataValue(15, WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x01 | 0x08 | 0x10))
		);

		packet.getIntegers().write(0, entityId);
		packet.getDataValueCollectionModifier().writeSafely(0, values);
		return packet;
	}

	public static PacketContainer armorStandMeta2(int entityId, String text) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

		List<WrappedDataValue> values = Arrays.asList(
				new WrappedDataValue(15, WrappedDataWatcher.Registry.get(Byte.class), (byte) (0x01 | 0x08 | 0x10 | 0x20)),
				new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), true),
				new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), Optional.of(WrappedChatComponent.fromText(text).getHandle()))
		);

		packet.getIntegers().write(0, entityId);
		packet.getDataValueCollectionModifier().writeSafely(0, values);
		return packet;
	}

	public static PacketContainer entityMove(int entityId, byte changeX, byte changeY, byte changeZ) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE);
		packet.getIntegers().write(0, entityId);
		packet.getBytes().write(0, changeX);
		packet.getBytes().write(1, changeY);
		packet.getBytes().write(2, changeZ);
		return packet;
	}

	public static PacketContainer entityTeleport(int entityId, double newX, double newY, double newZ) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
		packet.getIntegers().write(0, entityId);
		packet.getDoubles().write(0, newX);
		packet.getDoubles().write(1, newY);
		packet.getDoubles().write(2, newZ);
		packet.getBytes().write(0, (byte) 0);
		packet.getBytes().write(1, (byte) 0);
		return packet;
	}

	public static PacketContainer entityDestroy(int ... entityId) {
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegerArrays().writeSafely(0, entityId);

		List<Integer> ints = new ArrayList<>();
		for (int id : entityId) {
			ints.add(id);
		}

		packet.getIntLists().writeSafely(0, ints);

		return packet;
	}

	public static void sendPackets(Player player, PacketContainer... packets) {
		for (PacketContainer packet : packets) {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		}
	}

}
