package org.fireflyest.essential.world;

import org.fireflyest.essential.bean.Domain;

public class Plot {
    
    // 世界和区块坐标
    private String loc;

    private int x;

    private int z;

    // 所属领地
    private Domain domain;

    public Plot() {
    }

    public Plot(String loc, int x, int z, Domain domain) {
        this.loc = loc;
        this.x = x;
        this.z = z;
        this.domain = domain;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public boolean in(int px, int pz) {
        return (x * 16 < px && z * 16 < pz && (x+1) * 16 > px && (z+1) * 16 > pz);
    }

}
