package org.fireflyest.essential.bean;

import org.fireflyest.craftdatabase.annotation.Column;
import org.fireflyest.craftdatabase.annotation.ID;
import org.fireflyest.craftdatabase.annotation.Table;

@Table("essential_prefix")
public class Prefix {

    @ID
    @Column
    private int id;

    @Column
    private String owner;

    @Column
    private String value;

    @Column
    private long deadline;

    public Prefix() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    

}
