package org.fireflyest.essential.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.TimeUtils;

public class ShipPage extends TemplatePage {

    private EssentialService service;

    private int index = 0;

    private final Map<Integer, Integer> levelMap = new HashMap<>();

    protected ShipPage(EssentialService service, String target) {
        super("§9§l好友关系", target, 0, 54);
        this.service = service;

        levelMap.put(0, 0);
        levelMap.put(1, (int) ((Math.pow(Math.E,  (1 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(2, (int) ((Math.pow(Math.E,  (2 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(3, (int) ((Math.pow(Math.E,  (3 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(4, (int) ((Math.pow(Math.E,  (4 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(5, (int) ((Math.pow(Math.E,  (5 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(6, (int) ((Math.pow(Math.E,  (6 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(7, (int) ((Math.pow(Math.E,  (7 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(8, (int) ((Math.pow(Math.E,  (8 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(9, (int) ((Math.pow(Math.E,  (9 + 1) * Math.log(10000) / 10) - 1) / 0.5));
        levelMap.put(10, (int) ((Math.pow(Math.E,  (10 + 1) * Math.log(10000) / 10) - 1) / 0.5));


    }

    @Override
    public Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        Player player = Bukkit.getPlayerExact(target);
        if (player == null) {
            return asyncButtonMap;
        }

        // 获取好友关系并排序
        List<Ship> shipList = Arrays.asList(service.selectShips(target, player.getUniqueId()));
        shipList.sort((o1,  o2) -> {
            String otherUid = o1.getBond().split("&")[1];
            OfflinePlayer other = Bukkit.getOfflinePlayer(UUID.fromString(otherUid));
            o1.setPos(o1.getLevel() + (other.isOnline() ? 2000 : 0) + (!"".equals(o1.getRequest()) ? 10000 : 0));
            return o1.getPos() < o2.getPos() ? 1 : -1;
        });

        // 放置按钮
        int pos = 0;
        boolean single = true;
        for (int i = index * 2; i < (index + 8) * 2; i++) {
            if (i >= shipList.size()) {
                break;
            }
            Ship ship = shipList.get(i);
            ItemStack friendItem;
            ItemStack shipItem;

            if (ship.getTarget().equals(target)) {
                // 好友申请的目标是我
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(ship.getBond().split("&")[0]));

                friendItem = new ButtonItemBuilder(Material.PLAYER_HEAD)
                        .actionPlayerCommand("ship build " + offlinePlayer.getName())
                        .name("§r$<hg=#218c74:#33d9b2>" + offlinePlayer.getName())
                        .lore(this.getRequestString(ship.getRequest()))
                        .lore("§r$<c=#778ca3>点击接受")
                        .colorful()
                        .build();
                
                shipItem = new ButtonItemBuilder(this.getTagItem(ship.getTag()))
                        .build();
            } else {
                // 已经是好友
                OfflinePlayer friend = Bukkit.getOfflinePlayer(UUID.fromString(ship.getBond().split("&")[1]));
                int genderNum = service.selectGender(friend.getUniqueId());
                String gender = this.getGenderIcon(genderNum);
                int level = this.getLevel(ship.getLevel());
                boolean isOnline = (friend != null && friend.isOnline() && !((Player)friend).isInvisible());

                friendItem = new ButtonItemBuilder(isOnline ? Material.PLAYER_HEAD : Material.WITHER_SKELETON_SKULL)
                        .actionPlayerCommand("interact " + ship.getTarget())
                        .name("§f" + gender + (isOnline ? "$<hg=#218c74:#33d9b2>" : "$<hg=#b33939:#ff5252>") + ship.getTarget())
                        .lore(isOnline ? "§a在线"  : "§7最后在线" + TimeUtils.getLocalDate(friend.getLastPlayed()))
                        .colorful()
                        .build();
                
                shipItem = new ButtonItemBuilder(this.getTagItem(ship.getTag()))
                        .name(this.getTagString(ship.getTag()))
                        .amount(level)
                        .colorful()
                        .build();
            }
            int itemPos = single ? pos : (pos++ + 18);
            asyncButtonMap.put(itemPos, friendItem);
            asyncButtonMap.put(itemPos + 9, shipItem);
            single = !single;
        }

        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        ItemStack close = new ButtonItemBuilder(Material.REDSTONE)
                .actionBack()
                .name("§c关闭")
                .build();
        buttonMap.put(53, close);

        ItemStack blank = new ButtonItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        for (int i = 36; i < 45; i++) {
            buttonMap.put(i, blank);
        }

        ItemStack pre = new ButtonItemBuilder(Material.PAPER)
                .actionPagePre()
                .name("§r◀")
                .build();
        buttonMap.put(45, pre);
        ItemStack next = new ButtonItemBuilder(Material.PAPER)
                .actionPageNext()
                .name("§r▶")
                .build();
        buttonMap.put(46, next);

        ItemStack apply = new ButtonItemBuilder(Material.WRITABLE_BOOK)
                .actionEdit()
                .name("§e申请")
                .build();
        buttonMap.put(48, apply);
        ItemStack gift = new ButtonItemBuilder(Material.GOLDEN_APPLE)
                .actionEdit()
                .name("§e礼物")
                .build();
        buttonMap.put(49, gift);
    }

    /**
     * 获取好友类型
     * @param tag 标签
     * @return 好友类型
     */
    private String getTagString(String tag) {
        switch (tag) {
            case "":
                return "§r$<c=#778ca3>好友请求中...";
            case "friend":
                return "§f[$<c=#006266>好友§f]";
            default:
                return tag;
        }
    }

    /**
     * 获取请求类型
     * @param request 请求
     * @return 请求类型
     */
    private String getRequestString(String request) {
        switch (request) {
            case "":
                return "无";
            case "friend":
                return "$<c=#006266>好友§r";
            default:
                return request;
        }
    }

    private Material getTagItem(String tag) {
        switch (tag) {
                case "":
                    return Material.REDSTONE;
                case "friend":
                default:
                    return Material.LAPIS_LAZULI;
            }
    }

    private int getLevel(int level) {
        if (level <= 1) return 1;
        return (int) (10 * Math.log(0.5 * level + 1) / Math.log(10000));
    }

    private String getGenderIcon(int number) {
        String gender = "";
        if (number == 1) {
            gender = "♂";
        } else if (number == 2) {
            gender = "♀";
        } else if (number == 3) {
            gender = "⚧️";
        }
        return gender;
    }

    
}
