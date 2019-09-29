package io.github.minieyez.paramaniaccrates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class Inventorys {

	

	

	public Inventory crateSettings() {
		Inventory inv = Bukkit.createInventory(null, 27,
				ChatColor.translateAlternateColorCodes('&', "&c&lCrate Settings"));

		ItemStack removeCrate = new ItemStack(Material.INK_SACK, 1, (byte) 15);
		ItemMeta removeCrateMeta = removeCrate.getItemMeta();
		removeCrateMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cDestroy crate location"));
		removeCrate.setItemMeta(removeCrateMeta);

		ItemStack crates = new ItemStack(Material.CHEST);
		ItemMeta cratesMeta = crates.getItemMeta();
		cratesMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cCrates"));
		crates.setItemMeta(cratesMeta);
 
		inv.setItem(11, removeCrate);
		inv.setItem(15, crates);

		return inv;
	}

	public Inventory crates() {
		ConfigManager configMan = new ConfigManager("crates.yml");
		FileConfiguration config = configMan.get();
		List<String> crates = new ArrayList<>();
		for (String i : config.getConfigurationSection("crates").getKeys(false)) {
			crates.add(i);
		}
		int rounded = 9 * (int) Math.ceil(crates.size() / 9.0);
		Inventory inv = Bukkit.createInventory(null, rounded,
				ChatColor.translateAlternateColorCodes('&', "&c&lCRATES"));
		for (int i = 0; i < crates.size(); i++) {
			ItemStack chest = new ItemStack(Material.CHEST);
			ItemMeta chestMeta = chest.getItemMeta();
			chestMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					config.getString("crates." + crates.get(i) + ".colouredName")));
			chest.setItemMeta(chestMeta);
			inv.addItem(chest);
		}
		return inv;
	}

	public Inventory crateItems(String crateName) {
		ConfigManager configMan = new ConfigManager("crates.yml");
		FileConfiguration config = configMan.get();
		ItemSerialize serialize = new ItemSerialize();
		List<String> configItems = config.getStringList("crates." + crateName + ".items");
		List<ItemStack> items = new ArrayList<>();
		
		for (String i : configItems) {
			try {
				ItemStack item = serialize.toItemFromString(i);
				items.add(item);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int rounded = 9 * (int) Math.ceil(items.size() / 9.0);
		Inventory inv = Bukkit.createInventory(null, rounded,
				ChatColor.translateAlternateColorCodes('&', config.getString("crates." + crateName + ".colouredName")+" &cSettings &7- Left click to remove!"));
		
		int index = 0;
		for (ItemStack item : items) {
			inv.setItem(index, item);
			index++;
			
		}
		
		return inv;

	}

}
