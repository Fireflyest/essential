package org.fireflyest.essential.bean;

import org.fireflyest.craftdatabase.annotation.Column;
import org.fireflyest.craftdatabase.annotation.Primary;
import org.fireflyest.craftdatabase.annotation.Table;

@Table("essential_plot")
public class Plot {
    
    // 世界和区块坐标
    @Primary
    private String loc;

    // 区块类型
    @Column(defaultValue = "")
    private String type;

    @Column(defaultValue = "")
    private String owner;

    // 所属领地
    @Column
    private int domain;

    public Plot() {
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getDomain() {
        return domain;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    

}
