package me.bewf.clique.gui;

import me.bewf.clique.userstate.UserStateManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public class StatusDotWidget extends AbstractButton {

    public static final int BTN_W = 110;
    public static final int BTN_H = 14;

    private final Runnable onToggle;

    public StatusDotWidget(int x, int y, Runnable onToggle) {
        super(x, y, BTN_W, BTN_H, stateLabel());
        this.onToggle = onToggle;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pt) {
        extractDefaultSprite(graphics);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        onToggle.run();
    }

    public void syncLabel() {
        setMessage(stateLabel());
    }

    static Component stateLabel() {
        return Component.literal(switch (UserStateManager.getState()) {
            case OFFLINE -> "◌ Appear Offline";
            case DND     -> "⊘ Do Not Disturb";
            default      -> "● Online";
        });
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}
}