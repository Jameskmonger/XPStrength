package com.jamesmonger.XPStrength.handlers;

import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import com.jamesmonger.XPStrength.XPStrength;
import com.jamesmonger.XPStrength.util.*;

public class Events implements Listener
{

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
		XPStrength.accountManager.savePlayer(p);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		XPStrength.accountManager.loadPlayer(p);
	}

	@EventHandler
	public void onExpChange(PlayerExpChangeEvent event)
	{
		Player player = event.getPlayer();
		if (XPStrength.levelCap > -1)
		{
			if (event.getAmount() > 0)
			{
				if (player.getLevel() >= XPStrength.levelCap)
				{
					if (!player.hasPermission("xpstrength.bypassCap") && !player.hasPermission("xpstrength.bypass.cap"))
					{
						event.setAmount(0);
						player.setExp(0.0F);
					}
				}
			}
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
				if (XPStrength.player_toggled.get(p.getName()))
				{
					int level = p.getLevel();

					int damageChance = random.nextInt(100);
					int damage = 0;
					if (damageChance > 51)
					{
						for (Entry<Integer, Integer> entry : Settings.plugin_bonuses_start
								.entrySet())
						{
							int key = entry.getKey();
							int value = entry.getValue();
							if (Settings.plugin_bonuses_max.get(key) != null)
							{
								if (level >= value)
								{
									if (Settings.plugin_bonuses_max.get(key) == -1)
									{
										damage = (int) (e.getDamage() + value);
									}
									else if (level <= Settings.plugin_bonuses_max
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
					e.setDamage((double) damage);
					
					if (XPStrength.xpDrain == true)
					{
						if (!p.hasPermission("xpstrength.bypass.drain"))
						{
							for(int i = 0; i < damage; i++)
							{
								int currentDamageDealt = XPStrength.player_damage_dealt.get(p.getName());
								currentDamageDealt++;
								
								if(currentDamageDealt == XPStrength.drainRate)
								{
									XPStrength.player_damage_dealt.put(p.getName(), 0);
									p.setTotalExperience(p.getTotalExperience() - 1);
								}
								else
								{
									XPStrength.player_damage_dealt.put(p.getName(), currentDamageDealt);
								}
							}
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
		for (Entry<Integer, Integer> entry : Settings.plugin_bonuses_start
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
														Settings.getNameForDamage(Settings.plugin_bonuses_start
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
														Settings.getNameForDamage(Settings.plugin_bonuses_start.get(e
																.getNewLevel()))));
					}
				}
				break;
			}
		}
		if (p.getLevel() < Settings.lowestLevel
				&& XPStrength.player_toggled.get(p.getName()) == true)
		{
			if (p.hasPermission("xpstrength.bonus"))
			{
				Languages.sendMessage(
						p,
						Languages.getLine("TOO_LOW").replace("%lvl%",
								"" + Settings.lowestLevel));
			}
			XPStrength.player_toggled.put(p.getName(), false);
		}

	}

}
