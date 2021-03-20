package kernitus.plugin.OldCombatMechanics.utilities.damage;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class DamageUtils {

    // Damage order: base + potion effects + critical hit + enchantments + armour effects
    // getDamage() returns without armour effects, getFinalDamage() returns with

    /**
     * Get sharpness damage multiplier for 1.9
     * @param level The level of the enchantment
     * @return Sharpness damage multiplier
     */
    public static double getNewSharpnessDamage(int level){
        return level >= 1 ? 1 + (level - 1) * 0.5 : 0;
    }

    /**
     * Get sharpness damage multiplier for 1.8
     * @param level The level of the enchantment
     * @return Sharpness damage multiplier
     */
    public static double getOldSharpnessDamage(int level){
        return level >= 1 ? level * 1.25 : 0;
    }

    /**
     * Check preconditions for critical hit
     * @param p Player to perform checks on
     * @return Whether hit is critical
     */
    public static boolean isCriticalHit(Player p) {
//        try {
//            Class.forName("com.destroystokyo.paper.PaperWorldConfig");
//            org.bukkit.World world = p.getWorld();
//            Object nmsWorld = world.getClass().getMethod("getHandle").invoke(world);
//        } catch (ClassNotFoundException | NoSuchMethodException ignored) {}

        // Removed !p.isSprinting() because you can critical hit while sprinting on 1.8
        return !p.isOnGround() && p.getFallDistance() > 0 &&
                !p.getLocation().getBlock().isLiquid() &&
                !p.isInsideVehicle() &&
                p.getActivePotionEffects().stream().noneMatch(pe -> pe.getType() == PotionEffectType.BLINDNESS);
    }
}
