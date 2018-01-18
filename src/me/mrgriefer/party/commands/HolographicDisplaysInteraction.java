package me.mrgriefer.slaparoo.hologram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import me.mrgriefer.slaparoo.Slaparoo;
import me.mrgriefer.slaparoo.Util;
import me.mrgriefer.slaparoo.commands.messages.MessageHandler;
import me.mrgriefer.slaparoo.commands.permission.Permission;
import me.mrgriefer.slaparoo.commands.permission.PermissionHandler;
import me.mrgriefer.slaparoo.database.sql.StatisticType;

public class HolographicDisplaysInteraction {

	private ArrayList<Location> hologramLocations;
	private Map<Player, List<Hologram>> holograms;
	
	public void addHologramLocation(Location eyeLocation) {
		this.hologramLocations.add(eyeLocation);
		updateHologramDatabase();
	}
	
	private Hologram createPlayerStatisticHologram(Player player, Location holoLocation) {
		final Hologram holo = HologramsAPI.createHologram(Slaparoo.getInstance(), holoLocation);
		holo.getVisibilityManager().setVisibleByDefault(false);
		holo.getVisibilityManager().showTo(player);
		
		updatePlayerStatisticHologram(player, holo);
		return holo;
	}
	
	private Hologram getHologramByLocation(List<Hologram> holograms, Location holoLocation) {
		for (Hologram holo : holograms) {
			if (holo.getLocation().getX() == holoLocation.getX()
					&& holo.getLocation().getY() == holoLocation.getY()
					&& holo.getLocation().getZ() == holoLocation.getZ()) {
				return holo;
			}
		}
		
		return null;
	}
	
	private Location getHologramLocationByLocation(Location holoLocation) {
		for (Location loc : hologramLocations) {
			if (loc.getX() == holoLocation.getX() && loc.getY() == holoLocation.getY() && loc.getZ() == holoLocation.getZ()) {
				return loc;
			}
		}
		
		return null;
	}
	
	public List<Hologram> getHolograms(Player player) {
		return holograms.get(player);
	}
	
	public Map<Player, List<Hologram>> getHolograms() {
		return holograms;
	}
	
	@SuppressWarnings("unchecked")
	public void loadHolograms() {
		if (!Slaparoo.getInstance().isHologramsEnabled()) {
			return;
		}
		
		if (holograms != null && hologramLocations != null) {
			unloadHolograms();
		}
		
		this.holograms = new HashMap<Player, List<Hologram>>();
		this.hologramLocations = new ArrayList<Location>();
		
		FileConfiguration config = Slaparoo.getInstance().getConfig();
		List<Object> locations = (List<Object>) config.get("Holographic-Stats.List");
		for (Object location : locations) {
			Location loc = Util.locationDeserialize(location);
			if (loc == null) {
				continue;
			}
			
			hologramLocations.add(loc);
		}
		
		if (hologramLocations.size() == 0) {
			return;
		}
		
		updateHolograms();
	}
	
	public void onHologramTouch(final Player player, final Hologram holo) {
		if (!player.hasMetadata("slaparoo-remove-holo")
				|| (!player.isOp() && !PermissionHandler.hasPermission(player, Permission.SETUP))) {
			return;
		}
		
		player.removeMetadata("slaparoo-remove-holo", Slaparoo.getInstance());
		Slaparoo.getInstance().getServer().getScheduler().runTask(Slaparoo.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Entry<Player, List<Hologram>> entry : getHolograms().entrySet()) {
					Iterator<Hologram> iterator = entry.getValue().iterator();
					while (iterator.hasNext()) {
						Hologram hologram = iterator.next();
						if (hologram.getX() == holo.getX() && hologram.getY() == holo.getY() && hologram.getZ() == holo.getZ()) {
							hologram.delete();
							iterator.remove();
						}
					}
				}
				
				Location holoLocation = getHologramLocationByLocation(holo.getLocation());
				if (holoLocation != null) {
					hologramLocations.remove(holoLocation);
					updateHologramDatabase();
				}
				player.sendMessage(MessageHandler.getMessage("Prefix") + "§aHologram statistic successfully removed!");
			}
		});
	}
	
	public void removeHologramPlayer(Player player) {
		holograms.remove(player);
	}
	
	public void unloadAllHolograms(Player player) {
		if (!holograms.containsKey(player)) {
			return;
		}
		
		for (Hologram holo : holograms.get(player)) {
			holo.delete();
		}
		
		holograms.remove(player);
	}
	
	public void unloadHolograms() {
		if (Slaparoo.getInstance().isHologramsEnabled()) {
			Iterator<Hologram> iterator = HologramsAPI.getHolograms(Slaparoo.getInstance()).iterator();
			while (iterator.hasNext()) {
				iterator.next().delete();
			}
		}
	}
	
	private void updateHologramDatabase() {
		try {
			FileConfiguration config = Slaparoo.getInstance().getConfig();
			List<Map<String, Object>> serializedLocations = new ArrayList<Map<String, Object>>();
			
			for (Location holoLocation : hologramLocations) {
				serializedLocations.add(Util.locationSerialize(holoLocation));
			}
			
			config.set("Holographic-Stats.List", serializedLocations);
			Slaparoo.getInstance().saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateHolograms() {
		for (final Player player : Slaparoo.getInstance().getServer().getOnlinePlayers()) {
			Slaparoo.getInstance().getServer().getScheduler().runTask(Slaparoo.getInstance(), new Runnable() {
				@Override
				public void run() {
					for (Location holoLocation : hologramLocations) {
						updatePlayerHologram(player, holoLocation);
					}
				}
			});
		}
	}
	
	public void updateHolograms(final Player player) {
		Slaparoo.getInstance().getServer().getScheduler().runTask(Slaparoo.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Location holoLocation : hologramLocations) {
					updatePlayerHologram(player, holoLocation);
				}
			}
		});
	}
	
	public void updateHolograms(final Player player, long delay) {
		Slaparoo.getInstance().getServer().getScheduler().runTaskLater(Slaparoo.getInstance(), new Runnable() {
			@Override
			public void run() {
				updateHolograms(player);
			}
		}, delay);
	}
	
	private void updatePlayerHologram(Player player, Location holoLocation) {
		List<Hologram> holograms = null;
		if (!this.holograms.containsKey(player)) {
			this.holograms.put(player, new ArrayList<Hologram>());
		}
		
		holograms = this.holograms.get(player);
		Hologram holo = getHologramByLocation(holograms, holoLocation);
		if (holo == null && player.getWorld() == holoLocation.getWorld()) {
			holograms.add(createPlayerStatisticHologram(player, holoLocation));
		} else if (holo != null) {
			if (holo.getLocation().getWorld() == player.getWorld()) {
				updatePlayerStatisticHologram(player, holo);
			} else {
				holograms.remove(holo);
				holo.delete();
			}
		}
	}
	
	private void updatePlayerStatisticHologram(Player player, final Hologram holo) {
		holo.clearLines();
		
		List<String> lines = Util.colorList(Slaparoo.getInstance().getConfig().getStringList("Holographic-Stats.Lines"));
		lines = Util.replaceAll(lines, "{player}", player.getName());
		lines = Util.replaceAll(lines, "{points}", Integer.toString(Slaparoo.getStatisticManager().get(player, StatisticType.SLAP_POINTS)));
		lines = Util.replaceAll(lines, "{wins}", Integer.toString(Slaparoo.getStatisticManager().get(player, StatisticType.VICTORIES)));
		lines = Util.replaceAll(lines, "{slapoffs}", Integer.toString(Slaparoo.getStatisticManager().get(player, StatisticType.SLAP_OFFS)));
		lines = Util.replaceAll(lines, "{falls}", Integer.toString(Slaparoo.getStatisticManager().get(player, StatisticType.FALLS)));
		lines = Util.replaceAll(lines, "{played}", Integer.toString(Slaparoo.getStatisticManager().get(player, StatisticType.GAMES_PLAYED)));
		
		for (String line : lines) {
			TextLine textLine = holo.appendTextLine(line);
			textLine.setTouchHandler(new TouchHandler() {
				@Override
				public void onTouch(Player player) {
					onHologramTouch(player, holo);
				}
			});
		}
	}
	
}
