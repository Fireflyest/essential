package org.fireflyest.essential.gui;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.util.TimeUtils;

public class InteractPage extends TemplatePage {

    private EssentialService service;

    protected InteractPage(EssentialService service, String target) {
        super("§9§l交互", target, 0, 18);
        this.service = service;
    }

    @Override
    public Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        String[] targets = target.split("&");
        UUID myUid = UUID.fromString(targets[0]);
        String targetName = targets[1];
        UUID targetUid = UUID.fromString(service.selectSteveUid(targets[1]));
        Ship ship = service.selectShip(myUid, targetUid);
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetUid);
        int genderNum = service.selectGender(targetUid);
        String gender = this.getGenderIcon(genderNum);

        ItemStack targetItem = new ButtonItemBuilder(targetPlayer.isOnline() ? Material.PLAYER_HEAD : Material.WITHER_SKELETON_SKULL)
                .name("§f" + gender + (targetPlayer.isOnline() ? "$<hg=#218c74:#33d9b2>" : "$<hg=#b33939:#ff5252>") + targetName)
                .lore(targetPlayer.isOnline() ? "§a在线"  : "§7最后在线" + TimeUtils.getLocalDate(targetPlayer.getLastPlayed()))
                .colorful()
                .build();

        ItemStack shipItem;
        if (ship != null) {
            int level = this.getLevel(ship.getLevel());
            shipItem = new ButtonItemBuilder(this.getTagItem(ship.getTag()))
                    .name(this.getTagString(ship.getTag()))
                    .amount(level)
                    .colorful()
                    .build();
        } else {
            shipItem = new ButtonItemBuilder(Material.EMERALD)
                    .actionPlayerCommand("ship " + targetName)
                    .name("§f[§a添加好友§f]")
                    .build();
        }

        asyncButtonMap.put(0, targetItem);
        asyncButtonMap.put(9, shipItem);

        return asyncButtonMap;
    }

    @Override
    public void refreshPage() {
        String targetName  = target.split("&")[1];

        ItemStack blank = new ButtonItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name(" ")
                .build();
        buttonMap.put(1, blank);
        buttonMap.put(10, blank);

        ItemStack tphere = new ButtonItemBuilder(Material.ENDER_EYE)
                .actionPlayerCommand("tphere " + targetName)
                .name("§a邀请到身边")
                .build();
        buttonMap.put(2, tphere);

        ItemStack tpa = new ButtonItemBuilder(Material.ENDER_PEARL)
                .actionPlayerCommand("tpa " + targetName)
                .name("§a请求传送")
                .build();
        buttonMap.put(3, tpa);

        ItemStack message = new ButtonItemBuilder(Material.FEATHER)
                .actionPlayerCommand("message " + targetName)
                .name("§a私信频道")
                .build();
        buttonMap.put(4, message);

        ItemStack pvp = new ButtonItemBuilder(Material.SHIELD)
                .actionPlayerCommand("pvp " + targetName)
                .name("§a决斗")
                .build();
        buttonMap.put(5, pvp);
    }
    
    private String getTagString(String tag) {
        switch (tag) {
            case "apply_for":
                return "§r$<c=#778ca3>好友请求中...";
            case "friend":
                return "§f[$<c=#006266>好友§f]";
            default:
                return tag;
        }
    }

    private Material getTagItem(String tag) {
        switch (tag) {
                case "apply_for":
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
