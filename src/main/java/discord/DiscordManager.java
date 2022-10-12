package discord;

import java.util.EnumSet;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import commands.discords.RegisterJavaCommand;
import commands.discords.ServerCommand;
import configs.ConfigManager;
import events.discords.OnUserConfirm;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import services.sentry.SentryService;

public class DiscordManager {
    public WhitelistJe plugin;
    public JDA jda;

    private Logger logger;
    private String servername = "Discord® Server";
    private boolean isPrivateBot = true;
    private String guildId;
    private String inviteUrl;
	private Guild guild;
    private String ownerId;
    static private ConfigManager Configs = new ConfigManager();

    public DiscordManager(WhitelistJe plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager");

        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
        this.connect();
        this.setupCommands();
        this.setupListener();

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public void connect() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager.connect");
        try {
            jda = JDABuilder.create(Configs.get("discordBotToken", null),
                    EnumSet.allOf(GatewayIntent.class))
                    .build()
                    .awaitReady();

            if(this.isPrivateBot) {this.checkGuild();}
            this.plugin.getSentryService().setUsername(this.ownerId);
            this.plugin.getSentryService().setUserId(this.getGuild().getId());
            
            if (jda == null) {
                throw new LoginException("Cannot initialize JDA");
            }
            
        } catch (LoginException | InterruptedException e) {
            process.setThrowable(e);
            process.setStatus(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
            Bukkit.shutdown();
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private String setInvite() {
        InviteAction invite = jda.getGuildById(this.guildId).getTextChannels().get(0).createInvite();
        return invite.setMaxAge(0)
        .complete().getUrl();
    }

    public String getServerName() {
        return this.servername;
    }

    private void checkGuild() {
        String guildId;
        try {
            if(jda.getGuilds().size() > 1) {
                this.logger.warning("Discord bot's tokken already in use on another DS !!!");
            }

            guildId = Configs.get("discordServerId", null);
            this.guild = jda.getGuildById(guildId);
            this.servername = this.guild.getName();
            this.guildId = this.guild.getId();
            this.inviteUrl = this.setInvite();
            this.ownerId = this.guild.getOwnerId();
        } catch (Exception e) {
            this.logger.warning("Discord bot's not authorized into this guild. (Check: " + Configs.getClass().getSimpleName() +")");
        }
    }

    private void setupListener() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager.setupListener");

        jda.addEventListener(new OnUserConfirm(plugin));

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private void setupCommands() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
        .startChild("DiscordManager.setupCommands");

        try {
            // Serveer
            final String srvCmd = this.plugin.getConfigManager().get("serverCmdName", "server");
            jda.addEventListener(new ServerCommand(plugin));
            jda.upsertCommand(srvCmd, "Afficher les informations du serveur `Minecraft®`")
            .queue();

            // Register Java
            final String rgstrJavaCmd = this.plugin.getConfigManager().get("registerCmdName", "register");
            jda.addEventListener(new RegisterJavaCommand(plugin));
            jda.upsertCommand(rgstrJavaCmd + "Java", "S'enregister par Java sur le serveur")
            .addOption(OptionType.STRING, "pseudoJava", "Votre pseudo Java -> `Minecraft®`", true)
            .queue();

            // Register Bedrock
            final String rgstrBedCmd = this.plugin.getConfigManager().get("registerCmdName", "register");
            jda.addEventListener(new RegisterJavaCommand(plugin));
            jda.upsertCommand(rgstrBedCmd + "Bedrock", "S'enregister par Bedrock sur le serveur")
            .addOption(OptionType.STRING, "pseudoJava", "Votre pseudo Bedrock -> `Minecraft®`", true)
            .queue();
    
            // // Whitelist
            // jda.addEventListener(new WhitelistCommand(plugin));
            // jda.upsertCommand("whitelist", "Commande whitelist du
            // serveur").addOption(OptionType.STRING,
            // "action", "add/remove/on/off",true).addOption(OptionType.STRING, "pseudo", "Pseudo du joueur", false)
            // .queue();
    
            // // Deny
            // jda.addEventListener(new DenyCommand(plugin));
            // jda.upsertCommand("deny", "Refuser l'accès au serveur (réservé au
            // staff)").addOption(OptionType.MENTIONABLE,
            // "mention", "Mentionner un membre", true).queue();
            
        } catch (Exception e) {
            this.logger.warning("Failed to initialize DS commands correctly");
        }

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    public void disconnect() {
        if (jda != null)
            jda.shutdownNow();
    }

    public String getInviteUrl() {
        return this.inviteUrl;
    }

	public Guild getGuild() {
		return this.guild;
	}
}
