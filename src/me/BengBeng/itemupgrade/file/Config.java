package me.BengBeng.itemupgrade.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.BengBeng.itemupgrade.utils.Utils;

public class Config {
	
	private static File getPluginFolder() {
		return Utils.getMain().getDataFolder();
	}
	
	
	
	private static FileConfiguration config, gui, index, item, message;
	private static File configF, guiF, indexF, itemF, messageF;
	
	private static void copy(InputStream in, File out) {
		InputStream fis = in;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 * KHỞI ĐỘNG FILE:
	 */
	
	public static void loadConfig() {
		configF = new File(getPluginFolder(), "config.yml");
		config = new YamlConfiguration();
		if(!configF.exists()) {
			File parent = configF.getParentFile();
			if((parent != null) && (!parent.isFile()) && (!parent.exists())) {
				parent.mkdirs();
			}
			try {
				configF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/config.yml");
				copy(in, configF);
				config.load(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				config.load(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void loadGui() {
		guiF = new File(getPluginFolder(), "gui.yml");
		gui = new YamlConfiguration();
		if(!guiF.exists()) {
			File parent = guiF.getParentFile();
			if((parent != null) && (!parent.isFile()) && (!parent.exists())) {
				parent.mkdirs();
			}
			try {
				guiF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/gui.yml");
				copy(in, guiF);
				gui.load(guiF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				gui.load(guiF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void loadIndex() {
		indexF = new File(getPluginFolder(), "index.yml");
		index = new YamlConfiguration();
		if(!indexF.exists()) {
			File parent = indexF.getParentFile();
			if((parent != null) && (!parent.isFile()) && (!parent.exists())) {
				parent.mkdirs();
			}
			try {
				indexF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/index.yml");
				copy(in, indexF);
				index.load(indexF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				index.load(indexF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void loadItem() {
		itemF = new File(getPluginFolder(), "item.yml");
		item = new YamlConfiguration();
		if(!itemF.exists()) {
			File parent = itemF.getParentFile();
			if((parent != null) && (!parent.isFile()) && (!parent.exists())) {
				parent.mkdirs();
			}
			try {
				itemF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/item.yml");
				copy(in, itemF);
				item.load(itemF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				item.load(itemF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void loadMessage() {
		messageF = new File(getPluginFolder(), "message.yml");
		message = new YamlConfiguration();
		if(!messageF.exists()) {
			File parent = messageF.getParentFile();
			if((parent != null) && (!parent.isFile()) && (!parent.exists())) {
				parent.mkdirs();
			}
			try {
				messageF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/message.yml");
				copy(in, messageF);
				message.load(messageF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				message.load(messageF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 * LƯU FILE:
	 */
	
	public static void saveConfig() {
		if(!configF.exists()) {
			loadConfig();
		} else {
			try {
				config.save(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void saveGui() {
		if(!guiF.exists()) {
			loadGui();
		} else {
			try {
				gui.save(guiF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void saveIndex() {
		if(!indexF.exists()) {
			loadIndex();
		} else {
			try {
				index.save(indexF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void saveItem() {
		if(!itemF.exists()) {
			loadItem();
		} else {
			try {
				item.save(itemF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void saveMessage() {
		if(!messageF.exists()) {
			loadMessage();
		} else {
			try {
				message.save(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 * LÀM MỚI FILE:
	 */
	
	public static void reloadConfig() {
		try {
			config.load(configF);
			InputStream input = new FileInputStream(configF);
			if(input != null) {
				config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void reloadGui() {
		try {
			gui.load(guiF);
			InputStream input = new FileInputStream(guiF);
			if(input != null) {
				gui.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void reloadIndex() {
		try {
			index.load(indexF);
			InputStream input = new FileInputStream(indexF);
			if(input != null) {
				index.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void reloadItem() {
		try {
			item.load(itemF);
			InputStream input = new FileInputStream(itemF);
			if(input != null) {
				item.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void reloadMessage() {
		try {
			message.load(messageF);
			InputStream input = new FileInputStream(messageF);
			if(input != null) {
				message.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	/*
	 * LẤY FILE:
	 */
	
	public static FileConfiguration getConfig() {
		loadConfig();
		return config;
	}
	
	public static FileConfiguration getGui() {
		loadGui();
		return gui;
	}
	
	public static FileConfiguration getIndex() {
		loadIndex();
		return index;
	}
	
	public static FileConfiguration getItem() {
		loadItem();
		return item;
	}
	
	public static FileConfiguration getMessage() {
		loadMessage();
		return message;
	}
	
}
