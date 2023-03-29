package org.fireflyest.essential.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.fireflyest.essential.bean.Domain;
import org.fireflyest.essential.service.EssentialService;

public class Dimension {
    
    public static final int PLAYER = 0;
    public static final int LEAGUE = 1;
    public static final int SERVER = 2;

    public static final int PERMISSION_USE =              0b00000000000000000000000000000001; // 使用
    
    public static final int PERMISSION_DESTROY =      0b00000000000000000000000000001000; // 破坏
    public static final int PERMISSION_PLACE =          0b00000000000000000000000000010000; // 放置
    public static final int PERMISSION_BUCKET =        0b00000000000000000000000000100000; // 水桶
    public static final int PERMISSION_BUILD =           0b00000000000000000000000000111000; // 建筑
    
    public static final int PERMISSION_PVE =               0b00000000000000000000000001000000; // 杀怪
    public static final int PERMISSION_OPEN =           0b00000000000000000000000010000000; // 容器
    public static final int PERMISSION_TP =                 0b00000000000000000000000100000000; // 传送

    public static final int FLAG_PVP =                           0b10000000000000000000000000000000; // 对决
    public static final int FLAG_MONSTER =                 0b01000000000000000000000000000000; // 刷怪
    public static final int FLAG_EXPLODE =                  0b00100000000000000000000000000000; // 爆炸
    public static final int FLAG_PISTON =                     0b00010000000000000000000000000000; // 活塞

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
     * 获取保护判断
     * @return 是否保护
     */
    public boolean isProtect() {
        return protect;
    }

    /**
     * 获取对决许可
     * @return 是否允许对决
     */
    public EventResult pvp(String loc) {
        EventResult result = new EventResult();

        return result;
    }

    /**
     * 获取杀怪许可
     * @return 是否允许杀怪
     */
    public EventResult pve(String loc) {
        EventResult result = new EventResult();

        return result;
    }

    /**
     * 爆炸许可
     * @return 是否允许爆炸
     */
    public EventResult explode(String loc) {
        EventResult result = new EventResult();

        return result;
    }

    public EventResult place(String loc, String uid) {
        EventResult result = new EventResult();

        return result;
    }
    
    public EventResult destroy(String loc, String uid) {
        EventResult result = new EventResult();

        return result;
    }

    public EventResult use(String loc, String uid) {
        EventResult result = new EventResult();

        return result;
    }

    public EventResult bucket(String loc, String uid) {
        EventResult result = new EventResult();

        return result;
    }

    public EventResult open(String loc, String uid) {
        EventResult result = new EventResult();

        return result;
    }

    public EventResult tp(String loc, String uid) {
        EventResult result = new EventResult();

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
