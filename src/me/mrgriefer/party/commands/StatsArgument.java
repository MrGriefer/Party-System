package me.mrgriefer.slaparoo.commands.arguments;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.mrgriefer.slaparoo.Slaparoo;
import me.mrgriefer.slaparoo.Util;
import me.mrgriefer.slaparoo.commands.messages.MessageHandler;
import me.mrgriefer.slaparoo.commands.permission.Permission;
import me.mrgriefer.slaparoo.commands.permission.PermissionHandler;
import me.mrgriefer.slaparoo.database.sql.StatisticType;

public class StatsArgument {

	private CommandSender sender;
	private String[] args;
	
	public StatsArgument(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public boolean execute() {
		if (!PermissionHandler.hasPermission(sender, Permission.STATS)) {
			sender.sendMessage(MessageHandler.getMessage("No-Permission"));
			return true;
		}
		
		String name = sender.getName();
		
		if (args.length > 1) {
			name = args[1];
		}
		
		if (name.length() < 4 || name.length() > 16 || !name.matches("[a-zA-Z0-9_]*")) {
			sender.sendMessage(MessageHandler.getMessage("Stats-Player-Not-Found").replace("{player}", name));
			return true;
		}
		
		Player pl = Bukkit.getPlayer(name);
		if (pl != null) {
			List<String> messages = Util.colorList(Slaparoo.getInstance().getConfig().getStringList("Stats-Lines"));
			messages = Util.replaceAll(messages, "{player}", pl.getName());
			messages = Util.replaceAll(messages, "{points}", Integer.toString(Slaparoo.getStatisticManager().get(pl, StatisticType.SLAP_POINTS)));
			messages = Util.replaceAll(messages, "{wins}", Integer.toString(Slaparoo.getStatisticManager().get(pl, StatisticType.VICTORIES)));
			messages = Util.replaceAll(messages, "{slapoffs}", Integer.toString(Slaparoo.getStatisticManager().get(pl, StatisticType.SLAP_OFFS)));
			messages = Util.replaceAll(messages, "{falls}", Integer.toString(Slaparoo.getStatisticManager().get(pl, StatisticType.FALLS)));
			messages = Util.replaceAll(messages, "{played}", Integer.toString(Slaparoo.getStatisticManager().get(pl, StatisticType.GAMES_PLAYED)));
			for (String str : messages) {
				pl.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
			}
		}
		
		return true;
	}
	
}
