package me.BengBeng.itemupgrade.events;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BengBeng.itemupgrade.enums.Type;
import me.BengBeng.itemupgrade.file.Config;
import me.BengBeng.itemupgrade.utils.GUI;
import me.BengBeng.itemupgrade.utils.GUI.KeyType;
import me.BengBeng.itemupgrade.utils.Utils;

public class InventoryClick
	implements Listener {
	
	@EventHandler
	public synchronized void onClose(InventoryCloseEvent event) {
		HumanEntity hEntity = event.getPlayer();
		if(hEntity instanceof Player) {
			Player player = (Player)hEntity;
			String name = player.getName();
			
			if(Utils.getUser(name).isInMap()) {
				closeAndRollbackItem(player);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onClick(InventoryClickEvent event) {
		HumanEntity hEntity = event.getWhoClicked();
		if(hEntity instanceof Player) {
			Player player = (Player)hEntity;
			String pName = player.getName();
			
			String invTitle = GUI.getUpgradeGui().getTitle();
			String openedTitle = event.getInventory().getTitle();
			
			if(!invTitle.equals(openedTitle)) {
				return;
			} else {
				ClickType click = event.getClick();
				if((click == ClickType.SHIFT_LEFT) || (click == ClickType.SHIFT_RIGHT)) {
					event.setCancelled(true);
					return;
				}
			}
			
			Inventory pInv = player.getOpenInventory().getTopInventory();
			Inventory clickedInv = event.getClickedInventory();
			
			if(!pInv.equals(clickedInv)) {
				return;
			}
			
			event.setCancelled(true);
			player.updateInventory();
			
			if(!Utils.getUser(pName).isInMap()) {
				Utils.getUser(pName).setToMap(clickedInv, true);
			}
			
			int rawSlot = event.getRawSlot() + 1;
			
			
			
			int noPreviewSlot = GUI.getSlot(KeyType.MAIN, "Preview-No");
			
			ItemStack previewNo = clickedInv.getItem(noPreviewSlot - 1);
			ItemStack defPreviewNo = GUI.getMainItem("Preview-No");
			
			int itemSlot = GUI.getSlot(KeyType.MAIN, "Item");
			ItemStack defItem = GUI.getMainItem("Item");
			if(rawSlot == itemSlot) {
				ItemStack currentItem = event.getCurrentItem();
				ItemStack cursor = event.getCursor();
				Material type = cursor.getType();
				if(cursor.getType() == Material.AIR) {
					if(!defItem.isSimilar(currentItem)) {
						event.setCursor(currentItem);
						event.setCurrentItem(GUI.getMainItem("Item"));
						if(!previewNo.isSimilar(defPreviewNo)) {
							clickedInv.setItem(noPreviewSlot - 1, defPreviewNo);
						}
					}
					return;
				}
				if(!Utils.getAllowMaterials().contains(type.name())) {
					Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.not-allow-material"));
					return;
				}
				if(currentItem.isSimilar(cursor)) {
					return;
				}
				if(cursor.hasItemMeta()) {
					ItemMeta cursorMeta = cursor.getItemMeta();
					String name = cursorMeta.getDisplayName();
					if(Utils.isContainLevel(name)) {
						int level = Utils.getLevel(name);
						if(level >= Utils.getMaxLevel()) {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.item-reached-max-level"));
							return;
						}
					}
				}
				if(!Utils.hasAnyStats(cursor)) {
					Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.item-does-not-have-stats"));
					return;
				}
				event.setCurrentItem(cursor);
				event.setCursor((currentItem.isSimilar(defItem)) ? new ItemStack(Material.AIR) : currentItem);
				String name = cursor.getItemMeta().getDisplayName();
				if(!Utils.isContainLevel(name)) {
					ItemStack preview = Utils.getPreviewItem(cursor, 1);
					clickedInv.setItem(noPreviewSlot - 1, preview);
				} else {
					int level = Utils.getLevel(name);
					int nextLevel = level + 1;
					if(nextLevel >= Utils.getMaxLevel()) {
						nextLevel = Utils.getMaxLevel();
					}
					ItemStack preview = Utils.getPreviewItem(cursor, nextLevel);
					clickedInv.setItem(noPreviewSlot - 1, preview);
				}
				return;
			}
			
			int upgradeSlot = GUI.getSlot(KeyType.MAIN, "Upgrade");
			ItemStack defUpgradeItem = GUI.getMainItem("Upgrade");
			if(rawSlot == upgradeSlot) {
				int count = 0;
				ItemStack currentItem = event.getCurrentItem();
				ItemStack cursor = event.getCursor();
				if(cursor.getType() == Material.AIR) {
					if(!defUpgradeItem.isSimilar(currentItem)) {
						event.setCursor(currentItem);
						event.setCurrentItem(GUI.getMainItem("Upgrade"));
					}
					return;
				}
				if(currentItem.isSimilar(cursor)) {
					return;
				}
				for(String key : Config.getItem().getConfigurationSection("UPGRADE").getKeys(false)) {
					int size = Config.getItem().getConfigurationSection("UPGRADE").getKeys(false).size();
					count += 1;
					ItemStack item = Utils.getItem(Type.UPGRADE, key).getItem();
					if(count != size) {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defUpgradeItem) ? new ItemStack(Material.AIR) : currentItem));
							break;
						}
					} else {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defUpgradeItem) ? new ItemStack(Material.AIR) : currentItem));
							break;
						} else {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.not-valid-upgrade-item"));
							return;
						}
					}
				}
				return;
			}
			
			int luckSlot = GUI.getSlot(KeyType.MAIN, "Luck");
			ItemStack defLuckItem = GUI.getMainItem("Luck");
			if(rawSlot == luckSlot) {
				int count = 0;
				ItemStack currentItem = event.getCurrentItem();
				ItemStack cursor = event.getCursor();
				if(cursor.getType() == Material.AIR) {
					if(!defLuckItem.isSimilar(currentItem)) {
						event.setCursor(currentItem);
						event.setCurrentItem(GUI.getMainItem("Luck"));
					}
					return;
				}
				if(currentItem.isSimilar(cursor)) {
					return;
				}
				for(String key : Config.getItem().getConfigurationSection("LUCK").getKeys(false)) {
					int size = Config.getItem().getConfigurationSection("LUCK").getKeys(false).size();
					count += 1;
					ItemStack item = Utils.getItem(Type.LUCK, key).getItem();
					if(count != size) {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defLuckItem)) ? new ItemStack(Material.AIR) : currentItem);
							break;
						}
					} else {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defLuckItem)) ? new ItemStack(Material.AIR) : currentItem);
							break;
						} else {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.not-valid-luck-item"));
							return;
						}
					}
				}
				return;
			}
			
			int levelSlot = GUI.getSlot(KeyType.MAIN, "Level");
			ItemStack defLevelItem = GUI.getMainItem("Level");
			if(rawSlot == levelSlot) {
				int count = 0;
				ItemStack currentItem = event.getCurrentItem();
				ItemStack cursor = event.getCursor();
				if(cursor.getType() == Material.AIR) {
					if(!defLevelItem.isSimilar(currentItem)) {
						event.setCursor(currentItem);
						event.setCurrentItem(GUI.getMainItem("Level"));
					}
					return;
				}
				if(currentItem.isSimilar(cursor)) {
					return;
				}
				for(String key : Config.getItem().getConfigurationSection("LEVEL").getKeys(false)) {
					int size = Config.getItem().getConfigurationSection("LEVEL").getKeys(false).size();
					count += 1;
					ItemStack item = Utils.getItem(Type.LEVEL, key).getItem();
					if(count != size) {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defLevelItem)) ? new ItemStack(Material.AIR) : currentItem);
							break;
						}
					} else {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defLevelItem)) ? new ItemStack(Material.AIR) : currentItem);
							break;
						} else {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.not-valid-level-item"));
							return;
						}
					}
				}
				return;
			}
			
			int fairySlot = GUI.getSlot(KeyType.MAIN, "Fairy");
			ItemStack defFairyItem = GUI.getMainItem("Fairy");
			if(rawSlot == fairySlot) {
				int count = 0;
				ItemStack currentItem = event.getCurrentItem();
				ItemStack cursor = event.getCursor();
				if(cursor.getType() == Material.AIR) {
					if(!defFairyItem.isSimilar(currentItem)) {
						event.setCursor(currentItem);
						event.setCurrentItem(GUI.getMainItem("Fairy"));
					}
					return;
				}
				if(currentItem.isSimilar(cursor)) {
					return;
				}
				for(String key : Config.getItem().getConfigurationSection("FAIRY").getKeys(false)) {
					int size = Config.getItem().getConfigurationSection("FAIRY").getKeys(false).size();
					count += 1;
					ItemStack item = Utils.getItem(Type.FAIRY, key).getItem();
					if(count != size) {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defFairyItem)) ? new ItemStack(Material.AIR) : currentItem);
							break;
						}
					} else {
						if(item.isSimilar(cursor)) {
							event.setCurrentItem(cursor);
							event.setCursor((currentItem.isSimilar(defFairyItem)) ? new ItemStack(Material.AIR) : currentItem);
							break;
						} else {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.not-valid-fairy-item"));
							return;
						}
					}
				}
				return;
			}
			
			double random = Math.random() * 100.0;
			double result = (((int)(random * 100)) / 100.0);
			
			boolean hasSlot = GUI.hasSlot(KeyType.MAIN, "Confirm");
			boolean hasSlots = GUI.hasSlots(KeyType.MAIN, "Confirm");
			if((hasSlot) && (!hasSlots)) {
				int slot = GUI.getSlot(KeyType.MAIN, "Confirm");
				if(rawSlot == slot) {
					
					return;
				}
			} else if((!hasSlot) && (hasSlots)) {
				List<Integer> slots = GUI.getSlots(KeyType.MAIN, "Confirm");
				for(Integer slot : slots) {
					if(rawSlot == slot) {
						ItemStack item = clickedInv.getItem(itemSlot - 1);
						if(item.isSimilar(defItem)) {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.item-to-upgrade-is-null"));
							return;
						}
						
						if(item.hasItemMeta()) {
							ItemMeta cursorMeta = item.getItemMeta();
							String name = cursorMeta.getDisplayName();
							if(Utils.isContainLevel(name)) {
								int level = Utils.getLevel(name);
								if(level >= Utils.getMaxLevel()) {
									Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.item-reached-max-level"));
									return;
								}
							}
						}
						if(!Utils.hasAnyStats(item)) {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.item-does-not-have-stats"));
							return;
						}
						
						ItemStack upgradeItem = clickedInv.getItem(upgradeSlot - 1);
						if(upgradeItem.isSimilar(defUpgradeItem)) {
							Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.upgrade-item-is-null"));
							return;
						}
						
						ItemStack luckItem = clickedInv.getItem(luckSlot - 1);
						if(!luckItem.isSimilar(defLuckItem)) {
							for(String key : Config.getItem().getConfigurationSection(Type.LUCK.getTypeName()).getKeys(false)) {
								ItemStack stack = Utils.getItem(Type.LUCK, key).getItem();
								if(luckItem.isSimilar(stack)) {
									double chance = Utils.getItem(Type.LUCK, key).getChance();
									result -= chance;
									if(result <= 0) {
										result = 0.0;
									}
									break;
								}
							}
						}
						
						ItemStack levelItem = clickedInv.getItem(levelSlot - 1);
						boolean keepLevel = ((!levelItem.isSimilar(defLevelItem)) ? true : false);
						
						ItemStack fairyItem = clickedInv.getItem(fairySlot - 1);
						boolean isFairy = ((!fairyItem.isSimilar(defFairyItem)) ? true : false);
						
						for(String key : Config.getItem().getConfigurationSection("UPGRADE").getKeys(false)) {
							ItemStack stack = Utils.getItem(Type.UPGRADE, key).getItem();
							if(upgradeItem.isSimilar(stack)) {
								ItemMeta meta = item.getItemMeta();
								String name = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name();
								if(!Utils.isContainLevel(name)) {
									if(!previewNo.isSimilar(defPreviewNo)) {
										clickedInv.setItem(noPreviewSlot - 1, defPreviewNo);
									}
									double chance = Utils.getChance(1);
									if(result <= chance) {
										ItemStack resultItem = Utils.getItemSuccess(item, 1);
										
										removeItemSlot(clickedInv);
										
										Utils.getUser(pName).giveItem(resultItem);
										Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.ON-GUI.upgrade-successfully"));
										Utils.playSuccessSound(player);
									} else {
										Utils.getUser(pName).giveItem(item);
										removeItemSlot(clickedInv);
										Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.ON-GUI.upgrade-failure"));
										Utils.playFailureSound(player);
									}
								} else {
									int level = Utils.getLevel(name);
									if(level >= Utils.getMaxLevel()) {
										Utils.sendMessage(player, Config.getMessage().getString("ERROR.ON-GUI.item-reached-max-level"));
										return;
									}
									int nextLevel = level + 1;
									if(!previewNo.isSimilar(defPreviewNo)) {
										clickedInv.setItem(noPreviewSlot - 1, defPreviewNo);
									}
									double chance = Utils.getChance(nextLevel);
									if(result <= chance) {
										ItemStack resultItem = Utils.getItemSuccess(item, nextLevel);
										
										removeItemSlot(clickedInv);
										
										Utils.getUser(pName).giveItem(resultItem);
										Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.ON-GUI.upgrade-successfully"));
										Utils.playSuccessSound(player);
									} else {
										if((keepLevel == false) && (isFairy == false)) {
											int previousLevel = level - 1;
											ItemStack resultItem = Utils.getItemFail(item, previousLevel);
											
											removeItemSlot(clickedInv);
											
											Utils.getUser(pName).giveItem(resultItem);
											Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.ON-GUI.upgrade-failure"));
										} else if(((keepLevel == true) || (keepLevel == false)) && (isFairy == true)) {
											Utils.getUser(pName).giveItem(item);
											removeItemSlot(clickedInv);
											Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.ON-GUI.upgrade-failure"));
											takeFairyItem(clickedInv);
											Utils.playFailureSound(player);
											return;
										} else if((keepLevel == true) && (isFairy == false)) {
											Utils.getUser(pName).giveItem(item);
											removeItemSlot(clickedInv);
											Utils.sendMessage(player, Config.getMessage().getString("SUCCESS.ON-GUI.upgrade-failure"));
											takeLevelItem(clickedInv);
										}
										Utils.playFailureSound(player);
									}
								}
								withdrawSubSlot(clickedInv, true, true);
								break;
							}
						}
						
						return;
					}
				}
			}
			
		}
	}
	
	
	
	private synchronized void withdrawSubSlot(Inventory inv, boolean upgrade, boolean luck) {
		if(upgrade == true) {
			takeUpgradeItem(inv);
		}
		if(luck == true) {
			takeLuckItem(inv);
		}
	}
	
	private synchronized void removeItemSlot(Inventory inv) {
		int itemSlot = GUI.getSlot(KeyType.MAIN, "Item") - 1;
		ItemStack item = inv.getItem(itemSlot);
		int itemAmount = item.getAmount();
		if(itemAmount <= 1) {
			inv.setItem(itemSlot, GUI.getMainItem("Item"));
		} else {
			item.setAmount(itemAmount - 1);
		}
	}
	
	private void takeUpgradeItem(Inventory inv) {
		int upgradeSlot = GUI.getSlot(KeyType.MAIN, "Upgrade") - 1;
		ItemStack defUpgrade = GUI.getMainItem("Upgrade");
		ItemStack upgrade = inv.getItem(upgradeSlot);
		if(!upgrade.isSimilar(defUpgrade)) {
			int upgradeAmount = upgrade.getAmount();
			if(upgradeAmount <= 1) {
				inv.setItem(upgradeSlot, defUpgrade);
			} else {
				upgrade.setAmount(upgradeAmount - 1);
			}
		}
	}
	
	private void takeLuckItem(Inventory inv) {
		int luckSlot = GUI.getSlot(KeyType.MAIN, "Luck") - 1;
		ItemStack defLuck = GUI.getMainItem("Luck");
		ItemStack luck = inv.getItem(luckSlot);
		if(!luck.isSimilar(defLuck)) {
			int luckAmount = luck.getAmount();
			if(luckAmount <= 1) {
				inv.setItem(luckSlot, defLuck);
			} else {
				luck.setAmount(luckAmount - 1);
			}
		}
	}
	
	private void takeLevelItem(Inventory inv) {
		int levelSlot = GUI.getSlot(KeyType.MAIN, "Level") - 1;
		ItemStack defLevel = GUI.getMainItem("Level");
		ItemStack level = inv.getItem(levelSlot);
		if(!level.isSimilar(defLevel)) {
			int levelAmount = level.getAmount();
			if(levelAmount <= 1) {
				inv.setItem(levelSlot, defLevel);
			} else {
				level.setAmount(levelAmount - 1);
			}
		}
	}
	
	private void takeFairyItem(Inventory inv) {
		int fairySlot = GUI.getSlot(KeyType.MAIN, "Fairy") - 1;
		ItemStack defFairy = GUI.getMainItem("Fairy");
		ItemStack fairy = inv.getItem(fairySlot);
		if(!fairy.isSimilar(defFairy)) {
			int fairyAmount = fairy.getAmount();
			if(fairyAmount <= 1) {
				inv.setItem(fairySlot, defFairy);
			} else {
				fairy.setAmount(fairyAmount - 1);
			}
		}
	}
	
	private synchronized void closeAndRollbackItem(Player player) {
		String name = player.getName();
		Inventory inv = Utils.getUser(name).getInventory();
		int itemSlot = GUI.getSlot(KeyType.MAIN, "Item") - 1;
		int upgradeSlot = GUI.getSlot(KeyType.MAIN, "Upgrade") - 1;
		int luckSlot = GUI.getSlot(KeyType.MAIN, "Luck") - 1;
		int levelSlot = GUI.getSlot(KeyType.MAIN, "Level") - 1;
		int fairySlot = GUI.getSlot(KeyType.MAIN, "Fairy") - 1;
		
		
		ItemStack defItem = GUI.getMainItem("Item");
		ItemStack defUpgrade = GUI.getMainItem("Upgrade");
		ItemStack defLuck = GUI.getMainItem("Luck");
		ItemStack defLevel = GUI.getMainItem("Level");
		ItemStack defFairy = GUI.getMainItem("Fairy");
		
		
		ItemStack item = inv.getItem(itemSlot);
		if(!item.isSimilar(defItem)) {
			Utils.getUser(name).giveItem(item);
		}
		
		ItemStack upgrade = inv.getItem(upgradeSlot);
		if(!upgrade.isSimilar(defUpgrade)) {
			Utils.getUser(name).giveItem(upgrade);
		}
		
		ItemStack luck = inv.getItem(luckSlot);
		if(!luck.isSimilar(defLuck)) {
			Utils.getUser(name).giveItem(luck);
		}
		
		ItemStack level = inv.getItem(levelSlot);
		if(!level.isSimilar(defLevel)) {
			Utils.getUser(name).giveItem(level);
		}
		
		ItemStack fairy = inv.getItem(fairySlot);
		if(!fairy.isSimilar(defFairy)) {
			Utils.getUser(name).giveItem(fairy);
		}
		
		Utils.getUser(name).setToMap(null, false);
		player.closeInventory();
	}
	
}
