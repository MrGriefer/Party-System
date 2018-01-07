package me.mrgriefer.slaparoo.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.mrgriefer.slaparoo.Slaparoo;

public class PartyItems {

	private static FileConfiguration cfg = Slaparoo.instance.getConfig();
	
	// BACK ITEM
	public static ItemStack backItem() {
		String[] ids = cfg.getString("Party.Party-Players-Menu.Back-Item.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Party-Players-Menu.Back-Item.Name").replace("&", "§"));
		item.setItemMeta(meta);
		return item;
	}
	
	// LEADER ITEM
	public static ItemStack leaderItem(String leader) {
		String[] ids = cfg.getString("Party.Settings-Menu.Leader.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Settings-Menu.Leader.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§a");
		lore.add("§6" + leader);
		lore.add("§b");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
	
	// PUBLIC PRIVACY ITEM
	public static ItemStack publicPrivacyItem(String privacy) {
		String[] ids = cfg.getString("Party.Settings-Menu.Public-Privacy.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Settings-Menu.Public-Privacy.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§a");
		lore.add(privacy);
		lore.add("§b");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
	
	// PRIVATE PRIVACY ITEM
	public static ItemStack privatePrivacyItem(String privacy) {
		String[] ids = cfg.getString("Party.Settings-Menu.Private-Privacy.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Settings-Menu.Private-Privacy.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§a");
		lore.add(privacy);
		lore.add("§b");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
	
	// INVITE ITEM
	public static ItemStack inviteItem() {
		String[] ids = cfg.getString("Party.Settings-Menu.Invite.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Settings-Menu.Invite.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§a");
		lore.add("§7Click to invite players");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
	
	// LEAVE ITEM
	public static ItemStack leaveItem() {
		String[] ids = cfg.getString("Party.Settings-Menu.Leave.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Settings-Menu.Leave.Name").replace("&", "§"));
		List<String> lore = new ArrayList<String>();
		lore.add("§a");
		lore.add("§7Click to leave the party");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
	
	// SmartInventory Items
	public static ItemStack nextPageItem() {
		String[] ids = cfg.getString("Party.Join-Menu.Next-Page.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Join-Menu.Next-Page.Name").replace("&", "§"));
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack previousPageItem() {
		String[] ids = cfg.getString("Party.Join-Menu.Previous-Page.ID").split(":");
		@SuppressWarnings("deprecation")
		ItemStack item = new ItemStack(Material.getMaterial(Integer.parseInt(ids[0])), 1, (byte) Byte.parseByte(ids[1]));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cfg.getString("Party.Join-Menu.Previous-Page.Name").replace("&", "§"));
		item.setItemMeta(meta);
		return item;
	}
	
}
