package org.fireflyest.essential.dao;

import org.fireflyest.craftdatabase.annotation.Dao;
import org.fireflyest.craftdatabase.annotation.Insert;
import org.fireflyest.craftdatabase.annotation.Select;
import org.fireflyest.craftdatabase.annotation.Update;
import org.fireflyest.essential.bean.Steve;

@Dao("org.fireflyest.essential.bean.Steve")
public interface SteveDao {
    
    /**
     * 唯一id选择
     * @param uid 唯一id
     * @return 玩家数据
     */
    @Select("SELECT * FROM `essential_steve` WHERE `uid`='${uid}';")
    Steve selectSteveByUid(String uid);

    /**
     * 名称选择
     * @param name 名称
     * @return 玩家数据
     */
    @Select("SELECT * FROM `essential_steve` WHERE `name`='${name}' LIMIT 1;")
    Steve selectSteveByName(String name);

    /**
     * 获取uid
     * @param name 游戏名
     * @return uid
     */
    @Select("SELECT `uid` FROM `essential_steve` WHERE `name`='${name}' LIMIT 1;")
    String selectSteveUid(String name);

    /**
     * 获取名称
     * @param uid uid
     * @return 游戏名
     */
    @Select("SELECT `name` FROM `essential_steve` WHERE `uid`='${uid}';")
    String selectSteveName(String uid);

    /**
     * 插入玩家数据
     * @return id
     */
    @Insert("INSERT INTO `essential_steve` (`name`,`uid`,`register`,`prefix`,`money`,`legal`) VALUES ('${name}','${uid}',${register},'${prefix}',${money},${legal});")
    long insertSteve(String name, String uid, long register, String prefix, int money, boolean legal);

    /**
     * 更新离开位置
     * @param uid 玩家id
     * @param quit 离开位置
     */
    @Update("UPDATE `essential_steve` SET `quit`='${quit}' WHERE `uid`='${uid}';")
    long updateQuit(String quit, String uid);

    /**
     * 获取离开位置
     * @param uid 玩家id
     * @return 位置
     */
    @Select("SELECT `quit` FROM `essential_steve` WHERE `uid`='${uid}';")
    String selectQuit(String uid);

    /**
     * 更新密码
     * @param uid 玩家id
     * @param password 离开位置
     */
    @Update("UPDATE `essential_steve` SET `password`='${password}' WHERE `uid`='${uid}';")
    long updatePassword(String password, String uid);

    /**
     * 获取密码
     * @param uid 玩家id
     * @return 密码
     */
    @Select("SELECT `password` FROM `essential_steve` WHERE `uid`='${uid}';")
    String selectPassword(String uid);

    /**
     * 更新邮箱
     * @param uid 玩家id
     * @param email 离开位置
     */
    @Update("UPDATE `essential_steve` SET `email`='${email}' WHERE `uid`='${uid}';")
    long updateEmail(String email, String uid);

    /**
     * 获取邮箱
     * @param uid 玩家id
     * @return 邮箱
     */
    @Select("SELECT `email` FROM `essential_steve` WHERE `uid`='${uid}';")
    String selectEmail(String uid);

    /**
     * 更新钱
     * @param symbol 修改符号
     * @param money 修改数量
     * @param uid 玩家id 
     */
    @Update("UPDATE `essential_steve` SET `money`=`money`${symbol}${money} WHERE `uid`='${uid}';")
    long updateMoney(String symbol, double money, String uid);

    /**
     * 获取钱
     * @param uid 玩家id
     * @return 钱
     */
    @Select("SELECT `money` FROM `essential_steve` WHERE `uid`='${uid}';")
    double selectMoney(String uid);

    /**
     * 更新钱
     * @param symbol 修改符号
     * @param money 修改数量
     * @param name 游戏名
     */
    @Update("UPDATE `essential_steve` SET `money`=`money`${symbol}${money} WHERE `name`='${name}';")
    long updateMoneyByName(String symbol, double money, String name);

    /**
     * 获取钱
     * @param name 游戏名
     * @return 钱
     */
    @Select("SELECT `money` FROM `essential_steve` WHERE `name`='${name}' LIMIT 1;")
    double selectMoneyByName(String name);

    /**
     * 获取头衔
     * @param uid uid
     * @return 头衔
     */
    @Select("SELECT `prefix` FROM `essential_steve` WHERE `uid`='${uid}';")
    String selectPrefix(String uid);

    /**
     * 更新头衔
     * @param prefix 头衔
     * @param uid uid
     * @return 更新条目
     */
    @Update("UPDATE `essential_steve` SET `prefix`='${prefix}' WHERE `uid`='${uid}';")
    long updatePrefix(String prefix, String uid);

    /**
     * 获取性别
     * @param uid uid
     * @return 性别
     */
    @Select("SELECT `gender` FROM `essential_steve` WHERE `uid`='${uid}';")
    int selectGender(String uid);

    /**
     * 更新性别
     * @param gender 性别
     * @param uid uid
     * @return 更新条目
     */
    @Update("UPDATE `essential_steve` SET `gender`=${gender} WHERE `uid`='${uid}';")
    long updateGender(int gender, String uid);
    
    @Select("SELECT `legal` FROM `essential_steve` WHERE `uid`='${uid}';")
    boolean selectLegal(String uid);

    @Update("UPDATE `essential_steve` SET `legal`=${legal} WHERE `uid`='${uid}';")
    long updateLegal(boolean legal, String uid);

    @Update("UPDATE `essential_steve` SET `uid`=${newUid} WHERE `uid`='${uid}';")
    long updateSteveUid(String newUid, String uid);

    @Update("UPDATE `essential_steve` SET `name`=${name} WHERE `uid`='${uid}';")
    long updateSteveName(String name, String uid);

}
