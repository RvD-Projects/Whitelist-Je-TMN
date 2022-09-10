package configs;

import java.util.HashMap;
import java.util.List;

public final class ConfigManager {

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private String discordBotToken="MTAxNzk3MTY5MDM0MjQ1NzM1Ng.Gz1fRt.6zMP2hYuyaF-03LW0NPkibe3jCstAqvVzVLYlQ";
    private String discordInviteLink="https://discord.com/api/oauth2/authorize?client_id=1017971690342457356&permissions=8&scope=applications.commands%20bot";

    private String discordServerId="123456789";
    private String discordOwnerUserId="12345679";
    private String discordTextChanelId="12345679";
    private String discordAdminChanelId="12345679";
    private String discordWhitelistChanelId="12345679";
    private String discordAdminRoleId="123456789";
    private String discordModeratorRoleId="123456789";
    private String discordDevRoleId="123456789";

    private String mysqlHost="127.0.0.1";
    private String mysqlPort="3306";
    private String mysqlDb="whitelist_je";
    private String mysqlUser="whitelist_je";
    private String mysqlPass="@whitelist_je2022";
    private String mysqlDefTable="users";

    private String portJava="25565";
    private String portBedrock="19132";
    private String paperMcIp="server.minecraft.tumeniaises.ca";

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private HashMap<String, String> configs = new HashMap<>();

    public ConfigManager() {
        configs.put("discordBotToken", this.discordBotToken);
        configs.put("discordInviteLink", this.discordInviteLink);
        configs.put("discordServerId", this.discordServerId);
        configs.put("discordOwnerUserId", this.discordOwnerUserId);
        configs.put("discordTextChanelId", this.discordTextChanelId);
        configs.put("discordAdminChanelId", this.discordAdminChanelId);
        configs.put("discordWhitelistChanelId", this.discordWhitelistChanelId);
        configs.put("discordAdminRoleId", this.discordAdminRoleId);
        configs.put("discordModeratorRoleId", this.discordModeratorRoleId);
        configs.put("discordDevRoleId", this.discordDevRoleId);
        configs.put("mysqlHost", this.mysqlHost);
        configs.put("mysqlPort", this.mysqlPort);
        configs.put("mysqlDb", this.mysqlDb);
        configs.put("mysqlUser", this.mysqlUser);
        configs.put("mysqlPass", this.mysqlPass);
        configs.put("mysqlDefTable", this.mysqlDefTable);
        configs.put("portJava", this.portJava);
        configs.put("portBedrock", this.portBedrock);
        configs.put("paperMcIp", this.paperMcIp);
    }

    public String get(String key, String defaultValue) {
        String result = this.configs.get(key);
        return result == null ? defaultValue : result;
    }


}
