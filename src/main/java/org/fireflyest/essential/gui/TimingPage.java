package org.fireflyest.essential.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.fireflyest.craftgui.api.ViewPage;
import org.fireflyest.craftgui.button.ButtonItemBuilder;
import org.fireflyest.craftgui.view.TemplatePage;
import org.fireflyest.craftitem.builder.ItemBuilder;
import org.fireflyest.essential.world.EssentialTimings;
import org.fireflyest.essential.world.EssentialTimings.Line;

public class TimingPage extends TemplatePage {

    private final Pattern pattern = Pattern.compile("[a-zA-Z0-9]+: [0-9a-zA-Z ]+([v ]?)+[\\-0-9a-zA-Z.:\\(\\)\\$]+");
    private final List<ItemStack> temps = new ArrayList<>();
    private Map<String, String> commands;
    private EssentialTimings timings;

    protected TimingPage(int page, EssentialTimings timings) {
        super("§9§l性能监控", null, page, 54);
        this.timings = timings;

        this.refreshPage();
    }

    @Override
    public Map<Integer, ItemStack> getItemMap() {
        asyncButtonMap.clear();
        asyncButtonMap.putAll(buttonMap);

        if (temps.isEmpty()) {
            this.prepare();
        }

        int pos = 0;
        for (int i = page * 45; i < (page + 1) * 45; i++) {
            if (i >= temps.size()) {
                break;
            }
            asyncButtonMap.put(pos++, temps.get(i));
        }

        return asyncButtonMap;
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        switch (slot) {
            case 51:
                timings.refresh();
                temps.clear();
                break;
            case 52:
                timings.reload();
                timings.refresh();
                temps.clear();
                break;
            default:
                break;
        }
        return super.getItem(slot);
    }

    public void prepare() {
        ItemBuilder serverBuilder = new ButtonItemBuilder(Material.WATER_BUCKET)
            .name("§e§l服务器")
            .colorful();
        ItemBuilder entityBuilder = new ButtonItemBuilder(Material.IRON_HORSE_ARMOR)
            .name("§e§l实体")
            .colorful();
        Map<String, ItemBuilder> worldMap = new HashMap<>();
        Map<String, ItemBuilder> taskMap = new HashMap<>();
        Map<String, ItemBuilder> pluginMap = new HashMap<>();
        Map<String, ItemBuilder> commandMap = new HashMap<>();

        for (Line line : timings.getLines()) {
            String color = line.star ? "$<hg=#e17055:#fab1a0>" : "$<hg=#00b894:#55efc4>";
            String end = "$<c=#74b9ff>A§f" + line.avg + ((line.violations > 0) ? " $<c=#a29bfe>V§f" + line.violations : "");

            if (line.name.startsWith("Task")) {
                // 任务
                Matcher matcher = pattern.matcher(line.name);
                String name = "";
                String task = "";
                if (matcher.find()) {
                    name = matcher.group().substring(6);
                    if (name.contains(" ")) {
                        name = name.substring(0, name.indexOf(" "));
                    }
                }
                if (matcher.find()) {
                    task = matcher.group().substring(10);
                    int lastBrackets = task.lastIndexOf("(");
                    task = task.substring(task.lastIndexOf(".") + 1, lastBrackets == -1 ? task.length() : lastBrackets);
                    if (task.contains("$")) {
                        task = task.substring(0, task.indexOf("$"));
                    }
                }
                taskMap.computeIfAbsent(name, k -> new ButtonItemBuilder(Material.BOOK).name("§e§l" + k).colorful());
                taskMap.get(name).lore(this.getStartString(color, task, line.count, line.time) + end);
            } else if (line.name.startsWith("Plugin") && !line.name.equals("Plugins")) {
                // 插件
                Matcher matcher = pattern.matcher(line.name);
                String name = "";
                String event = "";
                if (matcher.find()) {
                    name = matcher.group().substring(8);
                    if (name.contains(" ")) {
                        name = name.substring(0, name.indexOf(" "));
                    }                
                }
                if (matcher.find()) {
                    event = matcher.group().substring(7);
                    int lastBrackets = event.lastIndexOf("(");
                    event = event.substring(event.lastIndexOf(".") + 1, lastBrackets == -1 ? event.length() : lastBrackets);
                    if (event.contains(":")) {
                        event = event.substring(event.indexOf("::") + 2);
                    }   
                }
                pluginMap.computeIfAbsent(name, k -> new ButtonItemBuilder(Material.LAVA_BUCKET).name("§e§l" + k).colorful());
                pluginMap.get(name).lore(this.getStartString(color, event, line.count, line.time) + end);
            } else if (line.name.startsWith("Command") && !line.name.equals("Command Functions")) {
                // 指令
                Matcher matcher = pattern.matcher(line.name);
                String name = "";
                String plugin = "";
                if (matcher.find()) {
                    name = matcher.group().substring(9);                 
                }
                plugin = commands.getOrDefault(name, "Default");
                pluginMap.computeIfAbsent(plugin, k -> new ButtonItemBuilder(Material.COMMAND_BLOCK).name("§e§l" + k).colorful());
                pluginMap.get(plugin).lore(this.getStartString(color, name, line.count, line.time) + end);
            } else if (line.name.contains(" - ")) {
                String[] kv = line.name.split(" - ");
                Material material = this.getTickMaterial(kv[0]);
                worldMap.computeIfAbsent(kv[0], k -> new ButtonItemBuilder(material).name("§e§l" + k).colorful());
                worldMap.get(kv[0]).lore(this.getStartString(color, kv[1], line.count, line.time) + end);
            } else if (line.name.contains("Entity") || line.name.contains("entity")) {
                // 实体
                entityBuilder.lore(this.getStartString(color, line.name, line.count, line.time) + end);
            } else {
                // 服务器
                serverBuilder.lore(this.getStartString(color, line.name, line.count, line.time) + end);
            }
        }

        temps.add(serverBuilder.build());
        temps.add(entityBuilder.build());
        for (ItemBuilder value : worldMap.values()) {
            temps.add(value.build());
        }
        for (ItemBuilder value : taskMap.values()) {
            temps.add(value.build());
        }
        for (ItemBuilder value : pluginMap.values()) {
            temps.add(value.build());
        }
        for (ItemBuilder value : commandMap.values()) {
            temps.add(value.build());
        }
    }

    @Override
    public @Nullable ViewPage getNext() {
        if (next == null && page < 10) {
            next = new TimingPage(page + 1, timings);
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
        ItemStack refresh = new ButtonItemBuilder(Material.STRING)
                .actionEdit()
                .name("§e刷新")
                .build();
        buttonMap.put(51, refresh);
        ItemStack reload = new ButtonItemBuilder(Material.FEATHER)
                .actionEdit()
                .name("§e重计")
                .build();
        buttonMap.put(52, reload);

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

        // 获取所有指令对应的插件
        commands = new HashMap<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            for (String command : plugin.getDescription().getCommands().keySet()) {
                commands.put(command, plugin.getName());
            }
        }
    }
    
    /**
     * 获取头部
     * @param color 颜色
     * @param name 名称
     * @param count 数量
     * @param time 时间
     * @return 文本
     */
    private String getStartString(String color, String name, int count, String time) {
        String start = "§r§7[" + color + name + " §f" + count + "§7] $<c=#ffeaa7>" + time;
        int c = 80 - start.length();
        String blank = " ".repeat(c < 0 ? 0 : c);
        return start + blank;
    }

    /**
     * 获取材料
     * @param key 键
     * @return 材料
     */
    private Material getTickMaterial(String key) {
        if (key.equals("world")) {
            return Material.GRASS_BLOCK;
        } else if (key.equals("world_nether")) {
            return Material.NETHERRACK;
        } else if (key.equals("world_the_end")) {
            return Material.END_STONE;
        } else if (key.startsWith("world")) {
            return Material.STONE_BRICKS;
        } else if (key.equals("tickTileEntity")) {
            return Material.SPAWNER;
        } else if (key.equals("tickEntity")) {
            return Material.EGG;
        } else {
            return Material.FIREWORK_ROCKET;
        }
    }

}
