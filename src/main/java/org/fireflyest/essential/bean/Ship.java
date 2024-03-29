package org.fireflyest.essential.bean;

import org.fireflyest.craftdatabase.annotation.Column;
import org.fireflyest.craftdatabase.annotation.Primary;
import org.fireflyest.craftdatabase.annotation.Skip;
import org.fireflyest.craftdatabase.annotation.Table;

@Table("essential_ship")
public class Ship {
    
    @Primary
    @Column
    private String bond;
    @Column
    private String tag;
    @Column
    private String request;
    @Column
    private String target;
    // 亲密度
    @Column(defaultValue = "0")
    private int level;
    // 开始时间
    @Column
    private long outset;
    @Column
    private boolean intimate;

    // 用来在好友列表排位
    @Skip
    private int pos;

    public String getBond() {
        return bond;
    }
    public void setBond(String bond) {
        this.bond = bond;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getRequest() {
        return request;
    }
    public void setRequest(String request) {
        this.request = request;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public long getOutset() {
        return outset;
    }
    public void setOutset(long outset) {
        this.outset = outset;
    }
    public boolean isIntimate() {
        return intimate;
    }
    public void setIntimate(boolean intimate) {
        this.intimate = intimate;
    }
    public int getPos() {
        return pos;
    }
    public void setPos(int pos) {
        this.pos = pos;
    }

}
