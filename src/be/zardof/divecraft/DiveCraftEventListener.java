package be.zardof.divecraft;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class DiveCraftEventListener implements Listener {

	DiveCraft _plugin;

	public DiveCraftEventListener(DiveCraft diveCraft) {
		_plugin = diveCraft;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING
				&& event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();

			if (!p.hasPermission("divecraft.diver")) {
				// No permission to use diving equipment, take drowning damage
				return;
			}
			
			int helmet = p.getInventory().getHelmet().getTypeId();
			int fuel = _plugin.getDiveFuel();
			
			int usage; // amount of fuel we need for a full refill
			try {
				usage = _plugin.getHelmetUsage(helmet);
			} catch (InvalidHelmetException e) {
				// Invalid helmet, take drowning damage.
				return;
			}
			
			// Only check if we're carrying fuel
			if (p.getInventory().contains(fuel)) {
				int used = 0; // fuel used
				

				for (Entry<Integer, ? extends ItemStack> entry : p
						.getInventory().all(fuel).entrySet()) {
					ItemStack fs = entry.getValue();
					int amount = fs.getAmount();
					int fuel_needed = usage - used;
					if (amount <= fuel_needed) {
						// Not enough fuel in the stack for it to exist
						// anymore
						p.getInventory().clear(entry.getKey());
						used += amount;
					} else {
						// Enough fuel in the stack to just subtract it
						p.getInventory().getItem(entry.getKey())
								.setAmount(amount - fuel_needed);
						used += fuel_needed;
					}

					// If we found enough fuel, stop
					if (used >= usage)
						break;
				}
				// If we had fuel but it was not enough for a full refill,
				// do a partial refill
				int newAir = (p.getMaximumAir() * used) / usage;
				p.setRemainingAir(newAir);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent pde = (PlayerDeathEvent) event;
			Player p = (Player) pde.getEntity();
			
			if (!p.hasPermission("divecraft.diver")) {
				// No permission to use diving equipment, take drowning damage
				return;
			}

			int helmet = p.getInventory().getHelmet().getTypeId();

			if (p.getLastDamageCause().getCause() == DamageCause.DROWNING
					&& _plugin.isDiveHelmet(helmet)) {
				pde.setDeathMessage(p.getName() + " forgot to bring enough diving equipment and drowned");
			}
		}
	}
}
