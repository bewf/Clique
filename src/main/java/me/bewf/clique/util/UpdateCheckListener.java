package me.bewf.clique.util;

import me.bewf.clique.CliqueMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class UpdateCheckListener {

    private static boolean started = false;

    private UpdateCheckListener() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (started) return;
            started = true;

            UpdateChecker.checkOnce(
                    CliqueMod.MODRINTH_PROJECT_ID,
                    CliqueMod.MODRINTH_SLUG,
                    CliqueMod.NAME,
                    CliqueMod.VERSION,
                    CliqueMod.MC_VERSION,
                    CliqueMod.LOADER
            );
        });
    }
}