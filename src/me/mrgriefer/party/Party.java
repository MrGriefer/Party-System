package packa-ge;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.mrgriefer.slaparoo.Slaparoo;
import me.mrgriefer.slaparoo.commands.messages.MessageHandler;
import me.mrgriefer.slaparoo.game.GamePlayer;
import me.mrgriefer.slaparoo.util.SmartInventory;

public class Party {

	private String leader;
	private PartyPrivacy privacy;
	private int capacity;
	
	private ArrayList<String> players = new ArrayList<>();
	private ArrayList<String> invited = new ArrayList<>();
	private Inventory gui;
	private Inventory playersInventory;
	
	private FileConfiguration cfg = Slaparoo.instance.getConfig();
	
	private int id;
	private int slot;
	
	public Party(Player leader, int capacity) {
		this.capacity = capacity;
		this.players.add(leader.getName());
		this.gui = Bukkit.createInventory(null, 36, cfg.getString("Party.Settings-Menu.Title").replace("&", "§"));
		this.playersInventory = Bukkit.createInventory(null, getInventorySize(capacity + 1), cfg.getString("Party.Party-Players-Menu.Title").replace("&", "§"));
		this.playersInventory.setItem(playersInventory.getSize() - 1, PartyItems.backItem());
		setLeader(leader);
		setPrivacy(PartyPrivacy.INVITE);
		updatePlayers();
		this.gui.setItem(16, PartyItems.inviteItem());
		this.gui.setItem(31, PartyItems.leaveItem());
	}
	
	public void setLeader(Player leader) {
		this.leader = leader.getName();
		this.gui.setItem(10, PartyItems.leaderItem(leader.getName()));
	}
	
	public void setPrivacy(PartyPrivacy privacy) {
		this.privacy = privacy;
		if (privacy == PartyPrivacy.INVITE) {
			this.gui.setItem(12, PartyItems.privatePrivacyItem(privacy.toString()));
		} else {
			this.gui.setItem(12, PartyItems.publicPrivacyItem(privacy.toString()));
		}
	}
	
	public void updatePlayers() {
		String[] ids = cfg.getString("Party.Settings-Menu.Players.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Settings-Menu.Players.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§b" + players.size() + "§7/§f" + capacity);
		lore.add("§a");
		
		int i = 0;
		for (int j = 0; j < capacity; j++) {
			playersInventory.setItem(j, new ItemStack(Material.AIR));
		}
		
		for (String name : players) {
			String color = name.equals(leader) ? "§6" : "§7";
			lore.add("§7- " + color + name);
			
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
			skullMeta.setOwner(name);
			skullMeta.setDisplayName(color + name);
			skull.setItemMeta(skullMeta);
			if (!name.equals(leader)) {
				List<String> skullLore = new ArrayList<String>();
				skullLore.add("§7Left click to promote to leader!");
				skullLore.add("§7Right click to kick!");
			}
			playersInventory.setItem(i, skull);
			i++;
		}
		gui.setItem(14, item);
	}
	
	public void updateItem() {
		SmartInventory partySelector = Slaparoo.getPartyManager().getPartySelector();
		partySelector.setItem(id, slot, partyItem());
	}
	
	public void sendMessage(String message) {
		for (String name : players) {
			if (Bukkit.getPlayer(name) != null) {
				Player player = Bukkit.getPlayer(name);
				player.sendMessage(message);
			}
		}
	}
	
	public void leaveParty(GamePlayer player) {
		this.players.remove(player.getName());
		player.setParty(null);
		player.getPlayer().closeInventory();
		player.sendMessage(MessageHandler.getMessage("Party-Player-Leave"));
		if (players.isEmpty()) {
			Slaparoo.getPartyManager().getParties().remove(this);
			Slaparoo.getPartyManager().updatePartiesInventory();
		} else {
			sendMessage(MessageHandler.getMessage("Party-Player-Leave-Alert").replace("{player}", player.getName()));
			if (player.getName().equals(leader)) {
				Player leader = Bukkit.getPlayer(players.get(0));
				setLeader(leader);
				sendMessage(MessageHandler.getMessage("Party-New-Leader").replace("{leader}", leader.getName()));
			}
			updatePlayers();
		}
	}
	
	public void joinParty(GamePlayer player) {
		if (players.size() >= capacity) {
			player.sendMessage(MessageHandler.getMessage("Party-Full"));
			return;
		}
		
		if ((privacy == PartyPrivacy.PUBLIC) || (invited.contains(player.getName()))) {
			players.add(player.getName());
			player.setParty(this);
			updatePlayers();
			sendMessage(MessageHandler.getMessage("Party-Join").replace("{player}", player.getName()));
			player.getPlayer().closeInventory();
		} else {
			player.sendMessage(MessageHandler.getMessage("Party-Not-Invited"));
		}
	}
	
	public void inviteParty(final GamePlayer player) {
		sendMessage(MessageHandler.getMessage("Party-Invite-Send").replace("{leader}", leader).replace("{target}", player.getName()));
		invited.add(player.getName());
		player.sendMessage(MessageHandler.getMessage("Party-Invite-Receive").replace("{leader}", leader).replace("{seconds}", String.valueOf(cfg.getInt("Party.Invitation-Length"))));
		Bukkit.getScheduler().scheduleSyncDelayedTask(Slaparoo.instance, new Runnable() {
			public void run() {
				invited.remove(player.getName());
				if (!players.contains(player.getName())) {
					player.sendMessage(MessageHandler.getMessage("Party-Invitation-Expire"));
				}
			}
		}, cfg.getInt("Party.Invitation-Length") * 20);
	}
	
	public void kick(GamePlayer player) {
		player.sendMessage(MessageHandler.getMessage("Party-Player-Kick"));
		leaveParty(player);
		sendMessage(MessageHandler.getMessage("Party-Player-Kick-Alert").replace("{leader}", leader).replace("{player}", player.getName()));
	}
	
	public String getLeader() {
		return leader;
	}
	
	public PartyPrivacy getPrivacy() {
		return privacy;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public Inventory getGUI() {
		return gui;
	}
	
	public Inventory getPlayersInventory() {
		return playersInventory;
	}
	
	public ItemStack partyItem() {
		String[] ids = cfg.getString("Party.Join-Menu.Party-Icon.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Join-Menu.Party-Icon.Name").replace("&", "§").replace("{leader}", leader));
		List<String> lore = new ArrayList<>();
		lore.add("§7Privacy: " + privacy);
		lore.add("§a");
		lore.add("§ePlayers: §a" + players.size() + "/" + capacity);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	private int getInventorySize(int num) {
		return num < 46 ? 45 : num < 37 ? 36 : num < 28 ? 27 : num < 19 ? 18 : num < 10 ? 9 : 54;
	}
	
}
