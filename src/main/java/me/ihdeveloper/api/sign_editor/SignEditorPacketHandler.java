package me.ihdeveloper.api.sign_editor;

import me.ihdeveloper.api.SignEditorAPI;
import me.ihdeveloper.api.sign_editor.reflection.SignReflection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

public final class SignEditorPacketHandler extends PacketHandler {
    private final SignReflection reflection;

    public SignEditorPacketHandler(Plugin plugin, SignReflection reflection) {
       super(plugin);
       this.reflection = reflection;
    }

    @Override
    public void onSend(SentPacket sentPacket) {
        /* We don't have to intercept any packets being sent */
    }

    @Override
    public void onReceive(ReceivedPacket receivedPacket) {
        if (receivedPacket.getPacketName().equals("PacketPlayInUpdateSign")) {
            Player player = receivedPacket.getPlayer();

            if (player == null) {
                return;
            }

            if (SignEditorAPI.isInEditor(player)) {
                String[] lines = reflection.readLines(receivedPacket.getPacket());
                SignEditorAPI.close(player, lines);
            }
        }
    }

}
