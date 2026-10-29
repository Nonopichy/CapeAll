package com.nonopichy.capeall.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nonopichy.capeall.CapeAllClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Minecraft 1.21.1 screen: forwards input to {@link CapeMenu} and draws what it asks for. */
public class CapeScreen extends Screen implements CapeMenu.Canvas {

    private final CapeMenu menu = new CapeMenu(this);
    private GuiGraphics g;

    public CapeScreen() {
        super(Component.literal("CapeAll"));
    }

    @Override
    protected void init() {
        menu.init(width, height);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick); // background
        g = graphics;
        menu.render(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        menu.mouseClicked((int) mouseX, (int) mouseY, button);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        menu.mouseReleased();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        menu.mouseDragged((int) mouseX);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        menu.mouseScrolled((int) mouseX, (int) mouseY, scrollY);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (CapeAllClient.KEY.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
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
        g.drawString(font, text, x, y, 0xFF000000 | rgb);
    }

    @Override
    public int textWidth(String text) {
        return font.width(text);
    }

    @Override
    public void cape(String cape, int x, int y, int w, int h) {
        RenderSystem.enableBlend();
        g.blit(CapeAllClient.capeTexture(cape), x, y, w, h, 1, 1, 10, 16, 64, 32);
        RenderSystem.disableBlend();
    }

    @Override
    public void head(String playerName, int x, int y, int size) {
        ClientPacketListener connection = minecraft.getConnection();
        PlayerInfo info = connection != null ? connection.getPlayerInfo(playerName) : null;
        if (info != null) {
            PlayerFaceRenderer.draw(g, info.getSkin(), x, y, size);
        } else {
            PlayerFaceRenderer.draw(g, DefaultPlayerSkin.getDefaultTexture(), x, y, size);
        }
    }

    @Override
    public void player(int x1, int y1, int x2, int y2, int scale, float yaw) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        float bodyRot = player.yBodyRot;
        float yRot = player.getYRot();
        float xRot = player.getXRot();
        float headRotO = player.yHeadRotO;
        float headRot = player.yHeadRot;

        player.yBodyRot = 180.0F + yaw;
        player.setYRot(180.0F + yaw);
        player.setXRot(0.0F);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();

        g.enableScissor(x1, y1, x2, y2);
        InventoryScreen.renderEntityInInventory(g, (x1 + x2) / 2.0F, (y1 + y2) / 2.0F, scale / player.getScale(),
                new Vector3f(0.0F, player.getBbHeight() / 2.0F, 0.0F),
                new Quaternionf().rotateZ((float) Math.PI), new Quaternionf(), player);
        g.disableScissor();

        player.yBodyRot = bodyRot;
        player.setYRot(yRot);
        player.setXRot(xRot);
        player.yHeadRotO = headRotO;
        player.yHeadRot = headRot;
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
        g.renderTooltip(font, Component.literal(text), x, y);
    }
}
