package me.bewf.clique.mixin;

import me.bewf.clique.userstate.UserStateManager;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerSocialManager.class)
public class PlayerSocialManagerMixin {

    @Inject(method = "startOnlineMode", at = @At("HEAD"), cancellable = true)
    private void blockOnlineMode(CallbackInfo ci) {
        if (UserStateManager.isOffline()) {
            ci.cancel();
        }
    }
}