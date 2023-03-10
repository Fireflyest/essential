package org.fireflyest.essential.data;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.fireflyest.craftdatabase.yaml.YamlService;

public class EssentialYaml extends YamlService {

    private FileConfiguration world;
    private FileConfiguration group;

    /**
     * 文件数据
     * @param plugin 插件
     */
    public EssentialYaml(@Nonnull JavaPlugin plugin) {
        super(plugin);
        this.setupConfig(Config.class);
        this.setupLanguage(Language.class, Config.LANGUAGE);
        this.world = this.loadYamlFile("worlds");
        this.group = this.loadYamlFile("groups");
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
    
}
