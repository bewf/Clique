package me.bewf.clique.mixin;

import me.bewf.clique.userstate.UserStateManager;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FriendsOverlayScreen.class)
public class FriendsOverlayScreenMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void blockPresenceTick(CallbackInfo ci) {
        if (UserStateManager.isOffline()) {
            ci.cancel();
        }
    }
}