package me.ihdeveloper.api.sign_editor.reflection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a reflection helper to support multiple versions of NMS
 */
public interface SignReflection {
    /**
     * Reads an input packet of sign being updated
     *
     * @param packet The input packet of the sign being updated
     * @return The lines stored in the input packet
     */
    String[] readLines(Object packet);

    /**
     * Generates the sign editor packet
     *
     * @param location The location of the sign for the player's world or the game world
     * @return The output packet that includes the required information to open the sign editor
     */
    Object openSignEditor(Location location);

    /**
     * Update sign lines to the player
     *
     * @param player The player that you aim to update the sign for
     * @param location The location of the sign in the player's world
     * @param lines The new values of the lines of the sign
     */
    void updateSignToPlayer(Player player, Location location, String[] lines);

    /**
     * Send a block change that sets a certain block to be a sign.
     * Legacy and latest versions of Bukkit API includes different type of sign Material
     *
     * @param player The player to send the block change to
     * @param location The location of the block
     * @param data Raw data in bytes about the block
     */
    void sendSignChangeToPlayer(Player player, Location location, byte data);

    /**
     * Sends a packet to the player using NMS
     *
     * @param player The player that you are going to send the packet to
     * @param packet The object of the output packet
     */
    void sendPacketToPlayer(Player player, Object packet);
}
