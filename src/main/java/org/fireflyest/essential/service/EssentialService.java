package org.fireflyest.essential.service;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.fireflyest.craftdatabase.annotation.Auto;
import org.fireflyest.craftdatabase.annotation.Service;
import org.fireflyest.craftdatabase.sql.SQLService;
import org.fireflyest.essential.bean.Home;
import org.fireflyest.essential.bean.Point;
import org.fireflyest.essential.bean.Steve;
import org.fireflyest.essential.dao.HomeDao;
import org.fireflyest.essential.dao.PointDao;
import org.fireflyest.essential.dao.SteveDao;
import org.fireflyest.util.SerializationUtil;

@Service
public class EssentialService extends SQLService {
    
    /**
     * 数据服务
     * @param url sql地址
     */
    public EssentialService(@Nonnull String url) {
        super(url);
    }

    @Auto
    public SteveDao steveDao;

    @Auto
    public HomeDao homeDao;

    @Auto
    public PointDao pointDao;

    /**
     * 查找玩家数据
     * @param uid uid
     * @return 玩家数据
     */
    public Steve selectSteveByUid(String uid) {
        return steveDao.selectSteveByUid(uid);
    }

    /**
     * 查找玩家数据
     * @param name name
     * @return 游戏名
     */
    public Steve selectSteveByName(String name) {
        return steveDao.selectSteveByName(name);
    }

    /**
     * 插入玩家数据
     * @param name 名称
     * @param uid uid
     * @param register 注册时间
     * @return id
     */
    public long insertSteve(String name, String uid, long register) {
        return steveDao.insertSteve(name, uid, register);
    }

    /**
     * 更新玩家离开位置
     * @param quit 离开位置
     * @param uid uid
     */
    public void updateQuit(Location quit, String uid) {
        steveDao.updateQuit(SerializationUtil.serialize(quit), uid);
    }

    /**
     * 查找玩家离开位置
     * @param uid uid
     * @return 玩家离开位置
     */
    public Location selectQuit(String uid) {
        String quit = steveDao.selectQuit(uid);
        if (quit == null || "".equals(quit)) {
            return null;
        }
        return SerializationUtil.deserialize(steveDao.selectQuit(uid), Location.class);
    }

    /**
     * 更新密码
     * @param password 密码
     * @param uid uid
     */
    public void updatePassword(String password, String uid) {
        steveDao.updatePassword(password, uid);
    }

    /**
     * 查找玩家密码
     * @param uid uid
     * @return 玩家密码
     */
    public String selectPassword(String uid) {
        return steveDao.selectPassword(uid);
    }

    /**
     * 更新邮箱
     * @param email 邮箱
     * @param uid uid
     */
    public void updateEmail(String email, String uid) {
        steveDao.updateEmail(email, uid);
    }

    /**
     * 查找邮箱
     * @param uid uid
     * @return 邮箱
     */
    public String selectEmail(String uid) {
        return steveDao.selectEmail(uid);
    }

    /**
     * 更新钱
     * @param symbol 符号
     * @param money 数量
     * @param uid 玩家id
     * @return 更新条目
     */
    public long updateMoney(String symbol, double money, String uid) {
        return steveDao.updateMoney(symbol, money, uid);
    }

    /**
     * 获取钱
     * @param uid 玩家id
     * @return 钱
     */
    public double selectMoney(String uid) {
        return steveDao.selectMoney(uid);
    }

    /**
     * 更新钱
     * @param symbol 符号
     * @param money 数量
     * @param name 游戏名
     * @return 更新条目
     */
    public long updateMoneyByName(String symbol, double money, String name) {
        return steveDao.updateMoneyByName(symbol, money, name);
    }

    /**
     * 获取钱
     * @param name 游戏名
     * @return 钱
     */
    public double selectMoneyByName(String name) {
        return steveDao.selectMoneyByName(name);
    }

    /*****************************************************************************/

    /**
     * 删除家
     * @param owner 主人
     * @param name 名称
     * @return 删除数量
     */
    public long deleteHome(String owner, String name) {
        return homeDao.deleteHome(owner, name);
    }

    /**
     * 插入家
     * @param owner 主人
     * @param name 名称
     * @param loc 位置
     * @return id
     */
    public long insertHome(String owner, String name, Location loc) {
        return homeDao.insertHome(owner, name, SerializationUtil.serialize(loc));
    }

    /**
     * 查找家
     * @param owner 主人
     * @param name 名称
     * @return 家
     */
    public Home selectHome(String owner, String name) {
        return homeDao.selectHome(owner, name);
    }

    /**
     * 查找家
     * @param owner 主人
     * @return 家
     */
    public Home[] selectHomes(String owner) {
        return homeDao.selectHomes(owner);
    }

    /**
     * 更新家的位置
     * @param loc 位置
     * @param owner 主人
     * @param name 名称
     * @return 更新数目
     */
    public long updateHome(Location loc, String owner, String name) {
        return homeDao.updateHome(SerializationUtil.serialize(loc), owner, name);
    }

    /*****************************************************************************/

    /**
     * 插入传送点
     * @param name 名称
     * @param loc 位置
     * @param cost 花费
     * @return id
     */
    public long insertPoint(String name, Location loc, int cost) {
        return pointDao.insertPoint(name, SerializationUtil.serialize(loc), cost);
    }

    /**
     * 查询点
     * @param name 名称
     * @return 传送点
     */
    public Point selectPoint(String name) {
        return pointDao.selectPoint(name);
    }

    /**
     * 更新点的位置
     * @param loc 位置
     * @param name 名称
     * @return 更新数
     */
    public long updatePoint(Location loc, String name) {
        return pointDao.updatePoint(SerializationUtil.serialize(loc), name);  
    }

}
