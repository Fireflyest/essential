package org.fireflyest.essential.gui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.craftitem.builder.ItemBuilder;
import org.fireflyest.essential.Essential;

public class PluginPage extends TemplatePage {

    private List<String> pluginFiles;

    protected PluginPage(int page, int size) {
        super("§9§l插件管理", null, page, size);
    }

    @Override
    public @Nonnull Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        int pos = 0;
        for (int i = page * 45 ; i < (page + 1) * 45; i++) {
            if (i >= pluginFiles.size()) break;
            String pluginFile = pluginFiles.get(i);
            Material material = Material.BUCKET;
            ItemStack item;
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginFile);
            if (plugin != null) {
                material = plugin.isEnabled() ? Material.LAVA_BUCKET : Material.WATER_BUCKET;
                ItemBuilder pluginButtonBuilder = new ButtonItemBuilder(material)
                        .name(String.format("§e§l%s", pluginFile))
                        .lore(String.format("§3§l版本§7: §f%s", plugin.getDescription().getVersion()));
                pluginButtonBuilder.lore("§3§l作者§7: ");
                for (String author : plugin.getDescription().getAuthors()) {
                    pluginButtonBuilder.lore(String.format(" §7• §f%s", author));
                }
                item = pluginButtonBuilder.build();
            } else {
                item = new ButtonItemBuilder(material)
                        .name(String.format("§e§l%s", pluginFile))
                        .lore("§f插件未加载")
                        .lore("§f请检查前置插件是否安装")
                        .build();
            }
            asyncButtonMap.put(pos++, item);
        }
        return asyncButtonMap;
    }

    

    @Override
    public @Nullable ViewPage getNext() {
        if (next == null && page < 10) {
            next = new PluginPage(page + 1, size);
            next.setPre(this);
        }
        return next;
    }

    @Override
    public void refreshPage() {
        ItemStack close = new ButtonItemBuilder(Material.REDSTONE)
                .actionBack()
                .name("§c关闭")
                .build();
        buttonMap.put(53, close);

        ItemStack pre = new ButtonItemBuilder(Material.PAPER)
                .actionPagePre()
                .name("§3◀")
                .build();
        if (page > 0) buttonMap.put(45, pre);
        ItemStack next = new ButtonItemBuilder(Material.PAPER)
                .actionPageNext()
                .name("§3▶")
                .build();
        buttonMap.put(46, next);

        this.pluginFiles = new ArrayList<>();
        // 文件夹
        File folder = Essential.getPlugin().getDataFolder().getParentFile();
        File[] files = folder.listFiles();
        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".jar")) {
                continue;
            }
            pluginFiles.add(this.getPluginFileName(file));
        }
        pluginFiles.sort((o1,  o2) -> o1.charAt(0) > o2.charAt(0) ? 1 : -1);
    }

    private String getPluginFileName(File file) {
        String name = null;
        try (FileInputStream input = new FileInputStream(file);
                    ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(input), Charset.defaultCharset())) {
            
            ZipEntry ze = null;
            while ((ze = zipInputStream.getNextEntry()) != null) {
                if (ze.getName().equals("plugin.yml")) {
                    //读取
                    BufferedReader br = new BufferedReader(new InputStreamReader(zipInputStream,Charset.defaultCharset()));
                    String line;
                    //内容不为空，输出
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("name: ")) {
                            name = line.split(" ")[1];
                            break;
                        }
                    }
                    zipInputStream.closeEntry();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    
}
