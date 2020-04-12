package de.dal3x.enterprise.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.main.EnterprisePlugin;

public class PlayerOnlineListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Filehandler.loadPlayerData(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLeave(PlayerQuitEvent event) {
		EnterprisePlugin.getInstance().unloadIfOffline(EnterprisePlugin.getInstance().getCompanyForPlayerID(event.getPlayer().getUniqueId()));
		EnterprisePlugin.getInstance().removePlayerData(event.getPlayer().getUniqueId());
	}
}
