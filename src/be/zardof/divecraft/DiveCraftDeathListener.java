package be.zardof.divecraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DiveCraftDeathListener implements Listener {

	DiveCraft _plugin;

	public DiveCraftDeathListener(DiveCraft diveCraft) {
		_plugin = diveCraft;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent pde = (PlayerDeathEvent) event;
			Player p = (Player) pde.getEntity();

			int helmet = p.getInventory().getHelmet().getTypeId();

			if (p.getLastDamageCause().getCause() == DamageCause.DROWNING
					&& _plugin.isDiveHelmet(helmet)) {
				pde.setDeathMessage(p.getName() + " forgot to bring enough diving equipment and drowned");
			}
		}
	}
}
