package com.github.lockoct.entity;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class MarkData {
    private Player player;
    private LocalDateTime markStartTime;
    private Location markPoint1;
    private Location markPoint2;
    private int saveTaskId;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getMarkStartTime() {
        return markStartTime;
    }

    public void setMarkStartTime(LocalDateTime markStartTime) {
        this.markStartTime = markStartTime;
    }

    public Location getMarkPoint1() {
        return markPoint1;
    }

    public void setMarkPoint1(Location markPoint1) {
        this.markPoint1 = markPoint1;
    }

    public Location getMarkPoint2() {
        return markPoint2;
    }

    public void setMarkPoint2(Location markPoint2) {
        this.markPoint2 = markPoint2;
    }

    public int getSaveTaskId() {
        return saveTaskId;
    }

    public void setSaveTaskId(int saveTaskId) {
        this.saveTaskId = saveTaskId;
    }

}
