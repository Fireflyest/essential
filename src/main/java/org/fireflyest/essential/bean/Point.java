package org.fireflyest.essential.bean;

import org.fireflyest.craftdatabase.annotation.Column;
import org.fireflyest.craftdatabase.annotation.Primary;
import org.fireflyest.craftdatabase.annotation.Table;

@Table("essential_point")
public class Point {
    
    @Primary
    @Column
    private String name;

    @Column
    private String loc;

    @Column
    private int cost;

    public Point() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    

}
