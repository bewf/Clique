package me.bewf.clique.input;

import me.bewf.clique.userstate.UserStateManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public final class Keybinds {

    private static boolean wasPressed = false;
    private static long windowHandle = -1;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (windowHandle == -1) {
                windowHandle = extractWindowHandle();
                if (windowHandle == -1) return;
            }

            boolean pressed = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_P) == GLFW.GLFW_PRESS;

            if (pressed && !wasPressed) {
                UserStateManager.toggleOffline();
                System.out.println("[Clique] Offline: " + UserStateManager.isOffline());
            }

            wasPressed = pressed;
        });
    }

    private static long extractWindowHandle() {
        try {
            Minecraft mc = Minecraft.getInstance();

            for (Field f : mc.getWindow().getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object value = f.get(mc.getWindow());

                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
            }
        } catch (Exception ignored) {}

        return -1;
    }
}