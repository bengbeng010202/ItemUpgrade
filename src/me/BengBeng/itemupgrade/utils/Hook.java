package me.BengBeng.itemupgrade.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import api.praya.myitems.main.MyItemsAPI;

public class Hook {
	
	private PluginManager pm = Bukkit.getServer().getPluginManager();
	
	public MyItemsAPI myitemsAPI = null;
	
	public boolean isHookMyItems() {
		return ((pm.getPlugin("MyItems") != null) && (pm.getPlugin("MyItems").isEnabled()));
	}
	
	public MyItemsAPI getMyItemsAPI() {
		if(isHookMyItems()) {
			myitemsAPI = MyItemsAPI.getInstance();
		}
		return myitemsAPI;
	}
	
}
