package com.jamesmonger.XPStrength;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import com.jamesmonger.XPStrength.handlers.Commands;
import com.jamesmonger.XPStrength.handlers.Events;
import com.jamesmonger.XPStrength.util.Bonuses;
import com.jamesmonger.XPStrength.util.Languages;

public class XPStrength extends JavaPlugin
{

	public void onEnable()
	{
		new Events(this);
		getCommand("xpbonus").setExecutor(new Commands(this));
		try
		{
			File file = new File("" + getDataFolder());
			if (!file.exists())
			{
				file.mkdir();
			}
			file = new File("" + getDataFolder() + "/users/");
			if (!file.exists())
			{
				file.mkdir();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Bonuses.loadOptionsAndBonuses(this);
		Languages.loadLanguages(this);
	}

	public void onDisable()
	{

	}

}
