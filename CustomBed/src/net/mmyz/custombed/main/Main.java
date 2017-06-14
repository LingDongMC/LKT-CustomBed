package net.mmyz.custombed.main;

import net.mmyz.custombed.command.MyCommand;
import net.mmyz.custombed.eventlistener.RecordBedLocation;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	@Override
	public void onEnable(){
		getLogger().info("插件启动");
		this.getCommand("custombed").setExecutor(new MyCommand());
		Bukkit.getPluginManager().registerEvents(new RecordBedLocation(),this);
	}
}
