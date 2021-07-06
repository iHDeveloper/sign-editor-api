package me.ihdeveloper.api;

import me.ihdeveloper.api.sign_editor.SignEditorCallback;
import me.ihdeveloper.api.sign_editor.reflection.SignReflection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * An API for using custom sign editor with players.
 */
@SuppressWarnings("deprecation")
public final class SignEditorAPI {
    private static Logger logger;
    private static SignReflection signReflection;
    private static Map<Object, SignEditorCallback> editorCallback;
    private static Map<Object, Material> savedMaterial;
    private static Map<Object, Byte> savedBlockData;
    private static boolean onlineMode;

    /**
     * Initializes the API. Only by the API plugin
     *
     * @param logger Logger of the plugin
     * @param server The bukkit server
     */
    public static void initialize(Logger logger, Server server, SignReflection reflection) {
        SignEditorAPI.logger = logger;
        signReflection = reflection;
        onlineMode = server.getOnlineMode();
        editorCallback = new TreeMap<>();
        savedMaterial = new TreeMap<>();
        savedBlockData = new TreeMap<>();
    }

    /**
     * Checks if the player is using custom sign editor.
     *
     * @param player The player who might be using the sign editor
     * @return True if the player is using the sign editor. False if it's not using the sign editor or using a vanilla one.
     */
    public static boolean isInEditor(Player player) {
        if (onlineMode) {
            return editorCallback.containsKey(player.getUniqueId());
        } else {
            return editorCallback.containsKey(player.getName());
        }
    }

    /**
     * Opens the custom sign editor for the player
     *
     * @param player The player to open the sign editor
     * @param callback A callback for firing events that happens on the sign editor
     * @param lines The default lines of the sign editor
     */
    @SuppressWarnings("unused")
    public static void open(Player player, SignEditorCallback callback, String... lines) {
        try {
            if (isInEditor(player)) {
                logger.warning("Player " + player.getName() + " has the sign editor already opened!");
                logger.warning("This overrides the callback with the new one! This might generate unknown bugs.");
            }

            Location fakeSignLocation = player.getLocation();
            fakeSignLocation.subtract(0, -2, 0);
            Block savedBlock = fakeSignLocation.getBlock();
            Material material = savedBlock.getType();
            byte blockData = savedBlock.getData();

//            player.sendBlockChange(fakeSignLocation, Material.OAK_SIGN, (byte) 0);
            signReflection.sendSignChangeToPlayer(player, fakeSignLocation, (byte) 0);

            if (lines != null && lines.length == 4) {
                Object packet = signReflection.updateSign(fakeSignLocation, lines);
                signReflection.sendPacketToPlayer(player, packet);
            }

            Object packet = signReflection.openSignEditor(fakeSignLocation);
            signReflection.sendPacketToPlayer(player, packet);

            if (onlineMode) {
                editorCallback.put(player.getUniqueId(), callback);
                savedMaterial.put(player.getUniqueId(), material);
                savedBlockData.put(player.getUniqueId(), blockData);
            } else {
                editorCallback.put(player.getName(), callback);
                savedMaterial.put(player.getName(), material);
                savedBlockData.put(player.getName(), blockData);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Fired when the player clicks "Done" on the sign editor.
     * Only used by the API plugin.
     *
     * @param player The player who clicked "Done" on the sign editor.
     * @param lines The submitted lines by the player
     */
    public static void close(Player player, String[] lines) {
        try {
            SignEditorCallback callback;
            Material oldMaterial;
            byte oldBlockData;

            if (onlineMode) {
                callback = editorCallback.remove(player.getUniqueId());
                oldMaterial = savedMaterial.remove(player.getUniqueId());
                oldBlockData = savedBlockData.remove(player.getUniqueId());
            } else {
                callback = editorCallback.remove(player.getName());
                oldMaterial = savedMaterial.remove(player.getName());
                oldBlockData = savedBlockData.remove(player.getName());
            }

            Location fakeSignLocation = player.getLocation();
            fakeSignLocation.subtract(0, -2, 0);

            player.sendBlockChange(fakeSignLocation, oldMaterial, oldBlockData);

            callback.onSubmit(player, lines);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
