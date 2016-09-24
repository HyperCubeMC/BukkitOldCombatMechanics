package kernitus.plugin.OldCombatMechanics.utilities;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import kernitus.plugin.OldCombatMechanics.ModuleLoader;
import kernitus.plugin.OldCombatMechanics.OCMMain;

/**
 * Created by Rayzr522 on 6/14/16.
 */
public class Config {

	public static final int CONFIG_VERSION = 6;

	private static OCMMain plugin;
	private static FileConfiguration config;

	public static void Initialise(OCMMain plugin) {

		Config.plugin = plugin;
		config = plugin.getConfig();

		if (!checkConfigVersion()) {
			load();
		}
	}


	private static boolean checkConfigVersion() {

		if (config.getInt("config-version") != CONFIG_VERSION) {
			plugin.getLogger().warning("Config version does not match, backing up old config and creating a new one");
			plugin.upgradeConfig();
			reload();
			return true;
		}

		return false;

	}


	public static void reload() {

		if (plugin.doesConfigymlExist()) {
			plugin.reloadConfig();
			config = plugin.getConfig();
		} else
			plugin.upgradeConfig();

		checkConfigVersion();

		plugin.restartTask(); //Restart no-collisions check
		plugin.restartSweepTask(); //Restart sword sweep check
		load();

		OfflinePlayer[] pleyas = Bukkit.getOfflinePlayers();
		Player playa = pleyas[1].getPlayer();
		//World world = playa.getWorld();

		double GAS = plugin.getConfig().getDouble("disable-attack-cooldown.general-attack-speed");

		//if (Config.moduleEnabled("disable-attack-cooldown", world)) {// Setting to no cooldown
			AttributeInstance attribute = playa.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
			double baseValue = attribute.getBaseValue();
			if (baseValue != GAS) {
				attribute.setBaseValue(GAS);
				playa.saveData();
		/*	}
		} else {// Re-enabling cooldown
			AttributeInstance attribute = playa.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
			double baseValue = attribute.getBaseValue();
			if (baseValue != 4) {
				attribute.setBaseValue(4);
				playa.saveData();
			}*/
		}

	}

	private static void load() {

		Messenger.DEBUG_ENABLED = config.getBoolean("debug.enabled");

		WeaponDamages.Initialise(plugin); //Reload weapon damages from config
		ArmourValues.Initialise(plugin); //Reload armour values from config

		ModuleLoader.ToggleModules();

	}

	public static boolean moduleEnabled(String name, World world) {

		ConfigurationSection section = config.getConfigurationSection(name);

		if (section == null) {
			System.err.println("Tried to check module '" + name + "', but it didn't exist!");
			return false;
		}

		if (section.getBoolean("enabled")) {

			if (world != null && section.getList("worlds").size() > 0 && !section.getList("worlds").contains(world.getName())) {

				return false;

			}

			return true;

		}

		return false;

	}

	public static boolean moduleEnabled(String name) {
		return moduleEnabled(name, null);
	}

	public static boolean debugEnabled() {
		return moduleEnabled("debug", null);
	}

	public static List<?> getWorlds(String moduleName) {
		return config.getList(moduleName + ".worlds");
	}

	public static boolean moduleSettingEnabled(String moduleName, String moduleSettingName) {

		return config.getBoolean(moduleName + "." + moduleSettingName);

	}

	public static void setModuleSetting(String moduleName, String moduleSettingName, boolean value) {

		config.set(moduleName + "." + moduleSettingName, value);
		plugin.saveConfig();

	}

}
