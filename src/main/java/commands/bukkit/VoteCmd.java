package commands.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import dao.DaoManager;
import helpers.Helper;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import locals.LocalManager;
import services.sentry.SentryService;
import main.WhitelistJe;
import models.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class VoteCmd extends PlayerBaseCmd {

  public final static String linkKey = "VOTE_LINK";
  public final static String messageKey = "VOTE_TITLE";

  public VoteCmd(WhitelistJe plugin, String cmdName) {
    super(plugin, cmdName);
  }

  @Override
  public void execute(CommandSender sender, Command cmd, String label, String[] args) {
    ITransaction tx = Sentry.startTransaction("Wje-Vote", "custom vote command");

    Player player = (Player) sender;
    LocalManager LOCAL = WhitelistJe.LOCALES;
    String userLang = LOCAL.getNextLang();

    try {
      final JSONObject playerData = plugin.getMinecraftDataJson(player.getUniqueId());

      if (playerData == null) {
        final String msg = LOCAL.translate("NOTREGISTERED") + "\n" +
            LOCAL.translate("USERONLY_CMD") + "\n" +
            LOCAL.translate("DOREGISTER") + " :: " + LOCAL.translate("CONTACT_ADMNIN");
        player.kickPlayer(msg);

        tx.setData("state", "player not yet registered");
        tx.finish(SpanStatus.OK);
        return;
      }

      final Integer userId = playerData.getInt("user_id");
      final User user = DaoManager.getUsersDao().findUser(userId);
      final Member member = plugin.getGuildManager().findMember(user.getDiscordId());

      userLang = user.getLang().toLowerCase();

      final String link = LOCAL.translateBy(VoteCmd.linkKey, userLang);
      final String msg = LOCAL.translateBy(VoteCmd.messageKey, userLang);

      this.sendDiscordVoteLink(member, player, msg, link);

    } catch (Exception e) {
      player.sendMessage(LOCAL.translateBy("CMD_ERROR", userLang));

      tx.setThrowable(e);
      tx.setData("state", "/vote error");
      tx.finish(SpanStatus.INTERNAL_ERROR);
      SentryService.captureEx(e);
    }

    tx.setData("state", "/vote success");
    tx.finish(SpanStatus.OK);
  }

  private void sendDiscordVoteLink(Member member, Player player, String msg, String link) {
    final String discordId = member.getUser().getId();

    final JSONObject clickEvent = new JSONObject();
    clickEvent.put("action", "open_url");
    clickEvent.put("value", link);

    final JSONObject obj = new JSONObject();
    obj.put("text", "§9" + msg);
    obj.put("clickEvent", clickEvent);

    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + obj.toString());

    this.plugin.getDiscordManager().jda.openPrivateChannelById(discordId).queue(channel -> {
      final MessageEmbed msgEmbedded = Helper.jsonToMessageEmbed(VoteCmd.buildLinkEmbed(msg, link));
      Helper.preparePrivateCustomMsg(channel, msgEmbedded, null).submit(true);
    });
  }

  public static String buildLinkEmbed(String msg, String url) {

    final String jsonEmbedded = """
        {
          "content": "",
          "tts": true,
          "embeds": [
            {
              "type": "rich",
              "title": '""" + msg + "'," + """
        "description": "",
        "color": "cc00ff",
        "url": '""" + url + "'" + """
            }
          ]
        }""";

    return jsonEmbedded;
  }
}
