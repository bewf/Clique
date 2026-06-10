package me.bewf.clique.mixin;

import me.bewf.clique.gui.StatusDotWidget;
import me.bewf.clique.userstate.UserState;
import me.bewf.clique.userstate.UserStateManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(FriendsOverlayScreen.class)
public abstract class FriendsScreenMixin extends Screen {

    protected FriendsScreenMixin(Component title) { super(title); }

    //
    @Unique private static final int PANEL_WIDTH = 264;
    @Unique private static final int PROFILE_Y   = 79;  // bump ±2 if misaligned
    @Unique private static final int POPUP_W     = 130;
    @Unique private static final int POPUP_H     = 12;
    @Unique private static final int PADDING     = 5;

    @Unique private StatusDotWidget  cliqueStatusDot;
    @Unique private List<Button>     cliquePopupOptions;
    @Unique private boolean          cliquePopupOpen = false;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void clique$init(CallbackInfo ci) {
        cliquePopupOpen = false;
        if (cliquePopupOptions == null) cliquePopupOptions = new ArrayList<>();
        else cliquePopupOptions.clear();

        final int panelRight = (this.width + PANEL_WIDTH) / 2;
        final int dotX = panelRight - StatusDotWidget.DISPLAY_SIZE - PADDING;
        final int dotY = PROFILE_Y;

        cliqueStatusDot = new StatusDotWidget(dotX, dotY, () -> {
            cliquePopupOpen = !cliquePopupOpen;
            clique$syncPopup();
        });
        addRenderableWidget(cliqueStatusDot);

        // Popup drops below the dot, right-aligned with it
        final int popupX = dotX + StatusDotWidget.DISPLAY_SIZE - POPUP_W;
        final int popupStartY = dotY + StatusDotWidget.DISPLAY_SIZE + 2;

        record Opt(String label, UserState state) {}
        var opts = List.of(
                new Opt("● Online",         UserState.NORMAL),
                new Opt("◌ Appear Offline", UserState.OFFLINE),
                new Opt("⊘ Do Not Disturb", UserState.DND)
        );

        for (int i = 0; i < opts.size(); i++) {
            final Opt opt = opts.get(i);
            Button btn = Button.builder(Component.literal(opt.label()), b -> {
                UserStateManager.setState(opt.state());
                cliquePopupOpen = false;
                clique$syncPopup();
            }).bounds(popupX, popupStartY + i * (POPUP_H + 2), POPUP_W, POPUP_H).build();
            btn.visible = false;
            cliquePopupOptions.add(btn);
            addRenderableWidget(btn);
        }
    }

    @Unique
    private void clique$syncPopup() {
        cliquePopupOptions.forEach(b -> b.visible = cliquePopupOpen);
    }
}