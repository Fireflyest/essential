package org.fireflyest.essential.bean;

import org.fireflyest.craftdatabase.annotation.Column;
import org.fireflyest.craftdatabase.annotation.Primary;
import org.fireflyest.craftdatabase.annotation.Table;

@Table("essential_steve")
public class Steve {
    
    @Primary
    @Column
    private String uid;

    @Column
    private String name;

    @Column(defaultValue = "")
    private String password;

    @Column(defaultValue = "")
    private String email;

    @Column
    private long register;

    @Column(defaultValue = "0")
    private double money;

    @Column
    private String prefix;

    @Column
    private String quit;

    /**
     * 玩家数据
     */
    public Steve() {
    }

    public Steve(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getRegister() {
        return register;
    }

    public void setRegister(long register) {
        this.register = register;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getQuit() {
        return quit;
    }

    public void setQuit(String quit) {
        this.quit = quit;
    }

}
