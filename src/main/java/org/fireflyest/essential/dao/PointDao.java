package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Point;

@Dao("org.fireflyest.essential.bean.Point")
public interface PointDao {

    /**
     * 新增点
     * @param name 名称
     * @param loc 位置
     * @param cost 花费
     */
    @Insert("INSERT INTO `essential_point` (`name`,`loc`,`cost`) VALUES ('${name}','${loc}','${cost}');")
    long insertPoint(String name, String loc, int cost);

    /**
     * 获取特定点
     * @param name 名称
     * @return 家
     */
    @Select("SELECT * FROM `essential_point` WHERE `name`='${name}';")
    Point selectPoint(String name);

    /**
     * 更新位置
     * @param loc 位置
     * @param name 名称
     */
    @Update("UPDATE `essential_point` SET `loc`='${loc}' WHERE `name`='${name}';")
    long updatePoint(String loc, String name);

}
