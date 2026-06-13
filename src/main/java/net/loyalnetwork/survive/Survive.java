package net.loyalnetwork.survive;

import lombok.Getter;
import net.loyalnetwork.survive.listener.DeathListener;
import net.loyalnetwork.survive.listener.GameListener;
import net.loyalnetwork.survive.listener.QuitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Survive extends JavaPlugin {

    @Getter
    private static Survive instance;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        getLogger().info("Survive habilitado!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Survive desabilitado!");
    }
}
