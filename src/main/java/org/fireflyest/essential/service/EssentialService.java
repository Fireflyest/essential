package org.fireflyest.essential.service;

import java.time.Instant;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.fireflyest.craftdatabase.annotation.Auto;
import org.fireflyest.craftdatabase.annotation.Service;
import org.fireflyest.craftdatabase.sql.SQLService;
import org.fireflyest.essential.bean.Confer;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.bean.Home;
import org.fireflyest.essential.bean.Permit;
import org.fireflyest.essential.bean.Point;
import org.fireflyest.essential.bean.Prefix;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.bean.Steve;
import org.fireflyest.essential.dao.ConferDao;
import org.fireflyest.essential.dao.DomainDao;
import org.fireflyest.essential.dao.HomeDao;
import org.fireflyest.essential.dao.PermitDao;
import org.fireflyest.essential.dao.PointDao;
import org.fireflyest.essential.dao.PrefixDao;
import org.fireflyest.essential.dao.ShipDao;
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

    @Auto
    public PermitDao permitDao;

    @Auto
    public ConferDao conferDao;

    @Auto
    public PrefixDao prefixDao;

    @Auto
    public ShipDao shipDao;

    @Auto
    public DomainDao domainDao;

    /**
     * 查找玩家数据
     * @param uid uid
     * @return 玩家数据
     */
    public Steve selectSteveByUid(UUID uid) {
        return steveDao.selectSteveByUid(uid.toString());
    }

    /**
     * 查找玩家数据
     * @param name name
     * @return 游戏名
     */
    public Steve selectSteveByName(String name) {
        return steveDao.selectSteveByName(name);
    }

    public String selectSteveUid(String name) {
        return steveDao.selectSteveUid(name);
    }

    public String selectSteveName(String uid) {
        return steveDao.selectSteveName(uid);
    }

    /**
     * 插入玩家数据
     * @param name 名称
     * @param uid uid
     * @param register 注册时间
     * @param prefix 头衔
     * @return id
     */
    public long insertSteve(String name, String uid, long register, String prefix, int money, boolean legal) {
        return steveDao.insertSteve(name, uid, register, prefix, money, legal);
    }

    /**
     * 更新玩家离开位置
     * @param quit 离开位置
     * @param uid uid
     */
    public void updateQuit(Location quit, UUID uid) {
        steveDao.updateQuit(SerializationUtil.serialize(quit), uid.toString());
    }

    /**
     * 查找玩家离开位置
     * @param uid uid
     * @return 玩家离开位置
     */
    public Location selectQuit(UUID uid) {
        String quit = steveDao.selectQuit(uid.toString());
        if (quit == null || "".equals(quit)) {
            return null;
        }
        return SerializationUtil.deserialize(steveDao.selectQuit(uid.toString()), Location.class);
    }

    /**
     * 更新密码
     * @param password 密码
     * @param uid uid
     */
    public void updatePassword(String password, UUID uid) {
        steveDao.updatePassword(password, uid.toString());
    }

    /**
     * 查找玩家密码
     * @param uid uid
     * @return 玩家密码
     */
    public String selectPassword(UUID uid) {
        return steveDao.selectPassword(uid.toString());
    }

    /**
     * 更新邮箱
     * @param email 邮箱
     * @param uid uid
     */
    public void updateEmail(String email, UUID uid) {
        steveDao.updateEmail(email, uid.toString());
    }

    /**
     * 查找邮箱
     * @param uid uid
     * @return 邮箱
     */
    public String selectEmail(UUID uid) {
        return steveDao.selectEmail(uid.toString());
    }

    /**
     * 更新钱
     * @param symbol 符号
     * @param money 数量
     * @param uid 玩家id
     * @return 更新条目
     */
    public long updateMoney(String symbol, double money, UUID uid) {
        return steveDao.updateMoney(symbol, money, uid.toString());
    }

    /**
     * 获取钱
     * @param uid 玩家id
     * @return 钱
     */
    public double selectMoney(UUID uid) {
        return steveDao.selectMoney(uid.toString());
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
    
    /**
     * 获取头衔
     * @param uid uid
     * @return 头衔
     */
    public String selectStevePrefix(UUID uid) {
        return steveDao.selectPrefix(uid.toString());
    }

    /**
     * 更新头衔
     * @param prefix 头衔
     * @param uid uid
     * @return 更新条目
     */
    public long updatePrefix(String prefix, UUID uid) {
        return steveDao.updatePrefix(prefix, uid.toString());
    }

    /**
     * 获取玩家性别
     * @param uid uid
     * @return 性别
     */
    public int selectGender(UUID uid) {
        return steveDao.selectGender(uid.toString());
    }

    /**
     * 更新玩家性别
     * @param gender 性别
     * @param uid uid
     * @return 更新条目
     */
    public long updateGender(int gender, UUID uid) {
        return steveDao.updateGender(gender, uid.toString());
    }

    public boolean selectLegal(UUID uid) {
        return steveDao.selectLegal(uid.toString());
    }

    public long updateLegal(boolean legal, UUID uid) {
        return steveDao.updateLegal(legal, uid.toString());
    }

    public long updateSteveUid(String newUid, String uid) {
        return steveDao.updateSteveUid(newUid, uid);
    }

    public long updateSteveName(String name, String uid) {
        return steveDao.updateSteveName(name, uid);
    }


    /*****************************************************************************/

    /**
     * 删除家
     * @param owner 主人
     * @param name 名称
     * @return 删除数量
     */
    public long deleteHome(UUID owner, String name) {
        return homeDao.deleteHome(owner.toString(), name);
    }

    /**
     * 插入家
     * @param owner 主人
     * @param name 名称
     * @param loc 位置
     * @return id
     */
    public long insertHome(UUID owner, String name, Location loc) {
        return homeDao.insertHome(owner.toString(), name, SerializationUtil.serialize(loc));
    }

    /**
     * 查找家
     * @param owner 主人
     * @param name 名称
     * @return 家
     */
    public Home selectHome(UUID owner, String name) {
        return homeDao.selectHome(owner.toString(), name);
    }

    /**
     * 查找家
     * @param owner 主人
     * @return 家
     */
    public Home[] selectHomes(UUID owner) {
        return homeDao.selectHomes(owner.toString());
    }

    /**
     * 更新家的位置
     * @param loc 位置
     * @param owner 主人
     * @param name 名称
     * @return 更新数目
     */
    public long updateHome(Location loc, UUID owner, String name) {
        return homeDao.updateHome(SerializationUtil.serialize(loc), owner.toString(), name);
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

    /*****************************************************************************/

    public long insertPermit(UUID owner, String name, boolean value, String world, long deadline) {
        return permitDao.insertPermit(owner.toString(), name, value, world, deadline);
    }

    public Permit[] selectPermits(UUID owner) {
        return permitDao.selectPermits(owner.toString());
    }

    public Permit selectPermit(UUID owner, String name) {
        return permitDao.selectPermit(owner.toString(), name);
    }

    public long updatePermit(long deadline, long id) {
        return permitDao.updatePermit(deadline, id);
    }

    public long deletePermit(UUID owner, String name) {
        return permitDao.deletePermit(owner.toString(), name);
    }
    
    /*****************************************************************************/

    public long insertConfer(UUID owner, String group, String world, long deadline) {
        return conferDao.insertConfer(owner.toString(), group, world, deadline);
    }

    public Confer[] selectConfers(UUID owner) {
        return conferDao.selectConfers(owner.toString());
    }

    public Confer selectConfer(UUID owner, String group) {
        return conferDao.selectConfer(owner.toString(), group);
    }

    public long updateConfer(long deadline, long id) {
        return conferDao.updateConfer(deadline, id);
    }

    public long deleteConfer(UUID owner, String group) {
        return conferDao.deleteConfer(owner.toString(), group);
    }

    /*****************************************************************************/

    public long insertPrefix(UUID owner, String value, long deadline) {
        return prefixDao.insertPrefix(owner.toString(), value, deadline);
    }

    public Prefix[] selectPrefixs(UUID owner) {
        return prefixDao.selectPrefixs(owner.toString());
    }

    public Prefix selectPrefix(long id) {
        return prefixDao.selectPrefix(id);
    }

    public Prefix selectPrefix(UUID owner, String value) {
        return prefixDao.selectPrefix(owner.toString(), value);
    }

    public long updatePrefix(long deadline, long id) {
        return prefixDao.updatePrefix(deadline, id);
    }

    public long deletePrefix(long id) {
        return prefixDao.deletePrefix(id);
    }

    /*****************************************************************************/

    public long insertShip(UUID b, UUID d, String tag, String request, String target, long outset) {
        return shipDao.insertShip(b.toString() + "&" + d.toString(), tag, request, target, outset);
    }

    public Ship selectShip(UUID b, UUID d) {
        return shipDao.selectShip(b.toString() + "&" + d.toString());
    }

    public Ship[] selectShips(String target, UUID uid) {
        return shipDao.selectShips(target, uid.toString());
    }

    public String[] selectShipBondByTag(String tag, UUID uid) {
        return shipDao.selectShipBondByTag(tag, uid.toString());
    }

    public long updateShipLevel(int level, String bond) {
        return shipDao.updateShipLevel(level, bond);
    }

    public long updateShipIntimate(boolean intimate, String bond) {
        return updateShipIntimate(intimate, bond);
    }


    public long updateShipTag(String tag, String bond) {
        return shipDao.updateShipTag(tag, bond);
    }

    public long updateShipRequest(String request, String bond) {
        return shipDao.updateShipRequest(request, bond);
    }


    public long deleteShip(String bond) {
        return shipDao.deleteShip(bond);
    }

    /*****************************************************************************/

    public long insertDomain(String name, UUID owner, Location location) {
        String plot = location.getChunk().getX() + ":" + location.getChunk().getZ();
        return domainDao.insertDomain(name, owner.toString(), location.getWorld().getName(), Instant.now().toEpochMilli(), SerializationUtil.serialize(location), plot);
    }

    public Domain[] selectDomainsByWorld(String world) {
        return domainDao.selectDomainsByWorld(world);
    }

    public Domain[] selectDomainsByPlayer(UUID owner) {
        return domainDao.selectDomainsByPlayer(owner.toString());
    }

    public String[] selectDomainsNameByPlayer(UUID owner) {
        return domainDao.selectDomainsNameByPlayer(owner.toString());
    }

    public Domain selectDomainByName(String name) {
        return domainDao.selectDomainByName(name);
    }

    public String selectDomainOwner(String name) {
        return domainDao.selectDomainOwner(name);
    }

    public String selectDomainWorld(String name) {
        return domainDao.selectDomainWorld(name);
    }

    public long updateDomainPlots(String plots, String name) {
        return domainDao.updateDomainPlots(plots, name);
    }

    public long updateDomainOwner(String owner, String name) {
        return domainDao.updateDomainOwner(owner, name);
    }

    public long domainLevelUp(String name) {
        return domainDao.domainLevelUp(name);
    }

    public long domainLevelDown(String name) {
        return domainDao.domainLevelDown(name);
    }

    public long updateDomainCenter(String center, String name) {
        return domainDao.updateDomainCenter(center, name);
    }
    
    public long updateDomainGlobe(long globe, String name) {
        return domainDao.updateDomainGlobe(globe, name);
    }
    
    public long updateDomainFriend(long friend, String name) {
        return domainDao.updateDomainFriend(friend, name);
    }
    
    public long updateDomainIntimate(long intimate, String name) {
        return domainDao.updateDomainIntimate(intimate, name);
    }
    
    public long updateDomainName(String newName, String name) {
        return domainDao.updateDomainName(newName, name);
    }

    public long updateDomainShare(String share, String name) {
        return domainDao.updateDomainShare(share, name);
    }

    public long updateDomainType(int type, String name) {
        return domainDao.updateDomainType(type, name);
    }

    public long deleteDomain(String name) {
        return domainDao.deleteDomain(name);
    }
}
