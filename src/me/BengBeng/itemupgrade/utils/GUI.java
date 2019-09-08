package me.BengBeng.itemupgrade.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BengBeng.itemupgrade.file.Config;

public class GUI {
	
	private static FileConfiguration gui = Config.getGui();
	
	public enum KeyType {
		DECORATE("DECORATE"),
		MAIN("MAIN");
		
		private String keyName;
		
		KeyType(String keyName) {
			this.keyName = keyName;
		}
		
		public String getKeyName() {
			return keyName;
		}
		
	}
	
	
	public static boolean hasItemType(KeyType keyType, String key) {
		boolean has = false;
		String path = gui.getString(keyType.getKeyName() + "." + key + ".type");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none")) && (path.contains(":"))) {
			has = true;
		}
		return has;
	}
	
	public static String getItemType(KeyType keyType, String key) {
		return gui.getString(keyType.getKeyName() + "." + key + ".type");
	}
	
	public static boolean hasDisplayName(KeyType keyType, String key) {
		boolean has = false;
		String path = gui.getString(keyType.getKeyName() + "." + key + ".name");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none"))) {
			has = true;
		}
		return has;
	}
	
	public static String getDisplayName(KeyType keyType, String key) {
		return Utils.toColor(gui.getString(keyType.getKeyName() + "." + key + ".name"));
	}
	
	public static boolean hasGlow(KeyType keyType, String key) {
		boolean has = false;
		String path = gui.getString(keyType.getKeyName() + "." + key + ".glow");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none"))) {
			has = true;
		}
		return has;
	}
	
	public static boolean getGlow(KeyType keyType, String key) {
		return (gui.getBoolean(keyType.getKeyName() + "." + key + ".glow") == true);
	}
	
	public static boolean hasLore(KeyType keyType, String key) {
		boolean has = false;
		List<String> lores = gui.getStringList(keyType.getKeyName() + "." + key + ".lore");
		if((lores != null) && (!lores.isEmpty()) && (lores.size() > 0)) {
			has = true;
		}
		return has;
	}
	
	public static List<String> getLore(KeyType keyType, String key) {
		return gui.getStringList(keyType.getKeyName() + "." + key + ".lore");
	}
	
	public static boolean hasSlot(KeyType keyType, String key) {
		boolean has = false;
		String path = gui.getString(keyType.getKeyName() + "." + key + ".slot");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none")) && (Utils.isInt(path))) {
			has = true;
		}
		return has;
	}
	
	public static int getSlot(KeyType keyType, String key) {
		return gui.getInt(keyType.getKeyName() + "." + key + ".slot");
	}
	
	public static boolean hasSlots(KeyType keyType, String key) {
		boolean has = false;
		List<String> slots = gui.getStringList(keyType.getKeyName() + "." + key + ".slots");
		if((slots != null) && (!slots.isEmpty()) && (slots.size() > 0)) {
			has = true;
		}
		return has;
	}
	
	public static List<Integer> getSlots(KeyType keyType, String key) {
		return gui.getIntegerList(keyType.getKeyName() + "." + key + ".slots");
	}
	
	
	
	public static ItemStack getDecorate(String key) {
		if(!hasItemType(KeyType.DECORATE, key)) {
			Utils.sendConsoleMessage("&6[&bItemUpgrade&6] &4LỖI! &cKhông thể tìm thấy &e&ltype &ccủa một vật phẩm trong &bgui.yml&c.");
			return null;
		}
		
		String type = getItemType(KeyType.DECORATE, key);
		
		String strMaterial = type.substring(0, type.indexOf(':'));
		Material material = Utils.getMaterial(strMaterial);
		
		String strAmount = type.substring(type.indexOf(':') + 1, type.lastIndexOf(':'));
		int amount = Integer.parseInt(strAmount);
		
		String strData = type.substring(type.lastIndexOf(':') + 1);
		byte data = Byte.parseByte(strData);
		
		ItemStack item = new ItemStack(material, amount, data);
		ItemMeta meta = item.getItemMeta();
		
		if(hasDisplayName(KeyType.DECORATE, key)) {
			meta.setDisplayName(getDisplayName(KeyType.DECORATE, key));
		}
		
		if(hasLore(KeyType.DECORATE, key)) {
			List<String> lores = getLore(KeyType.DECORATE, key);
			for(int x = 0; x < lores.size(); x++) {
				String str = Utils.toColor(lores.get(x));
				lores.set(x, str);
			}
			meta.setLore(lores);
		}
		
		item.setItemMeta(meta);
		
		if(hasGlow(KeyType.DECORATE, key)) {
			if(getGlow(KeyType.DECORATE, key)) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			}
		}
		
		return item;
	}
	
	public static ItemStack getMainItem(String key) {
		if(!hasItemType(KeyType.MAIN, key)) {
			Utils.sendConsoleMessage("&6[&bItemUpgrade&6] &4LỖI! &cKhông thể tìm thấy &e&ltype &ccủa một vật phẩm trong &bgui.yml&c.");
			return null;
		}
		
		String type = getItemType(KeyType.MAIN, key);
		
		String strMaterial = type.substring(0, type.indexOf(':'));
		Material material = Utils.getMaterial(strMaterial);
		
		String strAmount = type.substring(type.indexOf(':') + 1, type.lastIndexOf(':'));
		int amount = Integer.parseInt(strAmount);
		
		String strData = type.substring(type.lastIndexOf(':') + 1);
		byte data = Byte.parseByte(strData);
		
		ItemStack item = new ItemStack(material, amount, data);
		ItemMeta meta = item.getItemMeta();
		
		if(hasDisplayName(KeyType.MAIN, key)) {
			meta.setDisplayName(getDisplayName(KeyType.MAIN, key));
		}
		
		if(hasLore(KeyType.MAIN, key)) {
			List<String> lores = getLore(KeyType.MAIN, key);
			for(int x = 0; x < lores.size(); x++) {
				String str = Utils.toColor(lores.get(x));
				lores.set(x, str);
			}
			meta.setLore(lores);
		}
		
		item.setItemMeta(meta);
		
		if(hasGlow(KeyType.MAIN, key)) {
			if(getGlow(KeyType.MAIN, key)) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			}
		}
		
		return item;
	}
	
	
	
	/*
	 * GUI CƯỜNG HOÁ VẬT PHẨM:
	 */
	
	public static Inventory getUpgradeGui() {
		Inventory inv = Bukkit.getServer().createInventory(null, Config.getGui().getInt("SETTINGS.rows"), Utils.toColor(Config.getGui().getString("SETTINGS.title")));
		
		for(String key : gui.getConfigurationSection("DECORATE").getKeys(false)) {
			ItemStack item = getDecorate(key);
			if((hasSlot(KeyType.DECORATE, key)) && (!hasSlots(KeyType.DECORATE, key))) {
				int slot = getSlot(KeyType.DECORATE, key) - 1;
				inv.setItem(slot, item);
			} else if((!hasSlot(KeyType.DECORATE, key)) && (hasSlots(KeyType.DECORATE, key))) {
				List<Integer> slots = getSlots(KeyType.DECORATE, key);
				for(int slot : slots) {
					slot = slot - 1;
					inv.setItem(slot, item);
				}
			} else {
				for(int x = 0; x < 54; x++) {
					inv.setItem(x, item);
				}
			}
		}
		
		for(String key : gui.getConfigurationSection("MAIN").getKeys(false)) {
			if(!key.equals("Preview-Yes")) {
				ItemStack item = getMainItem(key);
				if((hasSlot(KeyType.MAIN, key)) && (!hasSlots(KeyType.MAIN, key))) {
					int slot = getSlot(KeyType.MAIN, key) - 1;
					inv.setItem(slot, item);
				} else if((!hasSlot(KeyType.MAIN, key)) && (hasSlots(KeyType.MAIN, key))) {
					List<Integer> slots = getSlots(KeyType.MAIN, key);
					for(Integer slot : slots) {
						slot = slot - 1;
						inv.setItem(slot, item);
					}
				}
			}
		}
		
		return inv;
	}
	
}
