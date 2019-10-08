package me.BengBeng.itemupgrade.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class User {
	
	private String name;
	private Player player;
	private Map<String, Inventory> map = Collections.synchronizedMap(new HashMap<String, Inventory>());
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Player getPlayer() {
		player = Bukkit.getServer().getPlayer(getName());
		return player;
	}
	
	
	
	public boolean isActive() {
		return ((getPlayer() != null) && (getPlayer().isOnline()));
	}
	
	public boolean isStaff() {
		return ((getPlayer().isOp()) || (getPlayer().hasPermission("*")));
	}
	
	
	
	public void openInventory(Inventory inv) {
		getPlayer().openInventory(inv);
	}
	
	public void giveItem(ItemStack... items) {
		for(int x = 0; x < items.length; x++) {
			ItemStack item = items[x];
			getPlayer().getInventory().addItem(new ItemStack[] { item });
		}
	}
	
	
	
	public boolean isInMap() {
		return map.containsKey(getName());
	}
	
	public Inventory getInventory() {
		return map.get(getName());
	}
	
	public void setToMap(Inventory inv, boolean set) {
		if(set == true) {
			map.put(getName(), inv);
		} else {
			map.remove(getName());
		}
	}
	
}
