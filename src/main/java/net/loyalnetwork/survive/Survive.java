package net.loyalnetwork.survive;

import net.loyalnetwork.survive.arena.ArenaManager;
import net.loyalnetwork.survive.arena.SimpleArenaManager;
import net.loyalnetwork.survive.match.MatchManager;
import net.loyalnetwork.survive.match.MatchTickTask;
import net.loyalnetwork.survive.match.Matchmaker;
import net.loyalnetwork.survive.match.SimpleMatchManager;
import net.loyalnetwork.survive.queue.Queue;
import org.bukkit.plugin.java.JavaPlugin;

public final class Survive extends JavaPlugin {

    private MatchManager matchManager;
    private ArenaManager arenaManager;
    private Queue queue;

    private MatchTickTask matchTickTask;
    private Matchmaker matchmaker;

    @Override
    public void onEnable() {

        loadManagers();
        loadConfig();
        startSystems();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // cleanup futuramente
    }
    private void loadManagers() {
        this.matchManager = new SimpleMatchManager();
        this.arenaManager = new SimpleArenaManager(); // vamos criar
        this.queue = new Queue();
    }

    private void loadConfig() {
        saveDefaultConfig();

        ArenaConfigLoader loader = new ArenaConfigLoader(this, arenaManager);
        loader.load();
    }

    private void startSystems() {

        this.matchTickTask = new MatchTickTask(matchManager);
        matchTickTask.runTaskTimer(this, 0L, 20L);

        this.matchmaker = new Matchmaker(queue, matchManager, arenaManager);
        matchmaker.runTaskTimer(this, 0L, 20L);
    }


}