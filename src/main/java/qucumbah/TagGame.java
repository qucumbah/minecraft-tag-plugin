package qucumbah;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

public class TagGame {
  private static TagGame instance = new TagGame();

  public static TagGame getInstance() {
    return instance;
  }

  private Timer gameTimer;

  private TagGame() {
    gameTimer = new Timer(this::executeGameTick, 1000);
  }

  private Player hunter;
  private Player victim;
  private World gameWorld;

  private int secondsLeft;

  private boolean isOngoing;

  public void startGame(Player hunter, Player victim) {
    this.hunter = hunter;
    this.victim = victim;

    gameWorld = hunter.getWorld();

    // Hunter's X and Z coordinates don't change at the start of the game
    int hunterX = hunter.getLocation().getBlockX();
    int hunterZ = hunter.getLocation().getBlockZ();
    int hunterY = gameWorld.getHighestBlockYAt(hunterX, hunterZ) + 1;

    // Victim is at the opposite side of the arena
    final int ARENA_SIZE = 100;
    int victimX = hunterX + ARENA_SIZE - 10;
    int victimZ = hunterZ + ARENA_SIZE - 10;
    int victimY = gameWorld.getHighestBlockYAt(victimX, victimZ) + 1;

    hunter.teleport(new Location(gameWorld, hunterX, hunterY, hunterZ));
    victim.teleport(new Location(gameWorld, victimX, victimY, victimZ));

    int borderCenterX = (hunterX + victimX) / 2;
    int borderCenterZ = (hunterZ + victimZ) / 2;

    BukkitUtil.clearWeatherAndSetDay(gameWorld);

    BukkitUtil.feedAndHealPlayer(hunter);
    BukkitUtil.feedAndHealPlayer(victim);

    BukkitUtil.createBorder(gameWorld, borderCenterX, borderCenterZ, ARENA_SIZE);

    BukkitUtil.clearInventory(hunter);
    BukkitUtil.clearInventory(victim);
    BukkitUtil.giveBasicItemsTo(hunter);
    BukkitUtil.giveBasicItemsTo(victim);
    BukkitUtil.giveTrackingCompassTo(hunter);

    secondsLeft = 120;
    gameTimer.start();
    isOngoing = true;
    Bukkit.broadcastMessage("The game has started!");
    hunter.sendMessage("You are hunting " + victim.getName());
    victim.sendMessage("You are being hunted by " + hunter.getName());
  }
  
  private void executeGameTick() {
    if (secondsLeft == 0) {
      winAsVictim();
    }

    broadcastMessageIfNeeded();

    secondsLeft -= 1;
  }

  public void endGame() {
    BukkitUtil.removeBorder(gameWorld);
    isOngoing = false;
    gameTimer.stop();
  }

  private void winAsVictim() {
    endGame();
    Bukkit.broadcastMessage("Victim (" + victim.getName() + ") has won!");
  }

  private void winAsHunter() {
    endGame();
    Bukkit.broadcastMessage("Hunter (" + hunter.getName() + ") has won!");
  }

  private void broadcastMessageIfNeeded() {
    if (secondsLeft == 60 || secondsLeft == 30 || secondsLeft <= 10) {
      Bukkit.broadcastMessage(secondsLeft + " seconds left");
    }
  }

  public void handlePlayerUse(Player player, EquipmentSlot hand, Action action) {
    if (player != hunter) {
      return;
    }

    if (hunter.getInventory().getItem(hand).getType() != Material.COMPASS) {
      return;
    }

    if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }

    hunter.setCompassTarget(victim.getLocation());
    hunter.sendMessage("Victim is at y=" + victim.getLocation().getBlockY());
  }

  public void handlePlayerDeath(Player deadPlayer) {
    if (deadPlayer == hunter) {
      winAsVictim();
    }

    if (deadPlayer == victim) {
      winAsHunter();
    }
  }

  public void handlePlayerAttack(Player attacker, Player attacked) {
    if (attacker == hunter && attacked == victim) {
      winAsHunter();
    }
  }

  public boolean isOngoing() {
    return isOngoing;
  }
}
