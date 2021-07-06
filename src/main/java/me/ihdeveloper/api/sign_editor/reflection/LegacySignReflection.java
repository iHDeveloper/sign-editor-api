package me.ihdeveloper.api.sign_editor.reflection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.resolver.ConstructorResolver;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;

import java.lang.reflect.Array;
import java.util.Objects;

public final class LegacySignReflection implements SignReflection {
    private final NMSClassResolver classResolver;

    /* Player Connection */

    private FieldResolver nmsPlayerFieldResolver;
    private MethodResolver craftPlayerMethodResolver;

    private Class<?> playerConnection$class;
    private MethodResolver playerConnectionMethodResolver;

    /* NMS Logic */

    private Class<?> world$class;

    private Class<?> blockPosition$class;
    private ConstructorResolver blockPositionResolver;

    private Class<?> chatBaseComponent$class;

    private Class<?> chatTextComponent$class;
    private ConstructorResolver chatTextComponentResolver;

    /* Packets */

    private Class<?> packetOpenSignEditor$class;
    private ConstructorResolver packetOpenSignEditorResolver;

    private Class<?> packetUpdateSign$class;
    private ConstructorResolver packetUpdateSignResolver;

    /* Bukkit Material */

    public LegacySignReflection() {
        this.classResolver = new NMSClassResolver();
    }

    @Override
    public Object openSignEditor(Location location) {
        try {
            if (blockPosition$class == null) {
                blockPosition$class = classResolver.resolve("BlockPosition");
            }
            if (packetOpenSignEditor$class == null) {
                packetOpenSignEditor$class = classResolver.resolve("PacketPlayOutOpenSignEditor");
            }
            if (packetOpenSignEditorResolver == null) {
                packetOpenSignEditorResolver = new ConstructorResolver(packetOpenSignEditor$class);
            }

            Object blockPosition = getBlockPositionFromLocation(location);
            packetOpenSignEditorResolver.resolve(new Class[] { blockPosition$class }).newInstance(blockPosition);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public Object updateSign(Location location, String[] lines) {
        try {
            if (world$class == null) {
                world$class = classResolver.resolve("World");
            }
            if (blockPosition$class == null) {
                blockPosition$class = classResolver.resolve("BlockPosition");
            }
            if (chatBaseComponent$class == null) {
                chatBaseComponent$class = classResolver.resolve("IChatBaseComponent");
            }
            if (chatTextComponent$class == null) {
                chatTextComponent$class = classResolver.resolve("ChatTextComponent");
            }
            if (chatTextComponentResolver == null) {
                chatTextComponentResolver = new ConstructorResolver(chatTextComponent$class);
            }
            if (packetUpdateSign$class == null) {
                packetUpdateSign$class = classResolver.resolve("PacketPlayOutUpdateSign");
            }
            if (packetUpdateSignResolver == null) {
                packetUpdateSignResolver = new ConstructorResolver(packetUpdateSign$class);
            }

            Object[] linesAsComponents = (Object[]) Array.newInstance(chatBaseComponent$class, 4);

            for (int line = 0; line < 4; line++) {
                linesAsComponents[line] = chatTextComponentResolver.resolve(new Class[] { String.class }).newInstance(lines[line]);
            }

            packetUpdateSignResolver.resolve(new Class[] { world$class, blockPosition$class, linesAsComponents.getClass() }).newInstance(null, blockPosition$class, linesAsComponents);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendSignChangeToPlayer(Player player, Location location, byte data) {
        player.sendBlockChange(location, Objects.requireNonNull(Material.getMaterial("SIGN_POST")), data);
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
                playerConnection$class = classResolver.resolve("PlayerConnection");
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

    private Object getBlockPositionFromLocation(Location location) {
        try {
            if (blockPosition$class == null) {
                blockPosition$class = classResolver.resolve("BlockPosition");
            }
            if (blockPositionResolver == null) {
                blockPositionResolver = new ConstructorResolver(blockPosition$class);
            }

            return blockPositionResolver.resolve(new Class[] { int.class, int.class, int.class }).newInstance(location.getBlock(), location.getBlockY(), location.getBlockZ());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
