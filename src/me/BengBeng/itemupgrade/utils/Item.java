package me.BengBeng.itemupgrade.utils;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BengBeng.itemupgrade.enums.Type;
import me.BengBeng.itemupgrade.file.Config;

public class Item {
	
	private FileConfiguration item = Config.getItem();
	
	private Type type;
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	private String key;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	
	
	public boolean isExist() {
		boolean exist = false;
		String path = item.getString(getType().getTypeName() + "." + getKey());
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none")) && (item.getConfigurationSection(getType().getTypeName()).getKeys(false).contains(getKey()))) {
			exist = true;
		};
		return exist;
	}
	
	
	
	public boolean hasItemType() {
		boolean has = false;
		String path = item.getString(getType().getTypeName() + "." + getKey() + ".type");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none")) && (path.contains(":"))) {
			has = true;
		}
		return has;
	}
	
	public String getItemType() {
		return item.getString(getType().getTypeName() + "." + getKey() + ".type");
	}
	
	public boolean hasDisplayName() {
		boolean has = false;
		String path = item.getString(getType().getTypeName() + "." + getKey() + ".name");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none"))) {
			has = true;
		}
		return has;
	}
	
	public String getDisplayName() {
		return Utils.toColor(item.getString(getType().getTypeName() + "." + getKey() + ".name"));
	}
	
	public boolean hasGlow() {
		boolean has = false;
		String path = item.getString(getType().getTypeName() + "." + getKey() + ".glow");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none"))) {
			has = true;
		}
		return has;
	}
	
	public boolean getGlow() {
		return (item.getBoolean(getType().getTypeName() + "." + getKey() + ".glow") == true);
	}
	
	public boolean hasLore() {
		boolean has = false;
		List<String> lores = item.getStringList(getType().getTypeName() + "." + getKey() + ".lore");
		if((lores != null) && (!lores.isEmpty()) && (lores.size() > 0)) {
			has = true;
		}
		return has;
	}
	
	public List<String> getLore() {
		return item.getStringList(getType().getTypeName() + "." + getKey() + ".lore");
	}
	
	
	
	public synchronized ItemStack getItem() {
		if(!hasItemType()) {
			Utils.sendConsoleMessage("&6[&bItemUpgrade&6] &4LỖI! &cKhông thể tìm thấy &e&ltype &ccủa một vật phẩm trong &bitem.yml&c.");
			return null;
		}
		String type = getItemType();
		
		String strMaterial = type.substring(0, type.indexOf(':'));
		Material material = Utils.getMaterial(strMaterial);
		
		String strAmount = type.substring(type.indexOf(':') + 1, type.lastIndexOf(':'));
		int amount = Integer.parseInt(strAmount);
		
		String strData = type.substring(type.lastIndexOf(':') + 1);
		byte data = Byte.parseByte(strData);
		
		ItemStack item = new ItemStack(material, amount, data);
		ItemMeta meta = item.getItemMeta();
		
		if(hasDisplayName()) {
			meta.setDisplayName(getDisplayName());
		}
		
		if(hasLore()) {
			List<String> lores = getLore();
			for(int x = 0; x < lores.size(); x++) {
				String str = Utils.toColor(lores.get(x));
				lores.set(x, str);
			}
			meta.setLore(lores);
		}
		
		item.setItemMeta(meta);
		
		if(hasGlow()) {
			if(getGlow()) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			}
		}
		
		return item;
	}
	
	public synchronized ItemStack getItem(int amount) {
		if(!hasItemType()) {
			Utils.sendConsoleMessage("&6[&bItemUpgrade&6] &4LỖI! &cKhông thể tìm thấy &e&ltype &ccủa một vật phẩm trong &bitem.yml&c.");
			return null;
		}
		String type = getItemType();
		
		String strMaterial = type.substring(0, type.indexOf(':'));
		Material material = Utils.getMaterial(strMaterial);
		
		String strData = type.substring(type.lastIndexOf(':') + 1);
		byte data = Byte.parseByte(strData);
		
		ItemStack item = new ItemStack(material, amount, data);
		ItemMeta meta = item.getItemMeta();
		
		if(hasDisplayName()) {
			meta.setDisplayName(getDisplayName());
		}
		
		if(hasLore()) {
			List<String> lores = getLore();
			for(int x = 0; x < lores.size(); x++) {
				String str = Utils.toColor(lores.get(x));
				lores.set(x, str);
			}
			meta.setLore(lores);
		}
		
		item.setItemMeta(meta);
		
		if(hasGlow()) {
			if(getGlow()) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			}
		}
		
		return item;
	}
	
	
	
	public boolean hasChance() {
		boolean has = false;
		String path = item.getString(getType().getTypeName() + "." + getKey() + ".chance");
		if((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none")) && (Utils.isDouble(path))) {
			has = true;
		}
		return has;
	}
	
	public double getChance() {
		return item.getDouble(getType().getTypeName() + "." + getKey() + ".chance");
	}
	
}
