package com.jamesmonger.XPStrength.handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.jamesmonger.XPStrength.XPStrength;
import com.jamesmonger.XPStrength.util.Settings;
import com.jamesmonger.XPStrength.util.Languages;

public class Commands implements CommandExecutor
{

	@SuppressWarnings("unused")
	private XPStrength plugin;

	public Commands(XPStrength plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("xpbonus"))
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage("This command can only be run by a player.");
			}
			else
			{
				Player p = (Player) sender;
				if (p.hasPermission("xpstrength.bonus"))
				{
					if (p.getLevel() < Settings.lowestLevel)
					{
						Languages.sendMessage(p, Languages.getLine("TOO_LOW")
								.replace("%lvl%", "" + Settings.lowestLevel));
					}
					else
					{
						if (XPStrength.player_toggled.get(p.getName()) == false)
						{
							if (XPStrength.xpDrain == true)
							{
								Languages
										.sendMessage(p, Languages
												.getLine("BONUS_NOW_ON_DRAIN"));
							}
							else
							{
								Languages.sendMessage(p, Languages
										.getLine("BONUS_NOW_ON_NODRAIN"));
							}
							XPStrength.player_toggled.put(p.getName(), true);
						}
						else
						{
							if (XPStrength.xpDrain == true)
							{
								Languages.sendMessage(p, Languages
										.getLine("BONUS_NOW_OFF_DRAIN"));
							}
							else
							{
								Languages.sendMessage(p, Languages
										.getLine("BONUS_NOW_OFF_NODRAIN"));
							}
							XPStrength.player_toggled.put(p.getName(), false);
						}
					}
				}
				else
				{
					Languages
							.sendMessage(p, Languages.getLine("NO_PERMISSION"));
				}
			}
			return true;
		}
		return false;
	}

}
