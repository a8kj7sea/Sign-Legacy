package me.kirug.signgui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class SignPacketManager {

    public static void openSignEditor(Player player, List<String> lines, Material signType) {
        if (player == null || !player.isOnline()) return;

        BlockPosition position = new BlockPosition(
                player.getLocation().getBlockX(),
                Math.max(0, player.getLocation().getBlockY() - 5),
                player.getLocation().getBlockZ()
        );

        try {
            PacketContainer blockChange = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            blockChange.getBlockPositionModifier().write(0, position);
            blockChange.getBlockData().write(0, WrappedBlockData.createData(signType));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, blockChange);

            PacketContainer updateSign = new PacketContainer(PacketType.Play.Server.UPDATE_SIGN);
            updateSign.getBlockPositionModifier().write(0, position);

            WrappedChatComponent[] components = new WrappedChatComponent[4];
            for (int i = 0; i < 4; i++) {
                String text = (lines != null && i < lines.size()) ? lines.get(i) : "";
                components[i] = WrappedChatComponent.fromText(text != null ? text : "");
            }
            updateSign.getChatComponentArrays().write(0, components);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, updateSign);

            PacketContainer openEditor = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_EDITOR);
            openEditor.getBlockPositionModifier().write(0, position);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, openEditor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cleanUpSign(Player player, BlockPosition position) {
        if (player == null || !player.isOnline()) return;

        try {
            PacketContainer airChange = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            airChange.getBlockPositionModifier().write(0, position);
            airChange.getBlockData().write(0, WrappedBlockData.createData(Material.AIR));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, airChange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}