package me.ihdeveloper.api.sign_editor;

import me.ihdeveloper.api.SignEditorAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.lang.reflect.Method;

public final class SignEditorPacketHandler extends PacketHandler {

    public SignEditorPacketHandler(Plugin plugin) {
       super(plugin);
    }

    @Override
    public void onSend(SentPacket sentPacket) {
        // We don't have to intercept any packet being sent
    }

    @Override
    public void onReceive(ReceivedPacket receivedPacket) {
        if (receivedPacket.getPacketName().equals("PacketPlayInUpdateSign")) {
            Player player = receivedPacket.getPlayer();

            if (player == null) {
                return;
            }

            if (SignEditorAPI.isInEditor(player)) {
                try {
                    Class<?> nmsClass$IChatBaseComponent = ReflectionUtils.getNMSClass("IChatBaseComponent");
                    Class<?> craftClass$CraftChatMessage = ReflectionUtils.getCraftClass("util.CraftChatMessage");
                    Method craftChatMessage$fromComponent = craftClass$CraftChatMessage.getMethod("fromComponent", nmsClass$IChatBaseComponent);

                    Object[] linesAsComponents = (Object[]) receivedPacket.getPacketValue("b");
                    String[] lines = new String[4];

                    for (int line = 0; line < 4; line++) {
                        lines[line] = (String) craftChatMessage$fromComponent.invoke(null, linesAsComponents[line]);
                    }

                    SignEditorAPI.close(player, lines);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

}
