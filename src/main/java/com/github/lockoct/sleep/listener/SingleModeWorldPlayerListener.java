package com.github.lockoct.sleep.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class SingleModeWorldPlayerListener implements Listener {
    @EventHandler
    public void onDead(PlayerDeathEvent e) {
        Player player = e.getEntity();
        SleepListener.getSleepingPlayer().remove(player);
        SleepListener.interruptSkipNight(player);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (SleepListener.getSleepingPlayer().get(player) != null) {
                SleepListener.wakeUp(player);
            }
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        World world = e.getFrom();
        if (world.getEnvironment() == World.Environment.NORMAL) {
            HashMap<Player, Location> sleepingPlayer = SleepListener.getSleepingPlayer();
            if (sleepingPlayer.get(player) != null) {
                sleepingPlayer.remove(player);
                SleepListener.interruptSkipNight(player);
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        SleepListener.getSleepingPlayer().remove(player);
        SleepListener.interruptSkipNight(player);
    }
}
