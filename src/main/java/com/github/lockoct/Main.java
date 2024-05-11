package com.github.lockoct;

import com.github.lockoct.sleep.listener.SingleModeWorldPlayerListener;
import com.github.lockoct.sleep.listener.SleepListener;
import com.github.lockoct.bed.listener.MarkListener;
import com.github.lockoct.sleep.listener.AllModeWorldPlayerListener;
import com.github.lockoct.entity.BasePlugin;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.JarResourceLocation;

import java.io.IOException;

public class Main extends BasePlugin {
	public static Main plugin;
	public static String sleepMode;

	@Override
	public void onEnable(){
		plugin = this;

		// 初始化语言配置
		I18nUtil.init(this);

		if (DatabaseUtil.getDao() == null) {
			ColorLogUtil.logError(this, I18nUtil.getText(this, "pluginMsg.dbConnectFailed"));
			setEnabled(false);
			return;
		}

		// 保存配置文件
		saveDefaultConfig();

		// 命令注册
		PluginCommand command = getCommand("custombed");
		if (command != null) {
			CommandRouter cr = new CommandRouter();
			command.setExecutor(cr);
			command.setTabCompleter(cr);
		} else {
			ColorLogUtil.logError(this, I18nUtil.getText(this, "pluginMsg.commandConfigNotFound"));
		}

		// 监听器注册
		Bukkit.getPluginManager().registerEvents(new MarkListener(),this);
		Bukkit.getPluginManager().registerEvents(new SleepListener(),this);

		// 数据库表初始化
		initTables();

		// 获取睡觉模式
		FileConfiguration config = getConfig();
		sleepMode = config.getString("skipNight");
		if (StringUtils.equals(sleepMode, "single")) {
			Bukkit.getPluginManager().registerEvents(new SingleModeWorldPlayerListener(),this);
		} else if (StringUtils.equals(sleepMode, "all")) {
			Bukkit.getPluginManager().registerEvents(new AllModeWorldPlayerListener(),this);
		} else {
			sleepMode = "all";
			Bukkit.getPluginManager().registerEvents(new AllModeWorldPlayerListener(),this);
		}

		ColorLogUtil.logSuccess(this, I18nUtil.getText(this, "pluginMsg.enableSuccess"));
	}

	private void initTables() {
		Dao dao = DatabaseUtil.getDao();
		if (dao != null) {
			try {
				Scans.me().addResourceLocation(new JarResourceLocation(jarFile.getAbsolutePath()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 自动建表
			Daos.createTablesInPackage(dao, "com.github.lockoct.entity", false);
			// 自动迁移表结构
			Daos.migration(dao, "com.github.lockoct.entity", true, false, false);
		}
	}
}
