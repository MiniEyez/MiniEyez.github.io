package io.github.minieyez.paramaniaccrates;

import java.awt.List;

import net.md_5.bungee.api.ChatColor;

public class CrateType {

	String name;

	public CrateType(String crateName) {
		this.name = crateName;
	}

	public String getColouredName() {
		//Get coloured name from the config
		ConfigManager crates = new ConfigManager("crates.yml");
		crates.get().getString("crates."+name.toLowerCase()+".colouredName");
		return new String(ChatColor.translateAlternateColorCodes('&', crates.get().getString("crates."+name+".colouredName")));
	}

	public List[] getItems() { 
		return null;
		//Get items from config
		
	}

}
