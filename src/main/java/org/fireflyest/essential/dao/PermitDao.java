package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Delete;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Permit;

@Dao("org.fireflyest.essential.bean.Permit")
public interface PermitDao {
    
    @Insert("INSERT INTO `essential_permission` (`owner`,`name`,`value`,`world`,`deadline`) VALUES ('${owner}','${name}',${value},'${world}',${deadline});")
    long insertPermit(String owner, String name, boolean value, String world, long deadline);

    @Select("SELECT * FROM `essential_permission` WHERE `owner`='${owner}';")
    Permit[] selectPermits(String owner);

    @Select("SELECT * FROM `essential_permission` WHERE `owner`='${owner}' AND `name`='${name}';")
    Permit selectPermit(String owner, String name);

    @Update("UPDATE `essential_permission` SET `deadline`=${deadline} WHERE `id`=${id};")
    long updatePermit(long deadline, long id);

    @Delete("DELETE FROM `essential_permission` WHERE `owner`='${owner}' AND `name`='${name}';")
    long deletePermit(String owner, String name);

}
