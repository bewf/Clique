package me.bewf.clique.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilFriendsService;
import me.bewf.clique.userstate.UserStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(YggdrasilFriendsService.class)
public class PresenceBlockMixin {

    @Inject(method = "presence", at = @At("HEAD"), cancellable = true)
    private void blockPresence(String status, CallbackInfo ci) {
        if (UserStateManager.isOffline()) {
            ci.cancel();
        }
    }
}