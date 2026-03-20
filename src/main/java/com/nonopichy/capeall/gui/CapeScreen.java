package com.nonopichy.capeall.gui;

import com.nonopichy.capeall.CapeAll;
import com.nonopichy.capeall.cape.CapeManager;
import com.nonopichy.capeall.cape.CapeRegistry;
import com.nonopichy.capeall.config.ConfigManager;
import com.nonopichy.capeall.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;

public class CapeScreen extends GuiScreen {

    private static final int THUMB_W = 20;
    private static final int THUMB_H = 28;
    private static final int THUMB_PAD = 3;
    private static final int LIST_ENTRY_H = 20;

    private static final int COL_BG = 0xCC0A0A0A;
    private static final int COL_PANEL = 0xCC1A1A1A;
    private static final int COL_BORDER = 0xFF333333;
    private static final int COL_SELECTED = 0xFF00CC00;
    private static final int COL_HOVERED = 0xFFFFCC00;
    private static final int COL_CELL_BG = 0xFF222222;

    private String selectedCape;
    private String hoveredCape = null;
    private String[] capeNames;

    // Layout
    private int previewX, previewY, previewW, previewH;
    private int gridX, gridY, gridW, gridH;
    private int listX, listY, listW, listH;

    // Grid
    private int gridCols;
    private int capeScroll = 0;
    private int maxCapeScroll = 0;
    private int gridContentY;

    // Player list
    private int playerScroll = 0;
    private int maxPlayerScroll = 0;
    private int listContentY;

    // Preview rotation
    private float previewYaw = 160.0f;
    private boolean dragging = false;
    private int dragLastX;

    @Override
    public void initGui() {
        selectedCape = ModConfig.selectedCape;
        capeNames = CapeRegistry.getAllCapeNames();

        int m = 4;
        int topH = (int) (height * 0.62);

        // Preview (top-left ~30%)
        previewX = m;
        previewY = m;
        previewW = (int) (width * 0.28);
        previewH = topH;

        // Cape grid (top-right ~70%)
        gridX = previewX + previewW + m;
        gridY = m;
        gridW = width - gridX - m;
        gridH = topH;
        gridContentY = gridY + 14;

        // Player list (bottom full)
        listX = m;
        listY = previewY + previewH + m;
        listW = width - m * 2;
        listH = height - listY - m;
        listContentY = listY + 14;

        recalcScroll();
    }

    private void recalcScroll() {
        int cellW = THUMB_W + THUMB_PAD;
        gridCols = Math.max(1, (gridW - THUMB_PAD * 2) / cellW);
        int totalRows = (capeNames.length + gridCols - 1) / gridCols;
        int visibleRows = (gridH - 18) / (THUMB_H + THUMB_PAD);
        maxCapeScroll = Math.max(0, totalRows - visibleRows);
        capeScroll = Math.min(capeScroll, maxCapeScroll);

        int playerCount = getPlayerList().size();
        int visibleEntries = (listH - 18) / LIST_ENTRY_H;
        maxPlayerScroll = Math.max(0, playerCount - visibleEntries);
        playerScroll = Math.min(playerScroll, maxPlayerScroll);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        hoveredCape = null;

        drawPreviewPanel(mouseX, mouseY);
        drawCapeGrid(mouseX, mouseY);
        drawPlayerList(mouseX, mouseY);

        // Tooltip
        if (hoveredCape != null) {
            drawHoveringText(Collections.singletonList(hoveredCape), mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    // ==================== PREVIEW PANEL ====================

    private void drawPreviewPanel(int mouseX, int mouseY) {
        drawPanel(previewX, previewY, previewW, previewH);
        drawString(fontRendererObj, "\u00A7lSua Capa", previewX + 5, previewY + 3, 0xFFFFFF);

        if (mc.thePlayer != null) {
            // Set preview cape for mixin
            CapeManager.setPreviewCape(hoveredCape);

            int modelX = previewX + previewW / 2;
            int modelY = previewY + previewH - 25;
            int scale = (int) (previewH * 0.38);

            renderPlayerModel(modelX, modelY, scale, previewYaw, 0, mc.thePlayer);

            CapeManager.setPreviewCape(null);
        }

        // Cape name label
        String label = selectedCape != null ? selectedCape : "nenhuma";
        int labelW = fontRendererObj.getStringWidth(label);
        drawString(fontRendererObj, "\u00A77Capa: \u00A7e" + label,
                previewX + (previewW - labelW) / 2 - 10, previewY + previewH - 12, 0xFFFFFF);
    }

    // ==================== CAPE GRID ====================

    private void drawCapeGrid(int mouseX, int mouseY) {
        drawPanel(gridX, gridY, gridW, gridH);
        drawString(fontRendererObj, "\u00A7lCapas Disponiveis", gridX + 5, gridY + 3, 0xFFFFFF);

        ScaledResolution sr = new ScaledResolution(mc);
        int sf = sr.getScaleFactor();

        int contentH = gridH - 18;
        int contentY = gridContentY;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(gridX * sf, mc.displayHeight - (contentY + contentH) * sf, gridW * sf, contentH * sf);

        int cellW = THUMB_W + THUMB_PAD;
        int cellH = THUMB_H + THUMB_PAD;
        int startX = gridX + THUMB_PAD;

        for (int i = 0; i < capeNames.length; i++) {
            int row = i / gridCols - capeScroll;
            int col = i % gridCols;

            int cx = startX + col * cellW;
            int cy = contentY + row * cellH;

            if (cy + cellH < contentY || cy > contentY + contentH) continue;

            String cape = capeNames[i];
            boolean isSelected = cape.equals(selectedCape);
            boolean isHovered = mouseX >= cx && mouseX < cx + THUMB_W
                    && mouseY >= cy && mouseY < cy + THUMB_H
                    && mouseY >= contentY && mouseY < contentY + contentH;

            if (isHovered) hoveredCape = cape;

            // Border
            if (isSelected) {
                drawRect(cx - 1, cy - 1, cx + THUMB_W + 1, cy + THUMB_H + 1, COL_SELECTED);
            } else if (isHovered) {
                drawRect(cx - 1, cy - 1, cx + THUMB_W + 1, cy + THUMB_H + 1, COL_HOVERED);
            }

            // Cell background
            drawRect(cx, cy, cx + THUMB_W, cy + THUMB_H, COL_CELL_BG);

            // Cape texture thumbnail (front face: u=1, v=1, w=10, h=16 in 64x32 texture)
            try {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.color(1, 1, 1, 1);
                mc.getTextureManager().bindTexture(
                        new ResourceLocation("minecraft:textures/cape/" + cape + ".png"));
                Gui.drawScaledCustomSizeModalRect(cx + 1, cy + 1, 1, 1, 10, 16,
                        THUMB_W - 2, THUMB_H - 2, 64, 32);
                GlStateManager.disableBlend();
            } catch (Exception ignored) {
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Scrollbar
        if (maxCapeScroll > 0) {
            int totalRows = (capeNames.length + gridCols - 1) / gridCols;
            int barX = gridX + gridW - 4;
            int barH = contentH;
            int thumbH = Math.max(10, barH * barH / (totalRows * cellH));
            int thumbY = contentY + (int) ((float) capeScroll / maxCapeScroll * (barH - thumbH));
            drawRect(barX, contentY, barX + 3, contentY + barH, 0xFF111111);
            drawRect(barX, thumbY, barX + 3, thumbY + thumbH, 0xFF555555);
        }
    }

    // ==================== PLAYER LIST ====================

    private void drawPlayerList(int mouseX, int mouseY) {
        drawPanel(listX, listY, listW, listH);
        drawString(fontRendererObj, "\u00A7lJogadores com CapeAll", listX + 5, listY + 3, 0xFFFFFF);

        List<Map.Entry<String, String>> players = getPlayerList();

        ScaledResolution sr = new ScaledResolution(mc);
        int sf = sr.getScaleFactor();
        int contentH = listH - 18;
        int contentY = listContentY;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(listX * sf, mc.displayHeight - (contentY + contentH) * sf, listW * sf, contentH * sf);

        for (int i = 0; i < players.size(); i++) {
            int row = i - playerScroll;
            int ey = contentY + row * LIST_ENTRY_H;

            if (ey + LIST_ENTRY_H < contentY || ey > contentY + contentH) continue;

            Map.Entry<String, String> entry = players.get(i);
            String name = entry.getKey();
            String cape = entry.getValue();
            boolean isLocal = mc.thePlayer != null && name.equals(mc.thePlayer.getName());

            // Row background
            int rowBg = (i % 2 == 0) ? 0x20FFFFFF : 0x10FFFFFF;
            drawRect(listX + 2, ey, listX + listW - 2, ey + LIST_ENTRY_H - 1, rowBg);

            // Player head (16x16)
            int headX = listX + 6;
            int headY = ey + 2;
            int headSize = LIST_ENTRY_H - 4;
            drawPlayerHead(headX, headY, headSize, name);

            // Player name
            String nameColor = isLocal ? "\u00A7a" : "\u00A7f";
            drawString(fontRendererObj, nameColor + name, headX + headSize + 4, ey + 2, 0xFFFFFF);

            // Cape name
            if (cape != null) {
                drawString(fontRendererObj, "\u00A77" + cape,
                        headX + headSize + 4, ey + 10, 0xAAAAAA);
            }

            // Mini cape icon
            if (cape != null) {
                try {
                    GlStateManager.enableBlend();
                    GlStateManager.color(1, 1, 1, 1);
                    mc.getTextureManager().bindTexture(
                            new ResourceLocation("minecraft:textures/cape/" + cape + ".png"));
                    int iconX = listX + listW - 20;
                    Gui.drawScaledCustomSizeModalRect(iconX, ey + 1, 1, 1, 10, 16,
                            8, 14, 64, 32);
                    GlStateManager.disableBlend();
                } catch (Exception ignored) {
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Scrollbar
        if (maxPlayerScroll > 0) {
            int barX = listX + listW - 4;
            int thumbH = Math.max(10, contentH * contentH / (players.size() * LIST_ENTRY_H));
            int thumbY = contentY + (int) ((float) playerScroll / maxPlayerScroll * (contentH - thumbH));
            drawRect(barX, contentY, barX + 3, contentY + contentH, 0xFF111111);
            drawRect(barX, thumbY, barX + 3, thumbY + thumbH, 0xFF555555);
        }
    }

    // ==================== HELPERS ====================

    private void drawPanel(int x, int y, int w, int h) {
        drawRect(x, y, x + w, y + h, COL_PANEL);
        // Borders
        drawRect(x, y, x + w, y + 1, COL_BORDER);
        drawRect(x, y + h - 1, x + w, y + h, COL_BORDER);
        drawRect(x, y, x + 1, y + h, COL_BORDER);
        drawRect(x + w - 1, y, x + w, y + h, COL_BORDER);
    }

    private void drawPlayerHead(int x, int y, int size, String playerName) {
        ResourceLocation skin = getPlayerSkin(playerName);
        if (skin == null) return;

        try {
            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(skin);
            // Face layer (8,8 -> 16,16 in 64x64 skin)
            Gui.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, size, size, 64, 64);
            // Hat overlay (40,8 -> 48,16)
            Gui.drawScaledCustomSizeModalRect(x, y, 40, 8, 8, 8, size, size, 64, 64);
            GlStateManager.disableBlend();
        } catch (Exception ignored) {
        }
    }

    private ResourceLocation getPlayerSkin(String playerName) {
        if (mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            NetworkPlayerInfo info = mc.thePlayer.sendQueue.getPlayerInfo(playerName);
            if (info != null) {
                return info.getLocationSkin();
            }
        }
        return new ResourceLocation("textures/entity/steve.png");
    }

    private List<Map.Entry<String, String>> getPlayerList() {
        List<Map.Entry<String, String>> list = new ArrayList<>();

        // Local player first
        if (mc.thePlayer != null) {
            list.add(new AbstractMap.SimpleEntry<>(mc.thePlayer.getName(), ModConfig.selectedCape));
        }

        // Other mod players
        Map<String, String> others = CapeManager.getAllPlayerCapes();
        for (Map.Entry<String, String> e : others.entrySet()) {
            if (mc.thePlayer != null && e.getKey().equals(mc.thePlayer.getName())) continue;
            list.add(e);
        }

        return list;
    }

    private void renderPlayerModel(int x, int y, int scale, float yaw, float pitch, EntityLivingBase entity) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float oldBodyYaw = entity.renderYawOffset;
        float oldYaw = entity.rotationYaw;
        float oldPitch = entity.rotationPitch;
        float oldPrevHead = entity.prevRotationYawHead;
        float oldHead = entity.rotationYawHead;

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);

        entity.renderYawOffset = yaw;
        entity.rotationYaw = yaw;
        entity.rotationPitch = pitch;
        entity.rotationYawHead = yaw;
        entity.prevRotationYawHead = yaw;

        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.setPlayerViewY(180.0F);
        rm.setRenderShadow(false);
        rm.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rm.setRenderShadow(true);

        entity.renderYawOffset = oldBodyYaw;
        entity.rotationYaw = oldYaw;
        entity.rotationPitch = oldPitch;
        entity.prevRotationYawHead = oldPrevHead;
        entity.rotationYawHead = oldHead;

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private boolean isInArea(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ==================== INPUT ====================

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            // Cape grid click
            if (isInArea(mouseX, mouseY, gridX, gridContentY, gridW, gridH - 18)) {
                int cellW = THUMB_W + THUMB_PAD;
                int cellH = THUMB_H + THUMB_PAD;
                int startX = gridX + THUMB_PAD;

                int col = (mouseX - startX) / cellW;
                int row = (mouseY - gridContentY) / cellH + capeScroll;
                int index = row * gridCols + col;
//
                if (col >= 0 && col < gridCols && index >= 0 && index < capeNames.length) {
                    selectedCape = capeNames[index];
                    ModConfig.selectedCape = selectedCape;
                    ConfigManager.saveSelectedCape(selectedCape);
                    CapeAll.getCapeManager().forceBroadcast();
                }
            }

            // Preview drag start
            if (isInArea(mouseX, mouseY, previewX, previewY, previewW, previewH)) {
                dragging = true;
                dragLastX = mouseX;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (dragging) {
            previewYaw += (mouseX - dragLastX) * 1.5f;
            dragLastX = mouseX;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll == 0) return;

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if (isInArea(mouseX, mouseY, gridX, gridY, gridW, gridH)) {
            capeScroll += (scroll > 0) ? -1 : 1;
            capeScroll = Math.max(0, Math.min(maxCapeScroll, capeScroll));
        } else if (isInArea(mouseX, mouseY, listX, listY, listW, listH)) {
            playerScroll += (scroll > 0) ? -1 : 1;
            playerScroll = Math.max(0, Math.min(maxPlayerScroll, playerScroll));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || keyCode == CapeAll.getKeyBind().getKeyCode()) {
            mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        CapeManager.setPreviewCape(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
