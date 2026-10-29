package com.nonopichy.capeall.gui;

import com.nonopichy.capeall.CapeAllClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Minecraft 26.3 screen: forwards input to {@link CapeMenu} and draws what it asks for. */
public class CapeScreen extends Screen implements CapeMenu.Canvas {

    private final CapeMenu menu = new CapeMenu(this);
    private GuiGraphicsExtractor g;

    public CapeScreen() {
        super(Component.literal("CapeAll"));
    }

    @Override
    protected void init() {
        menu.init(width, height);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        g = graphics;
        menu.render(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        menu.mouseClicked((int) event.x(), (int) event.y(), event.button());
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        menu.mouseReleased();
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        menu.mouseDragged((int) event.x());
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        menu.mouseScrolled((int) mouseX, (int) mouseY, scrollY);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (CapeAllClient.KEY.matches(event)) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void removed() {
        menu.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ==================== CANVAS ====================

    @Override
    public void fill(int x1, int y1, int x2, int y2, int argb) {
        g.fill(x1, y1, x2, y2, argb);
    }

    @Override
    public void text(String text, int x, int y, int rgb) {
        g.text(font, text, x, y, 0xFF000000 | rgb);
    }

    @Override
    public int textWidth(String text) {
        return font.width(text);
    }

    @Override
    public void cape(String cape, int x, int y, int w, int h) {
        g.blit(RenderPipelines.GUI_TEXTURED, CapeAllClient.capeTexture(cape), x, y, 1, 1, w, h, 10, 16, 64, 32);
    }

    @Override
    public void head(String playerName, int x, int y, int size) {
        ClientPacketListener connection = minecraft.getConnection();
        PlayerInfo info = connection != null ? connection.getPlayerInfo(playerName) : null;
        PlayerFaceExtractor.extractRenderState(g, info != null ? info.getSkin() : DefaultPlayerSkin.getDefaultSkin(), x, y, size);
    }

    @Override
    public void player(int x1, int y1, int x2, int y2, int scale, float yaw) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        // Same as InventoryScreen.extractEntityInInventoryFollowsMouse, with a fixed yaw instead of the mouse.
        EntityRenderState state = minecraft.getEntityRenderDispatcher().getRenderer(player).createRenderState(player, 1.0F);
        state.shadowPieces.clear();
        state.outlineColor = 0;
        if (state instanceof LivingEntityRenderState living) {
            living.bodyRot = 180.0F + yaw;
            living.yRot = 0.0F;
            living.xRot = 0.0F;
            living.boundingBoxWidth = living.boundingBoxWidth / living.scale;
            living.boundingBoxHeight = living.boundingBoxHeight / living.scale;
            living.scale = 1.0F;
        }

        g.entity(state, scale, new Vector3f(0.0F, state.boundingBoxHeight / 2.0F, 0.0F),
                new Quaternionf().rotateZ((float) Math.PI), new Quaternionf(), x1, y1, x2, y2);
    }

    @Override
    public void scissor(int x1, int y1, int x2, int y2) {
        g.enableScissor(x1, y1, x2, y2);
    }

    @Override
    public void endScissor() {
        g.disableScissor();
    }

    @Override
    public void tooltip(String text, int x, int y) {
        g.setTooltipForNextFrame(font, Component.literal(text), x, y);
    }
}
