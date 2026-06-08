package me.bewf.clique.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilFriendsService;
import java.time.Duration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(YggdrasilFriendsService.class)
public class YggdrasilFriendsServiceMixin {

    @Inject(method = "getFriendsPollInterval", at = @At("HEAD"), cancellable = true)
    private void fastFriends(CallbackInfoReturnable<java.util.Optional<Duration>> cir) {
        cir.setReturnValue(java.util.Optional.of(Duration.ofSeconds(2)));
    }

    @Inject(method = "getPresencePollInterval", at = @At("HEAD"), cancellable = true)
    private void fastPresence(CallbackInfoReturnable<java.util.Optional<Duration>> cir) {
        cir.setReturnValue(java.util.Optional.of(Duration.ofSeconds(2)));
    }
}