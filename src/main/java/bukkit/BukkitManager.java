package bukkit;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import commands.bukkit.ConfirmLinkCmd;
import dao.DaoManager;
import events.bukkit.OnPlayerJoin;
import events.bukkit.OnPlayerLoggin;
import events.bukkit.OnServerLoad;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import models.BedrockData;
import models.JavaData;
import services.api.PlayerDbApi;
import services.sentry.SentryService;

public class BukkitManager {
    private WhitelistJe plugin;
    private Logger logger;

    public BukkitManager(WhitelistJe plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("BukkitManager");

        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
        this.registerEvents(plugin);
        this.registerCommands(plugin);

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public Server getServer() {
        return Bukkit.getServer();
    }

    public String getServerInfoString() {
        final String ip = Bukkit.getServer().getIp();
        final String version = Bukkit.getServer().getVersion();
        final String description = Bukkit.getServer().getMotd();
        final GameMode gameMode = Bukkit.getServer().getDefaultGameMode();
        final boolean onlineMode = Bukkit.getServer().getOnlineMode();
        final boolean usingWhiteList = Bukkit.getServer().hasWhitelist();
        final boolean forceWhitelist = Bukkit.getServer().isWhitelistEnforced();
        final String onlineStr = onlineMode ? "`true`" : "`false`";
        final String fwStr = forceWhitelist ? "`true`" : "`false`";

        final String protJ = this.plugin.getConfigManager().get("portJava", "???");
        final String portB = this.plugin.getConfigManager().get("portBedrock", "???");
        final String paperMcIp = this.plugin.getConfigManager().get("paperMcIp", "???");

        StringBuilder sb = new StringBuilder();
        sb.append("\n\tIp: `" + paperMcIp + "`");
        sb.append("\n\tPort Java: `" + protJ + "`");
        sb.append("\n\tPort Bedrock: `" + portB + "`");
        sb.append("\n\tVersion: `" + version + "`");
        sb.append("\n\tOnline Mode: `" + onlineStr + "`");
        sb.append("\n\tWhitelisted: `" + fwStr + "`");
        sb.append("\n\tDefault Gamemode: `" + gameMode.name() + "`");
        sb.append("\n\tDescription: `" + description + "`");

        return sb.toString();
    }

    private void registerEvents(WhitelistJe plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("BukkitManager.registerEvents");
        try {
            Bukkit.getPluginManager().registerEvents(new OnPlayerLoggin(plugin), plugin);
            Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(plugin), plugin);
            Bukkit.getPluginManager().registerEvents(new OnServerLoad(plugin), plugin);
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private void registerCommands(WhitelistJe plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("BukkitManager.registerCommands");

        final String linkCmd = this.plugin.getConfigManager().get("confirmLinkCmdName", "wje-link");

        try {
            this.plugin.getCommand(linkCmd).setExecutor(new ConfirmLinkCmd(this.plugin, linkCmd));
        } catch (Exception e) {
            SentryService.captureEx(e);
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public void sanitizeAnKickPlayer(String uuid) {
        try {
            final UUID UUID = java.util.UUID.fromString(uuid);
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID);
            if (player != null)
                player.setWhitelisted(false);

            Player onlinePlayer = getServer().getPlayer(UUID);
            if (onlinePlayer != null) {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    public void run() {
                        if (onlinePlayer != null) {
                            onlinePlayer.kickPlayer(
                                    "§lVous ne faites plus partie de l'aventure...\n Ce compte n'est plus enregistré.");
                        }
                    }
                });
            }

            if(!plugin.deletePlayerRegistration(UUID)) {
                logger.warning("Could not find any registered player with UUID: " + uuid);
                throw new Exception("Could not find any registered player with UUID: " + uuid);
            }

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public boolean setPlayerAsAllowed(Integer userId, String msgId, boolean allowed, String moderatorId, String uuid, boolean confirmed, String pseudo) {
        final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
        final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

        if(javaData != null && javaData.isAllowed()) {
            javaData.setAsAllowed(msgId, allowed, moderatorId);
            return true;
        }
        
        else if(bedData != null && bedData.isAllowed()) {
            bedData.setAsAllowed(msgId, allowed, moderatorId);
            return true;
        }

        else {
            final String foundJava = PlayerDbApi.getMinecraftUUID(pseudo);
            final String foundXbox = PlayerDbApi.getXboxUUID(pseudo);

            if(foundJava != null && foundJava.equals(uuid)) {
                JavaData data = new JavaData();
                data.setMcName(pseudo);
                if(allowed)
                    data.setAcceptedBy(moderatorId);
                else
                    data.setRevokedBy(moderatorId);
                    
                
                data.setUUID(uuid);
                data.setUserId(userId);
                data.setAsAllowed(msgId, allowed, moderatorId);
                data.setAsConfirmed(confirmed);
                data.save(DaoManager.getJavaDataDao());
                return true;
            }

            else if(foundXbox != null && foundXbox.equals(uuid)) {
                BedrockData data = new BedrockData();
                data.setMcName(pseudo);
                if(allowed)
                    data.setAcceptedBy(moderatorId);
                else
                    data.setRevokedBy(moderatorId);
                
                data.setUUID(uuid);
                data.setUserId(userId);
                data.setAsAllowed(msgId, allowed, moderatorId);
                data.setAsConfirmed(confirmed);
                data.save(DaoManager.getBedrockDataDao());
                return true;
            }
        }

        logger.warning("Could not find any allowed player with UUID: " + uuid);
        return false;
    }


    public void setPlayerAsConfirmed(String uuid) {
        try {
            final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
            final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);

            if(javaData != null && javaData.isAllowed()) {
                javaData.setAsConfirmed(true);
                javaData.save(DaoManager.getJavaDataDao());
            }
            
            else if(bedData != null && bedData.isAllowed()) {
                bedData.setAsConfirmed(true);
                bedData.save(DaoManager.getBedrockDataDao());
            }

            else {
                logger.warning("Could not find any allowed player with UUID: " + uuid);
            }

        } catch (Exception e) {
            SentryService.captureEx(e);
        }
    }

    public Object getPlayerData(String uuid) {
        final JavaData javaData = DaoManager.getJavaDataDao().findWithUuid(uuid);
        final BedrockData bedData = DaoManager.getBedrockDataDao().findWithUuid(uuid);
        return javaData != null ? javaData : bedData != null ? bedData : null; 
    }
}
