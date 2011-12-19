package be.zardof.divecraft;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class DiveCraftDamageListener extends EntityListener {

	DiveCraft dc;

	public DiveCraftDamageListener(DiveCraft diveCraft) {
		dc = diveCraft;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING
				&& event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			int helmet = p.getInventory().getHelmet().getTypeId();
			int fuel = dc.getDiveFuel();
			
			int usage; // amount of fuel we need for a full refill
			try {
				usage = dc.getHelmetUsage(helmet);
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
}
