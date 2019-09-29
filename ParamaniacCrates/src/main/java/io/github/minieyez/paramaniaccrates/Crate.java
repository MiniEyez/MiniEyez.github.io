package io.github.minieyez.paramaniaccrates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;

public class Crate {

	Player p;
	String crateName;

	Plugin plugin = ParamaniacCrates.getPlugin(ParamaniacCrates.class);
	ItemSerialize serialize = new ItemSerialize();
	ConfigManager configMan = new ConfigManager("crates.yml");
	FileConfiguration config = configMan.get();

	public String colouredMessage(String string) {
		return new String(ChatColor.translateAlternateColorCodes('&', string));
	}

	public Crate(Player player, String name) {
		this.p = player;
		this.crateName = name;
	}

	public boolean Open() {

		CrateType type = new CrateType(crateName);
		final Inventory crateInventory = Bukkit.createInventory(null, 45, type.getColouredName()+".");

		
		List<String> itemConfig = config.getStringList("crates." + crateName + ".items");
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for (String x : itemConfig) {
			String[] split = x.split(",");
			for (int z = 0; z < Integer.parseInt(split[1]); z++) {
				try {
					ItemStack item = serialize.toItemFromString(split[0]);
					items.add(item);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		if(items.isEmpty()) {
			return false;
		}
		
		p.openInventory(crateInventory);
		Runnable runnable = new BukkitRunnable() {

			public void run() {
				for (int i = 0; i < crateInventory.getContents().length; i++) {
					if (i != 22) {
						ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1,
								(byte) (new Random().nextInt(13) + 1));
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("Rolling...");
						item.setItemMeta(meta);
						crateInventory.setItem(i, item);
					}
					if (i == 22) {

						crateInventory.setItem(22, items.get(new Random().nextInt(items.size())));
					}
				}

			}
		};

		BukkitTask runnableID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0L, 5L);

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				runnableID.cancel();
				p.getInventory().addItem(crateInventory.getItem(22));
				if (crateInventory.getItem(22).getItemMeta().getDisplayName() == null) {
					p.sendMessage(colouredMessage("&c&lParamaniac Crates &8-&7 Congratulations, you have won "+ crateInventory.getItem(22).getAmount() + " x "
							+ crateInventory.getItem(22).getType().name().replace("_", " ")));
				} else
					p.sendMessage(colouredMessage("&c&lParamaniac Crates &8-&7 Congratulations, you have won "+ crateInventory.getItem(22).getAmount() + " x "
							+ crateInventory.getItem(22).getItemMeta().getDisplayName()));
			}
		}, 60L);
		return true;

	}

}
