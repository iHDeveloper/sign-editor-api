package me.ihdeveloper.api.sign_editor.reflection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface SignReflection {
    Object openSignEditor(Location location);
    Object updateSign(Location location, String[] lines);

    void sendSignChangeToPlayer(Player to, Location location, byte data);
    void sendPacketToPlayer(Player to, Object packet);
}
