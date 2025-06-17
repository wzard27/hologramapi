package de.truhera.hologramapi.listener;

import de.truhera.hologramapi.HologramAPI;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		HologramAPI.getApi().removeHolograms(event.getPlayer());
	}

	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		HologramAPI.getApi().updateHolograms(event.getPlayer());
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		HologramAPI.getApi().updateHolograms(event.getPlayer());
	}

	@EventHandler
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		Bukkit.getScheduler().runTaskLater(HologramAPI.getApi().getPlugin(), new Runnable() {
			@Override
			public void run() {
				HologramAPI.getApi().checkLoadableHolograms(event.getPlayer());
			}
		}, 10L);
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskLater(HologramAPI.getApi().getPlugin(), new Runnable() {
			@Override
			public void run() {
				HologramAPI.getApi().checkLoadableHolograms(event.getPlayer());
			}
		}, 10L);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getWorld() == event.getTo().getWorld() &&
				event.getFrom().getBlockX() == event.getTo().getBlockX() &&
				event.getFrom().getBlockY() == event.getTo().getBlockY() &&
				event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		Player player = event.getPlayer();
		Chunk oldChunk = event.getFrom().getChunk();
		Chunk newChunk = event.getTo().getChunk();

		// Only when the player moves a complete chunk:
		if (oldChunk.getWorld() != newChunk.getWorld() || oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()) {
			HologramAPI.getApi().checkLoadableHolograms(player);
		}
	}

}
