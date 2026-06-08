package me.bewf.clique.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.bewf.clique.userstate.UserStateManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {

    private static final KeyMapping.Category CLIQUE_CATEGORY =
            KeyBindingHelper.registerKeyCategory(Component.translatable("key.category.clique"));

    private static KeyMapping togglePresenceKey;

    private Keybinds() {}

    public static void register() {
        togglePresenceKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.clique.toggle_presence",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                CLIQUE_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (togglePresenceKey.consumeClick()) {
                UserStateManager.toggleOffline();
                boolean offline = UserStateManager.isOffline();
                client.player.sendSystemMessage(
                        Component.literal("[Clique] Status: ")
                                .append(Component.literal(offline ? "Appear Offline" : "Online")
                                        .withStyle(offline ? ChatFormatting.GRAY : ChatFormatting.GREEN))
                );
            }
        });
    }
}