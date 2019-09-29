package io.github.minieyez.paramaniaccrates;

import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {

	public String colouredMessage(String string) {
		return new String(ChatColor.translateAlternateColorCodes('&', string));
	}
	
	Plugin plugin = ParamaniacCrates.getPlugin(ParamaniacCrates.class);

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			ConfigManager configMan = new ConfigManager("crates.yml");
			FileConfiguration config = configMan.get();
			if (p.hasPermission("crates.admin")) {
				if (args.length == 0) {
					p.sendMessage(
							colouredMessage("&c&m---------------&7&l[ &c&lParamaniac Crates &7&l]&c&m---------------\n"
									+ "&c/crate create &n<crateName>&8 - &7Create a new crate\n"
									+ "&c/crate delete &n<crateName>&8 - &7Delete a crate\n"
									+ "&c/crate additem &n<crateName>&c &n<probability>&8 - &7Add the item in your hand to a crate\n"
									+ "&c/crate scn &n<crateName>&c &n<colouredName>&8 - &7Set the coloured name of a crate\n"
									+ "&c/crate setkey &n<crateName>&8 - &7Set the key to opening a crate to the item in your hand\n"
									+ "&c/crate give &n<crateName>&c &n<player>&c &n<amount>&8 - &7Give a crate key to a specific player\n"
									+ "&c/crate list &8- &7List the current crates available\n"
									+ "&c/crate givecrate&8 - &7Gives a crate chest to place\n"
									+ "&c/crate help&8 - &7Brings up this menu\n"
									+ "&c&m----------------------------------------------------"));
					return true;

				}

				else if (args.length >= 1) {

					if (args[0].equalsIgnoreCase("help")) {
						p.sendMessage(colouredMessage(
								"&c&m---------------&7&l[ &c&lParamaniac Crates &7&l]&c&m---------------\n"
										+ "&c/crate create &n<crateName>&8 - &7Create a new crate\n"
										+ "&c/crate delete &n<crateName>&8 - &7Delete a crate\n"
										+ "&c/crate additem &n<crateName>&c &n<probability>&8 - &7Add the item in your hand to a crate\n"
										+ "&c/crate scn &n<crateName>&c &n<colouredName>&8 - &7Set the coloured name of a crate\n"
										+ "&c/crate setkey &n<crateName>&8 - &7Set the key to opening a crate to the item in your hand\n"
										+ "&c/crate give &n<crateName>&c &n<player>&c &n<amount>&8 - &7Give a crate key to a specific player\n"
										+ "&c/crate list &8- &7List the current crates available\n"
										+ "&c/crate givecrate &8- &7Gives a crate chest to place\n"
										+ "&c/crate help&8 - &7Brings up this menu\n"
										+ "&c&m----------------------------------------------------"));
						return true;
					}
					
					else if(args[0].equalsIgnoreCase("list")) {
						if(!config.contains("crates")) {
							p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7There are currently no crates!"));
							return true;
						}
						String crateList = "";
						for(String c : config.getConfigurationSection("crates").getKeys(false)) {
							crateList= crateList + (c + ", ");
						}
						crateList = crateList.substring(0, crateList.length() - 2);
						p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7There current crates are: "+crateList));
						return true;
					}

					else if (args[0].equalsIgnoreCase("create")) {
						if (args.length == 2) {
							if (config.isSet("crates." + args[1].toLowerCase())) {
								p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7A crate with the name "
										+ args[1] + " already exists!"));
								return true;
							}

							config.set("crates." + args[1].toLowerCase() + ".colouredName", args[1]);
							configMan.saveConfig();
							p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7A crate with the name " + args[1]
									+ " has been created!"));
							return true;
						} else {
							p.sendMessage(colouredMessage(
									"&c&lParamaniac Crates &8- &7Incorrect usage! /crate create &n<crateName>"));
							return true;
						}
					}

					else if (args[0].equalsIgnoreCase("delete")) {
						if (args.length == 2) {
							if (config.isSet("crates." + args[1].toLowerCase())) {
								config.set("crates." + args[1].toLowerCase(), null);
								configMan.saveConfig();
								p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7A crate with the name "
										+ args[1] + " has been deleted!"));
								return true;
							}
							p.sendMessage(colouredMessage(
									"&c&lParamaniac Crates &8- &7A crate with the name " + args[1] + " doesnt exist!"));
							return true;
						} else {
							p.sendMessage(colouredMessage(
									"&c&lParamaniac Crates &8- &7Incorrect usage! /crate delete &n<crateName>"));
							return true;
						}
					}

					else if (args[0].equalsIgnoreCase("additem")) {
						if (args.length == 3) {
							if (config.isSet("crates." + args[1].toLowerCase())) {
								if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
									p.sendMessage(colouredMessage(
											"&c&lParamaniac Crates &8- &7You cannot add Air to the crates!"));
									return true;
								}
								if(isInteger(args[2])) {
									if(Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) < 100) {
										List<String> items = config.getStringList("crates." + args[1].toLowerCase() + ".items");
										ItemSerialize serialize = new ItemSerialize();
										items.add(new String(serialize.itemToString(p.getInventory().getItemInMainHand(), Integer.parseInt(args[2]))));
										
										config.set("crates."+args[1].toLowerCase()+".items", items);
										configMan.saveConfig();
										p.sendMessage(colouredMessage(
												"&c&lParamaniac Crates &8- &7The item in your main hand has been added to the crate "+ args[1]+" with a probability of " + args[2]));
										return true;
									}else {
										p.sendMessage(colouredMessage(
												"&c&lParamaniac Crates &8- &7Please input a value between 0 and 100!"));
										return true;
									}
								}else {
									p.sendMessage(colouredMessage(
											"&c&lParamaniac Crates &8- &7Please input an integer as the probablility!"));
									return true;
								}
							} else {
								p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7A crate with the name "
										+ args[1] + " doesnt exist!"));
								return true;
							}
						} else {
							p.sendMessage(colouredMessage(
									"&c&lParamaniac Crates &8- &7Incorrect usage! /crate additem &n<crateName>&7 &n<probability>"));
							return true;
						}
					}

					else if (args[0].equalsIgnoreCase("scn")) {
						if (args.length == 3) {
							if(config.isSet("crates."+args[1].toLowerCase())) {
								config.set("crates."+args[1].toLowerCase()+".colouredName", args[2]);
								configMan.saveConfig();
								p.sendMessage(colouredMessage(
										"&c&lParamaniac Crates &8- &7The coloured name for the crate "+ args[1] + " has been set to "+args[2]));
								return true;
							}else {
								p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7A crate with the name "
										+ args[1] + " doesnt exist!"));
								return true;
							}
						} else {
							p.sendMessage(colouredMessage(
									"&c&lParamaniac Crates &8- &7Incorrect usage! /crate scn &n<crateName>&7 &n<colouredName>"));
							return true;
						}
					}

					else if (args[0].equalsIgnoreCase("setkey")) {
						if (args.length == 2) {
							if(config.isSet("crates."+args[1].toLowerCase())) {
								if(p.getInventory().getItemInMainHand().getType() == Material.AIR) {
									p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7You cannot set the key to Air!"));
									return true;
								}
								ItemStack item = p.getInventory().getItemInMainHand();
								ItemMeta itemMeta = item.getItemMeta();
								itemMeta.setUnbreakable(true);
								itemMeta.addEnchant(Enchantment.LUCK, 1, true);
								itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("crates."+args[1]+".colouredName")+" &c&lKey"));
								itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
								itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
								itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
								item.setItemMeta(itemMeta);
								item.setAmount(1);
								p.getInventory().setItemInMainHand(item);
								ItemSerialize serialize = new ItemSerialize();
								config.set("crates."+args[1].toLowerCase()+".key", new String(serialize.itemToStringNoProb(item)));
								configMan.saveConfig();
								p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7The key for the crate "
										+ args[1] + " has been changed!"));
								return true;
							}else {
								p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7A crate with the name "
										+ args[1] + " doesnt exist!"));
								return true;
							}
						} else {
							p.sendMessage(colouredMessage(
									"&c&lParamaniac Crates &8- &7Incorrect usage! /crate setkey &n<crateName>"));
							return true;
						}
					}

					else if (args[0].equalsIgnoreCase("give")) {
						if (args.length == 4) {
							if(config.isSet("crates."+args[1].toLowerCase())) {
								@SuppressWarnings("deprecation")
								Player target = Bukkit.getPlayerExact(args[2]);
								if(target == null) {
									p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7The specified player isn't online!"));
									return true;
								}
								if(!isInteger(args[3])) {
									p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7The amount specified isn't acceptable!"));
									return true;
								}
								if(config.getString("crates."+args[1].toLowerCase()+".key") == null) {
									p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7Currently there isnt a key set for the crate "+args[1]+"! Set one by doing the command /crate setkey &n<crateName>"));
									return true;
								}
								ItemSerialize serialize = new ItemSerialize();
								ItemStack item = null;
								try {
									item = serialize.toItemFromString(config.getString("crates."+args[1].toLowerCase()+".key"));
									item.setAmount(Integer.parseInt(args[3]));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								target.getInventory().addItem(item);
							}else {
								p.sendMessage(colouredMessage("&c&lParamaniac Crates &8- &7A crate with the name "
										+ args[1] + " doesnt exist!"));
								return true;
							}
						} else {
							p.sendMessage(colouredMessage(
									"&c&lParamaniac Crates &8- &7Incorrect usage! /crate give &n<crateName>&7 &n<player>&7 &n<amount>"));
							return true;
						}
					}

					else if (args[0].equalsIgnoreCase("givecrate")) {
						if (args.length == 1) {
							ItemStack item = new ItemStack(Material.CHEST);
							ItemMeta itemMeta = item.getItemMeta();
							itemMeta.setUnbreakable(true);
							itemMeta.addEnchant(Enchantment.LUCK, 1, true);
							itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lCRATE"));
							itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
							itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
							itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
							item.setItemMeta(itemMeta);
							p.getInventory().addItem(item);
							p.sendMessage(
									colouredMessage("&c&lParamaniac Crates &8- &7You have been given a crate. Place this crate to create a new destination for players to open their crates!"));
							return true;
							
						} else {
							p.sendMessage(
									colouredMessage("&c&lParamaniac Crates &8- &7Incorrect usage! /crate givecrate"));
							return true;
						}
					}

					else {
						p.sendMessage(colouredMessage(
								"&c&lParamaniac Crates &8- &7Unknown command. Please check /crate help"));
						return true;
					}
				}
			}

		}
		return true;
	}

	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

}
