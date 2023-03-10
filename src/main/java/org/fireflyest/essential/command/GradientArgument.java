package org.fireflyest.essential.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;
import org.fireflyest.craftcommand.argument.Argument;

public class GradientArgument implements Argument {

    private final Set<String> hgSet = new HashSet<>();

    public GradientArgument() {
        hgSet.add("$<hg=#ff626e:#ffbe71>");
        hgSet.add("$<hg=#eec9a3:#ef629f>");
        hgSet.add("$<hg=#bb5571:#f0c6b5>");
        hgSet.add("$<hg=#bc95c6:#7dc4cc>");
        hgSet.add("$<hg=#b2b9be:#2f4052>");
        hgSet.add("$<hg=#f6736b:#934f91>");
        hgSet.add("$<hg=#d66d75:#f2aa9d>");
        hgSet.add("$<hg=#6190e8:#a7bfe8>");
        hgSet.add("$<hg=#4da2cb:#67b26f>");
        hgSet.add("$<hg=#d9a7c7:#fffcdc>");
        hgSet.add("$<hg=#06beb6:#028ea1>");
        hgSet.add("$<hg=#f9957e:#f3f5d0>");
        hgSet.add("$<hg=#fdd819:#e04c4c>");
        hgSet.add("$<hg=#fff886:#f072b6>");
        hgSet.add("$<hg=#bb73df:#ff8ddb>");
        hgSet.add("$<hg=#0dcda4:#c2fcd4>");
        hgSet.add("$<hg=#e0b9ff:#ff9a9e>");
        hgSet.add("$<hg=#efbd8a:#d343ba>");
        hgSet.add("$<hg=#9600ff:#e1e1e1>");
        hgSet.add("$<hg=#ff9a9e:#f6e745>");
        hgSet.add("$<hg=#18545a:#f1f2b5>");
        hgSet.add("$<hg=#4ca1af:#c4e0e5>");
    }

    @Override
    public List<String> tab(@Nonnull CommandSender sender, @Nonnull String arg) {
        List<String> ret = new ArrayList<>();
        for (String hg : hgSet) {
            if (hg.startsWith(arg)) {
                ret.add(hg);
            }
        }
        return ret;
    }
    
}
