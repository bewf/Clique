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

    @Unique private static final int PANEL_WIDTH = 264;
    @Unique private static final int PROFILE_Y   = 79;
    @Unique private static final int PADDING     = 5;
    @Unique private static final int ITEM_GAP    = 2;

    @Unique private StatusDotWidget  cliquePresenceBtn;
    @Unique private List<Button>     cliquePopupBtns   = new ArrayList<>();
    @Unique private List<UserState>  cliquePopupStates = new ArrayList<>();
    @Unique private boolean          cliquePopupOpen   = false;
    @Unique private int              cliquePopupStartY;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void clique$init(CallbackInfo ci) {
        cliquePopupOpen = false;
        cliquePopupBtns.clear();
        cliquePopupStates.clear();

        final int panelRight = (this.width + PANEL_WIDTH) / 2;
        final int btnX       = panelRight - StatusDotWidget.BTN_W - PADDING;

        cliquePresenceBtn = new StatusDotWidget(btnX, PROFILE_Y, () -> {
            cliquePopupOpen = !cliquePopupOpen;
            clique$syncPopup();
        });
        addRenderableWidget(cliquePresenceBtn);

        cliquePopupStartY = PROFILE_Y + StatusDotWidget.BTN_H + ITEM_GAP;

        record Opt(String label, UserState state) {}
        var opts = List.of(
                new Opt("● Online",         UserState.NORMAL),
                new Opt("◌ Appear Offline", UserState.OFFLINE),
                new Opt("⊘ Do Not Disturb", UserState.DND)
        );

        for (Opt opt : opts) {
            Button btn = Button.builder(Component.literal(opt.label()), b -> {
                UserStateManager.setState(opt.state());
                cliquePresenceBtn.syncLabel();
                cliquePopupOpen = false;
                clique$syncPopup();
            }).bounds(btnX, cliquePopupStartY, StatusDotWidget.BTN_W, StatusDotWidget.BTN_H).build();
            btn.visible = false;
            cliquePopupBtns.add(btn);
            cliquePopupStates.add(opt.state());
            addRenderableWidget(btn);
        }
    }

    @Unique
    private void clique$syncPopup() {
        UserState current = UserStateManager.getState();
        int y = cliquePopupStartY;
        for (int i = 0; i < cliquePopupBtns.size(); i++) {
            Button btn   = cliquePopupBtns.get(i);
            boolean show = cliquePopupOpen && cliquePopupStates.get(i) != current;
            btn.visible  = show;
            if (show) {
                btn.setY(y);
                y += StatusDotWidget.BTN_H + ITEM_GAP;
            }
        }
    }
}