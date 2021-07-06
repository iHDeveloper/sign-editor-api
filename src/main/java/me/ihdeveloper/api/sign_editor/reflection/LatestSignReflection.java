package me.ihdeveloper.api.sign_editor.reflection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.resolver.ConstructorResolver;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Reflection that supports newer than 1.8.x (aka latest versions)
 */
public final class LatestSignReflection implements SignReflection {
    private final NMSClassResolver classResolver = new NMSClassResolver();

    /* NMS Logic */
    private Class<?> blockPosition$class;
    private ConstructorResolver blockPositionResolver;

    /* NMS Packets */
    private Class<?> packetOpenSignEditor$class;
    private ConstructorResolver packetOpenSignEditorResolver;

    private Class<?> packetInUpdateSign$class;
    private FieldResolver packetInUpdateSignFieldResolver;

    /* NMS Player Connection */
    private FieldResolver nmsPlayerFieldResolver;
    private MethodResolver craftPlayerMethodResolver;

    private Class<?> playerConnection$class;
    private MethodResolver playerConnectionMethodResolver;

    @Override
    public String[] readLines(Object packet) {
        try {
            if (packetInUpdateSign$class == null) {
                packetInUpdateSign$class = classResolver.resolve("network.protocol.game.PacketPlayInUpdateSign");
            }
            if (packetInUpdateSignFieldResolver == null) {
                packetInUpdateSignFieldResolver = new FieldResolver(packetInUpdateSign$class);
            }

            return (String[]) packetInUpdateSignFieldResolver.resolveByFirstType(String[].class).get(packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new String[0];
    }

    @Override
    public Object openSignEditor(Location location) {
        try {
            resolveBlockPosition();

            Object blockPosition = newBlockPositionFromLocation(location);

            if (packetOpenSignEditor$class == null) {
                packetOpenSignEditor$class = classResolver.resolve("network.protocol.game.PacketPlayOutOpenSignEditor");
            }
            if (packetOpenSignEditorResolver == null) {
                packetOpenSignEditorResolver = new ConstructorResolver(packetOpenSignEditor$class);
            }

            return packetOpenSignEditorResolver.resolve(new Class[] { blockPosition$class }).newInstance(blockPosition);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void updateSignToPlayer(Player player, Location location, String[] lines) {
        player.sendSignChange(location, lines);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendSignChangeToPlayer(Player player, Location location, byte data) {
        player.sendBlockChange(location, Objects.requireNonNull(Material.getMaterial("OAK_SIGN")), data);
    }

    @Override
    public void sendPacketToPlayer(Player player, Object packet) {
        try {
            if (craftPlayerMethodResolver == null) {
                craftPlayerMethodResolver = new MethodResolver(player.getClass());
            }

            Object nmsPlayer = craftPlayerMethodResolver.resolve("getHandle").invoke(player);

            if (nmsPlayerFieldResolver == null) {
                nmsPlayerFieldResolver = new FieldResolver(nmsPlayer.getClass());
            }

            if (playerConnection$class == null) {
                playerConnection$class = classResolver.resolve("server.network.PlayerConnection");
            }
            if (playerConnectionMethodResolver == null) {
                playerConnectionMethodResolver = new MethodResolver(playerConnection$class);
            }

            Object playerConnection = nmsPlayerFieldResolver.resolveByFirstType(playerConnection$class).get(nmsPlayer);
            playerConnectionMethodResolver.resolve("sendPacket").invoke(playerConnection, packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Object newBlockPositionFromLocation(Location location) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return blockPositionResolver.resolve(new Class[] { int.class, int.class, int.class }).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /* Resolve Utils */

    private void resolveBlockPosition() throws ClassNotFoundException {
        if (blockPosition$class == null) {
            blockPosition$class = classResolver.resolve("core.BlockPosition");
        }
        if (blockPositionResolver == null) {
            blockPositionResolver = new ConstructorResolver(blockPosition$class);
        }
    }
}
