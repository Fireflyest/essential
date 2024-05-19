package org.fireflyest.essential.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fireflyest.craftdatabase.yaml.YamlService;
import org.fireflyest.craftitem.builder.ItemBuilder;

public class EssentialYaml extends YamlService {

    private Map<String, ItemBuilder> itemMap = new HashMap<>();
    private FileConfiguration world;
    private FileConfiguration group;
    private FileConfiguration ship;

    /**
     * 文件数据
     * @param plugin 插件
     */
    public EssentialYaml(@Nonnull JavaPlugin plugin) {
        super(plugin);
        this.setupConfig(Config.class);
        this.setupLanguage(Language.class, Config.LANGUAGE);
        this.setupItems(itemMap);
        this.world = this.loadYamlFile("worlds");
        this.group = this.loadYamlFile("groups");
        this.ship = this.loadYamlFile("ships");
    }

    /**
     * 多世界配置
     * @return 配置文件
     */
    public FileConfiguration getWorld() {
        return world;
    }

    /**
     * 权限组配置
     * @return 配置文件
     */
    public FileConfiguration getGroup() {
        return group;
    }

    /**
     * 好友关系配置
     * @return 配置文件
     */
    public FileConfiguration getShip() {
        return ship;
    }

    /**
     * 获取物品
     */
    public ItemBuilder getItemBuilder(String name) {
        return itemMap.get(name);
    }

    /**
     * 获取一些列物品
     * @param name 名称
     * @return 集
     */
    public Set<ItemBuilder> getItemBuilders(String name) {
        Set<ItemBuilder> builders = new HashSet<>();
        Iterator<Entry<String,ItemBuilder>> iterator = itemMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<String, ItemBuilder> entry = iterator.next();
            if (entry.getKey().startsWith(name)) {
                builders.add(entry.getValue());
            }
        }
        return builders;
    }

}
