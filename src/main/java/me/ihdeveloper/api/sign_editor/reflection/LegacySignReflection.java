package me.ihdeveloper.api.sign_editor.reflection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.resolver.ConstructorResolver;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.MethodResolver;
import org.inventivetalent.reflection.resolver.ResolverQuery;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.inventivetalent.reflection.resolver.minecraft.OBCClassResolver;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Reflection that supports older than 1.9
 */
public final class LegacySignReflection implements SignReflection {
    private final NMSClassResolver nmsClassResolver = new NMSClassResolver();
    private final OBCClassResolver obcClassResolver = new OBCClassResolver();

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

    private Class<?> craftChatMessage$class;
    private MethodResolver craftChatMessageMethodResolver;

    /* Packets */

    private Class<?> packetOpenSignEditor$class;
    private ConstructorResolver packetOpenSignEditorResolver;

    private Class<?> packetUpdateSign$class;
    private ConstructorResolver packetUpdateSignResolver;

    private Class<?> packetInUpdateSign$class;
    private FieldResolver packetInUpdateSignFieldResolver;

    @Override
    public String[] readLines(Object packet) {
        try {
            if (chatBaseComponent$class == null) {
                chatBaseComponent$class = nmsClassResolver.resolve("IChatBaseComponent");
            }
            if (craftChatMessage$class == null) {
                craftChatMessage$class = obcClassResolver.resolve("util.CraftChatMessage");
            }
            if (craftChatMessageMethodResolver == null) {
                craftChatMessageMethodResolver = new MethodResolver(craftChatMessage$class);
            }

            Method componentToString = craftChatMessageMethodResolver.resolve(new ResolverQuery("fromComponent", chatBaseComponent$class));

            if (packetInUpdateSign$class == null) {
                packetInUpdateSign$class = nmsClassResolver.resolve("PacketPlayInUpdateSign");
            }
            if (packetInUpdateSignFieldResolver == null) {
                packetInUpdateSignFieldResolver = new FieldResolver(packetInUpdateSign$class);
            }

            Object[] linesAsComponent = (Object[]) packetInUpdateSignFieldResolver.resolve("b").get(packet);

            String[] lines = new String[4];

            for (int line = 0; line < 4; line++) {
                lines[line] = (String) componentToString.invoke(null, linesAsComponent[line]);
            }

            return lines;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new String[0];
    }

    @Override
    public Object openSignEditor(Location location) {
        try {
            if (blockPosition$class == null) {
                blockPosition$class = nmsClassResolver.resolve("BlockPosition");
            }
            if (packetOpenSignEditor$class == null) {
                packetOpenSignEditor$class = nmsClassResolver.resolve("PacketPlayOutOpenSignEditor");
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
    public void updateSignToPlayer(Player player, Location location, String[] lines) {
        try {
            if (world$class == null) {
                world$class = nmsClassResolver.resolve("World");
            }
            if (blockPosition$class == null) {
                blockPosition$class = nmsClassResolver.resolve("BlockPosition");
            }
            if (chatBaseComponent$class == null) {
                chatBaseComponent$class = nmsClassResolver.resolve("IChatBaseComponent");
            }
            if (chatTextComponent$class == null) {
                chatTextComponent$class = nmsClassResolver.resolve("ChatTextComponent");
            }
            if (chatTextComponentResolver == null) {
                chatTextComponentResolver = new ConstructorResolver(chatTextComponent$class);
            }
            if (packetUpdateSign$class == null) {
                packetUpdateSign$class = nmsClassResolver.resolve("PacketPlayOutUpdateSign");
            }
            if (packetUpdateSignResolver == null) {
                packetUpdateSignResolver = new ConstructorResolver(packetUpdateSign$class);
            }

            Object[] linesAsComponents = (Object[]) Array.newInstance(chatBaseComponent$class, 4);

            for (int line = 0; line < 4; line++) {
                linesAsComponents[line] = chatTextComponentResolver.resolve(new Class[] { String.class }).newInstance(lines[line]);
            }

            Object packet = packetUpdateSignResolver.resolve(new Class[] { world$class, blockPosition$class, linesAsComponents.getClass() }).newInstance(null, blockPosition$class, linesAsComponents);

            sendPacketToPlayer(player, packet);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
                playerConnection$class = nmsClassResolver.resolve("PlayerConnection");
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
                blockPosition$class = nmsClassResolver.resolve("BlockPosition");
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
