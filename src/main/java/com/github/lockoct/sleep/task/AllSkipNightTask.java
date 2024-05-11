package com.github.lockoct.sleep.task;

import com.github.lockoct.Main;
import com.github.lockoct.sleep.listener.SleepListener;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class AllSkipNightTask extends BukkitRunnable {
    private final World world;
    private int countdown = 16;

    public AllSkipNightTask(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        if (countdown == 0) {
            cancel();
            SleepListener.setAllSkipNightTask(null);
            // 在异步方法中调用Bukkit API，必须在里面多套一层同步
            new BukkitRunnable() {
                @Override
                public void run() {
                    SleepListener.skipNight(world);
                }
            }.runTask(Main.plugin);
        } else {
            countdown--;
        }
    }
}
