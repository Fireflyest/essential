package org.fireflyest.essential.util;

import org.bukkit.event.entity.EntityDamageEvent;

public class DeathMsgUtils {

    private DeathMsgUtils(){

    }

    /**
     * 转化死亡信息
     * @param name 名称
     * @param msg 消息
     * @param cause 事件
     * @return 消息
     */
    public static String convertDeathMsg(String name, String msg, EntityDamageEvent.DamageCause cause) {
        if (msg.contains("using")) {
            return null;
        }
        String[] entity;
        switch (cause){
            case ENTITY_ATTACK:
                entity = msg.split(" was slain by ");
                msg = entityAttackMas(name, entity[1]);
                break;
            case PROJECTILE:
                if (msg.contains("shot")) {
                    entity = msg.split(" was shot by ");
                    switch (entity[1]) {
                        case "Skeleton":
                            msg = name + "跑得没有骷髅射手的箭快";
                            break;
                        case "Stray":
                            msg = name + "成为流浪者的靶心";
                            break;
                        case "Pillager":
                            msg = name + "被掠夺者抢剩一条内裤";
                            break;
                        default:
                            msg = name + "被" + entity[1] + "射杀";
                            break;
                    }
                } else if (msg.contains("fireballed")) {
                    entity = msg.split(" was fireballed by ");
                    msg = name + "被" + entity[1] + "的火球击杀";
                } else if (msg.contains("pummeled")) {
                    entity = msg.split(" was pummeled by ");
                    msg = name + "被" + entity[1] + "砸死了";
                }
                break;
            case FALL:
                if(msg.contains("place")){
                    msg = name +"不自量力地冲撞地球";
                }else if(msg.contains("ladder")){
                    msg = name +"不小心在梯子上摔了下来";
                }else if(msg.contains("vines")){
                    msg = name +"爬藤蔓时手滑了";
                }else if(msg.contains("water")){
                    msg = name +"试图在空气中游泳";
                }else if(msg.contains("doomed")) {
                    msg = msg.replace("by", "").replace("using", "使用");
                    msg = msg.replace(" was doomed to fall ", "被") + "追杀到走投无路，跳崖自尽";
                }
                break;
            case FIRE:
                if(msg.contains("flames")){
                    msg = name +"被烤的嘣嘠脆";
                }else{
                    msg = msg.replace(" walked into fire whilst fighting ", "与")+"战斗时踩上了火焰";
                }
                break;
            case FIRE_TICK:
                if(msg.contains("crisp")){
                    msg = msg.replace(" was burnt to a crisp whilst fighting ", "与")+"战斗时被他身上的火焰烧死了";
                }else{
                    msg = name +"被烧成了黑炭";
                }
                break;
            case LAVA:
                if(msg.contains("escape")){
                    msg = msg.replace(" tried to swim in lava to escape ", "想通过在岩浆中游泳来逃避")+"的追杀";
                }else{
                    msg = name+"在岩浆里泡温泉";
                }
                break;
            case VOID:
                msg = name + "堕入无尽的黑暗";
                break;
            case SUFFOCATION:
                msg = name+"认为他的头比方块更加坚硬";
                break;
            case DROWNING:
                msg = name+"想跟鱼儿一样游泳";
                break;
            case CONTACT:
                if(msg.contains("escape")){
                    msg = msg.replace(" walked into a cactus whilst trying to escape ", "为了躲避")+"的追杀，躲在仙人掌后面";
                }else{
                    msg = name+"被扎成刺猬";
                }
                break;
            case MAGIC:
                msg = name + "说: 要用魔法打败魔法!";
                break;
            case THORNS:
                msg = msg.replace(" was killed trying to hurt ", "被")+"的附魔盔甲闪瞎了眼";
                break;
            case WITHER:
                msg = msg.replace(" walked into a cactus whilst trying to escape ", "为了躲避")+"的追杀，躲在仙人掌后面";
                if(msg.contains("whilst")){
                    msg = msg.replace(" withered away whilst fighting", "与")+"战斗时被魔剑凋零";
                }else{
                    msg = name + "凋零了";
                }
                break;
            case DRAGON_BREATH:
                msg = name+"淹没于龙息之中";
                break;
            case FALLING_BLOCK:
                if(msg.contains("anvil")){
                    msg = name+"认为自己的头比铁毡更铁";
                }else{
                    msg = name+"被砸扁了";
                }
                break;
            case BLOCK_EXPLOSION:
                msg = name+"恶灵的注视下安眠";
                break;
            case ENTITY_EXPLOSION:
                if(msg.contains("by")){
                    msg = msg.replace(" was blown up by ", "被")+"炸飞了";
                }else{
                    msg = name+"被炸飞了";
                }
                break;
            case ENTITY_SWEEP_ATTACK:
                msg = name+"被剑气所杀";
                break;
            case HOT_FLOOR:
                if(msg.contains("walked")){
                    msg = msg.replace(" walked into danger zone due to ", "被")+"逼杀到滚烫的危险地带";
                }else{
                    msg = name+"似乎被滚烫的地板烫熟了";
                }
                break;
            case LIGHTNING:
                msg = name + "受到神的制裁";
                break;
            default:
                msg = "安息吧 "+name;
                break;
        }
        return "§r☠ §f" + msg;
    }

    private static String entityAttackMas(String name, String entity) {
        String msg = null;
        switch (entity) {
            case "Zombie":
                msg = name + "被僵尸吃掉了脑子";
                break;
            case "Zombified Piglin":
                msg = name + "惹恼猪人后被群殴";
                break;
            case "Drowned":
                msg = name + "被溺尸拖入水底";
                break;
            case "Husk":
                msg = name + "被尸壳包裹成木乃伊";
                break;
            case "Spider":
                msg = name + "被蜘蛛肢解";
                break;
            case "Cave Spider":
                msg = name + "被洞穴蜘蛛拖回昏暗的洞穴";
                break;
            case "Slime":
                msg = name + "被史莱姆做成果冻";
                break;
            case "Wither Skeleton":
                msg = name + "败于凋零骷髅脚下";
                break;
            default:
                msg = name + "被" + entity + "击杀";
                break;
        }
        return msg;
    }

}
