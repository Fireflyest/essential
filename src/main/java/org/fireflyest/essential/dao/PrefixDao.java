package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Delete;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Prefix;

@Dao("org.fireflyest.essential.bean.Prefix")
public interface PrefixDao {
    
    @Insert("INSERT INTO `essential_prefix` (`owner`,`value`,`deadline`) VALUES ('${owner}','${value}',${deadline});")
    long insertPrefix(String owner, String value, long deadline);

    @Select("SELECT * FROM `essential_prefix` WHERE `owner`='${owner}';")
    Prefix[] selectPrefixs(String owner);

    @Select("SELECT * FROM `essential_prefix` WHERE `id`=${id};")
    Prefix selectPrefix(long id);

    @Select("SELECT * FROM `essential_prefix` WHERE `owner`='${owner}' AND `value`='${value}';")
    Prefix selectPrefix(String owner, String value);

    @Update("UPDATE `essential_prefix` SET `deadline`=${deadline} WHERE `id`=${id};")
    long updatePrefix(long deadline, long id);

    @Delete("DELETE FROM `essential_prefix` WHERE `id`=${id};")
    long deletePrefix(long id);

}
