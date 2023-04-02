package org.fireflyest.essential.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.service.EssentialService;

public class Dimension {
    
    public static final int PLAYER = 0;
    public static final int LEAGUE = 1;
    public static final int SERVER = 2;

    public static final int PERMISSION_USE =              0b00000000000000000000000000000001; // 使用
    
    public static final int PERMISSION_DESTROY =      0b00000000000000000000000000001000; // 破坏
    public static final int PERMISSION_PLACE =           0b00000000000000000000000000010000; // 放置
    public static final int PERMISSION_BUCKET =        0b00000000000000000000000000100000; // 水桶
    public static final int PERMISSION_IGNITE =          0b00000000000000000000000001000000; // 点燃
    public static final int PERMISSION_BUILD =           0b00000000000000000000000001111000; // 建筑
    
    public static final int PERMISSION_PVE =               0b00000000000000000000000010000000; // 杀生
    public static final int PERMISSION_OPEN =            0b00000000000000000000000100000000; // 容器
    public static final int PERMISSION_TP =                 0b00000000000000000000001000000000; // 传送
    public static final int PERMISSION_ARMOR =        0b00000000000000000000010000000000; // 框甲

    public static final int FLAG_PVP =                            0b10000000000000000000000000000000; // 对决
    public static final int FLAG_MONSTER =                 0b01000000000000000000000000000000; // 刷怪
    public static final int FLAG_EXPLODE =                   0b00100000000000000000000000000000; // 爆炸
    public static final int FLAG_PISTON =                      0b00010000000000000000000000000000; // 活塞

    public static final int FLAG_FLOW_WATER =          0b00001000000000000000000000000000; // 水流动
    public static final int FLAG_FLOW_LAVA =             0b00000100000000000000000000000000; // 岩浆流动
    public static final int FLAG_FLOW =                       0b00001100000000000000000000000000; // 液体流动

    private final Pattern chunkPattern = Pattern.compile("-?\\d+:-?\\d+");

    private String name;

    private String title;

    private boolean protect;

    private boolean pvp;

    private boolean explode;

    private EssentialService service;

    private Map<String, Plot> plotMap = new HashMap<>();

    private Map<String, Domain> domainMap = new HashMap<>();

    private Map<String, List<Domain>> roadMap = new HashMap<>();

    /**
     * 维度
     * @param name 名称
     * @param title 显示名称
     * @param protect 是否保护
     * @param pvp 玩家对战
     * @param explode 爆炸破坏
     * @param service 数据服务
     */
    public Dimension(String name, String title, boolean protect, boolean pvp, boolean explode, EssentialService service) {
        this.name = name;
        this.title = title;
        this.protect = protect;
        this.pvp = pvp;
        this.explode = explode;
        this.service = service;

        // 遍历该世界所有领地
        for (Domain domain : service.selectDomainsByWorld(name)) {
            // 获取领地的所有区块
            Matcher matcher = chunkPattern.matcher(domain.getPlots());
            while (matcher.find()) {
                // 领地区块
                String loc = matcher.group();
                String[] xz = loc.split(":");
                Plot plot = new Plot(loc, NumberConversions.toInt(xz[0]), NumberConversions.toInt(xz[1]), domain);
                
                plotMap.put(loc, plot);
                domain.getPlotList().add(plot);
                roadMap.remove(loc);

                // 道路区块
                for (String nearChunk : this.getNearChunk(loc)) {
                    // 是否领地
                    if (plotMap.containsKey(nearChunk)) { 
                        continue;
                    }
                    // 道路是否共享
                    List<Domain> roadBelong = roadMap.get(nearChunk);
                    if (roadBelong == null) { 
                        List<Domain> domains = new ArrayList<>();
                        domains.add(domain);
                        roadMap.put(nearChunk, domains);
                    } else if (!roadBelong.contains(domain)) { 
                        roadBelong.add(domain);
                    }
                }
            }
            domainMap.put(domain.getName(), domain);
        }
    }


    /**
     * 必须在无人占领的区块上创建
     * @return
     */
    public EventResult canCreate(String loc) {
        EventResult result = new EventResult();
        // 是否在领地内
        Plot plot = plotMap.get(loc);
        if (plot != null) {
            result.setType(plot.getDomain().getType());
            result.setAllow(false);
            return result;
        }
        // 是否在道路上
        List<Domain> roadBelong = roadMap.get(loc);
        if (roadBelong != null && !roadBelong.isEmpty()) {
            result.setType(roadBelong.size() > 1 ? EventResult.IN_SHARE_ROAD : EventResult.IN_ROAD);
            result.setAllow(false);
            return result;
        }
        return result;
    }

    /**
     * 创建领地
     */
    public void createDomain(String name, UUID owner, Location point) {
        service.insertDomain(name, owner, point);
        Domain domain = service.selectDomainsByName(name);
        if (domain != null) {
            // 区块记录
            String loc = domain.getPlots();
            String[] xz = loc.split(":");
            Plot plot = new Plot(loc, NumberConversions.toInt(xz[0]), NumberConversions.toInt(xz[1]), domain);
            plotMap.put(loc, plot);
            domain.getPlotList().add(plot);

            // 道路区块
            for (String nearChunk : this.getNearChunk(loc)) {
                // 道路是否共享
                List<Domain> roadBelong = roadMap.get(nearChunk);
                if (roadBelong == null) { 
                    List<Domain> domains = new ArrayList<>();
                    domains.add(domain);
                    roadMap.put(nearChunk, domains);
                } else if (!roadBelong.contains(domain)) { 
                    roadBelong.add(domain);
                }
            }
        }
    }

    /**
     * 地皮扩张
     * @param domain 领地
     * @param loc 扩张位置
     */
    public void expandDomain(Domain domain, String loc) {
        domain.setPlots(domain.getPlots() + " " + loc);
        domain.setLevel(domain.getLevel() + 1);
        service.updateDomainPlots(domain.getPlots(), domain.getName());
        service.domainLevelUp(domain.getName());
        String[] xz = loc.split(":");
        Plot plot = new Plot(loc, NumberConversions.toInt(xz[0]), NumberConversions.toInt(xz[1]), domain);
        plotMap.put(loc, plot);
        domain.getPlotList().add(plot);
        roadMap.remove(loc);
        // 道路区块
        for (String nearChunk : this.getNearChunk(loc)) {
            // 是否领地
            if (plotMap.containsKey(nearChunk)) { 
                continue;
            }
            // 道路是否共享
            List<Domain> roadBelong = roadMap.get(nearChunk);
            if (roadBelong == null) { 
                List<Domain> domains = new ArrayList<>();
                domains.add(domain);
                roadMap.put(nearChunk, domains);
            } else if (!roadBelong.contains(domain)) { 
                roadBelong.add(domain);
            }
        }
    }

    /**
     * 舍弃一个区块
     * @param domain 领地
     * @param plot 地皮
     */
    public void abandonPlot(Domain domain, Plot plot) {
        String loc = plot.getLoc();
        domain.setPlots(domain.getPlots().replace(" " + loc, ""));
        domain.setLevel(domain.getLevel() - 1);
        service.updateDomainPlots(domain.getPlots(), domain.getName());
        service.domainLevelDown(domain.getName());
        domain.getPlotList().remove(plot);

        // 抛弃后变成路
        List<Domain> domains = new ArrayList<>();
        domains.add(domain);
        roadMap.put(loc, domains);

        // 原来的路不一定还是路
        for (String nearChunk : this.getNearChunk(loc)) {
            // 是否领地
            if (plotMap.containsKey(nearChunk)) { 
                continue;
            }
            // 道路是否还是路
            List<Domain> roadBelong = roadMap.get(nearChunk);
            boolean isRoad = false;
            for (String sideChunk : this.getNearChunk(nearChunk)) {
                if (domain.getPlots().contains(sideChunk)) {
                    isRoad = true;
                    break;
                }
            }
            // 不再是路
            if (!isRoad) {
                if (roadBelong.size() == 1) {
                    roadMap.remove(nearChunk);
                } else {
                    roadBelong.remove(domain);
                }
            }
        }
        plotMap.remove(loc);
    }

    /**
     * 删除领地
     * @param domain 领地
     */
    public void removeDomain(Domain domain) {
        Iterator<Plot> iterator = domain.getPlotList().iterator();
        while (iterator.hasNext()) {
            Plot plot = iterator.next();

            // 删除附近道路
            for (String nearChunk : this.getNearChunk(plot.getLoc())) {
                List<Domain> roadBelong = roadMap.get(nearChunk);
                if (roadBelong == null || roadBelong.isEmpty()) {
                    continue;
                }
                if (roadBelong.size() == 1) {
                    roadMap.remove(nearChunk);
                } else {
                    roadBelong.remove(domain);
                }
            }
            // 删除地块
            plotMap.remove(plot.getLoc());
            iterator.remove();
        }
        // 删除领地
        domainMap.remove(domain.getName());
        service.deleteDomain(domain.getName());
    }

    /**
     * 获取领地附近的道路
     * @param domain 领地
     * @return 道路
     */
    public Set<String> nearRoads(Domain domain) {
        Set<String> nearRoads = new HashSet<>();
        for (Plot plot : domain.getPlotList()) {
            for (String nearChunk : this.getNearChunk(plot.getLoc())) {
                if (roadMap.containsKey(nearChunk)) {
                    nearRoads.add(nearChunk);
                }
            }
        }
        return nearRoads;
    }

    /**
     * 地皮转让
     * @param domain 领地
     * @param target 对方
     */
    public void giveDomain(Domain domain, String target) {
        domain.setOwner(target);
        service.updateDomainOwner(target, domain.getName());
    }

    /**
     * 获取地皮
     * @param loc 坐标
     * @return 地皮
     */
    public Plot getPlot(String loc) {
        return plotMap.get(loc);
    }

    /**
     * 获取道路
     * @param loc 坐标
     * @return 路
     */
    public List<Domain> getRoadBelong(String loc) {
        return roadMap.get(loc);
    }

    /**
     * 获取领地
     * @param domainName 名称
     * @return 领地
     */
    public Domain getDomain(String domainName) {
        return domainMap.get(domainName);
    }

    /**
     * 获取显示名称
     * @return 显示名称
     */
    public String getTitle() {
        return title;
    }

    /**
     * 世界名称
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取保护判断
     * @return 是否保护
     */
    public boolean isProtect() {
        return protect;
    }

    /**
     * 爆炸保护
     * @return 是否保护
     */
    public boolean isExplode() {
        return explode;
    }

    /**
     * 玩家伤害
     * @return 是否可以伤害
     */
    public boolean isPvp() {
        return pvp;
    }

    /**
     * 是否允许做某个操作
     * @param setting 当前设置
     * @param permit 权限
     * @return 是否允许
     */
    public boolean isPermit(long setting, long permit) {
        return (setting & permit) != 0;
    }

    /**
     * 开启允许
     * @param setting 当前设置
     * @param permit 权限
     * @return 开启结果
     */
    public long onPermit(long setting, long permit) {
        return setting | permit;
    }

    /**
     * 关闭允许
     * @param setting 当前设置
     * @param permit 权限
     * @return 关闭结果
     */
    public long offPermit(long setting, long permit) {
        return setting & (~permit);
    }

    /**
     * 切换权限
     * @param setting 当前设置
     * @param permit 权限
     * @return 切换结果
     */
    public long switchPermit(long setting, long permit) {
        return this.isPermit(setting, permit) ? this.offPermit(setting, permit) : this.onPermit(setting, permit);
    }

     /**
     * 触发某个规则
     * @param loc 位置
     * @param setting 权限设置
     * @return 触发结果
     */
    public EventResult triggerFlag(String loc, long setting) {
        return triggerFlag(loc, setting, true);
    }

    /**
     * 触发某个规则
     * @param loc 位置
     * @param setting 权限设置
     * @param worldSetting 世界权限设置
     * @return 触发结果
     */
    public EventResult triggerFlag(String loc, long setting, boolean worldSetting) {
        EventResult result = new EventResult();
        // 领地
        Plot plot = plotMap.get(loc);
        if (plot != null) {
            Domain domain = plot.getDomain();
            result.setAllow(this.isPermit(domain.getGlobe(), setting));
            result.setType(domain.getType());
            return result;
        }
        // 道路上的爆炸活塞流水
       List<Domain> roadBelong;
       if ((roadBelong = roadMap.get(loc)) != null && !roadBelong.isEmpty() && (setting & (FLAG_FLOW | FLAG_EXPLODE | FLAG_PISTON)) != 0) {
           result.setAllow(false);
           result.setType(roadBelong.size() > 1 ? EventResult.IN_SHARE_ROAD : EventResult.IN_ROAD);
           return result;
       }
         // 世界
        result.setAllow(worldSetting);
        result.setType(EventResult.WORLD_PROTECT);
        return result;
    }

    /**
     * 触发某个权限
     * @param loc 位置
     * @param uid 触发玩家
     * @param setting 权限设置
     * @return 触发结果
     */
    public EventResult triggerPermit(String loc, String uid, long setting) {        
        return triggerPermit(loc, uid, setting, true);
    }

    /**
     * 触发某个权限
     * @param loc 位置
     * @param uid 触发玩家
     * @param setting 权限设置
     * @param worldSetting 世界权限设置
     * @return 触发结果
     */
    public EventResult triggerPermit(String loc, String uid, long setting, boolean worldSetting) {
        EventResult result = new EventResult();
        // 领地
        Plot plot = plotMap.get(loc);
        if (plot != null) {
            Domain domain = plot.getDomain();
            Ship ship = null;
            if (domain.getOwner().equals(uid)) {
                // 本人
                return result;
            } else if ((ship = service.selectShip(UUID.fromString(domain.getOwner()), UUID.fromString(uid))) != null) {
                // 好友
                if (ship.isIntimate()) {
                    result.setAllow(this.isPermit(domain.getIntimate(), setting));
                    result.setType(domain.getType());
                } else {
                    result.setAllow(this.isPermit(domain.getFriend(), setting));
                    result.setType(domain.getType());
                }
                return result;
            } else {
                // 陌生人
                result.setAllow(this.isPermit(domain.getGlobe(), setting));
                result.setType(domain.getType());
                return result;
            }
        }
        // 道路上不能建筑
        List<Domain> roadBelong;
        if ((roadBelong = roadMap.get(loc)) != null && !roadBelong.isEmpty() && (setting & PERMISSION_BUILD) != 0) {
            result.setAllow(false);
            result.setType(roadBelong.size() > 1 ? EventResult.IN_SHARE_ROAD : EventResult.IN_ROAD);
            return result;
        }
        // 世界
        result.setAllow(worldSetting);
        result.setType(EventResult.WORLD_PROTECT);
        return result;
    }

    public static class EventResult {

        public static final int NONE = -1;
        public static final int IN_DOMAIN = 0;
        public static final int IN_LEAGUE = 1;
        public static final int SERVER_PROTECT = 2;
        public static final int WORLD_PROTECT = 3;
        public static final int IN_ROAD = 4;
        public static final int IN_SHARE_ROAD = 5;

        private boolean allow;
        private int type;
        private String domain;

        /**
         * 操作结果
         */
        public EventResult() {
            this.allow = true;
            this.type = NONE;
        }

        /**
         * 操作结果
         * @param allow 是否允许
         * @param type 结果类型
         */
        public EventResult(boolean allow, int type) {
            this.allow = allow;
            this.type = type;
        }

        public boolean isAllow() {
            return allow;
        }

        public void setAllow(boolean allow) {
            this.allow = allow;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }
        
    }

    /**
     * 获取邻近区块
     * @param loc 坐标
     * @return 邻近区块
     */
    private String[] getNearChunk(String loc) {
        String[] xz = loc.split(":");
        int x = NumberConversions.toInt(xz[0]);
        int z = NumberConversions.toInt(xz[1]);
        return new String[]{
            (x - 1) + ":" + (z - 1),
            (x - 1) + ":" + (z),
            (x - 1) + ":" + (z + 1),
            (x) + ":" + (z - 1),
            (x) + ":" + (z + 1),
            (x + 1) + ":" + (z - 1),
            (x + 1) + ":" + (z),
            (x + 1) + ":" + (z + 1)
        };
    }

}
