package qucumbah;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class BukkitUtil {
  private BukkitUtil() {
  }

  public static void clearWeatherAndSetDay(World world) {
    world.setTime(0);
    world.setStorm(false);
    world.setThundering(false);
    final int TICKS_PER_SECOND = 20;
    final int SECONDS_IN_DAY = 600;
    world.setWeatherDuration(TICKS_PER_SECOND * SECONDS_IN_DAY);
  }

  public static void feedAndHealPlayer(Player player) {
    player.setFoodLevel(20);
    player.setSaturation(20);
    player.setHealth(20);
  }

  public static void createBorder(World world, int centerX, int centerZ, int size) {
    if (world == null) {
      return;
    }

    world.getWorldBorder().setCenter(centerX, centerZ);
    world.getWorldBorder().setSize(size);
  }

  public static void removeBorder(World world) {
    if (world == null) {
      return;
    }

    world.getWorldBorder().setCenter(0, 0);
    final int DEFAULT_BORDER_SIZE = 30000000;
    world.getWorldBorder().setSize(DEFAULT_BORDER_SIZE);
  }

  public static void clearInventory(Player player) {
    player.getInventory().clear();
  }

  public static void giveBasicItemsTo(Player player) {
    player.getInventory().addItem(
        new ItemStack(Material.DIAMOND_SWORD),
        new ItemStack(Material.DIAMOND_SHOVEL),
        new ItemStack(Material.DIAMOND_PICKAXE),
        new ItemStack(Material.DIAMOND_AXE),
        new ItemStack(Material.COOKED_BEEF, 16),
        new ItemStack(Material.COBBLESTONE, 16)
    );
  }

  public static void giveTrackingCompassTo(Player player) {
    player.getInventory().addItem(new ItemStack(Material.COMPASS));
  }
}
