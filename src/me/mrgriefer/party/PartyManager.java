package me.mrgriefer.slaparoo.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.mrgriefer.slaparoo.Slaparoo;
import me.mrgriefer.slaparoo.util.SmartInventory;

public class PartyManager {

	private ArrayList<Party> parties;
	
	private SmartInventory partySelector;
	private SmartInventory playerInviter;
	
	private FileConfiguration cfg = Slaparoo.instance.getConfig();
	
	public PartyManager() {
		this.parties = new ArrayList<Party>();
		this.partySelector = new SmartInventory("§bParties");
		this.partySelector.addInventory("§c#1");
		this.partySelector.setItem(0, 49, PartyItems.backItem());
		
		this.playerInviter = new SmartInventory("§bInvite Players");
	}
	
	public void updatePartiesInventory() {
		for (int i = 0; i < partySelector.getSize(); i++) {
			partySelector.clear(i);
		}
		int i = 0;
		for (int id = 0; id < Math.ceil(parties.size() / partySelector.getSmartSlots().length); id++) {
			if (id >= partySelector.getSize()) {
				partySelector.addInventory("§c#" + (id + 1));
				partySelector.setItem(id, 49, PartyItems.backItem());
			}
			for (int emptySlots = 0; emptySlots < partySelector.getSmartSlots().length; emptySlots++) {
				int slot = partySelector.getSmartSlots()[emptySlots];
				if (i >= parties.size()) {
					break;
				}
				Party party = parties.get(i);
				party.setID(id);
				party.setSlot(slot);
				party.updateItem();
				i++;
			}
		}
	}
	
	public void updateOnJoin(Player player) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(player.getName());
		skullMeta.setDisplayName("§b" + player.getName());
		skull.setItemMeta(skullMeta);
		
		for (int i = 0; i < playerInviter.getSize(); i++) {
			if (playerInviter.addItem(i, skull)) {
				return;
			}
		}
		int inventory = playerInviter.addInventory("§c#" + (playerInviter.getSize() + 1));
		playerInviter.setItem(inventory, 49, PartyItems.backItem());
		playerInviter.addItem(inventory, skull);
	}
	
	public void updateOnLeave(Player player) {
		for (int i = 0; i < playerInviter.getSize(); i++) {
			for (int emptySlots = 0; emptySlots < playerInviter.getSmartSlots().length; emptySlots++) {
				int slot = playerInviter.getSmartSlots()[emptySlots];
				ItemStack item = playerInviter.getItem(i, slot);
				if (item != null) {
					if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(player.getName())) {
						playerInviter.removeItem(i, slot);
						return;
					}
				}
			}
		}
	}
	
	public Inventory getPartyMenu() {
		Inventory menu = Bukkit.createInventory(null, 27, "Select your option!");
		
		menu.setItem(11, join());
		menu.setItem(15, create());
		
		return menu;
	}
	
	private ItemStack create() { 
		String[] ids = cfg.getString("Party.Default-Menu.Create.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Default-Menu.Create.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§a");
		lore.add("§7Click to create a party!");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
	
	private ItemStack join() { 
		String[] ids = cfg.getString("Party.Default-Menu.Join.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Default-Menu.Join.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§a");
		lore.add("§7Click to join a party!");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
	
	public List<Party> getParties() {
		return parties;
	}
	
	public SmartInventory getPartySelector() {
		return partySelector;
	}
	
	public SmartInventory getPlayerInviter() {
		return playerInviter;
	}
	
}
