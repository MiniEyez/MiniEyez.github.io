package io.github.minieyez.paramaniacenchants;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import net.md_5.bungee.api.ChatColor;

public class PickaxeEnchants {

	ParamaniacEnchants main = ParamaniacEnchants.getPlugin(ParamaniacEnchants.class);

	WorldGuardPlugin worldGuard = main.getWorldGuard();

	public ItemStack createGuiItem(String name, ArrayList<String> desc, Material mat) {
		ItemStack i = new ItemStack(mat, 1);
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName(name);
		iMeta.setLore(desc);
		i.setItemMeta(iMeta);
		return i;
	}

	public void openPickaxeEnchants(Player player) {
		Inventory enchantInventory;
		enchantInventory = Bukkit.createInventory(null, 27, ChatColor.RED + "Pickaxe Enchants!");
		enchantInventory.setItem(10, createGuiItem("Explosion_Circle $1000000", new ArrayList<String>(Arrays.asList(
				ChatColor.translateAlternateColorCodes('&', "&5When you mine cause a circular explosion in the mines!"),
				ChatColor.translateAlternateColorCodes('&',
						"&c&lENABLED: &a&l" + main.getManager().getPlayerDataConfig()
								.getBoolean("players." + player.getUniqueId() + ".explosion_Circle")),
				ChatColor.translateAlternateColorCodes('&',
						"&c&lBOUGHT: &a&l" + main.getManager().getPlayerDataConfig()
								.getBoolean("players." + player.getUniqueId() + ".explosion_Circle_Bought")))),
				Material.TNT));
		enchantInventory.setItem(11, createGuiItem("Explosion_Square $2000000", new ArrayList<String>(Arrays.asList(
				ChatColor.translateAlternateColorCodes('&', "&5When you mine cause a square explosion in the mines!"),
				ChatColor.translateAlternateColorCodes('&',
						"&c&lENABLED: &a&l" + main.getManager().getPlayerDataConfig()
								.getBoolean("players." + player.getUniqueId() + ".explosion_Square")),
				ChatColor.translateAlternateColorCodes('&',
						"&c&lBOUGHT: &a&l" + main.getManager().getPlayerDataConfig()
								.getBoolean("players." + player.getUniqueId() + ".explosion_Square_Bought")))),
				Material.FLINT_AND_STEEL));
		enchantInventory.setItem(12,
				createGuiItem("Enhancement_Haste $500000",
						new ArrayList<String>(Arrays.asList(
								ChatColor.translateAlternateColorCodes('&', "&5Dig faster while in the mines!"),
								ChatColor.translateAlternateColorCodes('&',
										"&c&lENABLED: &a&l" + main.getManager().getPlayerDataConfig()
												.getBoolean("players." + player.getUniqueId() + ".enhancement_Haste")),
								ChatColor.translateAlternateColorCodes('&',
										"&c&lBOUGHT: &a&l" + main.getManager().getPlayerDataConfig().getBoolean(
												"players." + player.getUniqueId() + ".enhancement_Haste_Bought")))),
						Material.POTION));
		enchantInventory.setItem(13,
				createGuiItem("Enchantment_AutoRepair $200000", new ArrayList<String>(Arrays.asList(
						ChatColor.translateAlternateColorCodes('&', "&5Ensure that you always have a durable pickaxe!"),
						ChatColor.translateAlternateColorCodes('&',
								"&c&lENABLED: &a&l" + main.getManager().getPlayerDataConfig()
										.getBoolean("players." + player.getUniqueId() + ".enchantment_AutoRepair")),
						ChatColor.translateAlternateColorCodes('&',
								"&c&lBOUGHT: &a&l" + main.getManager().getPlayerDataConfig().getBoolean(
										"players." + player.getUniqueId() + ".enchantment_AutoRepair_Bought")))),
						Material.DIAMOND_PICKAXE));
		player.openInventory(enchantInventory);

	}

	public void explosion_Square(Player p, int radius, Location blockLoc) {

		ArrayList<Block> blocks = new ArrayList<Block>();
		for (double x = blockLoc.getX() - radius; x <= blockLoc.getX() + radius; x++) {
			for (double y = blockLoc.getY() - radius; y <= blockLoc.getY() + radius; y++) {
				for (double z = blockLoc.getZ() - radius; z <= blockLoc.getZ() + radius; z++) {
					Location loc = new Location(blockLoc.getWorld(), x, y, z);

					RegionManager regionManager = worldGuard.getRegionManager(p.getWorld());
					ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

					if (set.queryState(worldGuard.wrapPlayer(p), DefaultFlag.DAMAGE_ANIMALS) == StateFlag.State.DENY) {
						main.sendDebugMessage("state");
						if (blockLoc.getWorld().getName().equals("tempspawn")) {
							main.sendDebugMessage("world");
							if (loc.getBlock().getType() != Material.BEDROCK) {
								main.sendDebugMessage("Block added to array .explosion_Square");
								blocks.add(loc.getBlock());
							}

						}
					}

				}
			}
		}
		if (blocks.isEmpty()) {
			return;
		}
		blockLoc.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 3);
		main.sendDebugMessage("Blocks in .explosion_Square: " + blocks.toString());
		for (Block block : blocks) {
			// block.breakNaturally();
			if (okayToBreak(block)) {
				p.getInventory().addItem(toIngot(block));
				block.setType(Material.AIR);
			}

		}

	}

	public ItemStack toIngot(Block block) {
		ItemStack item;
		switch (block.getType().name()) {
		case "IRON_ORE":
			item = new ItemStack(Material.IRON_INGOT);
			break;
		case "DIAMOND_ORE":
			item = new ItemStack(Material.DIAMOND);
			break;
		case "COAL_ORE":
			item = new ItemStack(Material.COAL);
			break;
		case "LAPIS_ORE":
			item = new ItemStack(Material.INK_SACK, 1, (short) 4);
			break;
		case "EMERALD_ORE":
			item = new ItemStack(Material.EMERALD);
			break;
		case "STONE":
			item = new ItemStack(Material.COBBLESTONE);
			break;
		case "GOLD_ORE":
			item = new ItemStack(Material.GOLD_INGOT);
			break;
		default:
			item = new ItemStack(block.getType());
			break;
		}
		return item;

	}

	public boolean okayToBreak(Block block) {
		boolean okay = false;
		switch (block.getType().name()) {
		case "IRON_ORE":
			okay = true;
			break;
		case "DIAMOND_ORE":
			okay = true;
			break;
		case "COAL_ORE":
			okay = true;
			break;
		case "LAPIS_ORE":
			okay = true;
			break;
		case "EMERALD_ORE":
			okay = true;
			break;
		case "STONE":
			okay = true;
			break;
		case "EMERALD_BLOCK":
			okay = true;
			break;
		case "DIAMOND_BLOCK":
			okay = true;
			break;
		case "REDSTONE_ORE":
			okay = true;
			break;
		case "GOLD_ORE":
			okay = true;
			break;
		case "CONCRETE":
			okay = true;
			break;
		case "GLOWSTONE":
			okay = true;
			break;
		case "NETHER_BRICK":
			okay = true;
			break;
		case "OBSIDIAN":
			okay = true;
			break;
		default:
			break;
		}
		return okay;
	}

	public void explosion_Circle(Player p, int r, Location blockLoc) {

		ArrayList<Block> blocks = new ArrayList<Block>();

		int cx = blockLoc.getBlockX();
		int cy = blockLoc.getBlockY();
		int cz = blockLoc.getBlockZ();
		World w = blockLoc.getWorld();
		int rSquared = r * r;
		for (int x = cx - r; x <= cx + r; x++) {
			for (int z = cz - r; z <= cz + r; z++) {
				if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
					Location loc = new Location(w, x, cy, z);
					RegionManager regionManager = worldGuard.getRegionManager(p.getWorld());
					ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

					if (set.queryState(worldGuard.wrapPlayer(p), DefaultFlag.DAMAGE_ANIMALS) == StateFlag.State.DENY) {
						main.sendDebugMessage("query state!");
						if (blockLoc.getWorld().getName().equals("tempspawn")) {
							main.sendDebugMessage("world!");
							if (loc.getBlock().getType() != Material.BEDROCK) {
								main.sendDebugMessage("Block added to array .explosion_Circle");
								blocks.add(loc.getBlock());
							}

						}
					}
				}
			}
		}

		if (blocks.isEmpty()) {
			return;
		}
		p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 3);
		main.sendDebugMessage("Blocks in .explosion_Circle: " + blocks.toString());
		for (Block block : blocks) {
			if (okayToBreak(block)) {
				p.getInventory().addItem(toIngot(block));
				block.setType(Material.AIR);
			}

		}

	}

	public void haste(Player p) {
		PotionEffect hastePotion = new PotionEffect(PotionEffectType.FAST_DIGGING, 1000000, 1);
		p.addPotionEffect(hastePotion);
	}

	public void autosell() {

	}

	public void autoRepair(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item.getType().equals(Material.DIAMOND_PICKAXE)) {
			p.getInventory().getItemInMainHand().setDurability((short) -1);
		}

	}

}
