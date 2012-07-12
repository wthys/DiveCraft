package be.zardof.divecraft;

import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DiveCraftEventListener implements Listener {

	DiveCraft _plugin;
	private static int CHESTSLOT = 38;

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

			if (!p.hasPermission("divecraft.helmets")) {
				// No permission to use diving helmets, take drowning damage
				return;
			}

			ItemStack helmet_stack = p.getInventory().getHelmet();
			if (helmet_stack == null) {
				// No helmet
				return;
			}

			int helmet = helmet_stack.getTypeId();
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

			if (!p.hasPermission("divecraft.helmets")) {
				// No ability to use diving helmets, take drowning damage
				return;
			}

			if (p.getLastDamageCause().getCause() == DamageCause.DROWNING) {
				ItemStack helmet_stack = p.getInventory().getHelmet();
				if (helmet_stack == null) {
					pde.setDeathMessage(p.getName() + " forgot to bring enough "
							+ "diving equipment and drowned");
					return;
				}

				int helmet = helmet_stack.getTypeId();
				if (_plugin.isDiveHelmet(helmet)) {
					pde.setDeathMessage(p.getName() + " forgot to bring enough "
							+ "diving equipment and drowned");
				}
			}
		}
	}

	@EventHandler
	public void onInventoryChange(InventoryClickEvent event) {
		if (event.getSlot() != CHESTSLOT) {
			// we have no business here
			return;
		}

		Player p = (Player) event.getInventory().getHolder();
		if (!p.hasPermission("divecraft.tanks")) {
			// No ability to use tanks, do nothing
			p.setMaximumAir(300);
			return;
		}

		ItemStack after = event.getCursor();
		Material head = p.getLocation().add(0, 1, 0).getBlock().getType();
		int lung = _plugin.getLungCapacity();
		if (after == null || after.getType().equals(Material.AIR)
				|| !_plugin.isTank(after.getType())) {
			p.setMaximumAir(lung);
		} else {
			int tank = _plugin.getTankCapacity(after.getType());

			p.setMaximumAir(lung + tank);
		}

		if (!(head.equals(Material.WATER) || head
				.equals(Material.STATIONARY_WATER))
				|| p.getRemainingAir() > p.getMaximumAir()) {
			p.setRemainingAir(p.getMaximumAir());
		}
	}
}
