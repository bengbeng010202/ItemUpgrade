package me.BengBeng.itemupgrade.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.BengBeng.itemupgrade.enums.Type;
import me.BengBeng.itemupgrade.file.Config;
import me.BengBeng.itemupgrade.utils.GUI;
import me.BengBeng.itemupgrade.utils.Utils;

public class PluginCommands
	implements CommandExecutor {
	
	private String labelReg = "(?ium)(\\{label}|\\%label%)";
	private String versionReg = "(?ium)(\\{version}|\\%version%)";
	private String valueReg = "(?ium)(\\{value}|\\%value%)";
	private String amountReg = "(?ium)(\\{amount}|\\%amount%)";
	private String itemReg = "(?ium)(\\{item}|\\%item%)";
	private String senderReg = "(?ium)(\\{sender}|\\%sender%)";
	private String playerReg = "(?ium)(\\{player}|\\%player%)";
	
	private String adminPerm = Config.getConfig().getString("PERMISSIONS.Admin");
	
	private String noPerm = Config.getMessage().getString("FAIL.no-permission");
	private String tooMany = Config.getMessage().getString("FAIL.too-many-args");
	private String onlyPlayer = Config.getMessage().getString("FAIL.only-player");
	private String specifyPlayer = Config.getMessage().getString("FAIL.must-specify-player");
	private String notFound = Config.getMessage().getString("FAIL.player-not-found");
	private String notNumber = Config.getMessage().getString("FAIL.not-number");
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				Utils.sendMessage(sender, onlyPlayer);
				return true;
			}
			Player player = (Player)sender;
			String name = player.getName();
			Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.Open.done"));
			Utils.getUser(name).openInventory(GUI.getUpgradeGui());
			return true;
		}
		String cmd = String.valueOf(args[0]);
		if(Utils.regexMatches(cmd, "help \\?")) {
			if((!sender.hasPermission(adminPerm)) && (!sender.hasPermission(Config.getConfig().getString("PERMISSIONS.Help")))) {
				Utils.sendMessage(sender, noPerm);
				return true;
			}
			Utils.sendMessage(sender, Config.getMessage().getString("HELP.header").replaceAll(versionReg, Utils.getMain().getDescription().getVersion()));
			Utils.sendMessage(sender, Config.getMessage().getString("HELP.help").replaceAll(labelReg, label));
			if((sender.hasPermission(adminPerm)) || (sender.hasPermission(Config.getConfig().getString("PERMISSIONS.Open")))) {
				Utils.sendMessage(sender, Config.getMessage().getString("HELP.open").replaceAll(labelReg, label));
			}
			if((sender.hasPermission(adminPerm)) || (sender.hasPermission(Config.getConfig().getString("PERMISSIONS.Give")))) {
				Utils.sendMessage(sender, Config.getMessage().getString("HELP.give").replaceAll(labelReg, label));
			}
			if((sender.hasPermission(adminPerm)) || (sender.hasPermission(Config.getConfig().getString("PERMISSIONS.Reload")))) {
				Utils.sendMessage(sender, Config.getMessage().getString("HELP.reload").replaceAll(labelReg, label));
			}
			Utils.sendMessage(sender, Config.getMessage().getString("HELP.footer").replaceAll(versionReg, Utils.getMain().getDescription().getVersion()));
			return true;
		}
		if(Utils.regexMatches(cmd, "give")) {
			if((!sender.hasPermission(adminPerm)) && (!sender.hasPermission(Config.getConfig().getString("PERMISSIONS.Give")))) {
				Utils.sendMessage(sender, noPerm);
				return true;
			}
			if(args.length == 1) {
				Utils.sendMessage(sender, Config.getMessage().getString("FAIL.must-choose-type"));
				return true;
			}
			if(args.length == 2) {
				String strType = String.valueOf(args[1]).toUpperCase();
				if(!Type.isValid(strType)) {
					Utils.sendMessage(sender, Config.getMessage().getString("FAIL.invalid-type").replaceAll(valueReg, args[1]));
					return true;
				}
				Utils.sendMessage(sender, Config.getMessage().getString("FAIL.must-type-name"));
				return true;
			}
			if(args.length == 3) {
				if(!(sender instanceof Player)) {
					Utils.sendMessage(sender, specifyPlayer);
					return true;
				}
				Player player = (Player)sender;
				String name = player.getName();
				String strType = String.valueOf(args[1]).toUpperCase();
				if(!Type.isValid(strType)) {
					Utils.sendMessage(sender, Config.getMessage().getString("FAIL.invalid-type").replaceAll(valueReg, args[1]));
					return true;
				}
				Type type = Type.valueOf(strType);
				String key = String.valueOf(args[2]);
				if(!Utils.getItem(type, key).isExist()) {
					Utils.sendMessage(sender, Config.getMessage().getString("FAIL.key-not-exist").replaceAll(valueReg, args[2]));
					return true;
				}
				ItemStack item = Utils.getItem(type, key).getItem();
				Utils.getUser(name).giveItem(item);
				Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.Give.self").replaceAll(amountReg, "1").replaceAll(itemReg, (item.hasItemMeta() ? (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : key) : key)));
				return true;
			}
			if(args.length == 4) {
				String strType = String.valueOf(args[1]).toUpperCase();
				if(!Type.isValid(strType)) {
					Utils.sendMessage(sender, Config.getMessage().getString("FAIL.invalid-type").replaceAll(valueReg, args[1]));
					return true;
				}
				Type type = Type.valueOf(strType);
				String key = String.valueOf(args[2]);
				if(!Utils.getItem(type, key).isExist()) {
					Utils.sendMessage(sender, Config.getMessage().getString("FAIL.key-not-exist").replaceAll(valueReg, args[2]));
					return true;
				}
				String name = String.valueOf(args[3]);
				if(!Utils.getUser(name).isActive()) {
					Utils.sendMessage(sender, notFound.replaceAll(valueReg, args[3]));
					return true;
				}
				Player target = Utils.getUser(name).getPlayer();
				ItemStack item = Utils.getItem(type, key).getItem();
				Utils.getUser(name).giveItem(item);
				Utils.sendMessage(sender, Config.getMessage().getString("SUCCESS.Give.sender").replaceAll(playerReg, name).replaceAll(amountReg, "1").replaceAll(itemReg, (item.hasItemMeta() ? (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : key) : key)));
				Utils.sendMessage(target, Config.getMessage().getString("SUCCESS.Give.target").replaceAll(senderReg, sender.getName()).replaceAll(amountReg, "1").replaceAll(itemReg, (item.hasItemMeta() ? (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : key) : key)));
				return true;
			}
			if(args.length == 5) {
				String strType = String.valueOf(args[1]).toUpperCase();
				if(!Type.isValid(strType)) {
					Utils.sendMessage(sender, Config.getMessage().getString("FAIL.invalid-type").replaceAll(valueReg, args[1]));
					return true;
				}
				Type type = Type.valueOf(strType);
				String key = String.valueOf(args[2]);
				if(!Utils.getItem(type, key).isExist()) {
					Utils.sendMessage(sender, Config.getMessage().getString("FAIL.key-not-exist").replaceAll(valueReg, args[2]));
					return true;
				}
				String name = String.valueOf(args[3]);
				if(!Utils.getUser(name).isActive()) {
					Utils.sendMessage(sender, notFound.replaceAll(valueReg, args[3]));
					return true;
				}
				Player target = Utils.getUser(name).getPlayer();
				String strAmount = String.valueOf(args[4]);
				if(!Utils.isInt(strAmount)) {
					Utils.sendMessage(sender, notNumber.replaceAll(valueReg, args[4]));
					return true;
				}
				int amount = Integer.parseInt(strAmount);
				ItemStack item = Utils.getItem(type, key).getItem(amount);
				Utils.getUser(name).giveItem(item);
				Utils.sendMessage(sender, Config.getMessage().getString("SUCCESS.Give.sender").replaceAll(playerReg, name).replaceAll(amountReg, String.valueOf(amount)).replaceAll(itemReg, (item.hasItemMeta() ? (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : key) : key)));
				Utils.sendMessage(target, Config.getMessage().getString("SUCCESS.Give.target").replaceAll(senderReg, sender.getName()).replaceAll(amountReg, String.valueOf(amount)).replaceAll(itemReg, (item.hasItemMeta() ? (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : key) : key)));
				return true;
			}
			if(args.length > 5) {
				Utils.sendMessage(sender, tooMany);
				return true;
			}
			return true;
		}
		if(Utils.regexMatches(cmd, "reload rload rl")) {
			if((!sender.hasPermission(adminPerm)) && (!sender.hasPermission(Config.getConfig().getString("PERMISSIONS.Reload")))) {
				Utils.sendMessage(sender, noPerm);
				return true;
			}
			Config.reloadConfig();
			Config.reloadMessage();
			Utils.sendMessage(sender, Config.getMessage().getString("SUCCESS.config-reload"));
			return true;
		}
		return false;
	}

}
