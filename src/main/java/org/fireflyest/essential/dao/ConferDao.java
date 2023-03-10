package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Delete;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Confer;

@Dao("org.fireflyest.essential.bean.Confer")
public interface ConferDao {
    
    @Insert("INSERT INTO `essential_group` (`owner`,`group`,`world`,`deadline`) VALUES ('${owner}','${group}','${world}','${deadline}');")
    long insertConfer(String owner, String group, String world, long deadline);

    @Select("SELECT * FROM `essential_group` WHERE `owner`='${owner}';")
    Confer[] selectConfers(String owner);

    @Select("SELECT * FROM `essential_group` WHERE `owner`='${owner}' AND  `group`='${group}';")
    Confer selectConfer(String owner, String group);

    @Update("UPDATE `essential_group` SET `deadline`=${deadline} WHERE `id`=${id};")
    long updateConfer(long deadline, long id);

    @Delete("DELETE FROM `essential_group` WHERE `owner`='${owner}' AND `group`='${group}';")
    long deleteConfer(String owner, String group);

}
