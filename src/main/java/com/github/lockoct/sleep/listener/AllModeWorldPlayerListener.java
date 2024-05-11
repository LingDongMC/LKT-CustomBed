package com.github.lockoct.sleep.listener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class AllModeWorldPlayerListener implements Listener {
    @EventHandler
    public void onDead(PlayerDeathEvent e) {
        Player player = e.getEntity();
        SleepListener.getSleepingPlayer().remove(player);
        SleepListener.trySkipNight(player.getWorld(), null);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            // 这里使用get而不用containsKey是因为要排除掉使用普通床的玩家
            // 使用普通床的玩家收到伤害会自动起床，并发出事件，发出事件后会自动调用wakeUp
            // 就不需要走这里了，这里仅针对使用自定义床的玩家
            if (SleepListener.getSleepingPlayer().get(player) != null) {
                SleepListener.wakeUp(player);
            }
        }
    }

    @EventHandler
    public void onSpawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        SleepListener.interruptSkipNight(null);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
            SleepListener.interruptSkipNight(null);
            return;
        }

        World fromWorld = e.getFrom();
        if (fromWorld.getEnvironment() == World.Environment.NORMAL) {
            SleepListener.getSleepingPlayer().remove(player);
            SleepListener.trySkipNight(fromWorld, null);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        SleepListener.interruptSkipNight(null);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        SleepListener.getSleepingPlayer().remove(player);
        SleepListener.trySkipNight(player.getWorld(), null);
    }
}
