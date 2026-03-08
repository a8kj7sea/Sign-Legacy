package me.kirug.signgui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import com.comphenix.protocol.ProtocolLibrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SignGUI {

    private static final ConcurrentHashMap<UUID, SignInputHandler> ACTIVE_EDITORS = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    public static boolean STRIP_COLOR = true;

    @FunctionalInterface
    public interface SignInputHandler {
        void onInput(Player player, String[] lines);
    }

    @FunctionalInterface
    public interface SingleLineHandler {
        void onInput(Player player, String text);
    }

    private final List<String> lines;
    private final SignInputHandler handler;
    private final Material signType;

    private SignGUI(List<String> lines, SignInputHandler handler, Material signType) {
        this.lines = lines;
        this.handler = handler;
        this.signType = signType;
    }

    public static synchronized void init(Plugin plugin) {
        if (initialized) return;

        ProtocolLibrary.getProtocolManager().addPacketListener(new SignPacketListener(plugin));

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                ACTIVE_EDITORS.remove(event.getPlayer().getUniqueId());
            }
        }, plugin);

        initialized = true;
    }

    public static Builder builder() {
        if (!initialized) throw new IllegalStateException("SignGUI not initialized! Call init() first.");
        return new Builder();
    }

    public void open(Player player) {
        if (player == null || !player.isOnline()) return;

        ACTIVE_EDITORS.put(player.getUniqueId(), handler);
        SignPacketManager.openSignEditor(player, lines, signType);
    }

    public static void forceClose(Player player) {
        ACTIVE_EDITORS.remove(player.getUniqueId());
    }

    protected static SignInputHandler getAndRemoveHandler(UUID playerId) {
        return ACTIVE_EDITORS.remove(playerId);
    }

    public static class Builder {
        private final List<String> lines = new ArrayList<>(Arrays.asList("", "", "", ""));
        private SignInputHandler handler;
        private Material signType = Material.SIGN_POST;

        public Builder setLine(int index, String text) {
            if (index >= 0 && index < 4) {
                lines.set(index, text == null ? "" : text);
            }
            return this;
        }

        public Builder setLines(String... input) {
            for (int i = 0; i < 4; i++) {
                if (i < input.length && input[i] != null) {
                    lines.set(i, input[i]);
                }
            }
            return this;
        }

        public Builder type(Material type) {
            if (type != null && type.name().contains("SIGN")) {
                this.signType = type;
            }
            return this;
        }

        public Builder onInput(SignInputHandler handler) {
            this.handler = handler;
            return this;
        }

        public Builder onLineInput(int lineIndex, SingleLineHandler singleHandler) {
            this.handler = (player, lines) -> {
                String text = (lineIndex >= 0 && lineIndex < lines.length) ? lines[lineIndex] : "";
                singleHandler.onInput(player, text);
            };
            return this;
        }

        public SignGUI build() {
            if (handler == null) throw new IllegalStateException("SignInputHandler must be set!");
            return new SignGUI(lines, handler, signType);
        }
    }
}