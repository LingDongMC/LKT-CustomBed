package com.github.lockoct.sleep.task;

import com.github.lockoct.sleep.listener.SleepListener;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SingleSkipNightTask extends BukkitRunnable {
    private final World world;
    private final Player initiator;
    private int countdown = 16;

    public SingleSkipNightTask(World world, Player initiator) {
        this.world = world;
        this.initiator = initiator;
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        if (countdown == 0) {
            cancel();
            SleepListener.setSingleSkipNightTask(null);
            SleepListener.skipNight(world);
        } else {
            countdown--;
        }
    }

    public void interrupt(Player player) {
        if (player.equals(initiator)) {
            cancel();
        }
    }
}
