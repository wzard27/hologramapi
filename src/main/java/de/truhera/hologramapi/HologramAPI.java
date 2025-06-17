package de.truhera.hologramapi;

import de.truhera.hologramapi.GlobalHologram;
import de.truhera.hologramapi.Hologram;
import de.truhera.hologramapi.PlayerDefinedHologram;
import de.truhera.hologramapi.listener.PlayerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramAPI {
	private final Map<Player, List<Hologram>> holograms = new HashMap<Player, List<Hologram>>();
	private final List<GlobalHologram> globalHolograms = new ArrayList<GlobalHologram>();
	private final List<PlayerDefinedHologram> playerDefinedHolograms = new ArrayList<PlayerDefinedHologram>();
	private final Random random = new Random();
	private JavaPlugin plugin;
	private boolean viaversionEnabled = false;
	private static HologramAPI api;

	public HologramAPI(JavaPlugin plugin) {
		this.plugin = plugin;
		this.viaversionEnabled = plugin.getServer().getPluginManager().isPluginEnabled("ViaVersion");
		Bukkit.getPluginManager().registerEvents((Listener)new PlayerListener(), (Plugin)plugin);
	}

	public static void init(JavaPlugin plugin) {
		if (api != null) {
			return;
		}
		api = new HologramAPI(plugin);
	}

	public void onDisable() {
		for (Map.Entry<Player, List<Hologram>> entry : this.holograms.entrySet()) {
			for (Hologram hologram : entry.getValue()) {
				hologram.unload();
			}
		}
		for (GlobalHologram globalHologram : this.globalHolograms) {
			globalHologram.unloadAll();
		}
		for (PlayerDefinedHologram playerDefinedHologram : this.playerDefinedHolograms) {
			playerDefinedHologram.unloadAll();
		}
		this.holograms.clear();
		this.globalHolograms.clear();
		this.playerDefinedHolograms.clear();
	}

	public List<GlobalHologram> getGlobalHolograms() {
		return this.globalHolograms;
	}

	public void addGlobalHologram(GlobalHologram hologram) {
		this.globalHolograms.add(hologram);
		this.checkLoadableHologram(hologram);
	}

	public void removeGlobalHologram(GlobalHologram hologram) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			hologram.unload(player);
		}
		this.globalHolograms.remove(hologram);
	}

	public List<Hologram> getHolograms(Player player) {
		List<Hologram> list = this.holograms.get(player);
		if (list == null) {
			list = new ArrayList<Hologram>();
		}
		return list;
	}

	public void addHologram(Hologram hologram) {
		List<Hologram> list = this.getHolograms(hologram.getPlayer());
		list.add(hologram);
		this.holograms.put(hologram.getPlayer(), list);
		if (this.isInRange(hologram.getPlayer(), hologram)) {
			hologram.load();
		}
	}

	public void removeHologram(Hologram hologram) {
		hologram.unload();
		List<Hologram> list = this.getHolograms(hologram.getPlayer());
		list.remove(hologram);
		if (list.isEmpty()) {
			this.holograms.remove(hologram.getPlayer());
		} else {
			this.holograms.put(hologram.getPlayer(), list);
		}
	}

	public void removeHolograms(Player player) {
		for (Hologram hologram : this.getHolograms(player)) {
			hologram.unload();
		}
		this.holograms.remove(player);
	}

	public List<PlayerDefinedHologram> getPlayerDefinedHolograms() {
		return this.playerDefinedHolograms;
	}

	public void addPlayerDefinedHologram(PlayerDefinedHologram hologram) {
		this.playerDefinedHolograms.add(hologram);
		this.checkLoadableHologram(hologram);
	}

	public void removePlayerDefinedHologram(PlayerDefinedHologram hologram) {
		hologram.unloadAll();
		this.globalHolograms.remove(hologram);
	}

	public void updateHolograms(Player player) {
		for (Hologram hologram : this.getHolograms(player)) {
			hologram.unload();
		}
		for (GlobalHologram globalHologram : this.globalHolograms) {
			globalHologram.unload(player);
		}
		for (PlayerDefinedHologram playerDefinedHologram : this.playerDefinedHolograms) {
			playerDefinedHologram.unload(player);
		}
		this.checkLoadableHolograms(player);
	}

	public void checkLoadableHolograms(Player player) {
		for (Hologram hologram : this.getHolograms(player)) {
			this.checkLoadableHologram(hologram);
		}
		for (GlobalHologram globalHologram : this.globalHolograms) {
			boolean inRange = this.isInRange(player, globalHologram);
			if (!inRange && globalHologram.isLoaded(player)) {
				globalHologram.unload(player);
				continue;
			}
			if (!inRange || globalHologram.isLoaded(player)) continue;
			globalHologram.load(player);
		}
	}

	public void checkLoadableHologram(Hologram hologram) {
		boolean inRange = this.isInRange(hologram.getPlayer(), hologram);
		if (!inRange && hologram.isLoaded()) {
			hologram.unload();
		} else if (inRange && !hologram.isLoaded()) {
			hologram.load();
		}
	}

	public void checkLoadableHologram(GlobalHologram hologram) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			boolean inRange = this.isInRange(player, hologram);
			if (!inRange && hologram.isLoaded(player)) {
				hologram.unload(player);
				continue;
			}
			if (!inRange || hologram.isLoaded(player)) continue;
			hologram.load(player);
		}
	}

	public void checkLoadableHologram(PlayerDefinedHologram hologram) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			boolean inRange = this.isInRange(player, hologram);
			if (!inRange && hologram.isLoaded(player)) {
				hologram.unload(player);
				continue;
			}
			if (!inRange || hologram.isLoaded(player)) continue;
			hologram.load(player);
		}
	}

	public boolean isInRange(Player player, GlobalHologram hologram) {
		if (player.getWorld() != hologram.getLocation().getWorld()) {
			return false;
		}
		Chunk playerChunk = player.getLocation().getChunk();
		Chunk hologramChunk = hologram.getLocation().getChunk();
		return this.diff(playerChunk.getX(), hologramChunk.getX()) <= 4 && this.diff(playerChunk.getZ(), hologramChunk.getZ()) <= 4;
	}

	public boolean isInRange(Player player, Hologram hologram) {
		if (player.getWorld() != hologram.getLocation().getWorld()) {
			return false;
		}
		Chunk playerChunk = player.getLocation().getChunk();
		Chunk hologramChunk = hologram.getLocation().getChunk();
		return this.diff(playerChunk.getX(), hologramChunk.getX()) <= 4 && this.diff(playerChunk.getZ(), hologramChunk.getZ()) <= 4;
	}

	public boolean isInRange(Player player, PlayerDefinedHologram hologram) {
		if (player.getWorld() != hologram.getLocation().getWorld()) {
			return false;
		}
		Chunk playerChunk = player.getLocation().getChunk();
		Chunk hologramChunk = hologram.getLocation().getChunk();
		return this.diff(playerChunk.getX(), hologramChunk.getX()) <= 4 && this.diff(playerChunk.getZ(), hologramChunk.getZ()) <= 4;
	}

	private int diff(int x1, int x2) {
		return Math.abs(x1 - x2);
	}

	public boolean isViaversionEnabled() {
		return this.viaversionEnabled;
	}

	public JavaPlugin getPlugin() {
		return this.plugin;
	}

	public static HologramAPI getApi() {
		return api;
	}

	public int newId() {
		return this.random.nextInt(1000000) + 32000;
	}
}