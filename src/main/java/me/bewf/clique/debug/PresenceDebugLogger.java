package me.bewf.clique.debug;

import com.mojang.authlib.yggdrasil.response.PresenceStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;

public final class PresenceDebugLogger {

    public static void logState() {
        Minecraft mc = Minecraft.getInstance();
        PlayerSocialManager manager = mc.getPlayerSocialManager();

        System.out.println("[Clique] FriendListEnabled: " + manager.isFriendListEnabled());
        System.out.println("[Clique] AllowFriendRequests: " + manager.isAllowFriendRequests());
        System.out.println("[Clique] FriendState: " + manager.getFriendListState());
        System.out.println("[Clique] Friends: " + manager.getFriends().size());
        System.out.println("[Clique] Incoming: " + manager.getIncomingRequests().size());
        System.out.println("[Clique] Outgoing: " + manager.getOutgoingRequests().size());

        System.out.println("[Clique] Presence enum sample: " + PresenceStatus.OFFLINE);
    }

    public static void spamLog() {
        new Thread(() -> {
            while (true) {
                try {
                    logState();
                    System.out.println("[Clique] Offline mode: " + me.bewf.clique.userstate.UserStateManager.isOffline());
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "Clique-Debug").start();
    }
}