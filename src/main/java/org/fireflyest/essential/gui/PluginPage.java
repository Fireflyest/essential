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

    private List<PluginFile> pluginFiles;

    protected PluginPage(int page, int size) {
        super("§9§l插件管理", null, page, size);
    }

    @Override
    public @Nonnull Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        int pos = 0;
        for (int i = page * 45; i < (page + 1) * 45; i++) {
            if (i >= pluginFiles.size()) {
                break;
            }
            PluginFile pluginFile = pluginFiles.get(i);
            Material material = Material.BUCKET;
            String color = "§r$<hg=#f0932b:#eb4d4b>";
            ItemStack item;
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginFile.name);
            if (plugin != null) {
                material = plugin.isEnabled() ? Material.LAVA_BUCKET : Material.WATER_BUCKET;
                color = plugin.isEnabled() ? "§r$<hg=#f0932b:#f9ca24>" : "§r$<hg=#f0932b:#22a6b3>";
                ItemBuilder pluginButtonBuilder = new ButtonItemBuilder(material)
                        .actionPlayerCommand((plugin.isEnabled() ? "plugin disable " : "plugin enable ") + pluginFile.name)
                        .name(String.format("%s%s", color, pluginFile.name))
                        .colorful()
                        .lore(String.format("§r$<c=#6ab04c>版本$<c=#f6f6f6>: $<c=#ffbe76>%s", plugin.getDescription().getVersion()));
                pluginButtonBuilder.lore("§r$<c=#6ab04c>作者$<c=#f6f6f6>: ");
                for (String author : plugin.getDescription().getAuthors()) {
                    pluginButtonBuilder.lore(String.format("§r $<c=#f6f6f6>• $<c=#ffbe76>%s", author));
                }
                item = pluginButtonBuilder.build();
            } else {
                item = new ButtonItemBuilder(material)
                        .actionPlayerCommand("plugin load " + pluginFile.file)
                        .name(String.format("%s%s", color, pluginFile.name))
                        .lore("§r$<c=#f6f6f6>插件未加载")
                        .lore(String.format("§r$<c=#6ab04c>文件$<c=#f6f6f6>: $<c=#ffbe76>%s", pluginFile.file))
                        .lore(String.format("§r$<c=#6ab04c>前置$<c=#f6f6f6>: $<c=#ffbe76>%s", pluginFile.depend))
                        .colorful()
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
    public ItemStack getItem(int slot) {
        if (slot == 47) {
            refreshPage();
        }
        return super.getItem(slot);
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
                .name("§r◀")
                .build();
        if (page > 0) {
            buttonMap.put(45, pre);
        }
        ItemStack next = new ButtonItemBuilder(Material.PAPER)
                .actionPageNext()
                .name("§r▶")
                .build();
        buttonMap.put(46, next);
        ItemStack refresh = new ButtonItemBuilder(Material.COMPASS)
                .actionEdit()
                .name("§r刷新")
                .build();
        buttonMap.put(47, refresh);

        this.pluginFiles = new ArrayList<>();
        // 文件夹
        File folder = Essential.getPlugin().getDataFolder().getParentFile();
        File[] files = folder.listFiles();
        for (File file : files) {
            if (!file.isFile() || !file.getName().endsWith(".jar")) {
                continue;
            }
            PluginFile pluginFile = new PluginFile(file.getName());
            this.getPluginFileData(file, pluginFile);
            pluginFiles.add(pluginFile);
        }
        pluginFiles.sort((o1,  o2) -> o1.name.charAt(0) > o2.name.charAt(0) ? 1 : -1);
    }

    private void getPluginFileData(File file, PluginFile pluginFile) {
        int lineLimit = 0;
        try (FileInputStream input = new FileInputStream(file);
                    ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(input), Charset.defaultCharset())) {
            
            ZipEntry ze = null;
            while ((ze = zipInputStream.getNextEntry()) != null) {
                if (ze.getName().equals("plugin.yml")) {
                    //读取
                    BufferedReader br = new BufferedReader(new InputStreamReader(zipInputStream,Charset.defaultCharset()));
                    String line;
                    //内容不为空，输出
                    while ((line = br.readLine()) != null && lineLimit++ < 100) {
                        if (line.startsWith("name: ")) {
                            pluginFile.name = line.split(" ")[1];
                        } else if (line.startsWith("depend: ")) {
                            pluginFile.depend = line.split(" ")[1];
                        }
                    }
                    zipInputStream.closeEntry();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
class PluginFile {
    public PluginFile(String file) {
        this.file = file;
    }
    public String name;
    public String file;
    public String depend;
}

}
