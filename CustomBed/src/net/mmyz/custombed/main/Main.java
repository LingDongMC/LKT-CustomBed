package net.mmyz.custombed.main;

import net.mmyz.custombed.command.MyCommand;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	@Override
	public void onEnable(){
		getLogger().info("≤Âº˛∆Ù”√");
		this.getCommand("custombed").setExecutor(new MyCommand());
	}
}
