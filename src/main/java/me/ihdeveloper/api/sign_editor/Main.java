package me.ihdeveloper.api.sign_editor;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Sign Editor API by @iHDeveloper is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Sign Editor API by @iHDeveloper is disabled!");
    }

}
