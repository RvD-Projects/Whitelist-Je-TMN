package discord;

import java.util.EnumSet;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import commands.discords.DeleteUserDbCmd;
import commands.discords.FetchUserDbCmd;
import commands.discords.LookupMcPlayerCommand;
import commands.discords.RegisterCommand;
import commands.discords.ServerCommand;
import commands.discords.SetUserLanguageCmd;
import configs.ConfigManager;
import events.discords.OnGuildMemberJoin;
import events.discords.OnGuildMemberRemove;
import events.discords.OnGuildMemberUpdate;
import events.discords.OnUserConfirm;
import io.sentry.ISpan;
import io.sentry.SpanStatus;
import locals.Lang;
import locals.LocalManager;
import main.WhitelistJe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import services.sentry.SentryService;

public class DiscordManager {
    public WhitelistJe plugin;
    public JDA jda;

    private Guild guild;
    private Logger logger;
    private String ownerId;
    private String guildId;
    private String inviteUrl;
    private ConfigManager configs;
    private boolean isPrivateBot = true;
    private String servername = "Discord® Server";

    public DiscordManager(WhitelistJe plugin) {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("DiscordManager");

        this.logger = Logger.getLogger("WJE:" + this.getClass().getSimpleName());
        this.plugin = plugin;
        this.configs = plugin.getConfigManager();

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
            final String token = this.configs.get("discordBotToken", null);
            this.logger.warning("JDA TOKEN: " + token.substring(0, 64) + "********");

            jda = JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class))
                    .build()
                    .awaitReady();

            this.checkGuild();
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
            if (jda.getGuilds().size() > 1) {
                this.logger.warning("Discord bot's tokken already in use on another DS !!!");
            }

            guildId = this.configs.get("discordServerId", null);
            this.guild = jda.getGuildById(guildId);
            this.servername = this.guild.getName();
            this.guildId = this.guild.getId();
            this.inviteUrl = this.setInvite();
            this.ownerId = this.guild.getOwnerId();
        } catch (Exception e) {
            this.logger.warning("Discord bot's not authorized into this guild. (Check: "
                    + this.configs.getClass().getSimpleName() + ")");
        }
    }

    private void setupListener() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("DiscordManager.setupListener");

        jda.addEventListener(new OnUserConfirm(plugin));
        jda.addEventListener(new OnGuildMemberJoin(plugin));
        jda.addEventListener(new OnGuildMemberUpdate(plugin));
        jda.addEventListener(new OnGuildMemberRemove(plugin));

        process.setStatus(SpanStatus.OK);
        process.finish();
    }

    private void setupCommands() {
        ISpan process = plugin.getSentryService().findWithuniqueName("onEnable")
                .startChild("DiscordManager.setupCommands");

        final LocalManager LOCAL = WhitelistJe.LOCALES;

        try {
            String[] langArr = LOCAL.getBaseLangs();

            for (int i = 0; i < langArr.length; i++) {
                LOCAL.setNextLang(langArr[i]);
                // Serveur
                ServerCommand.REGISTER_CMD(jda, plugin);
            }

            for (int i = 0; i < langArr.length; i++) {
                LOCAL.setNextLang(langArr[i]);
                // Enregistrer
                RegisterCommand.REGISTER_CMD(jda, plugin);
            }

            for (int i = 0; i < langArr.length; i++) {
                LOCAL.setNextLang(langArr[i]);
                // Recherche
                LookupMcPlayerCommand.REGISTER_CMD(jda, plugin);
            }

            for (int i = 0; i < langArr.length; i++) {
                LOCAL.setNextLang(langArr[i]);
                // User transaltion
                SetUserLanguageCmd.REGISTER_CMD(jda, plugin);
            }

            for (int i = 0; i < langArr.length; i++) {
                LOCAL.setNextLang(langArr[i]);
                // User db fetch
                FetchUserDbCmd.REGISTER_CMD(jda, plugin);
            }

            for (int i = 0; i < langArr.length; i++) {
                LOCAL.setNextLang(langArr[i]);
                // User db remove
                DeleteUserDbCmd.REGISTER_CMD(jda, plugin);
            }

        } catch (Exception e) {
            this.logger.warning("Failed to initialize DS commands correctly");
            SentryService.captureEx(e);
        }

        LOCAL.nextIsDefault();
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

// TEST BIDON