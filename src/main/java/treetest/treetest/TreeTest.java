package treetest.treetest;

import org.bukkit.plugin.java.JavaPlugin;

public final class TreeTest extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new SaplingGrowListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
