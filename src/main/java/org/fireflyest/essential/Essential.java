package org.fireflyest.essential;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.fireflyest.craftcommand.argument.NumberArgs;
import org.fireflyest.craftcommand.argument.PlayerArgs;
import org.fireflyest.craftdatabase.sql.SQLConnector;
import org.fireflyest.craftgui.api.ViewGuide;
import org.fireflyest.essential.command.AccountCommand;
import org.fireflyest.essential.command.BackCommand;
import org.fireflyest.essential.command.ChangepwCommand;
import org.fireflyest.essential.command.DelhomeCommand;
import org.fireflyest.essential.command.EconomyCommand;
import org.fireflyest.essential.command.EinvCommand;
import org.fireflyest.essential.command.EmailCommand;
import org.fireflyest.essential.command.FlyCommand;
import org.fireflyest.essential.command.GradientArgument;
import org.fireflyest.essential.command.GroupArgument;
import org.fireflyest.essential.command.GroupCommand;
import org.fireflyest.essential.command.HatCommand;
import org.fireflyest.essential.command.HealCommand;
import org.fireflyest.essential.command.HomeArgument;
import org.fireflyest.essential.command.HomeCommand;
import org.fireflyest.essential.command.InvCommand;
import org.fireflyest.essential.command.LoginCommand;
import org.fireflyest.essential.command.LoreCommand;
import org.fireflyest.essential.command.LosepwCommand;
import org.fireflyest.essential.command.MessageCommand;
import org.fireflyest.essential.command.ModeCommand;
import org.fireflyest.essential.command.MoneyCommand;
import org.fireflyest.essential.command.NameCommand;
import org.fireflyest.essential.command.PayCommand;
import org.fireflyest.essential.command.PermissionArgument;
import org.fireflyest.essential.command.PermissionCommand;
import org.fireflyest.essential.command.PluginsCommand;
import org.fireflyest.essential.command.PrefixCommand;
import org.fireflyest.essential.command.RegisterCommand;
import org.fireflyest.essential.command.SethomeCommand;
import org.fireflyest.essential.command.SetwarpCommand;
import org.fireflyest.essential.command.SkullCommand;
import org.fireflyest.essential.command.SpawnCommand;
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
import org.fireflyest.essential.command.WarpCommand;
import org.fireflyest.essential.command.WorldCommand;
import org.fireflyest.essential.data.Config;
import org.fireflyest.essential.data.EssentialYaml;
import org.fireflyest.essential.data.Language;
import org.fireflyest.essential.data.StateCache;
import org.fireflyest.essential.gui.AccountView;
import org.fireflyest.essential.gui.ChunksView;
import org.fireflyest.essential.gui.PermissionView;
import org.fireflyest.essential.gui.PluginView;
import org.fireflyest.essential.gui.PrefixView;
import org.fireflyest.essential.gui.WorldsView;
import org.fireflyest.essential.listener.PlayerEventListener;
import org.fireflyest.essential.listener.WorldEventListener;
import org.fireflyest.essential.protocol.EssentialProtocol;
import org.fireflyest.essential.service.EssentialEconomy;
import org.fireflyest.essential.service.EssentialPermission;
import org.fireflyest.essential.service.EssentialService;

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

    private EssentialService service;
    private EssentialYaml yaml;
    private EssentialProtocol protocol;
    private EssentialEconomy economy;
    private EssentialPermission permissions;
    private StateCache cache;
    private ViewGuide guide;
    private String url;

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
        this.getServer().getPluginManager().registerEvents(new WorldEventListener(yaml, service), this);

        // 指令
        this.setupAccountCommand();
        this.setupBasicCommand();
        this.setupTeleportCommand();
        this.setupEconomyCommand();
        this.setupWorldCommand();
        this.setupGroupCommand();
        
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
            accountCommand.addSubCommand("changepw", new ChangepwCommand(service));
            accountCommand.addSubCommand("email", new EmailCommand(service));
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
            lore.setExecutor(new LoreCommand());
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
            plugins.setExecutor(new PluginsCommand(guide));
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
            sudo.setExecutor(new SudoCommand());
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
            sethome.setExecutor(new SethomeCommand(service));
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
            warp.setExecutor(new WarpCommand(service, cache));
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
            up.setExecutor(new UpCommand(cache));
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
            world.setExecutor(new WorldCommand(guide));
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

}
