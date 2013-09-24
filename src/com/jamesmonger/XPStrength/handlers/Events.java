package com.jamesmonger.XPStrength.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.jamesmonger.XPStrength.XPStrength;
import com.jamesmonger.XPStrength.util.Bonuses;
import com.jamesmonger.XPStrength.util.ExperienceManager;
import com.jamesmonger.XPStrength.util.Languages;

public class Events implements Listener
{

	public static Map<String, Boolean> player_toggled = new HashMap<String, Boolean>();

	XPStrength xps = null;

	public Events(XPStrength plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		xps = plugin;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player p = event.getPlayer();
		try
		{
			File file = new File(xps.getDataFolder() + "/users/", p.getName()
					+ ".txt");
			if (!file.exists())
			{
				FileOutputStream fos = new FileOutputStream(file);
				fos.flush();
				fos.close();
				player_toggled.put(p.getName(), false);
			}
			if (player_toggled.get(p.getName()) == null)
			{
				player_toggled.put(p.getName(), false);
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("toggled," + player_toggled.get(p.getName()));
			bw.newLine();
			bw.flush();
			bw.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		try
		{
			File file = new File(xps.getDataFolder() + "/users/", p.getName()
					+ ".txt");
			if (!file.exists())
			{
				FileOutputStream fos = new FileOutputStream(file);
				fos.flush();
				fos.close();

				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				player_toggled.put(p.getName(), false);
				bw.write("toggled," + player_toggled.get(p.getName()));
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
						player_toggled.put(p.getName(),
								Boolean.parseBoolean(tokens[1]));
					}
					else
					{
						player_toggled.put(p.getName(), false);
					}
				}
			}
			br.close();// Close the reader
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		if (p.hasPermission("xpstrength.bonus"))
		{
			if (player_toggled.get(p.getName()) == true
					&& p.hasPermission("xpstrength.bonus"))
			{
				Languages.sendMessage(p,
						Languages.getLine("NO_MORE_PERMISSION"));
			}
			if (p.getLevel() < Bonuses.lowestLevel)
			{
				Languages.sendMessage(
						p,
						Languages.getLine("TOO_LOW").replace("%lvl%",
								"" + Bonuses.lowestLevel));
				player_toggled.put(p.getName(), false);
			}
			else
			{
				if (player_toggled.get(p.getName()) == false)
				{
					Languages.sendMessage(p, Languages.getLine("BONUS_OFF"));
				}
				else
				{
					Languages.sendMessage(p, Languages.getLine("BONUS_ON"));
				}
			}
		}
		else
		{
			player_toggled.put(p.getName(), false);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e)
	{
		Entity damager = e.getDamager();
		Random random = new Random();
		try
		{
			if (damager instanceof Player)
			{
				Player p = (Player) damager;
				if (player_toggled.get(p.getName()))
				{
					int level = p.getLevel();

					int damageChance = random.nextInt(100);
					int damage = 0;
					if (damageChance > 51)
					{
						for (Entry<Integer, Integer> entry : Bonuses.plugin_bonuses_start
								.entrySet())
						{
							int key = entry.getKey();
							int value = entry.getValue();
							if (Bonuses.plugin_bonuses_max.get(key) != null)
							{
								if (level >= value)
								{
									if (Bonuses.plugin_bonuses_max.get(key) == -1)
									{
										damage = (int) (e.getDamage() + value);
									}
									else if (level <= Bonuses.plugin_bonuses_max
											.get(key))
									{
										damage = (int) (e.getDamage() + value);
									}
								}
							}
						}
					}
					else
					{
						damage = (int) e.getDamage();
					}
					e.setDamage(damage);
					if (Bonuses.plugin_options.get("xp_drain") == true)
					{
						ExperienceManager expMan = new ExperienceManager(p);
						int xpChance = random.nextInt(100);
						if (xpChance > 0 && xpChance < 51)
						{
							expMan.changeExp(-damage);
						}
						else if (xpChance == 99)
						{
							expMan.changeExp(-(damage * 2));
						}
					}
				}
				else
				{
					e.setDamage(e.getDamage());
				}
			}
		}
		catch (NullPointerException npe)
		{
			npe.printStackTrace();
		}
	}

	@EventHandler
	public void onLevelChange(PlayerLevelChangeEvent e)
	{
		Player p = e.getPlayer();
		for (Entry<Integer, Integer> entry : Bonuses.plugin_bonuses_start
				.entrySet())
		{
			int key = entry.getKey();
			if (e.getNewLevel() == key)
			{
				if (e.getNewLevel() > e.getOldLevel())
				{
					if (p.hasPermission("xpstrength.bonus"))
					{
						Languages
								.sendMessage(
										p,
										Languages
												.getLine("LEVEL_GAINED")
												.replace("%lvl%", "" + 20)
												.replace(
														"%bns%",
														Bonuses.getNameForDamage(Bonuses.plugin_bonuses_start
																.get(20))));
					}
				}
				else
				{
					if (p.hasPermission("xpstrength.bonus"))
					{
						Languages
								.sendMessage(
										p,
										Languages
												.getLine("LEVEL_LOST")
												.replace("%lvl%",
														"" + e.getNewLevel())
												.replace(
														"%bns%",
														Bonuses.getNameForDamage(Bonuses.plugin_bonuses_start.get(e
																.getNewLevel()))));
					}
				}
				break;
			}
		}
		if (p.getLevel() < Bonuses.lowestLevel
				&& player_toggled.get(p.getName()) == true)
		{
			if (p.hasPermission("xpstrength.bonus"))
			{
				Languages.sendMessage(
						p,
						Languages.getLine("TOO_LOW").replace("%lvl%",
								"" + Bonuses.lowestLevel));
			}
			player_toggled.put(p.getName(), false);
		}

	}

}
