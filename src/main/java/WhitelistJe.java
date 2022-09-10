package WhitelistJe;

import java.net.http.WebSocket.Listener;
import java.sql.ResultSet;

import org.bukkit.plugin.java.JavaPlugin;

import dao.UsersDao;


public final class WhitelistJe extends JavaPlugin implements Listener {

    public WhitelistJe instance;
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        this.instance = this;
        this.discordManager = new DiscordManager(this);
        
        UsersDao dao = new UsersDao();
        dao.findAll();
    }

    @Override
    public void onDisable() {
       discordManager.disconnect();
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }
}
