package WhitelistJe.events.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import WhitelistJe.WhitelistJe;
import WhitelistJe.functions.WhitelistManager;

public class PlayerJoin {
    private WhitelistJe main;

    public PlayerJoin(WhitelistJe main) {
        this.main = main;
    }

    private WhitelistManager whitelistManager;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!whitelistManager.getPlayersAllowed().contains(player.getName())) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§cLe serveur est sous whitelist discord");
        }
    }
}
