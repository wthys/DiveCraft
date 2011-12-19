/**
 * 
 */
package be.zardof.divecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Robbe <robbeuh@zardof.be>
 *
 */
public class DiveCraft extends JavaPlugin {
	
	protected FileConfiguration _config;
	
	Logger log = Logger.getLogger("Minecraft");
	
	DiveCraftDamageListener _dcdl = new DiveCraftDamageListener(this);
	DiveCraftDeathListener _dcdeath = new DiveCraftDeathListener(this);
	
	Map<Integer, Integer> _helmets = new HashMap<Integer, Integer>();
	private int _diveFuel;
	

	@Override
	public void onDisable() {
		log.info("Disabled " + getVersionString());
	}

	@Override
	public void onEnable() {
		_config = getConfig();
		
		for (Map<String, Object> helmet: _config.getMapList("helmets")) {
			Integer helmet_id = (Integer) helmet.get("item");
			Integer helmet_usage = (Integer) helmet.get("usage");
			_helmets.put(helmet_id, helmet_usage);
		}
		
		_diveFuel = _config.getInt("fuel");
		
		_config.options().copyDefaults(true);
		saveConfig();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, _dcdl, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DEATH, _dcdeath, Event.Priority.Normal, this);
		
		log.info("Enabled " + getVersionString() );
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

}