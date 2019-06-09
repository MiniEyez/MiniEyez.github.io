package io.github.minieyez.paramaniacenchants;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor{
	
	ParamaniacEnchants plugin = ParamaniacEnchants.getPlugin(ParamaniacEnchants.class);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("paramaniacenchants")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("debug")) {
					if(sender instanceof Player) {
						Player p = (Player) sender;
						p.sendMessage("enabled");
						plugin.onOffDebugMode(p);
					}
						
					
				}
			}
		}
		return false;
	}
	
	

}
