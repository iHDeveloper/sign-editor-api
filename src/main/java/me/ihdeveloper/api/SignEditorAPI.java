package me.ihdeveloper.api;

import me.ihdeveloper.api.sign_editor.SignEditorCallback;
import me.ihdeveloper.api.sign_editor.utils.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * An API for using custom sign editor with players.
 */
@SuppressWarnings("deprecation")
public final class SignEditorAPI {
    private static Logger logger;
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
    public static void initialize(Logger logger, Server server) {
        SignEditorAPI.logger = logger;
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

            player.sendBlockChange(fakeSignLocation, Material.SIGN_POST, (byte) 0);

            Class<?> nmsClass$BlockPosition = ReflectionUtils.getNMSClass("BlockPosition");
            Object blockPosition = nmsClass$BlockPosition.getConstructor(int.class, int.class, int.class).newInstance(fakeSignLocation.getBlockX(), fakeSignLocation.getBlockY(), fakeSignLocation.getBlockZ());

            Method player$getHandle = player.getClass().getMethod("getHandle");
            Object nmsPlayer = player$getHandle.invoke(player);

            Field nmsPlayer$connection = nmsPlayer.getClass().getField("playerConnection");

            Class<?> nmsClass$Packet = ReflectionUtils.getNMSClass("Packet");
            Object connection = nmsPlayer$connection.get(nmsPlayer);
            Method connection$sendPacket = connection.getClass().getMethod("sendPacket", nmsClass$Packet);

            if (lines != null && lines.length == 4) {
                Class<?> nmsClass$World = ReflectionUtils.getNMSClass("World");
                Class<?> nmsClass$IChatBaseComponent = ReflectionUtils.getNMSClass("IChatBaseComponent");
                Class<?> nmsClass$ChatComponentText = ReflectionUtils.getNMSClass("ChatComponentText");

                Object[] linesAsComponents = (Object[]) Array.newInstance(nmsClass$IChatBaseComponent, 4);

                for (int line = 0; line < 4; line++) {
                    linesAsComponents[line] = nmsClass$ChatComponentText.getConstructor(String.class).newInstance(lines[line]);
                }

                Class<?> nmsPacket$UpdateSign = ReflectionUtils.getNMSClass("PacketPlayOutUpdateSign");

                Object updateSign = nmsPacket$UpdateSign.getConstructor(nmsClass$World, nmsClass$BlockPosition, linesAsComponents.getClass()).newInstance(null, blockPosition, linesAsComponents);
                connection$sendPacket.invoke(connection, updateSign);
            }

            Class<?> nmsPacket$OpenSignEditor = ReflectionUtils.getNMSClass("PacketPlayOutOpenSignEditor");
            Object openSignEditor = nmsPacket$OpenSignEditor.getConstructor(nmsClass$BlockPosition).newInstance(blockPosition);
            connection$sendPacket.invoke(connection, openSignEditor);

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
