package io.github.minieyez.paramaniacenchants;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.EconomyResponse;

public class EventManager implements Listener {

	PickaxeEnchants pickaxeEnchants = new PickaxeEnchants();
	ParamaniacEnchants plugin = ParamaniacEnchants.getPlugin(ParamaniacEnchants.class);

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE) {
			double rand = Math.random();
			if (plugin.getManager().getPlayerDataConfig()
					.getBoolean("players." + e.getPlayer().getUniqueId() + ".enchantment_AutoRepair")) {
				pickaxeEnchants.autoRepair(e.getPlayer());
				plugin.sendDebugMessage("Block broken by: " + e.getPlayer().getName() + " .enchantment_AutoRepair");
			}
			if (plugin.getManager().getPlayerDataConfig()
					.getBoolean("players." + e.getPlayer().getUniqueId() + ".explosion_Square")) {
				if (rand <= 0.10D) {
					pickaxeEnchants.explosion_Square(e.getPlayer(), 1, e.getBlock().getLocation());
					plugin.sendDebugMessage("Block broken by: " + e.getPlayer().getName() + " .explosion_Square");
				}
			} else if (plugin.getManager().getPlayerDataConfig()
					.getBoolean("players." + e.getPlayer().getUniqueId() + ".explosion_Circle")) {
				if (rand <= 0.15D) {
					pickaxeEnchants.explosion_Circle(e.getPlayer(), 2, e.getBlock().getLocation());
					plugin.sendDebugMessage("Block broken by: " + e.getPlayer().getName() + " .explosion_Circle");
				}
			}
		}

	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		if (e.getItem() == null)
			return;
		if ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (e.getItem().getType().equals(Material.DIAMOND_PICKAXE)) {
				pickaxeEnchants.openPickaxeEnchants(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (plugin.getManager().getPlayerDataConfig()
				.getString("players." + e.getPlayer().getUniqueId() + ".name") == null) {
			plugin.getManager().getPlayerDataConfig().set("players." + e.getPlayer().getUniqueId() + ".name",
					e.getPlayer().getName());
			plugin.getManager().savePlayerDataConfig();
			plugin.getManager().reloadPlayerDataConfig();
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		String invName = e.getInventory().getName();
		if (!invName.equals(ChatColor.RED + "Pickaxe Enchants!"))
			return;
		if (e.getClick().equals(ClickType.NUMBER_KEY))
			e.setCancelled(true);
		e.setCancelled(true);

		Player p = (Player) e.getWhoClicked();
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedItem == null || clickedItem.getType().equals(Material.AIR))
			return;
		if (e.getRawSlot() == 10) {
			if (plugin.getManager().getPlayerDataConfig()
					.getBoolean("players." + p.getUniqueId() + ".explosion_Circle_Bought")) {
				if (!plugin.getManager().getPlayerDataConfig()
						.getBoolean("players." + p.getUniqueId() + ".explosion_Square")) {
					plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".explosion_Circle",
							!plugin.getManager().getPlayerDataConfig()
									.getBoolean("players." + p.getUniqueId() + ".explosion_Circle"));
					plugin.getManager().savePlayerDataConfig();
					plugin.getManager().reloadPlayerDataConfig();
					pickaxeEnchants.openPickaxeEnchants(p);
					return;
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cPlease disable explosion_Square first!"));
					return;
				}
			}

			EconomyResponse r = plugin.getEcon().withdrawPlayer(p, 1000000);
			if (r.transactionSuccess()) {
				plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".explosion_Circle",
						false);
				plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".explosion_Circle_Bought",
						true);
				plugin.getManager().savePlayerDataConfig();
				plugin.getManager().reloadPlayerDataConfig();
				pickaxeEnchants.openPickaxeEnchants(p);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have bought explosion_Circle"));
				return;
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInsufficient funds!"));
				return;
			}

		}
		if (e.getRawSlot() == 11) {
			if (plugin.getManager().getPlayerDataConfig()
					.getBoolean("players." + p.getUniqueId() + ".explosion_Square_Bought")) {
				if (!plugin.getManager().getPlayerDataConfig()
						.getBoolean("players." + p.getUniqueId() + ".explosion_Circle")) {
					plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".explosion_Square",
							!plugin.getManager().getPlayerDataConfig()
									.getBoolean("players." + p.getUniqueId() + ".explosion_Square"));
					plugin.getManager().savePlayerDataConfig();
					plugin.getManager().reloadPlayerDataConfig();
					pickaxeEnchants.openPickaxeEnchants(p);
					return;
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cPlease disable explosion_Circle first!"));
					return;
				}
			}

			EconomyResponse r = plugin.getEcon().withdrawPlayer(p, 2000000);
			if (r.transactionSuccess()) {
				plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".explosion_Square",
						false);
				plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".explosion_Square_Bought",
						true);
				plugin.getManager().savePlayerDataConfig();
				plugin.getManager().reloadPlayerDataConfig();
				pickaxeEnchants.openPickaxeEnchants(p);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have bought explosion_Square"));
				return;
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInsufficient funds!"));
				return;
			}
		}
		if (e.getRawSlot() == 12) {
			if (plugin.getManager().getPlayerDataConfig()
					.getBoolean("players." + p.getUniqueId() + ".enhancement_Haste_Bought")) {
				if (p.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
					p.removePotionEffect(PotionEffectType.FAST_DIGGING);
					plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".enhancement_Haste",
							false);
				} else {
					pickaxeEnchants.haste(p);
					plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".enhancement_Haste",
							true);
				}

				plugin.getManager().savePlayerDataConfig();
				plugin.getManager().reloadPlayerDataConfig();
				pickaxeEnchants.openPickaxeEnchants(p);
				return;
			}
			EconomyResponse r = plugin.getEcon().withdrawPlayer(p, 500000);
			if (r.transactionSuccess()) {
				plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".enhancement_Haste",
						true);
				plugin.getManager().getPlayerDataConfig()
						.set("players." + p.getUniqueId() + ".enhancement_Haste_Bought", true);
				plugin.getManager().savePlayerDataConfig();
				plugin.getManager().reloadPlayerDataConfig();
				pickaxeEnchants.openPickaxeEnchants(p);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have bought enhancement_Haste"));
				return;
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInsufficient funds!"));
				return;
			}

		}
		if (e.getRawSlot() == 13) {
			if (plugin.getManager().getPlayerDataConfig()
					.getBoolean("players." + p.getUniqueId() + ".enchantment_AutoRepair_Bought")) {
				plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".enchantment_AutoRepair",
						!plugin.getManager().getPlayerDataConfig()
								.getBoolean("players." + p.getUniqueId() + ".enchantment_AutoRepair"));

				plugin.getManager().savePlayerDataConfig();
				plugin.getManager().reloadPlayerDataConfig();
				pickaxeEnchants.openPickaxeEnchants(p);
				return;
			}
			EconomyResponse r = plugin.getEcon().withdrawPlayer(p, 200000);
			if (r.transactionSuccess()) {
				plugin.getManager().getPlayerDataConfig().set("players." + p.getUniqueId() + ".enchantment_AutoRepair",
						true);
				plugin.getManager().getPlayerDataConfig()
						.set("players." + p.getUniqueId() + ".enchantment_AutoRepair_Bought", true);
				plugin.getManager().savePlayerDataConfig();
				plugin.getManager().reloadPlayerDataConfig();
				pickaxeEnchants.openPickaxeEnchants(p);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have bought enchantment_AutoRepair"));
				return;
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInsufficient funds!"));
				return;
			}

		}
	}

}
