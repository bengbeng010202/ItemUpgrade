package me.BengBeng.itemupgrade.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import api.praya.myitems.enums.LoreStatsEnum;
import api.praya.myitems.enums.OptionStatsEnum;
import api.praya.myitems.main.MyItemsAPI;
import api.praya.myitems.manager.game.GameManagerAPI;
import api.praya.myitems.manager.game.LoreStatsManagerAPI;
import me.BengBeng.itemupgrade.ItemUpgrade;
import me.BengBeng.itemupgrade.enums.Type;
import me.BengBeng.itemupgrade.file.Config;

public class Utils {
	
	private static ItemUpgrade main = ItemUpgrade.getPlugin(ItemUpgrade.class);
	
	public static ItemUpgrade getMain() {
		return main;
	}
	
	
	
	/*
	 * KHAI BÁO CLASS:
	 */
	
	private static Hook hook = new Hook();
	
	public static Hook getHooked() {
		return hook;
	}
	
	private static Item item = new Item();
	
	public synchronized static Item getItem(Type type, String key) {
		item.setType(type);
		item.setKey(key);
		return item;
	}
	
	public static GameManagerAPI getMyItemsAPI() {
		MyItemsAPI api = MyItemsAPI.getInstance();
		GameManagerAPI gmAPI = api.getGameManagerAPI();
		return gmAPI;
	}
	
	private static User user = new User();
	
	public synchronized static User getUser(String name) {
		user.setName(name);
		return user;
	}
	
	
	
	/*
	 * CÁC THÔNG TIN LẤY TỪ CONFIG:
	 */
	
	public static List<String> getAllowMaterials() {
		List<String> list = Config.getConfig().getStringList("allow-upgrade-materials");
		for(int x = 0; x < list.size(); x++) {
			String str = list.get(x).toUpperCase();
			if(isMaterial(str)) {
				list.set(x, str);
			}
		}
		return list;
	}
	
	public static int getMaxLevel() {
		return Config.getConfig().getInt("max-level");
	}
	
	public static void playSuccessSound(Player player) {
		Location loc = player.getLocation();
		
		String sound = Config.getConfig().getString("SOUND.success.name").replace(".", "").toUpperCase();
		float distance = (float)Config.getConfig().getDouble("SOUND.success.distance");
		float high = (float)Config.getConfig().getDouble("SOUND.success.high");
		player.playSound(loc, Sound.valueOf(sound), distance, high);
	}
	
	public static void playFailureSound(Player player) {
		Location loc = player.getLocation();
		
		String sound = Config.getConfig().getString("SOUND.failure.name").replace(".", "").toUpperCase();
		float distance = (float)Config.getConfig().getDouble("SOUND.failure.distance");
		float high = (float)Config.getConfig().getDouble("SOUND.failure.high");
		player.playSound(loc, Sound.valueOf(sound), distance, high);
	}
	
	
	
	/*
	 * VẬT PHẨM NÂNG CẤP THÀNH CÔNG HOẶC THẤT BẠI:
	 */
	
	public static double getChance(int level) {
		return Config.getConfig().getDouble("SUCCESS-CHANCE.level-" + level);
	}
	
	public static String getFormatLevel(int level) {
		return toColor(Config.getConfig().getString("FORMAT.level-" + level));
	}
	
	public static boolean isContainLevel(String name) {
		Pattern pattern = Pattern.compile("(((((\\&|\\§)[a-fk-o0-9])+)?(\\[)?)(((\\&|\\§)[a-fk-o0-9])+)?(\\+)((((\\&|\\§)[a-fk-o0-9])+)?)(\\d+)(((\\&|\\§)[a-fk-o0-9])+)?(\\])?)");
		Matcher matcher = pattern.matcher(name);
		return matcher.find();
	}
	
	public static String getLevelFormatted(String name) {
		String result = null;
		Pattern pattern = Pattern.compile("(((((\\&|\\§)[a-fk-o0-9])+)?(\\[)?)(((\\&|\\§)[a-fk-o0-9])+)?(\\+)((((\\&|\\§)[a-fk-o0-9])+)?)(\\d+)(((\\&|\\§)[a-fk-o0-9])+)?(\\])?)");
		Matcher matcher = pattern.matcher(name);
		while(matcher.find()) {
			result = matcher.group(0);
		}
		return result;
	}
	
	public static int getLevel(String name) {
		int result = 0;
		Pattern pattern = Pattern.compile("(((((\\&|\\§)[a-fk-o0-9])+)?(\\[)?)(((\\&|\\§)[a-fk-o0-9])+)?(\\+)((((\\&|\\§)[a-fk-o0-9])+)?)(\\d+)(((\\&|\\§)[a-fk-o0-9])+)?(\\])?)");
		Matcher matcher = pattern.matcher(name);
		while(matcher.find()) {
			result = Integer.parseInt(matcher.group(0).replaceAll("(((\\&|\\§)[a-zk-o0-9])+)?((?ium)(\\[|\\]|\\+))?", ""));
		}
		return result;
	}
	
	public static String changeLevel(String name, String formatLevel) {
		name = (isContainLevel(name) ? name.replaceAll("(((((\\&|\\§)[a-fk-o0-9])+)?(\\[)?)((((\\&|\\§)[a-fk-o0-9])+)?(\\+))((((\\&|\\§)[a-fk-o0-9])+)?(\\d+))((((\\&|\\§)[a-fk-o0-9])+)?(\\])?))", formatLevel) : name + ' ' + formatLevel);
		return name;
	}
	
	
	public static boolean hasAnyStats(ItemStack item) {
		boolean has = false;
		LoreStatsManagerAPI lsmAPI = getMyItemsAPI().getLoreStatsManagerAPI();
		for(LoreStatsEnum stats : LoreStatsEnum.values()) {
			if((stats != LoreStatsEnum.STAMINA_MAX) && (stats != LoreStatsEnum.STAMINA_REGEN) && (stats != LoreStatsEnum.HEALTH_REGEN)) {
				if(lsmAPI.hasLoreStats(item, stats)) {
					has = true;
				}
			}
		}
		return has;
	}
	
	public static ItemStack getItemSuccess(ItemStack item, int itemLevel) {
		int count = 0;
		ItemStack stack = item;
		
		stack.setAmount(1);
		
		ItemMeta meta = stack.getItemMeta();
		String name = meta.getDisplayName();
		
		List<String> lores = meta.getLore();
		
		LoreStatsManagerAPI lsmAPI = getMyItemsAPI().getLoreStatsManagerAPI();
		OptionStatsEnum cur = OptionStatsEnum.CURRENT;
		
		for(LoreStatsEnum stats : LoreStatsEnum.values()) {
			if((stats != LoreStatsEnum.STAMINA_MAX) && (stats != LoreStatsEnum.STAMINA_REGEN) && (stats != LoreStatsEnum.HEALTH_REGEN)) {
				String statsName = stats.name();
				String upgradeStatsName = Config.getIndex().getString("UPGRADE." + itemLevel + "." + statsName.toUpperCase());
				if(upgradeStatsName != null) {
					double add = Double.parseDouble(upgradeStatsName);
					if(lsmAPI.hasLoreStats(stack, stats)) {
						count += 1;
						double current = lsmAPI.getLoreValue(stack, stats, cur);
						String lore = toColor(lsmAPI.getTextLoreStats(stats, current));
						double newValue = current + add;
						String newLore = toColor(lsmAPI.getTextLoreStats(stats, newValue));
						for(int x = 0; x < lores.size(); x++) {
							String str = toColor(lores.get(x));
							if(lore.equals(str)) {
								lores.set(x, newLore);
							}
						}
					}
					if(count > 0) {
						meta.setLore(lores);
					}
				}
			}
		}
		
		if(count > 0) {
			meta.setDisplayName(changeLevel(name, getFormatLevel(itemLevel)));
			stack.setItemMeta(meta);
		}
		
		return stack;
	}
	
	public static ItemStack getPreviewItem(ItemStack item, int itemLevel) {
		int count = 0;
		ItemStack stack = item;
		
		stack.setAmount(1);
		
		ItemMeta meta = stack.getItemMeta();
		String name = meta.getDisplayName();
		
		List<String> lores = meta.getLore();
		
		LoreStatsManagerAPI lsmAPI = getMyItemsAPI().getLoreStatsManagerAPI();
		OptionStatsEnum cur = OptionStatsEnum.CURRENT;
		
		for(LoreStatsEnum stats : LoreStatsEnum.values()) {
			if((stats != LoreStatsEnum.STAMINA_MAX) && (stats != LoreStatsEnum.STAMINA_REGEN) && (stats != LoreStatsEnum.HEALTH_REGEN)) {
				String statsName = stats.name();
				String upgradeStatsName = Config.getIndex().getString("UPGRADE." + itemLevel + "." + statsName.toUpperCase());
				if(upgradeStatsName != null) {
					double add = Double.parseDouble(upgradeStatsName);
					if(lsmAPI.hasLoreStats(stack, stats)) {
						count += 1;
						double current = lsmAPI.getLoreValue(stack, stats, cur);
						String lore = toColor(lsmAPI.getTextLoreStats(stats, current));
						double newValue = current + add;
						if(newValue < 0.0) {
							newValue = 0.0;
						}
						String newLore = toColor(lsmAPI.getTextLoreStats(stats, newValue));
						for(int x = 0; x < lores.size(); x++) {
							String str = toColor(lores.get(x));
							if(lore.equals(str)) {
								lores.set(x, newLore);
							}
						}
					}
					if(count > 0) {
						meta.setLore(lores);
					}
				}
			}
		}
		
		List<String> lorePreview = Config.getGui().getStringList("MAIN.Preview-Yes.lore");
		if((lorePreview != null) && (!lorePreview.isEmpty()) && (lorePreview.size() > 0)) {
			for(int x = 0; x < lorePreview.size(); x++) {
				String str = toColor(lorePreview.get(x));
				lores.add(str);
			}
			meta.setLore(lores);
		}
		
		if(count > 0) {
			meta.setDisplayName(changeLevel(name, getFormatLevel(itemLevel)));
		}
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	public static ItemStack getItemFail(ItemStack item, int itemLevel) {
		int count = 0;
		ItemStack stack = item;
		
		stack.setAmount(1);
		
		ItemMeta meta = stack.getItemMeta();
		String name = meta.getDisplayName();
		
		List<String> lores = meta.getLore();
		
		LoreStatsManagerAPI lsmAPI = getMyItemsAPI().getLoreStatsManagerAPI();
		OptionStatsEnum cur = OptionStatsEnum.CURRENT;
		
		for(LoreStatsEnum stats : LoreStatsEnum.values()) {
			if((stats != LoreStatsEnum.STAMINA_MAX) && (stats != LoreStatsEnum.STAMINA_REGEN) && (stats != LoreStatsEnum.HEALTH_REGEN)) {
				String statsName = stats.name();
				String upgradeStatsName = Config.getIndex().getString("UPGRADE." + (itemLevel + 1) + "." + statsName.toUpperCase());
				if(upgradeStatsName != null) {
					double take = Double.parseDouble(upgradeStatsName);
					if(lsmAPI.hasLoreStats(stack, stats)) {
						count += 1;
						double current = lsmAPI.getLoreValue(stack, stats, cur);
						String lore = toColor(lsmAPI.getTextLoreStats(stats, current));
						double newValue = current - take;
						if(newValue < 0.0) {
							newValue = 0.0;
						}
						String newLore = toColor(lsmAPI.getTextLoreStats(stats, newValue));
						for(int x = 0; x < lores.size(); x++) {
							String str = toColor(lores.get(x));
							if(lore.equals(str)) {
								lores.set(x, newLore);
							}
						}
					}
					if(count > 0) {
						meta.setLore(lores);
					}
				}
			}
		}
		
		if(count > 0) {
			meta.setDisplayName(changeLevel(name, getFormatLevel(itemLevel)));
			stack.setItemMeta(meta);
		}
		
		return stack;
	}
	
	
	
	/*
	 * CÁC PHẦN KHÁC:
	 */
	
	public static boolean isMaterial(String str) {
		Material material = null;
		str = str.toUpperCase();
		if(isInt(str)) {
			int id = Integer.parseInt(str);
			material = Material.getMaterial(id);
		} else {
			material = Material.valueOf(str);
		}
		List<Material> materials = new ArrayList<Material>();
		for(Material maters : Material.values()) {
			materials.add(maters);
		}
		if(materials.contains(material)) {
			return true;
		}
		return false;
	}
	
	public static Material getMaterial(String str) {
		Material material = null;
		str = str.toUpperCase();
		if(isInt(str)) {
			int id = Integer.parseInt(str);
			material = Material.getMaterial(id);
		} else {
			material = Material.valueOf(str);
		}
		return material;
	}
	
	
	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException ex) {
			return false;
		}
		return true;
	}
	
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
		} catch(NumberFormatException ex) {
			return false;
		}
		return true;
	}
	
	
	public static boolean regexMatches(String str, String regex) {
		return str.matches("(?ium)(" + regex.replaceAll("( |_|-)", "|") + ")");
	}
	
	public static String toColor(String str) {
		return ChatColor.translateAlternateColorCodes('&', str.replaceAll("(?ium)(\\{prefix}|\\%prefix%)", Config.getMessage().getString("PREFIX")));
	}
	
	public static void sendMessage(CommandSender sender, String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			if(sender != null) {
				sender.sendMessage(newMsg);
			}
		}
	}
	
	public static void sendPlayerMessage(String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			for(Player online : Bukkit.getServer().getOnlinePlayers()) {
				if(online != null) {
					online.sendMessage(newMsg);
				}
			}
		}
	}
	
	public static void sendConsoleMessage(String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
			ccs.sendMessage(newMsg);
		}
	}
	
	public static void sendBroadcastMessage(String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
			ccs.sendMessage(newMsg);
			for(Player online : Bukkit.getServer().getOnlinePlayers()) {
				if(online != null) {
					online.sendMessage(newMsg);
				}
			}
		}
	}
	
}
