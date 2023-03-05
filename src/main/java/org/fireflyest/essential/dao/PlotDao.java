package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Plot;

@Dao("org.fireflyest.essential.bean.Plot")
public interface PlotDao {
    
    /**
     * 新地块
     * @param loc 位置
     * @param type 地块类型
     * @param owner 归属
     * @param domain 领地
     * @return 0
     */
    @Insert("INSERT INTO `essential_plot` (`loc`,`type`,`owner`,`domain`) VALUES ('${loc}','${type}','${owner},'${domain}');")
    long insertPlot(String loc, String type, String owner, int domain);

    /**
     * 获取地块
     * @param loc 位置
     * @return 地块
     */
    @Select("SELECT * FROM `essential_plot` WHERE `loc`='${loc}';")
    Plot[] selectPlot(String loc);

    /**
     * 更新类型
     * @param type 类型
     * @param loc 位置
     */
    @Update("UPDATE `essential_plot` SET `type`='${type}' WHERE `loc` LIKE '${loc}%';")
    long updatePlotType(String type, String loc);

    /**
     * 更新归属
     * @param owner 归属
     * @param loc 位置
     */
    @Update("UPDATE `essential_plot` SET `owner`='${owner}' WHERE `loc` LIKE '${loc}%';")
    long updatePlotOwner(String owner, String loc);

    /**
     * 更新领地
     * @param domain 领地
     * @param loc 位置
     */
    @Update("UPDATE `essential_plot` SET `domain`='${domain}' WHERE `loc` LIKE '${loc}%';")
    long updatePlotDomain(String domain, String loc);

}
