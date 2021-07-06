package me.ihdeveloper.api.sign_editor;

import me.ihdeveloper.api.SignEditorAPI;
import me.ihdeveloper.api.sign_editor.reflection.LatestSignReflection;
import me.ihdeveloper.api.sign_editor.reflection.LegacySignReflection;
import me.ihdeveloper.api.sign_editor.reflection.SignReflection;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.minecraft.MinecraftVersion;

/**
 * Main class for the {@link SignEditorAPI}
 */
public final class Main extends JavaPlugin {
    private final SignReflection signReflection;

    public Main() {
        if (MinecraftVersion.getVersion().olderThan(Minecraft.Version.v1_9_R1)) {
            signReflection = new LegacySignReflection();
        } else {
            signReflection = new LatestSignReflection();
        }
    }

    @Override
    public void onEnable() {
        PacketListenerAPI.addPacketHandler(new SignEditorPacketHandler(this, signReflection));
        SignEditorAPI.initialize(getLogger(), getServer(), signReflection);
        getLogger().info("Sign Editor API by @iHDeveloper is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Sign Editor API by @iHDeveloper is disabled!");
    }
}
