package io.github.minieyez.paramaniaccrates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {

	ItemSerialize serialize = new ItemSerialize();

	public String colouredMessage(String string) {
		return new String(ChatColor.translateAlternateColorCodes('&', string));
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {

		ConfigManager configMan = new ConfigManager("crates.yml");
		FileConfiguration config = configMan.get();
		
		if(e.getClickedBlock() == null) {
			return;
		}
		for (String locCon : config.getStringList("locations")) {
			String loc = "";
			loc = loc + e.getClickedBlock().getWorld().getName() + ",";
			loc = loc + e.getClickedBlock().getX() + ".0,";
			loc = loc + e.getClickedBlock().getY() + ".0,";
			loc = loc + e.getClickedBlock().getZ() + ".0";
			if (loc.equals(locCon) && e.getAction() != Action.LEFT_CLICK_BLOCK) {
				e.setCancelled(true);
			}
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = null;
			String crateType = null;
			if (config.getConfigurationSection("crates").getKeys(false) == null || !config.contains("crates")) {
				return;
			}

			for (String i : config.getConfigurationSection("crates").getKeys(false)) {
				try {
					if(config.contains("crates."+i+".key")) {
						item = serialize.toItemFromString(config.getString("crates." + i + ".key"));
						ItemStack playerItem = new ItemStack(e.getItem());
						item.setAmount(1);
						playerItem.setAmount(1);
						if (playerItem.equals(item)) {
							crateType = i;
							e.setCancelled(true);
							break;
						}
					}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			for (String loc : config.getStringList("locations")) {
				String locx = "";
				locx = locx + e.getClickedBlock().getWorld().getName() + ",";
				locx = locx + e.getClickedBlock().getX() + ".0,";
				locx = locx + e.getClickedBlock().getY() + ".0,";
				locx = locx + e.getClickedBlock().getZ() + ".0";
				if (loc.equals(locx)) {
					e.setCancelled(true);
					if (item != null) {
						ItemStack playerItem = new ItemStack(e.getItem());
						playerItem.setAmount(1);
						if (item.equals(playerItem)) {
							Crate crate = new Crate(e.getPlayer(), crateType);
							if (crate.Open()) {
								item.setAmount(1);
								e.getPlayer().getInventory().removeItem(item);
							} else {
								e.getPlayer().sendMessage(colouredMessage(
										"&c&lParamaniac Crates &8-&7 Currently there are no items in the crate!"));
							}

						}

					} else {
						return;
					}
				}
			}

		}
	}

	@EventHandler
	public void BlockPlace(BlockPlaceEvent e) {
		ConfigManager configMan = new ConfigManager("crates.yml");
		FileConfiguration config = configMan.get();
		ItemStack crateChest = new ItemStack(Material.CHEST);
		ItemMeta itemMeta = crateChest.getItemMeta();
		itemMeta.setUnbreakable(true);
		itemMeta.addEnchant(Enchantment.LUCK, 1, true);
		itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lCRATE"));
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		crateChest.setItemMeta(itemMeta);
		if (e.getItemInHand().equals(crateChest)) {
			List<String> locations = config.getStringList("locations");

			Location xblockLoc = e.getBlock().getLocation();
			// "world,x,y,z"
			String loc = "";
			loc = loc + xblockLoc.getWorld().getName() + ",";
			loc = loc + xblockLoc.getX() + ",";
			loc = loc + xblockLoc.getY() + ",";
			loc = loc + xblockLoc.getZ();
			locations.add(loc);
			config.set("locations", locations);
			configMan.saveConfig();
			e.getPlayer().sendMessage(colouredMessage(
					"&c&lParamaniac Crates &8-&7 You have placed a crate! Players can now use their crate keys at that location!"));

			Location blockLoc = e.getBlock().getLocation();
			blockLoc.setX(blockLoc.getX() + 0.5D);
			blockLoc.setY(blockLoc.getY() - 1);
			blockLoc.setZ(blockLoc.getZ() + 0.5D);
			ArmorStand as = (ArmorStand) e.getBlock().getLocation().getWorld().spawnEntity(blockLoc,
					EntityType.ARMOR_STAND);
			as.setGravity(false);
			as.setCanPickupItems(false);
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&lCRATES"));
			as.setCustomNameVisible(true);
			as.setVisible(false);

		}
	}

	Inventorys invs = new Inventorys();
	Location blockBrokenLoc = null;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBreak(BlockBreakEvent e) {
		ConfigManager configMan = new ConfigManager("crates.yml");
		FileConfiguration config = configMan.get();
		for (String loc : config.getStringList("locations")) {
			String[] locargs = loc.split(",");
			if (e.getBlock().getLocation().getWorld().getName().equals(locargs[0])
					&& e.getBlock().getLocation().getX() == Double.parseDouble(locargs[1])
					&& e.getBlock().getLocation().getY() == Double.parseDouble(locargs[2])
					&& e.getBlock().getLocation().getZ() == Double.parseDouble(locargs[3])) {
				e.setCancelled(true);
				if (e.getPlayer().hasPermission("crates.admin")) {
					blockBrokenLoc = e.getBlock().getLocation();
					e.getPlayer().openInventory(invs.crateSettings());
				}

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInvClick(InventoryClickEvent e) {
		ConfigManager configMan = new ConfigManager("crates.yml");
		FileConfiguration config = configMan.get();
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
			return;
		List<String> invNames = new ArrayList<>();
		if (!config.contains("crates")) {
			return;
		}
		for (String invName : config.getConfigurationSection("crates").getKeys(false)) {
			invNames.add(ChatColor.translateAlternateColorCodes('&',
					config.getString("crates." + invName + ".colouredName")+"."));
		}
		String[] myInvName = e.getInventory().getName().split(" ");
		
		if (myInvName.length > 4) {
			if(myInvName[1].equals(ChatColor.RED+"Settings") && myInvName[2].equals(ChatColor.GRAY+"-") && myInvName[3].equals("Left")) {
				e.setCancelled(true);
				if (e.getCurrentItem().getType() == Material.AIR) {
					return;

				}
				String invName = invNames.get(invNames.indexOf(myInvName[0]+"."));
				invName = invName.substring(0, invName.length()-1);
				List<String> items = config
						.getStringList("crates." + ChatColor.stripColor(invName).toLowerCase() + ".items");

				Iterator<String> iter = items.iterator();
				if(items.size() == 1) {
					String i = items.get(0);
					String[] split = i.split(",");
					String desiredRemoval = serialize.itemToString(e.getCurrentItem(), Integer.parseInt(split[1]));
					if (i.equals(desiredRemoval)) {
						items.remove(desiredRemoval);
					}
				}else {
					while (iter.hasNext()) {
						String i = iter.next();
						String[] split = i.split(",");
						String desiredRemoval = serialize.itemToString(e.getCurrentItem(), Integer.parseInt(split[1]));
						if (i.equals(desiredRemoval)) {
							iter.remove();
						}
					}
				}
				
				config.set("crates." + ChatColor.stripColor(invName).toLowerCase() + ".items", items);
				configMan.saveConfig();
				e.getWhoClicked().openInventory(invs.crateItems(ChatColor.stripColor(invName).toLowerCase()));
			}
			

		}
		if (invNames.contains(e.getInventory().getName())) {
			e.setCancelled(true);
		}

		if (e.getInventory().getName().equals(invs.crateSettings().getName())) {
			e.setCancelled(true);
			ItemStack removeCrate = new ItemStack(Material.INK_SACK, 1, (byte) 15);
			ItemMeta removeCrateMeta = removeCrate.getItemMeta();
			removeCrateMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cDestroy crate location"));
			removeCrate.setItemMeta(removeCrateMeta);

			if (e.getCurrentItem().equals(removeCrate)) {
				List<String> locations = config.getStringList("locations");
				String loc = "";
				loc = loc + blockBrokenLoc.getWorld().getName() + ",";
				loc = loc + blockBrokenLoc.getX() + ",";
				loc = loc + blockBrokenLoc.getY() + ",";
				loc = loc + blockBrokenLoc.getZ();
				locations.remove(loc);
				config.set("locations", locations);
				configMan.saveConfig();
				e.getWhoClicked().getWorld().getBlockAt(blockBrokenLoc).setType(Material.AIR);
				e.getWhoClicked().sendMessage(
						colouredMessage("&c&lParamaniac Crates &8-&7 You have removed the crate location!"));
				e.getWhoClicked().closeInventory();
				Location holoLoc = blockBrokenLoc;
				holoLoc.setX(holoLoc.getX() + 0.5D);
				holoLoc.setY(holoLoc.getY() - 1);
				holoLoc.setZ(holoLoc.getZ() + 0.5D);

				for (Entity en : holoLoc.getChunk().getEntities()) {
					if (en.getCustomName() != null) {
						if (en.getCustomName().equals(ChatColor.translateAlternateColorCodes('&', "&c&lCRATES"))) {
							en.remove();
							break;
						}

					}
				}
			}

			ItemStack crates = new ItemStack(Material.CHEST);
			ItemMeta cratesMeta = crates.getItemMeta();
			cratesMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cCrates"));
			crates.setItemMeta(cratesMeta);

			if (e.getCurrentItem().equals(crates)) {
				e.getWhoClicked().openInventory(invs.crates());
			}
		}

		if (e.getInventory().getName().equals(invs.crates().getName())) {
			e.setCancelled(true);
			if (e.getCurrentItem().getType() == Material.CHEST) {
				e.getWhoClicked().openInventory(invs.crateItems(
						ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toLowerCase()));
			}
		}

	}

}
