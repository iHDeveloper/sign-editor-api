package me.ihdeveloper.api.sign_editor;

import me.ihdeveloper.api.SignEditorAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.packetlistener.PacketListenerAPI;

/**
 * Main class for the {@link SignEditorAPI}
 */
public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        PacketListenerAPI.addPacketHandler(new SignEditorPacketHandler(this));
        SignEditorAPI.initialize(getLogger(), getServer());
        getLogger().info("Sign Editor API by @iHDeveloper is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Sign Editor API by @iHDeveloper is disabled!");
    }
}
