package me.bewf.clique.gui;

import me.bewf.clique.userstate.UserStateManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class StatusDotWidget extends AbstractWidget {

    private static final Identifier TEX_ONLINE  = Identifier.of("clique", "textures/gui/online.png");
    private static final Identifier TEX_OFFLINE = Identifier.of("clique", "textures/gui/offline.png");
    private static final Identifier TEX_DND     = Identifier.of("clique", "textures/gui/dnd.png");

    public static final int DISPLAY_SIZE = 32;
    private static final int TEX_SIZE    = 512;

    private final Runnable onToggle;

    public StatusDotWidget(int x, int y, Runnable onToggle) {
        super(x, y, DISPLAY_SIZE, DISPLAY_SIZE, Component.empty());
        this.onToggle = onToggle;
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;
        float scale = (float) DISPLAY_SIZE / TEX_SIZE;
        var pose = graphics.pose();
        pose.pushPose();
        pose.translate(getX(), getY(), 0f);
        pose.scale(scale, scale, 1f);
        graphics.blit(currentTex(), 0, 0, 0, 0, TEX_SIZE, TEX_SIZE, TEX_SIZE, TEX_SIZE);
        pose.popPose();
    }

    @Override
    protected void onClick(double mouseX, double mouseY) {
        onToggle.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    private Identifier currentTex() {
        return switch (UserStateManager.getState()) {
            case OFFLINE -> TEX_OFFLINE;
            case DND     -> TEX_DND;
            default      -> TEX_ONLINE;
        };
    }
}