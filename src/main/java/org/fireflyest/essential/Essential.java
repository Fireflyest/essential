package org.fireflyest.essential;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.fireflyest.craftcommand.argument.EmailArgs;
import org.fireflyest.craftcommand.argument.NumberArgs;
import org.fireflyest.craftcommand.argument.PlayerArgs;
import org.fireflyest.craftcommand.argument.StringArgs;
import org.fireflyest.craftdatabase.sql.SQLConnector;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.bean.Ship;
import org.fireflyest.essential.command.AccountCommand;
import org.fireflyest.essential.command.BackCommand;
import org.fireflyest.essential.command.ChangepwCommand;
import org.fireflyest.essential.command.ChatArgument;
import org.fireflyest.essential.command.ChatCommand;
import org.fireflyest.essential.command.DelhomeCommand;
import org.fireflyest.essential.command.EconomyCommand;
import org.fireflyest.essential.command.EinvCommand;
import org.fireflyest.essential.command.EmailCommand;
import org.fireflyest.essential.command.EnchantArgument;
import org.fireflyest.essential.command.EnchantCommand;
import org.fireflyest.essential.command.FlyCommand;
import org.fireflyest.essential.command.GodCommand;
import org.fireflyest.essential.command.GradientArgument;
import org.fireflyest.essential.command.GroupArgument;
import org.fireflyest.essential.command.GroupCommand;
import org.fireflyest.essential.command.HatCommand;
import org.fireflyest.essential.command.HealCommand;
import org.fireflyest.essential.command.HoldCommand;
import org.fireflyest.essential.command.HomeArgument;
import org.fireflyest.essential.command.HomeCommand;
import org.fireflyest.essential.command.InteractCommand;
import org.fireflyest.essential.command.InvCommand;
import org.fireflyest.essential.command.LoginCommand;
import org.fireflyest.essential.command.LoreCommand;
import org.fireflyest.essential.command.LosepwCommand;
import org.fireflyest.essential.command.MessageCommand;
import org.fireflyest.essential.command.ModeCommand;
import org.fireflyest.essential.command.MoneyCommand;
import org.fireflyest.essential.command.MuteArgument;
import org.fireflyest.essential.command.MuteCommand;
import org.fireflyest.essential.command.NameCommand;
import org.fireflyest.essential.command.PayCommand;
import org.fireflyest.essential.command.PermissionArgument;
import org.fireflyest.essential.command.PermissionCommand;
import org.fireflyest.essential.command.PlotAbandonCommand;
import org.fireflyest.essential.command.PlotArgument;
import org.fireflyest.essential.command.PlotCommand;
import org.fireflyest.essential.command.PlotCreateCommand;
import org.fireflyest.essential.command.PlotExpandCommand;
import org.fireflyest.essential.command.PlotGiveCommand;
import org.fireflyest.essential.command.PlotMapCommand;
import org.fireflyest.essential.command.PlotRemoveCommand;
import org.fireflyest.essential.command.PlotTpCommand;
import org.fireflyest.essential.command.PluginsArgument;
import org.fireflyest.essential.command.PluginsCommand;
import org.fireflyest.essential.command.PluginsDisableCommand;
import org.fireflyest.essential.command.PluginsEnableCommand;
import org.fireflyest.essential.command.PluginsLoadArgument;
import org.fireflyest.essential.command.PluginsLoadCommand;
import org.fireflyest.essential.command.PrefixCommand;
import org.fireflyest.essential.command.RegisterCommand;
import org.fireflyest.essential.command.RepairCommand;
import org.fireflyest.essential.command.RideCommand;
import org.fireflyest.essential.command.SethomeCommand;
import org.fireflyest.essential.command.SetwarpCommand;
import org.fireflyest.essential.command.ShipCommand;
import org.fireflyest.essential.command.SkullCommand;
import org.fireflyest.essential.command.SpawnCommand;
import org.fireflyest.essential.command.StructureArgument;
import org.fireflyest.essential.command.StructureTypeArgument;
import org.fireflyest.essential.command.StructureCommand;
import org.fireflyest.essential.command.StructureCreateCommand;
import org.fireflyest.essential.command.StructureMirrorArgument;
import org.fireflyest.essential.command.StructurePlaceCommand;
import org.fireflyest.essential.command.StructureRotationArgument;
import org.fireflyest.essential.command.SudoCommand;
import org.fireflyest.essential.command.SuicideCommand;
import org.fireflyest.essential.command.SunCommand;
import org.fireflyest.essential.command.TableCommand;
import org.fireflyest.essential.command.TopCommand;
import org.fireflyest.essential.command.TpaCommand;
import org.fireflyest.essential.command.TpacceptCommand;
import org.fireflyest.essential.command.TphereCommand;
import org.fireflyest.essential.command.TprefuseCommand;
import org.fireflyest.essential.command.UpCommand;
import org.fireflyest.essential.command.VanishCommand;
import org.fireflyest.essential.command.WarpCommand;
import org.fireflyest.essential.command.WorldArgument;
import org.fireflyest.essential.command.WorldCommand;
import org.fireflyest.essential.command.WorldCreateCommand;
import org.fireflyest.essential.command.WorldTpCommand;
import org.fireflyest.essential.command.WorldUnloadCommand;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.gui.AccountView;
import org.fireflyest.essential.gui.ChunksView;
import org.fireflyest.essential.gui.InteractView;
import org.fireflyest.essential.gui.PermissionView;
import org.fireflyest.essential.gui.PluginView;
import org.fireflyest.essential.gui.PrefixView;
import org.fireflyest.essential.gui.ShipView;
import org.fireflyest.essential.gui.WorldsView;
import org.fireflyest.essential.listener.PlayerEventListener;
import org.fireflyest.essential.listener.WorldEventListener;
import org.fireflyest.essential.protocol.EssentialProtocol;
import org.fireflyest.essential.service.EssentialEconomy;
import org.fireflyest.essential.service.EssentialPermission;
import org.fireflyest.essential.service.EssentialService;
import org.fireflyest.essential.world.Dimension;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * essential.
 */
public class Essential extends JavaPlugin {

    public static final String VIEW_ACCOUNT = "essential.account";
    public static final String VIEW_WORLDS = "essential.worlds";
    public static final String VIEW_CHUNKS = "essential.chunks";
    public static final String VIEW_PLUGIN = "essential.plugin";
    public static final String VIEW_PERMISSION = "essential.permission";
    public static final String VIEW_PREFIX = "essential.prefix";
    public static final String VIEW_SHIP = "essential.ship";
    public static final String VIEW_INTERACT = "essential.interact";

    private EssentialService service;
    private EssentialYaml yaml;
    private EssentialProtocol protocol;
    private EssentialEconomy economy;
    private EssentialPermission permissions;
    private StateCache cache;
    private ViewGuide guide;
    private String url;

    private Map<String, Dimension> worldMap = new HashMap<>();

    /**
     * 插件加载入口
     */
    @Override
    public void onEnable() {
        // 数据
        this.getLogger().info("Enable data service.");
        yaml = new EssentialYaml(this);
        cache = new StateCache();
        try {
            if (Config.SQL) {
                url = Config.URL;
                SQLConnector.setupConnect(SQLConnector.MYSQL, url, Config.USER, Config.PASSWORD);
            } else {
                url = "jdbc:sqlite:" + getDataFolder().getParent() + "/" + this.getClass().getSimpleName() + "/storage.db";
                SQLConnector.setupConnect(SQLConnector.SQLITE, url, null, null);
            }
            service = new EssentialService(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 经济服务
        this.economy = new EssentialEconomy(service);
        this.getServer().getServicesManager().register(Economy.class, economy, this, ServicePriority.Normal);
        // 权限服务
        this.permissions = new EssentialPermission(yaml, service);
        this.getServer().getServicesManager().register(Permission.class, permissions, this, ServicePriority.Normal);
        
        // 界面
        this.setupGuide();

        this.protocol = new EssentialProtocol(guide);
        
        // 监听
        this.getLogger().info("Lunching listener.");
        this.getServer().getPluginManager().registerEvents(new PlayerEventListener(service, yaml, permissions, cache, guide), this);
        this.getServer().getPluginManager().registerEvents(new WorldEventListener(yaml, service, worldMap), this);

        // 指令
        this.setupAccountCommand();
        this.setupBasicCommand();
        this.setupTeleportCommand();
        this.setupEconomyCommand();
        this.setupWorldCommand();
        this.setupGroupCommand();
        this.setupShipCommand();
        this.setupPlotCommand();
        
    }

    @Override
    public void onDisable() {
        // 保存玩家位置
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (StateCache.LOGIN.equals(cache.get(player.getName() + ".account.state"))) {
                service.updateQuit(player.getLocation(), player.getUniqueId());
                player.sendMessage(Language.LOGIN);
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                player.kickPlayer("Reloading...");
            }
        }
        
        // 关闭数据库
        if (service != null) {
            SQLConnector.close(url);
        }
    }

    /**
     * 获取插件对象
     * @return 插件对象
     */
    public static Essential getPlugin() {
        return getPlugin(Essential.class);
    }

    private void setupGuide() {
        RegisteredServiceProvider<ViewGuide> rsp = Bukkit.getServer().getServicesManager().getRegistration(ViewGuide.class);
        if (rsp == null) {
            this.getLogger().warning("CraftGUI not found!");
            return;
        }
        guide = rsp.getProvider();

        guide.addView(VIEW_ACCOUNT, new AccountView(cache));
        guide.addView(VIEW_WORLDS, new WorldsView(yaml));
        guide.addView(VIEW_CHUNKS, new ChunksView());
        guide.addView(VIEW_PLUGIN, new PluginView());
        guide.addView(VIEW_PERMISSION, new PermissionView(service, yaml));
        guide.addView(VIEW_PREFIX, new PrefixView(service));
        guide.addView(VIEW_SHIP, new ShipView(service));
        guide.addView(VIEW_INTERACT, new InteractView(service));
    }

    /**
     * 账户相关指令
     */
    private void setupAccountCommand() {
        PluginCommand login = this.getCommand("login");
        if (login != null) {
            login.setExecutor(new LoginCommand(service, cache));
        }
        PluginCommand register = this.getCommand("register");
        if (register != null) {
            register.setExecutor(new RegisterCommand(service, cache));
        }
        PluginCommand account = this.getCommand("account");
        if (account != null) {
            AccountCommand accountCommand = new AccountCommand();
            EmailCommand emailCommand = new EmailCommand(service);
            emailCommand.setArgument(0, new EmailArgs());
            ChangepwCommand changepwCommand = new ChangepwCommand(service);
            changepwCommand.setArgument(0, new StringArgs("[old_password]"));
            changepwCommand.setArgument(1, new StringArgs("[new_password]"));
            accountCommand.addSubCommand("changepw", changepwCommand);
            accountCommand.addSubCommand("email", emailCommand);
            accountCommand.addSubCommand("losepw", new LosepwCommand(service));
            account.setExecutor(accountCommand);
            account.setTabCompleter(accountCommand);
        }
    }

    /**
     * 基础指令
     */
    private void setupBasicCommand() {
        PluginCommand einv = this.getCommand("einv");
        if (einv != null) {
            EinvCommand einvCommand = new EinvCommand();
            einvCommand.setArgument(0, new PlayerArgs());
            einv.setExecutor(einvCommand);
            einv.setTabCompleter(einvCommand);
        }
        PluginCommand fly = this.getCommand("fly");
        if (fly != null) {
            FlyCommand flyCommand = new FlyCommand();
            flyCommand.setArgument(0, new PlayerArgs());
            fly.setExecutor(flyCommand);
            fly.setTabCompleter(flyCommand);
        }
        PluginCommand hat = this.getCommand("hat");
        if (hat != null) {
            hat.setExecutor(new HatCommand());
        }
        PluginCommand heal = this.getCommand("heal");
        if (heal != null) {
            HealCommand healCommand = new HealCommand();
            healCommand.setArgument(0, new PlayerArgs());
            heal.setExecutor(healCommand);
            heal.setTabCompleter(healCommand);
        }
        PluginCommand inv = this.getCommand("inv");
        if (inv != null) {
            InvCommand invCommand = new InvCommand();
            invCommand.setArgument(0, new PlayerArgs());
            inv.setExecutor(invCommand);
            inv.setTabCompleter(invCommand);
        }
        PluginCommand lore = this.getCommand("lore");
        if (lore != null) {
            LoreCommand loreCommand = new LoreCommand();
            loreCommand.setArgument(0, new StringArgs("[lore]"));
            loreCommand.setArgument(1, new NumberArgs());
            lore.setExecutor(loreCommand);
            lore.setTabCompleter(loreCommand);
        }
        PluginCommand message = this.getCommand("message");
        if (message != null) {
            MessageCommand messageCommand = new MessageCommand();
            messageCommand.setArgument(0, new PlayerArgs());
            message.setExecutor(messageCommand);
            message.setTabCompleter(messageCommand);
        }
        PluginCommand mode = this.getCommand("mode");
        if (mode != null) {
            mode.setExecutor(new ModeCommand());
        }
        PluginCommand name = this.getCommand("name");
        if (name != null) {
            name.setExecutor(new NameCommand());
        }
        PluginCommand plugins = this.getCommand("plugin");
        if (plugins != null) {
            PluginsCommand pluginsCommand = new PluginsCommand(guide);
            PluginsLoadCommand pluginsLoadCommand = new PluginsLoadCommand();
            pluginsLoadCommand.setArgument(0, new PluginsLoadArgument());
            PluginsEnableCommand pluginsEnableCommand = new PluginsEnableCommand();
            pluginsEnableCommand.setArgument(0, new PluginsArgument());
            PluginsDisableCommand pluginsDisableCommand = new PluginsDisableCommand();
            pluginsDisableCommand.setArgument(0, new PluginsArgument());
            pluginsCommand.addSubCommand("load", pluginsLoadCommand);
            pluginsCommand.addSubCommand("enable", pluginsEnableCommand);
            pluginsCommand.addSubCommand("disable", pluginsDisableCommand);
            plugins.setExecutor(pluginsCommand);
        }
        PluginCommand skull = this.getCommand("skull");
        if (skull != null) {
            SkullCommand skullCommand = new SkullCommand();
            skullCommand.setArgument(0, new PlayerArgs());
            skull.setExecutor(skullCommand);
            skull.setTabCompleter(skullCommand);
        }
        PluginCommand sudo = this.getCommand("sudo");
        if (sudo != null) {
            SudoCommand sudoCommand = new SudoCommand();
            sudoCommand.setArgument(0, new PlayerArgs());
            sudoCommand.setArgument(1, new StringArgs("[command]"));
            sudo.setExecutor(sudoCommand);
            sudo.setTabCompleter(sudoCommand);
        }
        PluginCommand suicide = this.getCommand("suicide");
        if (suicide != null) {
            suicide.setExecutor(new SuicideCommand());
        }
        PluginCommand sunday = this.getCommand("sun");
        if (sunday != null) {
            sunday.setExecutor(new SunCommand());
        }
        PluginCommand table = this.getCommand("table");
        if (table != null) {
            table.setExecutor(new TableCommand());
        }
        PluginCommand mute = this.getCommand("mute");
        if (mute != null) {
            MuteCommand muteCommand = new MuteCommand(cache);
            muteCommand.setArgument(0, new PlayerArgs());
            muteCommand.setArgument(1, new NumberArgs());
            muteCommand.setArgument(2, new MuteArgument());
            mute.setExecutor(muteCommand);
            mute.setTabCompleter(muteCommand);
        }
        PluginCommand repair = this.getCommand("repair");
        if (repair != null) {
            repair.setExecutor(new RepairCommand());
        }
        PluginCommand vanish = this.getCommand("vanish");
        if (vanish != null) {
            vanish.setExecutor(new VanishCommand());
        }
        PluginCommand god = this.getCommand("god");
        if (god != null) {
            god.setExecutor(new GodCommand());
        }
        PluginCommand chat = this.getCommand("chat");
        if (chat != null) {
            ChatCommand chatCommand = new ChatCommand(cache);
            chatCommand.setArgument(0, new ChatArgument(cache));
            chat.setExecutor(chatCommand);
            chat.setTabCompleter(chatCommand);
        }
        PluginCommand enchant = this.getCommand("enchant");
        if (enchant != null) {
            EnchantCommand enchantCommand = new EnchantCommand();
            enchantCommand.setArgument(0, new EnchantArgument());
            enchantCommand.setArgument(1, new NumberArgs());
            enchant.setExecutor(enchantCommand);
            enchant.setTabCompleter(enchantCommand);
        }
        PluginCommand interact = this.getCommand("interact");
        if (interact != null) {
            InteractCommand interactCommand = new InteractCommand(guide);
            interactCommand.setArgument(0, new PlayerArgs());
            interact.setExecutor(interactCommand);
            interact.setTabCompleter(interactCommand);
        }
        PluginCommand ride = this.getCommand("ride");
        if (ride != null) {
            RideCommand rideCommand = new RideCommand();
            rideCommand.setArgument(0, new PlayerArgs());
            ride.setExecutor(rideCommand);
            ride.setTabCompleter(rideCommand);
        }
        PluginCommand hold = this.getCommand("hold");
        if (hold != null) {
            HoldCommand holdCommand = new HoldCommand();
            holdCommand.setArgument(0, new PlayerArgs());
            hold.setExecutor(holdCommand);
            hold.setTabCompleter(holdCommand);
        }
    }

    /**
     * 传送指令
     */
    private void setupTeleportCommand() {
        PluginCommand delhome = this.getCommand("delhome");
        if (delhome != null) {
            DelhomeCommand delhomeCommand = new DelhomeCommand(service);
            delhomeCommand.setArgument(0, new HomeArgument(service));
            delhome.setExecutor(delhomeCommand);
            delhome.setTabCompleter(delhomeCommand);
        }
        PluginCommand home = this.getCommand("home");
        if (home != null) {
            HomeCommand homeCommand = new HomeCommand(service, cache);
            homeCommand.setArgument(0, new HomeArgument(service));
            home.setTabCompleter(homeCommand);
            home.setExecutor(homeCommand);
        }
        PluginCommand sethome = this.getCommand("sethome");
        if (sethome != null) {
            SethomeCommand sethomeCommand = new SethomeCommand(service);
            sethomeCommand.setArgument(0, new StringArgs("[name]"));
            sethome.setExecutor(sethomeCommand);
            sethome.setTabCompleter(sethomeCommand);
        }
        PluginCommand tpaccept = this.getCommand("tpaccept");
        if (tpaccept != null) {
            tpaccept.setExecutor(new TpacceptCommand(cache));
        }
        PluginCommand tpa = this.getCommand("tpa");
        if (tpa != null) {
            TpaCommand tpaCommand = new TpaCommand(cache);
            tpaCommand.setArgument(0, new PlayerArgs());
            tpa.setExecutor(tpaCommand);
            tpa.setTabCompleter(tpaCommand);
        }
        PluginCommand tphere = this.getCommand("tphere");
        if (tphere != null) {
            TphereCommand tphereCommand = new TphereCommand(cache);
            tphereCommand.setArgument(0, new PlayerArgs());
            tphere.setExecutor(tphereCommand);
            tphere.setTabCompleter(tphereCommand);
        }
        PluginCommand tprefuse = this.getCommand("tprefuse");
        if (tprefuse != null) {
            tprefuse.setExecutor(new TprefuseCommand(cache));
        }
        PluginCommand setwarp = this.getCommand("setwarp");
        if (setwarp != null) {
            setwarp.setExecutor(new SetwarpCommand(service));
        }
        PluginCommand warp = this.getCommand("warp");
        if (warp != null) {
            WarpCommand warpCommand = new WarpCommand(service, cache);
            warpCommand.setArgument(0, new StringArgs("[name]"));
            warp.setExecutor(warpCommand);
            warp.setTabCompleter(warpCommand);
        }
        PluginCommand back = this.getCommand("back");
        if (back != null) {
            back.setExecutor(new BackCommand(cache));
        }
        PluginCommand spawn = this.getCommand("spawn");
        if (spawn != null) {
            spawn.setExecutor(new SpawnCommand(cache));
        }
        PluginCommand top = this.getCommand("top");
        if (top != null) {
            top.setExecutor(new TopCommand(cache));
        }
        PluginCommand up = this.getCommand("up");
        if (up != null) {
            UpCommand upCommand = new UpCommand(cache);
            upCommand.setArgument(0, new NumberArgs());
            up.setExecutor(upCommand);
        }
    }

    private void setupEconomyCommand() {
        PluginCommand money = this.getCommand("money");
        if (money != null) {
            MoneyCommand moneyCommand = new MoneyCommand(economy);
            moneyCommand.setArgument(0, new PlayerArgs());
            money.setExecutor(moneyCommand);
            money.setTabCompleter(moneyCommand);
        }
        PluginCommand eco = this.getCommand("eco");
        if (eco != null) {
            EconomyCommand economyCommand = new EconomyCommand(economy);
            economyCommand.setArgument(0, new PlayerArgs());
            economyCommand.setArgument(1, new NumberArgs());
            eco.setExecutor(economyCommand);
            eco.setTabCompleter(economyCommand);
        }
        PluginCommand pay = this.getCommand("pay");
        if (pay != null) {
            PayCommand payCommand = new PayCommand(economy);
            payCommand.setArgument(0, new PlayerArgs());
            payCommand.setArgument(1, new NumberArgs());
            pay.setExecutor(payCommand);
            pay.setTabCompleter(payCommand);
        }
    }

    private void setupWorldCommand() {
        PluginCommand world = this.getCommand("world");
        if (world != null) {
            WorldCommand worldCommand = new WorldCommand(guide);
            WorldTpCommand worldTpCommand = new WorldTpCommand();
            worldTpCommand.setArgument(0, new WorldArgument());
            WorldUnloadCommand worldUnloadCommand = new WorldUnloadCommand();
            worldUnloadCommand.setArgument(0, new WorldArgument());
            WorldCreateCommand worldCreateCommand = new WorldCreateCommand();
            worldCommand.addSubCommand("tp", worldTpCommand);
            worldCommand.addSubCommand("unload", worldUnloadCommand);
            worldCommand.addSubCommand("create", worldCreateCommand);
            world.setExecutor(worldCommand);
        }
        PluginCommand structure = this.getCommand("structure");
        if (structure != null) {
            StructureCommand structureCommand = new StructureCommand();
            StructureCreateCommand structureCreateCommand = new StructureCreateCommand();
            structureCreateCommand.setArgument(0, new StructureTypeArgument());
            StructurePlaceCommand structurePlaceCommand = new StructurePlaceCommand();
            structurePlaceCommand.setArgument(0, new StructureArgument());
            structurePlaceCommand.setArgument(1, new StructureRotationArgument());
            structurePlaceCommand.setArgument(2, new StructureMirrorArgument());
            structureCommand.addSubCommand("create", structureCreateCommand);
            structureCommand.addSubCommand("place", structurePlaceCommand);
            structure.setExecutor(structureCommand);
            structure.setTabCompleter(structureCommand);
        }
    }

    private void setupGroupCommand() {
        PluginCommand group = this.getCommand("group");
        if (group != null) {
            GroupCommand groupCommand = new GroupCommand(permissions, guide);
            groupCommand.setArgument(0, new PlayerArgs());
            groupCommand.setArgument(1, new GroupArgument(yaml));
            groupCommand.setArgument(2, new NumberArgs());
            group.setExecutor(groupCommand);
            group.setTabCompleter(groupCommand);
        }
        PluginCommand permission = this.getCommand("permission");
        if (permission != null) {
            PermissionCommand permissionCommand = new PermissionCommand(permissions, guide);
            permissionCommand.setArgument(0, new PlayerArgs());
            permissionCommand.setArgument(1, new PermissionArgument());
            permissionCommand.setArgument(2, new NumberArgs());
            permission.setExecutor(permissionCommand);
            permission.setTabCompleter(permissionCommand);
        }
        PluginCommand prefix = this.getCommand("prefix");
        if (prefix != null) {
            PrefixCommand prefixCommand = new PrefixCommand(service, guide);
            prefixCommand.setArgument(0, new PlayerArgs());
            prefixCommand.setArgument(1, new GradientArgument());
            prefixCommand.setArgument(2, new NumberArgs());
            prefix.setExecutor(prefixCommand);
            prefix.setTabCompleter(prefixCommand);
        }
    }

    private void setupShipCommand() {
        PluginCommand ship = this.getCommand("ship");
        if (ship != null) {
            ShipCommand shipCommand = new ShipCommand(service, guide);
            shipCommand.setArgument(0, new PlayerArgs());
            ship.setExecutor(shipCommand);
            ship.setTabCompleter(shipCommand);
        }
    }

    private void setupPlotCommand() {
        PluginCommand plot = this.getCommand("plot");
        if (plot != null) {
            PlotCommand plotCommand = new PlotCommand();
            PlotCreateCommand plotCreateCommand = new PlotCreateCommand(service, worldMap);
            plotCreateCommand.setArgument(0, new StringArgs("[name]"));
            PlotTpCommand plotTpCommand = new PlotTpCommand(service, cache);
            plotTpCommand.setArgument(0, new PlotArgument(service));
            PlotRemoveCommand plotRemoveCommand = new PlotRemoveCommand(service, economy, cache, worldMap);
            plotRemoveCommand.setArgument(0, new PlotArgument(service));
            PlotGiveCommand plotGiveCommand = new PlotGiveCommand(service, cache, worldMap);
            plotGiveCommand.setArgument(0, new PlotArgument(service));
            plotGiveCommand.setArgument(1, new PlayerArgs());
            plotCommand.addSubCommand("create", plotCreateCommand);
            plotCommand.addSubCommand("tp", plotTpCommand);
            plotCommand.addSubCommand("remove", plotRemoveCommand);
            plotCommand.addSubCommand("give", plotGiveCommand);
            plotCommand.addSubCommand("expand", new PlotExpandCommand(economy, worldMap));
            plotCommand.addSubCommand("abandon", new PlotAbandonCommand(economy, worldMap));
            plotCommand.addSubCommand("map", new PlotMapCommand(service, economy, worldMap));
            plot.setExecutor(plotCommand);
            plot.setTabCompleter(plotCommand);
        }
    }

}
