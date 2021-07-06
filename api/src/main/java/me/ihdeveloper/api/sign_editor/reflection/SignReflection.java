package me.ihdeveloper.api.sign_editor.reflection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SignReflection {
    String[] readLines(Object packet);

    Object openSignEditor(Location location);

    void updateSignToPlayer(Player player, Location location, String[] lines);
    void sendSignChangeToPlayer(Player player, Location location, byte data);
    void sendPacketToPlayer(Player player, Object packet);
}
