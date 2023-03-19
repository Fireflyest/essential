package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Delete;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Ship;

@Dao("org.fireflyest.essential.bean.Ship")
public interface ShipDao {
    
    /**
     * 插入新的好友关系
     * @param bond 主&客
     * @param tag 标签
     * @param outset 开端
     * @return 无效数字
     */
    @Insert("INSERT INTO `essential_ship` (`bond`,`tag`,`request`,`target`,`outset`) VALUES ('${bond}','${tag}','${request}','${target}',${outset});")
    long insertShip(String bond, String tag, String request, String target, long outset);

    /**
     * 获取关系
     * @param bond 关系键
     * @return 关系
     */
    @Select("SELECT * FROM `essential_ship` WHERE `bond`='${bond}';")
    Ship selectShip(String bond);

    /**
     * 查询好友关系或好友申请
     * @param uid 玩家uid
     * @param target 玩家名称
     * @return 好友关系
     */
    @Select("SELECT * FROM `essential_ship` WHERE `target`='${target}' AND `tag`='' OR `bond` LIKE '${uid}&%';")
    Ship[] selectShips(String target, String uid);

    @Update("UPDATE `essential_ship` SET `level`=`level`+${level} WHERE `bond`='${bond}';")
    long updateShipLevel(int level, String bond);

    /**
     * 更新关系标签
     * @param tag 标签
     * @param bond 关系
     * @return 更新条目
     */
    @Update("UPDATE `essential_ship` SET `tag`='${tag}' WHERE `bond`='${bond}';")
    long updateShipTag(String tag, String bond);

    @Update("UPDATE `essential_ship` SET `request`='${request}' WHERE `bond`='${bond}';")
    long updateShipRequest(String request, String bond);

    @Delete("DELETE FROM `essential_ship` WHERE `bond`='${bond}';")
    long deleteShip(String bond);

}
