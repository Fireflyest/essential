package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Delete;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Home;

@Dao("org.fireflyest.essential.bean.Home")
public interface HomeDao {
    
    /**
     * 删除家
     * @param owner 主人
     * @param name 名称
     * @return 删除个数
     */
    @Delete("DELETE FROM `essential_home` WHERE `owner`='${owner}' AND `name`='${name}';")
    long deleteHome(String owner, String name);

    /**
     * 新增家
     * @param owner 主人
     * @param name 名称
     * @param loc 位置
     * @return id
     */
    @Insert("INSERT INTO `essential_home` (`owner`,`name`,`loc`) VALUES ('${owner}','${name}','${loc}');")
    long insertHome(String owner, String name, String loc);

    /**
     * 获取特定家
     * @param owner 主人
     * @param name 名称
     * @return 家
     */
    @Select("SELECT * FROM `essential_home` WHERE `owner`='${owner}' AND `name`='${name}';")
    Home selectHome(String owner, String name);

    /**
     * 获取某玩家所有家
     * @param owner 主人
     * @return 家列表
     */
    @Select("SELECT * FROM `essential_home` WHERE `owner`='${owner}';")
    Home[] selectHomes(String owner);

    /**
     * 更新家的位置
     * @param loc 位置
     * @param owner 主人
     * @param name 名称
     */
    @Update("UPDATE `essential_home` SET `loc`='${loc}' WHERE `owner`='${owner}' AND `name`='${name}';")
    long updateHome(String loc, String owner, String name);

}
