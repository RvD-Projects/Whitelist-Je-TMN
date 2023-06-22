//modifié


package configs;

import java.util.HashMap;

public final class ConfigManager {

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private final String envType="production";
    private final String discordBotToken="xxxxxxx";

    private final String discordServerId="770057600867237898";
    private final String discordWelcomeChanelId="1088581487470850140";
    private final String discordAdminChanelId="1060368458816172112";
    private final String discordWhitelistChanelId="1091582563971248149";
    private final String botLogChannelId="927283856053252106"; // unused yet
    private final String javaLogChannelId="927283856053252106"; // unused yet

    private final String discordAdminRoleId="1090853358786588795";
    private final String discordModeratorRoleId="1090853850489036811";
    private final String discordDevRoleId="1090854262751371344";
    private final String discordHelperRoleId="1090854380745535538";
    
    private final String dbType="mysql"; //msql or mariadb
    private final String dbHost="localhost";
    private final String dbPort="3306";
    private final String dbName="minecraft";
    private final String dbUser="minecraft";
    private final String dbPass="h6u9effkcj735vcypxxj8durh9rjzkjz";


    private final String dbDefTable="wje_users";
    private final String dbJdbcUrl="jdbc:" + dbType + "://"+ dbHost + ":" + dbPort 
    + "/" + dbName;
    private final String dbMaxConnection="15";
    private final String dbMaxConnectionIDLE="5";

    private final String portJava="25565";
    private final String portBedrock="19132";
    private final String javaIp="tempcity.click";
    private final String bedrockIp="bedrock.tempcity.click";
    private final String showSubWorlddMeteo="false";
    private final String hoursToConfirmMcAccount="48"; // zero to not use this feature

    private final String minecrafInfosLink="https://www.fiverr.com/rvdprojects";
    private final String pluginVersion="2023.3";

    // FR, EN, FR_EN, EN_FR
    private final String defaultLang="EN";
    private final String confirmLinkCmdName="wje-link";

    /////////////////////////// EDIT THE PRIVATE VARS ONLY \\\\\\\\\\\\\\\\\\\\\\\\\\\

    private HashMap<String, String> configs = new HashMap<>();

    public ConfigManager() {
        configs.put("envType", this.envType);
        configs.put("discordBotToken", this.discordBotToken);
        configs.put("discordServerId", this.discordServerId);
        configs.put("botLogChannelId", this.botLogChannelId);
        configs.put("javaLogChannelId", this.javaLogChannelId);
        configs.put("discordWelcomeChanelId", this.discordWelcomeChanelId);
        configs.put("discordAdminChanelId", this.discordAdminChanelId);
        configs.put("discordWhitelistChanelId", this.discordWhitelistChanelId);
        configs.put("discordAdminRoleId", this.discordAdminRoleId);
        configs.put("discordModeratorRoleId", this.discordModeratorRoleId);
        configs.put("discordDevRoleId", this.discordDevRoleId);
        configs.put("discordHelperRoleId", this.discordHelperRoleId);
        configs.put("dbType", this.dbType);
        configs.put("dbHost", this.dbHost);
        configs.put("dbPort", this.dbPort);
        configs.put("dbName", this.dbName);
        configs.put("dbUser", this.dbUser);
        configs.put("dbPass", this.dbPass);
        configs.put("dbDefTable", this.dbDefTable);
        configs.put("dbJdbcUrl", this.dbJdbcUrl);
        configs.put("dbMaxConnection", this.dbMaxConnection);
        configs.put("dbMaxConnectionIDLE", this.dbMaxConnectionIDLE);
        configs.put("portJava", this.portJava);
        configs.put("portBedrock", this.portBedrock);
        configs.put("javaIp", this.javaIp);
        configs.put("bedrockIp", this.bedrockIp);
        configs.put("pluginVersion", this.pluginVersion);
        configs.put("showSubWorlddMeteo", this.showSubWorlddMeteo);
        configs.put("hoursToConfirmMcAccount", this.hoursToConfirmMcAccount);
        configs.put("minecrafInfosLink", this.minecrafInfosLink);
        configs.put("defaultLang", this.defaultLang);
        configs.put("confirmLinkCmdName", this.confirmLinkCmdName);
    }

    public String get(String key, String defaultValue) {
        String result = this.configs.get(key);
        return result == null ? defaultValue : result;
    }

    public String get(String key) {
        return this.configs.get(key);
    }


}
