package org.fireflyest.essential.bean;

import java.util.ArrayList;
import java.util.List;

import org.fireflyest.craftdatabase.annotation.Column;
import org.fireflyest.craftdatabase.annotation.Primary;
import org.fireflyest.craftdatabase.annotation.Skip;
import org.fireflyest.craftdatabase.annotation.Table;
import org.fireflyest.essential.world.Plot;

@Table("essential_domain")
public class Domain {
    
    @Primary
    @Column
    private String name;

    @Column
    private String owner;

    @Column
    private String world;

    @Column
    private long outset;

    @Column(defaultValue = "0")
    private int level;

    @Column(defaultValue = "0")
    private int type;

    @Column
    private String center;

    @Column
    private String plots;

    @Skip
    private final List<Plot> plotList = new ArrayList<>();

    @Column(defaultValue = "0")
    private boolean finish;

    @Column
    private String msg;

    @Column(defaultValue = "0")
    private long globe;

    @Column(defaultValue = "1927")
    private long friend;

    @Column(defaultValue = "2047")
    private long intimate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }
    
    public long getOutset() {
        return outset;
    }

    public void setOutset(long outset) {
        this.outset = outset;
    }
    
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getPlots() {
        return plots;
    }

    public void setPlots(String plots) {
        this.plots = plots;
    }

    public List<Plot> getPlotList() {
        return plotList;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getGlobe() {
        return globe;
    }

    public void setGlobe(long globe) {
        this.globe = globe;
    }

    public long getFriend() {
        return friend;
    }

    public void setFriend(long friend) {
        this.friend = friend;
    }

    public long getIntimate() {
        return intimate;
    }

    public void setIntimate(long intimate) {
        this.intimate = intimate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
