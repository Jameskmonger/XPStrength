package com.jamesmonger.XPStrength.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.jamesmonger.XPStrength.XPStrength;

public class Languages
{

	public static Map<String, String> language_strings = new HashMap<String, String>();

	public static void loadLanguages(XPStrength xps)
	{
		try
		{
			File file = new File(xps.getDataFolder(), "lang.txt");
			if (!file.exists())
			{
				FileOutputStream fos = new FileOutputStream(file);
				fos.flush();
				fos.close();
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));

				String a;

				a = "You have reached level %lvl% and will now hit %bns% extra heart(s) of damage. Use /xpbonus to toggle.";
				bw.write("LEVEL_GAINED : " + a);
				language_strings.put("LEVEL_GAINED", a);
				bw.newLine();

				a = "You are now level %lvl% and will only hit %bns% extra heart(s) of damage. Use /xpbonus to toggle.";
				bw.write("LEVEL_LOST : " + a);
				language_strings.put("LEVEL_LOST", a);
				bw.newLine();

				a = "You are below level %lvl% and therefore your level cannot affect your strength.";
				bw.write("TOO_LOW : " + a);
				language_strings.put("TOO_LOW", a);
				bw.newLine();

				a = "Your level is currently affecting your strength. Use /xpbonus to toggle.";
				bw.write("BONUS_ON : " + a);
				language_strings.put("BONUS_ON", a);
				bw.newLine();

				a = "Your level is not currently affecting your strength. Use /xpbonus to toggle.";
				bw.write("BONUS_OFF : " + a);
				language_strings.put("BONUS_OFF", a);
				bw.newLine();

				a = "Your level will now influence your strength. However, you will lose xp when you hit enemies.";
				bw.write("BONUS_NOW_ON_DRAIN : " + a);
				language_strings.put("BONUS_NOW_ON_DRAIN", a);
				bw.newLine();

				a = "Your level will now influence your strength.";
				bw.write("BONUS_NOW_ON_NODRAIN : " + a);
				language_strings.put("BONUS_NOW_ON_NODRAIN", a);
				bw.newLine();

				a = "Your level will no longer influence your strength and you will not lose xp anymore.";
				bw.write("BONUS_NOW_OFF_DRAIN : " + a);
				language_strings.put("BONUS_NOW_OFF_DRAIN", a);
				bw.newLine();

				a = "Your level will no longer influence your strength.";
				bw.write("BONUS_NOW_OFF_NODRAIN : " + a);
				language_strings.put("BONUS_NOW_OFF_DRAIN", a);
				bw.newLine();

				a = "Your level was affecting your strength, but you no longer have the permission.";
				bw.write("NO_MORE_PERMISSION : " + a);
				language_strings.put("NO_MORE_PERMISSION", a);
				bw.newLine();

				a = "You do not have the required permission.";
				bw.write("NO_PERMISSION : " + a);
				language_strings.put("NO_PERMISSION", a);
				bw.newLine();

				bw.flush();
				bw.close();
			}
			else
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				String l;
				while ((l = br.readLine()) != null)
				{
					String[] tokens = l.split(" : ");
					language_strings.put(tokens[0], tokens[1]);
				}
				br.close();// Close the reader
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public static String getLine(String ref)
	{
		if (language_strings.get(ref) != null)
		{
			return language_strings.get(ref);
		}
		else
		{
			return "Language error: '" + ref + "'";
		}
	}

	public static void sendMessage(Player p, String msg)
	{
		p.sendMessage(ChatColor.LIGHT_PURPLE + "[XPStrength] "
				+ ChatColor.WHITE + msg);
	}

}
