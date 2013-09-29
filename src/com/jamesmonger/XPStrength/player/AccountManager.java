package com.jamesmonger.XPStrength.player;

import java.io.*;

import org.bukkit.entity.Player;

import com.jamesmonger.XPStrength.XPStrength;
import com.jamesmonger.XPStrength.util.Settings;
import com.jamesmonger.XPStrength.util.Languages;

public class AccountManager
{
	XPStrength plugin;
	
	public AccountManager(XPStrength plugin)
	{
		this.plugin = plugin;
	}
	
	public void savePlayer(Player player)
	{
		try
		{
			File file = new File(plugin.getDataFolder() + "/users/", player.getName()
					+ ".txt");
			if (!file.exists())
			{
				FileOutputStream fos = new FileOutputStream(file);
				fos.flush();
				fos.close();
				XPStrength.player_toggled.put(player.getName(), false);
			}
			if (XPStrength.player_toggled.get(player.getName()) == null)
			{
				XPStrength.player_toggled.put(player.getName(), false);
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("toggled," + XPStrength.player_toggled.get(player.getName()));
			bw.newLine();
			bw.flush();
			bw.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public void loadPlayer(Player player)
	{
		try
		{
			File file = new File(plugin.getDataFolder() + "/users/", player.getName()
					+ ".txt");
			if (!file.exists())
			{
				FileOutputStream fos = new FileOutputStream(file);
				fos.flush();
				fos.close();

				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				XPStrength.player_toggled.put(player.getName(), false);
				bw.write("toggled," + XPStrength.player_toggled.get(player.getName()));
				bw.newLine();
				bw.flush();
				bw.close();
			}
			BufferedReader br = new BufferedReader(new FileReader(file));
			String l;
			while ((l = br.readLine()) != null)
			{
				String[] tokens = l.split(",", 2);
				if (tokens[0].equals("toggled"))
				{
					if (tokens.length != 2)
						continue;
					if (tokens[1] != null)
					{
						XPStrength.player_toggled.put(player.getName(),
								Boolean.parseBoolean(tokens[1]));
					}
					else
					{
						XPStrength.player_toggled.put(player.getName(), false);
					}
				}
			}
			br.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		if (player.hasPermission("xpstrength.bonus"))
		{
			if (XPStrength.player_toggled.get(player.getName()) == true
					&& player.hasPermission("xpstrength.bonus"))
			{
				Languages.sendMessage(player,
						Languages.getLine("NO_MORE_PERMISSION"));
			}
			if (player.getLevel() < Settings.lowestLevel)
			{
				Languages.sendMessage(
						player,
						Languages.getLine("TOO_LOW").replace("%lvl%",
								"" + Settings.lowestLevel));
				XPStrength.player_toggled.put(player.getName(), false);
			}
			else
			{
				if (XPStrength.player_toggled.get(player.getName()) == false)
				{
					Languages.sendMessage(player, Languages.getLine("BONUS_OFF"));
				}
				else
				{
					Languages.sendMessage(player, Languages.getLine("BONUS_ON"));
				}
			}
		}
		else
		{
			XPStrength.player_toggled.put(player.getName(), false);
		}
	}
}
