package me.bewf.clique.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilFriendsService;
import com.mojang.authlib.yggdrasil.response.PresenceStatus;
import me.bewf.clique.CliqueMod;
import me.bewf.clique.userstate.UserStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = YggdrasilFriendsService.class, remap = false)
public class PresenceBlockMixin {

    @ModifyVariable(method = "presence", at = @At("HEAD"), argsOnly = true, remap = false)
    private String clique$interceptPresence(String status) {
        if (UserStateManager.isOffline()) {
            CliqueMod.LOGGER.info("[Clique] Presence blocked: {} → OFFLINE", status);
            return PresenceStatus.OFFLINE.name(); // OFFLINE
        }
        return status; // DND doesn't
    }
}