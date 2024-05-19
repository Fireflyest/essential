package org.fireflyest.essential.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.bean.Confer;
import org.fireflyest.essential.bean.Permit;
import org.fireflyest.essential.bean.Prefix;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.util.TimeUtils;

import net.milkbowl.vault.permission.Permission;

public class EssentialPermission extends Permission  {

    private EssentialService service;

    private final HashMap<String, Group> groupMap = new HashMap<>();
    private final List<String> groupList  = new ArrayList<>();

    public EssentialPermission(EssentialYaml yaml, EssentialService service) {
        this.service = service;

        // 读取所有权限组信息
        for (String group : yaml.getGroup().getKeys(false)) {
            Group g = new Group(group, 
                    yaml.getGroup().getString(group + ".prefix"), 
                    yaml.getGroup().getString(group + ".desc"), 
                    yaml.getGroup().getStringList(group + ".permissions"));
            groupList.add(group);
            groupMap.put(group, g);
        }
    }

    /**
     * 刷新玩家权限
     * @param player 玩家
     */
    public void refreshPlayerPermission(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 给权限组权限
                for (String groupName : getPlayerGroups("", player)) {
                    Group group = groupMap.get(groupName);
                    if (group == null) {
                        continue;
                    }
                    for (Entry<String,Boolean> entrySet : group.permissionMap.entrySet()) {
                        player.addAttachment(Essential.getPlugin(), entrySet.getKey(), entrySet.getValue());
                    }
                }
                // 给个人权限
                for (Permit permit : service.selectPermits(player.getUniqueId())) {
                    // 判断是否过期
                    if (permit.getDeadline() != -1 &&TimeUtils.getInstant(permit.getDeadline()).isBefore(Instant.now())) {
                        service.deletePermit(player.getUniqueId(), permit.getName());
                        continue;
                    }
                    player.addAttachment(Essential.getPlugin(), permit.getName(), permit.isValue());
                }
                
            }
        }.runTask(Essential.getPlugin());
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return false;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        return Bukkit.getPlayerExact(player).hasPermission(permission);
    }

    /**
     * 给玩家添加权限
     * @param world 世界
     * @param player 玩家
     * @param permission 权限
     * @param day 期限
     * @return 成功
     */
    public boolean playerAdd(String world, OfflinePlayer player, String permission, int day) {
        Permit permit = service.selectPermit(player.getUniqueId(), permission);
        if (permit == null) {
            long deadline = (day == -1 ? -1 : Instant.now().plus(day, ChronoUnit.DAYS).toEpochMilli());
            service.insertPermit(player.getUniqueId(), permission, true, world, deadline);
        } else {
            long deadline = (day == -1 ? -1 : TimeUtils.getInstant(permit.getDeadline()).plus(day, ChronoUnit.DAYS).toEpochMilli());
            service.updatePermit(deadline, permit.getId());
        }
        if (player.isOnline()) {
            Player onlinePlayer = player.getPlayer();
            onlinePlayer.addAttachment(Essential.getPlugin(), permission, true);
        }
        return true;
    }

    @Override
    public boolean playerAdd(String world, OfflinePlayer player, String permission) {
        return playerAdd(world, player, permission, -1);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return playerAdd(world, Bukkit.getPlayerExact(player), permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return playerRemove(world, Bukkit.getPlayerExact(player), permission);
    }

    @Override
    public boolean playerRemove(String world, OfflinePlayer player, String permission) {
        service.deletePermit(player.getUniqueId(), permission);
        return true;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        Group g = groupMap.get(group);
        if (g == null) {
            return false;
        }
        return g.permissionMap.getOrDefault(permission, false);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        // TODO: 
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        // TODO: 
        return true;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return service.selectConfer(Bukkit.getPlayerExact(player).getUniqueId(), group) != null;
    }

    /**
     * 授予玩家某个权限组
     * @param world 世界
     * @param player 玩家
     * @param group 权限组
     * @param day 期限
     * @return 成功
     */
    public boolean playerAddGroup(String world, OfflinePlayer player, String group, int day) {
        Confer confer = service.selectConfer(player.getUniqueId(), group);
        if (confer == null) {
            long deadline = (day == -1 ? -1 : Instant.now().plus(day, ChronoUnit.DAYS).toEpochMilli());
            service.insertConfer(player.getUniqueId(), group, world, deadline);
        } else {
            long deadline = (day == -1 ? -1 : TimeUtils.getInstant(confer.getDeadline()).plus(day, ChronoUnit.DAYS).toEpochMilli());
            service.updateConfer(deadline, confer.getId());
        }
        String prefixString = groupMap.get(group).prefix;
        Prefix prefix = service.selectPrefix(player.getUniqueId(), prefixString);
        if (prefix == null) {
            long deadline = (day == -1 ? -1 : Instant.now().plus(day, ChronoUnit.DAYS).toEpochMilli());
            service.insertPrefix(player.getUniqueId(), prefixString, deadline);
        } else {
            long deadline = (day == -1 ? -1 : TimeUtils.getInstant(prefix.getDeadline()).plus(day, ChronoUnit.DAYS).toEpochMilli());
            service.updatePrefix(deadline, prefix.getId());
        }
        
        Group g = groupMap.get(group);
        if (player.isOnline() && g != null) {
            Player onlinePlayer = player.getPlayer();
            for (Entry<String,Boolean> entrySet : g.permissionMap.entrySet()) {
                onlinePlayer.addAttachment(Essential.getPlugin(), entrySet.getKey(), entrySet.getValue());
            }
        }
        return true;
    }

    @Override
    public boolean playerAddGroup(String world, OfflinePlayer player, String group) {
        return playerAddGroup(world, player, group, -1);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return playerAddGroup(world, Bukkit.getPlayerExact(player), group);
    }

    @Override
    public boolean playerRemoveGroup(String world, OfflinePlayer player, String group) {
        service.deleteConfer(player.getUniqueId(), group);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return playerRemoveGroup(world, Bukkit.getPlayerExact(player), group);
    }

    /**
     * 获取 玩家所在的所有组
     * @param world 世界
     * @param player 玩家
     * @return 权限组
     */
    @Override
    public String[] getPlayerGroups(String world, OfflinePlayer player) {
        List<String> groups = new ArrayList<>();
        for (Confer confer : service.selectConfers(player.getUniqueId())) {
            // 判断是否过期
            if (confer.getDeadline() != -1 &&TimeUtils.getInstant(confer.getDeadline()).isBefore(Instant.now())) {
                service.deleteConfer(player.getUniqueId(), confer.getGroup());
                continue;
            }
            groups.add(confer.getGroup());
        }
        groups.add("default");
        return groups.toArray(new String[0]);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return getPlayerGroups(world, Bukkit.getPlayerExact(player));
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return "";
    }

    @Override
    public String[] getGroups() {
        return groupList.toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    class Group {
        public final String name;
        public final String prefix;
        public final String desc;
        public final Map<String, Boolean> permissionMap = new HashMap<>();
        public Group(String name, String prefix, String desc, List<String> permissions) {
            this.name = name;
            this.prefix = prefix;
            this.desc = desc;
            for (String p : permissions) {
                if (!p.contains(",")) {
                    continue;
                }
                String[] kv = p.split(",");
                permissionMap.put(kv[0], Boolean.parseBoolean(kv[1]));
            }
        }
    }
    
}
