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
    private String forcePresenceStatus(String status) {
        if (UserStateManager.isOffline()) {
            CliqueMod.LOGGER.info("[Clique] Presence intercepted: {} → OFFLINE", status);
            return PresenceStatus.OFFLINE.name();
        }
        CliqueMod.LOGGER.info("[Clique] Presence passthrough: {}", status);
        return status;
    }
}