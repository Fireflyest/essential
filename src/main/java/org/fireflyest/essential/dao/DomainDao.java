package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Delete;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Domain;

@Dao("org.fireflyest.essential.bean.Domain")
public interface DomainDao {

    @Insert("INSERT INTO `essential_domain` (`name`,`owner`,`world`,`outset`,`center`,`plots`) VALUES ('${name}','${owner}','${world}','${outset}','${center}','${plots}');")
    long insertDomain(String name, String owner, String world, long outset, String center, String plots);

    @Select("SELECT * FROM `essential_domain` WHERE `world`='${world}';")
    Domain[] selectDomainsByWorld(String world);

    @Select("SELECT * FROM `essential_domain` WHERE `owner`='${owner}';")
    Domain[] selectDomainsByPlayer(String owner);

    @Select("SELECT `name` FROM `essential_domain` WHERE `owner`='${owner}';")
    String[] selectDomainsNameByPlayer(String owner);

    @Select("SELECT * FROM `essential_domain` WHERE `name`='${name}';")
    Domain selectDomainByName(String name);

    @Select("SELECT `owner` FROM `essential_domain` WHERE `name`='${name}';")
    String selectDomainOwner(String name);

    @Select("SELECT `world` FROM `essential_domain` WHERE `name`='${name}';")
    String selectDomainWorld(String name);

    @Update("UPDATE `essential_domain` SET `plots`='${plots}' WHERE `name`='${name}';")
    long updateDomainPlots(String plots, String name);

    @Update("UPDATE `essential_domain` SET `owner`='${owner}' WHERE `name`='${name}';")
    long updateDomainOwner(String owner, String name);

    @Update("UPDATE `essential_domain` SET `level`=`level`+1 WHERE `name`='${name}';")
    long domainLevelUp(String name);

    @Update("UPDATE `essential_domain` SET `level`=`level`-1 WHERE `name`='${name}';")
    long domainLevelDown(String name);

    @Update("UPDATE `essential_domain` SET `center`='${center}' WHERE `name`='${name}';")
    long updateDomainCenter(String center, String name);

    @Update("UPDATE `essential_domain` SET `globe`='${globe}' WHERE `name`='${name}';")
    long updateDomainGlobe(long globe, String name);

    @Update("UPDATE `essential_domain` SET `friend`='${friend}' WHERE `name`='${name}';")
    long updateDomainFriend(long friend, String name);

    @Update("UPDATE `essential_domain` SET `intimate`='${intimate}' WHERE `name`='${name}';")
    long updateDomainIntimate(long intimate, String name);

    @Update("UPDATE `essential_domain` SET `name`='${newName}' WHERE `name`='${name}';")
    long updateDomainName(String newName, String name);

    @Update("UPDATE `essential_domain` SET `share`='${share}' WHERE `name`='${name}';")
    long updateDomainShare(String share, String name);

    @Update("UPDATE `essential_domain` SET `type`=${type} WHERE `name`='${name}';")
    long updateDomainType(int type, String name);

    @Delete("DELETE FROM `essential_domain` WHERE `name`='${name}';")
    long deleteDomain(String name);

}
