package org.fireflyest.essential.command;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.fireflyest.craftcommand.command.SimpleCommand;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.Essential;
import org.fireflyest.essential.data.Language;

public class WorldCommand extends SimpleCommand {

    private ViewGuide guide;

    /**
     * 世界监管视图
     * @param guide 导航
     */
    public WorldCommand(ViewGuide guide) {
        this.guide = guide;
    }


    @Override
    protected boolean execute(@Nonnull CommandSender sender) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_WORLDS, null);
        return true;
    }


    @Override
    protected boolean execute(@Nonnull CommandSender sender, @Nonnull String arg1) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        guide.openView(player, Essential.VIEW_CHUNKS, arg1);
        return true;
    }

    @Override
    protected boolean execute(CommandSender sender, String arg1, String arg2) {
        Player player = (sender instanceof Player) ? (Player)sender : null;
        if (player == null) {
            sender.sendMessage(Language.ONLY_PLAYER_USE);
            return false;
        }
        switch (arg2) {
            case "tp":
                World world = Bukkit.getWorld(arg1);
                if (world != null) {
                    player.teleport(world.getSpawnLocation());
                }
                break;
            case "create":
                WorldCreator worldCreator = new WorldCreator(arg1);
                Bukkit.createWorld(worldCreator);
                break;
            case "unload":
                Bukkit.unloadWorld(arg1, true);
                break;
            default:
                break;
        }

        return true;
    }
    
}
