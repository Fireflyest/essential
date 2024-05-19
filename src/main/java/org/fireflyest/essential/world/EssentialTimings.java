package org.fireflyest.essential.world;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.util.NumberConversions;
import org.spigotmc.CustomTimingsHandler;

public class EssentialTimings {

    private final Pattern pattern = Pattern.compile("[a-zA-Z]+: \\d+");
    private final List<Line> lines = new ArrayList<>();
    
    public EssentialTimings() {
        //
    }

    /**
     * 刷新
     */
    public void refresh() {
        lines.clear();
        String[] out = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024 * 1024 * 5);
            PrintStream printStream = new PrintStream(outputStream, true)) {
            CustomTimingsHandler.printTimings(printStream);
            out = outputStream.toString().split("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (out == null || out.length == 0) {
            return;
        }
        for (String lineString : out) {
            Line line = new Line();

            Matcher matcher = pattern.matcher(lineString);
            while (matcher.find()) {
                String[] kv = matcher.group().split(": ");
                switch (kv[0]) {
                    case "Time":
                        line.time = String.format("%.2f", NumberConversions.toInt(kv[1]) / 1000000.0);
                        break;
                    case "Count":
                        line.count = NumberConversions.toInt(kv[1]);
                        break;
                    case "Avg":
                        line.avg = String.format("%.2f", NumberConversions.toInt(kv[1]) / 1000000.0);
                        break;
                    case "Violations":
                        line.violations = NumberConversions.toInt(kv[1]);
                        break;
                    default:
                        break;
                }
            }
            if (line.count > 0) {
                String name = lineString.substring(4, lineString.indexOf("Time: ") - 1);
                line.star = name.startsWith("**");
                line.name = name.replace("** ", "");
                lines.add(line);
            }
        }
    }

    /**
     * 重新计算
     */
    public void reload() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timings on");
        CustomTimingsHandler.reload();
    }

    /**
     * 获取数据
     * @return 所有行
     */
    public List<Line> getLines() {
        return lines;
    }

    public static class Line {
        public String name;
        public String time;
        public int count;
        public String avg;
        public int violations;
        public boolean star;
    }

}
