/**
 * 
 */
package be.zardof.divecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Wim Thys
 * 
 */
public class DiveCraft extends JavaPlugin {

	protected FileConfiguration _config;

	Logger log = Logger.getLogger("Minecraft");

	DiveCraftEventListener _dcel = new DiveCraftEventListener(this);

	Map<Integer, Integer> _helmets = new HashMap<Integer, Integer>();
	private int _diveFuel;
	Map<Integer, Integer> _tanks = new HashMap<Integer, Integer>();

	private int _lungCap;

	@Override
	public void onDisable() {
		log.info("Disabled " + getVersionString());
	}

	@Override
	public void onEnable() {
		_config = getConfig();

		for (Map<?, ?> helmet : _config.getMapList("helmets")) {
			Integer helmet_id = (Integer) helmet.get("item");
			Integer helmet_usage = (Integer) helmet.get("usage");

			if (helmet_usage.intValue() < 1) {
				log.warning("Helmet `" + helmet_id + "' has a usage smaller "
						+ "than 1, it will not be added.");
				continue;
			} else {
				_helmets.put(helmet_id, helmet_usage);
			}
		}

		_diveFuel = _config.getInt("fuel");
		_lungCap = _config.getInt("lung capacity");

		for (Map<?, ?> tank : _config.getMapList("tanks")) {
			Integer tank_id = (Integer) tank.get("item");
			Integer tank_cap = (Integer) tank.get("capacity");

			Material m = Material.getMaterial(tank_id);
			if (m == null) {
				log.warning("Tank `" + tank_id + "' is not a valid material, "
						+ "it will not be added.");
				continue;
			} else {
				int cap = _lungCap + tank_cap;
				if (cap < 1) {
					log.warning("Tank `" + tank_id + "' would result in an "
							+ "invalid total capacity, it will not be added");
					continue;
				} else if (cap >= 300) {
					log.warning("Tank `" + tank_id + "' will cause GUI "
							+ "related oddities in the client (total capacity "
							+ "= " + cap + " >= 300)");
				}
				_tanks.put(tank_id, tank_cap);
			}
		}

		_config.options().copyDefaults(true);
		saveConfig();

		PluginManager pm = this.getServer().getPluginManager();

		// Register events
		pm.registerEvents(_dcel, this);

		log.info("Enabled " + getVersionString());
	}

	private String getVersionString() {
		return getDescription().getFullName();
	}

	public boolean isDiveHelmet(int typeId) {
		return _helmets.containsKey(typeId);
	}

	public int getHelmetUsage(int typeId) throws InvalidHelmetException {
		if (isDiveHelmet(typeId)) {
			return _helmets.get(typeId).intValue();
		}

		throw new InvalidHelmetException();
	}

	public int getDiveFuel() {
		return _diveFuel;
	}

	public boolean isTank(Material type) {
		// TODO Auto-generated method stub
		return isTank(type.getId());
	}

	public boolean isTank(int id) {
		return _tanks.containsKey(id);
	}

	public int getTankCapacity(int id) {
		if (isTank(id))
			return _tanks.get(id);
		else
			return 0;
	}

	public int getTankCapacity(Material type) {
		return getTankCapacity(type.getId());
	}

	public int getLungCapacity() {
		return _lungCap;
	}

}
