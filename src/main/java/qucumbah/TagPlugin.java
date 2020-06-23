package qucumbah;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TagPlugin extends JavaPlugin implements Listener {
  private TagGame gameInstance;

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
    gameInstance = TagGame.getInstance();
  }

  private boolean gameIsOngoing() {
    return gameInstance != null && gameInstance.isOngoing();
  }

  @EventHandler
  public void onPlayerUse(PlayerInteractEvent interactEvent) {
    if (!gameIsOngoing()) {
      return;
    }

    gameInstance.handlePlayerUse(
        interactEvent.getPlayer(),
        interactEvent.getHand(),
        interactEvent.getAction()
    );
  }

  @EventHandler
  public void handlePlayerDamage(EntityDamageByEntityEvent damageEvent) {
    if (!gameIsOngoing()) {
      return;
    }

    Entity attacker = damageEvent.getDamager();
    Entity attacked = damageEvent.getEntity();

    if (!(attacker instanceof Player) || !(attacked instanceof Player)) {
      return;
    }

    gameInstance.handlePlayerAttack((Player)attacker, (Player)attacked);
  }

  @EventHandler
  public void handlePlayerDeath(EntityDeathEvent deathEvent) {
    if (!gameIsOngoing()) {
      return;
    }

    Entity deadEntity = deathEvent.getEntity();
    if (!(deadEntity instanceof Player)) {
      return;
    }

    gameInstance.handlePlayerDeath((Player)deadEntity);
  }

  private boolean handleGameStart(CommandSender sender, String[] args) {
    if (gameInstance.isOngoing()) {
      sender.sendMessage("The game has already started");
    }

    if (!(sender instanceof Player)) {
      sender.sendMessage("This command can only be run by a player");
      return false;
    }

    if (args.length != 1) {
      return false;
    }
	
    Player hunter = (Player)sender;
    Player victim = Bukkit.getServer().getPlayer(args[0]);
	
    if (victim == null) {
      sender.sendMessage(args[0] + " is not online");
      return false;
    }

    gameInstance.startGame(hunter, victim);
    return true;
  }

  private boolean handleGameAbort(CommandSender sender) {
    if (!gameInstance.isOngoing()) {
      sender.sendMessage("The game hasn't yet been started");
    }

    gameInstance.endGame();
    return true;
  }

  private boolean handleBorderReset(CommandSender sender) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("This command can only be run by a player");
      return false;
    }

    World world = ((Player)sender).getWorld();
    BukkitUtil.removeBorder(world);
    return true;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    switch (command.getName()) {
      case "tagstart":
        return handleGameStart(sender, args);
      case "tagabort":
        return handleGameAbort(sender);
      case "tagresetborder":
        return handleBorderReset(sender);
      default:
        return false;
    }
  }
}
