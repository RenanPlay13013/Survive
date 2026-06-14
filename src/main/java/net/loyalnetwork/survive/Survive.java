package net.loyalnetwork.survive;

import lombok.Getter;
import net.loyalnetwork.coffeelib.api.config.CoffeeConfigApi;
import net.loyalnetwork.coffeelib.config.ConfigManager;
import net.loyalnetwork.survive.config.DatabaseConfig;
import net.loyalnetwork.survive.listener.DeathListener;
import net.loyalnetwork.survive.listener.GameListener;
import net.loyalnetwork.survive.listener.QuitListener;
import net.loyalnetwork.survive.listener.StatsListener;
import net.loyalnetwork.survive.persistance.database.DatabaseManager;
import net.loyalnetwork.survive.persistance.repository.MatchRepository;
import net.loyalnetwork.survive.seeder.DataSeeder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Survive extends JavaPlugin {

    @Getter
    private static Survive instance;

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        setupConfig();

        databaseManager = new DatabaseManager(getLogger());
        databaseManager.connect();

        MatchRepository matchRepository = new MatchRepository();

        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new StatsListener(matchRepository, this), this);

        getLogger().info("Survive habilitado!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("Survive desabilitado!");
    }

    //SETUP
    private void setupConfig() {
        RegisteredServiceProvider<CoffeeConfigApi> provider =
                getServer().getServicesManager().getRegistration(CoffeeConfigApi.class);

        if (provider == null) {
            getLogger().severe("CoffeeLib não encontrado! Certifique-se de que coffeelib está no servidor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ConfigManager configManager = provider.getProvider().configManager();
        configManager.getClass();

        ConfigManager.builder(this)
                .register(DatabaseConfig.class)
                .build()
                .load();
    }
}
