package me.BengBeng.itemupgrade;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import me.BengBeng.itemupgrade.commands.PluginCommands;
import me.BengBeng.itemupgrade.events.InventoryClick;
import me.BengBeng.itemupgrade.file.Config;
import me.BengBeng.itemupgrade.utils.Utils;

public class ItemUpgrade
	extends JavaPlugin {
	
	private PluginManager pm = Bukkit.getServer().getPluginManager();
	
	@Override
	public void onEnable() {
		loadFiles();
		if(!Utils.getHooked().isHookMyItems()) {
			Utils.sendConsoleMessage("&6[&bItemUpgrade&6] &cKhông thể tìm thấy plugin yêu cầu: &eMyItems&c.",
					"&6[&bItemUpgrade&6] &cVui lòng cài đặt plugin yêu cầu trước khi khởi động plugin này!",
					"&6[&bItemUpgrade&6] &cTự động dùng lại plugin...");
			try {
				unloadPlugin(this, true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return;
		} else {
			Utils.sendConsoleMessage("&6[&bItemUpgrade&6] &aĐã tìm thấy plugin yêu cầu: &eMyItems&a.");
		}
		loadEvents();
		
		getCommand("itemupgrade").setExecutor(new PluginCommands());
		
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
	
	private void loadEvents() {
		pm.registerEvents(new InventoryClick(), this);
	}
	
	private void loadFiles() {
		Config.loadConfig();
		Config.loadGui();
		Config.loadIndex();
		Config.loadItem();
		Config.loadMessage();
	}
	
	
	
	/*
	 * DỪNG LẠI PLUGIN:
	 */
	
	public File getFile(JavaPlugin plugin) {
		try {
			Field file = JavaPlugin.class.getDeclaredField("file");
			file.setAccessible(true);
			return (File)file.get(plugin);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void enablePlugin(Plugin plugin) {
		Bukkit.getServer().getPluginManager().enablePlugin(plugin);
	}
	
	public Plugin loadPlugin(File plugin) throws UnknownDependencyException, InvalidDescriptionException {
		try {
			Plugin p = Bukkit.getPluginManager().loadPlugin(plugin);
			try {
				p.onLoad();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return p;
		} catch (InvalidPluginException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public boolean unloadPlugin(Plugin plugin, boolean ReloadDependents) throws UnknownDependencyException, InvalidDescriptionException {
		PluginManager pluginManager = Bukkit.getServer().getPluginManager();
		String pName = plugin.getName();
		ArrayList<Plugin> reload = new ArrayList<Plugin>();
		if(ReloadDependents) {
			for(Plugin p : pluginManager.getPlugins()) {
				List<String> depend = (List<String>)p.getDescription().getDepend();
				if(depend != null) {
					for(String s : depend) {
						if(s.equals(pName) && !reload.contains(p)) {
							reload.add(p);
							unloadPlugin(p, false);
						}
					}
				}
				List<String> softDepend = (List<String>)p.getDescription().getSoftDepend();
				if(softDepend != null) {
					for(String s2 : softDepend) {
						if(s2.equals(pName) && !reload.contains(p)) {
							reload.add(p);
							unloadPlugin(p, false);
						}
					}
				}
			}
		}
		for(Plugin p2 : reload) {
			Bukkit.getServer().broadcastMessage(p2.getName() + "\n");
		}
		List<Plugin> plugins;
		Map<String, Plugin> names;
		SimpleCommandMap commandMap;
		Map<String, Command> commands;
		try {
			Field pluginsField = pluginManager.getClass().getDeclaredField("plugins");
			Field lookupNamesField = pluginManager.getClass().getDeclaredField("lookupNames");
			Field commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
			Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
			pluginsField.setAccessible(true);
			lookupNamesField.setAccessible(true);
			commandMapField.setAccessible(true);
			knownCommandsField.setAccessible(true);
			plugins = (List<Plugin>)pluginsField.get(pluginManager);
			names = (Map<String, Plugin>)lookupNamesField.get(pluginManager);
			commandMap = (SimpleCommandMap)commandMapField.get(pluginManager);
			commands = (Map<String, Command>)knownCommandsField.get(commandMap);
		} catch (NoSuchFieldException | IllegalAccessException ex) {
			ex.printStackTrace();
			return false;
		}
		if(commandMap != null) {
			synchronized(commandMap) {
				Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry<String, Command> entry = it.next();
					if(entry.getValue() instanceof PluginCommand) {
						PluginCommand c = (PluginCommand)entry.getValue();
						if(c.getPlugin() != plugin) {
							continue;
						}
						c.unregister((CommandMap)commandMap);
						it.remove();
					}
				}
			}
		}
		Bukkit.getServer().getPluginManager().disablePlugin(plugin);
		synchronized(pluginManager) {
			if(plugins != null && plugins.contains(plugin)) {
				plugins.remove(plugin);
			}
			if(names != null && names.containsKey(pName)) {
				names.remove(pName);
			}
		}
		JavaPluginLoader jpl = (JavaPluginLoader)plugin.getPluginLoader();
		Field loaders = null;
		try {
			loaders = jpl.getClass().getDeclaredField("loaders");
			loaders.setAccessible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
        }
		try {
			Map<String, ?> loaderMap = (Map<String, ?>)loaders.get(jpl);
			loaderMap.remove(plugin.getDescription().getName());
		} catch (Exception ex) {}
		ClassLoader cl = plugin.getClass().getClassLoader();
		try {
			((URLClassLoader)cl).close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.gc();
		if(ReloadDependents) {
			for(int i = 0; i < reload.size(); i++) {
				enablePlugin(loadPlugin(getFile((JavaPlugin)reload.get(i))));
			}
		}
		File loaded = getFile((JavaPlugin)plugin);
		// File unloaded = new File(getFile((JavaPlugin)plugin) + ".unloaded");
		File unloaded = new File(getFile((JavaPlugin)plugin) + "");
		return loaded.renameTo(unloaded);
	}
	
}
