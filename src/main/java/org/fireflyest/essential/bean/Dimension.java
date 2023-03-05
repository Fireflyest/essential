package org.fireflyest.essential.bean;

import java.util.HashMap;

import org.fireflyest.essential.service.EssentialService;

public class Dimension {
    
    public static final int SERVER = 0;
    public static final int DOMAIN = 1;
    public static final int ROAD = 2;
    public static final int WORLD = 3;

    private String name;

    private String title;

    private boolean protect;

    private boolean pvp;

    private boolean explode;

    private EssentialService service;

    private HashMap plotMap = new HashMap<>();

    private HashMap domainMap = new HashMap<>();

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
     * 获取对决许可
     * @return 是否允许对决
     */
    public EventResult explode(String loc) {
        EventResult result = new EventResult();

        return result;
    }

    public EventResult build(String loc, String uid) {
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


    public static class EventResult {
        public boolean cancel;
        public int type;

        public EventResult() {
        }
        
    }

}
