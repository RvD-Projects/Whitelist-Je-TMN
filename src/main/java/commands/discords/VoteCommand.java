package commands.discords;

import commands.bukkit.VoteCmd;
import helpers.Helper;
import io.sentry.SpanStatus;
import main.WhitelistJe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import services.sentry.SentryService;

public class VoteCommand extends WjeUserOnlyCmd {

    private final static String KEY_CMD_NAME = "CMD_VOTE";
    private final static String KEY_CMD_DESC = "DESC_VOTE";

    public static void REGISTER_CMD(JDA jda, WhitelistJe plugin) {
        String cmdName = LOCAL.translate(KEY_CMD_NAME);
        String cmdDesc = LOCAL.translate(KEY_CMD_DESC);

        // Vote
        jda.addEventListener(new VoteCommand(plugin));
        jda.upsertCommand(cmdName, cmdDesc)
                .submit(true);
    }

    public VoteCommand(WhitelistJe plugin) {
        super(plugin,
                "VoteCommand",
                "CMD_VOTE",
                "custom_voteCommand",
                "Show a vote link embed");
    }

    @Override
    protected final void execute() {
        try {
            final String userLang = user.getLang();
            final String link = LOCAL.translateBy(VoteCmd.linkKey, userLang);
            final String msg = LOCAL.translateBy(VoteCmd.messageKey, userLang);
            final String reply = LOCAL.translateBy("INFO_CHECK_YOUR_MSG", userLang);

            this.sendDiscordVoteLink(member, msg, link);
            submitReplyEphemeral(reply);
            
        } catch (Exception e) {
            tx.setThrowable(e);
            tx.setData("error-state", "error");
            tx.finish(SpanStatus.INTERNAL_ERROR);
            SentryService.captureEx(e);
        }

        tx.setData("state", "infos delivered");
        tx.finish(SpanStatus.OK);
        return;
    }

    private void sendDiscordVoteLink(Member member, String msg, String link) {
        final String discordId = member.getUser().getId();

        this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
            final MessageEmbed msgEmbedded = Helper.jsonToMessageEmbed(VoteCmd.buildLinkEmbed(msg, link));
            Helper.preparePrivateCustomMsg(channel, msgEmbedded, null).submit(true);
        });
    }

}
