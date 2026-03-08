package me.kirug.signgui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SignPacketListener extends PacketAdapter {

    public SignPacketListener(Plugin plugin) {
        super(plugin, PacketType.Play.Client.UPDATE_SIGN);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        SignGUI.SignInputHandler handler = SignGUI.getAndRemoveHandler(player.getUniqueId());
        if (handler == null) return;

        WrappedChatComponent[] components = event.getPacket().getChatComponentArrays().read(0);
        BlockPosition position = event.getPacket().getBlockPositionModifier().read(0);

        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            String raw = components[i].getJson();

            String line = (raw.length() >= 2 && raw.startsWith("\"") && raw.endsWith("\""))
                    ? raw.substring(1, raw.length() - 1)
                    : raw;

            line = unescapeUnicode(line);

            if (SignGUI.STRIP_COLOR) {
                line = ChatColor.stripColor(line);
            }

            lines[i] = line;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                SignPacketManager.cleanUpSign(player, position);
                handler.onInput(player, lines);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to process SignGUI input for: " + player.getName());
                e.printStackTrace();
            }
        });

        event.setCancelled(true);
    }

    private String unescapeUnicode(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            char c = input.charAt(i);
            if (c == '\\' && i + 1 < input.length() && input.charAt(i + 1) == 'u') {
                try {
                    String hex = input.substring(i + 2, i + 6);
                    sb.append((char) Integer.parseInt(hex, 16));
                    i += 6;
                } catch (Exception e) {
                    sb.append(c);
                    i++;
                }
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}