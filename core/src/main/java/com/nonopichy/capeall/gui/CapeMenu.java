package com.nonopichy.capeall.gui;

import com.nonopichy.capeall.CapeAll;
import com.nonopichy.capeall.cape.CapeManager;
import com.nonopichy.capeall.cape.CapeRegistry;
import com.nonopichy.capeall.config.ModConfig;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Layout, scrolling and clicks of the CapeAll menu. Version independent: each Minecraft version's
 * CapeScreen only forwards input here and implements the few drawing calls in {@link Canvas}.
 */
public class CapeMenu {

    public interface Canvas {
        void fill(int x1, int y1, int x2, int y2, int argb);

        /** Text with shadow; § color codes allowed. */
        void text(String text, int x, int y, int rgb);

        int textWidth(String text);

        /** Front face of a cape texture (u=1, v=1, 10x16 of 64x32) scaled into w x h. */
        void cape(String cape, int x, int y, int w, int h);

        void head(String playerName, int x, int y, int size);

        /** Local player model centered in the box, yaw 0 = facing the screen. */
        void player(int x1, int y1, int x2, int y2, int scale, float yaw);

        void scissor(int x1, int y1, int x2, int y2);

        void endScissor();

        void tooltip(String text, int x, int y);
    }

    private static final int THUMB_W = 20;
    private static final int THUMB_H = 28;
    private static final int THUMB_PAD = 3;
    private static final int LIST_ENTRY_H = 20;

    private static final int COL_PANEL = 0xCC1A1A1A;
    private static final int COL_BORDER = 0xFF333333;
    private static final int COL_SELECTED = 0xFF00CC00;
    private static final int COL_HOVERED = 0xFFFFCC00;
    private static final int COL_CELL_BG = 0xFF222222;

    private final Canvas c;

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

    public CapeMenu(Canvas canvas) {
        this.c = canvas;
    }

    public void init(int width, int height) {
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

        int cellW = THUMB_W + THUMB_PAD;
        gridCols = Math.max(1, (gridW - THUMB_PAD * 2) / cellW);
        int totalRows = (capeNames.length + gridCols - 1) / gridCols;
        int visibleRows = (gridH - 18) / (THUMB_H + THUMB_PAD);
        maxCapeScroll = Math.max(0, totalRows - visibleRows);
        capeScroll = Math.min(capeScroll, maxCapeScroll);
    }

    public void render(int mouseX, int mouseY) {
        // Grid first: it finds the hovered cape that the preview shows.
        hoveredCape = null;
        drawCapeGrid(mouseX, mouseY);
        drawPreviewPanel();
        drawPlayerList();

        if (hoveredCape != null) {
            c.tooltip(hoveredCape, mouseX, mouseY);
        }
    }

    // ==================== PREVIEW PANEL ====================

    private void drawPreviewPanel() {
        drawPanel(previewX, previewY, previewW, previewH);
        c.text("§lSua Capa", previewX + 5, previewY + 3, 0xFFFFFF);

        CapeManager.setPreviewCape(hoveredCape);
        c.player(previewX + 1, previewY + 14, previewX + previewW - 1, previewY + previewH - 14,
                (int) (previewH * 0.38), previewYaw);
        CapeManager.setPreviewCape(null);

        // Cape name label
        String label = selectedCape != null ? selectedCape : "nenhuma";
        int labelW = c.textWidth(label);
        c.text("§7Capa: §e" + label, previewX + (previewW - labelW) / 2 - 10, previewY + previewH - 12, 0xFFFFFF);
    }

    // ==================== CAPE GRID ====================

    private void drawCapeGrid(int mouseX, int mouseY) {
        drawPanel(gridX, gridY, gridW, gridH);
        c.text("§lCapas Disponiveis", gridX + 5, gridY + 3, 0xFFFFFF);

        int contentH = gridH - 18;
        int contentY = gridContentY;

        c.scissor(gridX, contentY, gridX + gridW, contentY + contentH);

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
                c.fill(cx - 1, cy - 1, cx + THUMB_W + 1, cy + THUMB_H + 1, COL_SELECTED);
            } else if (isHovered) {
                c.fill(cx - 1, cy - 1, cx + THUMB_W + 1, cy + THUMB_H + 1, COL_HOVERED);
            }

            // Cell background + cape thumbnail
            c.fill(cx, cy, cx + THUMB_W, cy + THUMB_H, COL_CELL_BG);
            c.cape(cape, cx + 1, cy + 1, THUMB_W - 2, THUMB_H - 2);
        }

        c.endScissor();

        // Scrollbar
        if (maxCapeScroll > 0) {
            int totalRows = (capeNames.length + gridCols - 1) / gridCols;
            int barX = gridX + gridW - 4;
            int barH = contentH;
            int thumbH = Math.max(10, barH * barH / (totalRows * cellH));
            int thumbY = contentY + (int) ((float) capeScroll / maxCapeScroll * (barH - thumbH));
            c.fill(barX, contentY, barX + 3, contentY + barH, 0xFF111111);
            c.fill(barX, thumbY, barX + 3, thumbY + thumbH, 0xFF555555);
        }
    }

    // ==================== PLAYER LIST ====================

    private void drawPlayerList() {
        drawPanel(listX, listY, listW, listH);
        c.text("§lJogadores com CapeAll", listX + 5, listY + 3, 0xFFFFFF);

        List<Map.Entry<String, String>> players = getPlayerList();
        String localName = CapeAll.getPlatform().localPlayerName();

        int contentH = listH - 18;
        int contentY = listContentY;
        maxPlayerScroll = Math.max(0, players.size() - contentH / LIST_ENTRY_H);
        playerScroll = Math.min(playerScroll, maxPlayerScroll);

        c.scissor(listX, contentY, listX + listW, contentY + contentH);

        for (int i = 0; i < players.size(); i++) {
            int row = i - playerScroll;
            int ey = contentY + row * LIST_ENTRY_H;

            if (ey + LIST_ENTRY_H < contentY || ey > contentY + contentH) continue;

            Map.Entry<String, String> entry = players.get(i);
            String name = entry.getKey();
            String cape = entry.getValue();
            boolean isLocal = name.equals(localName);

            // Row background
            int rowBg = (i % 2 == 0) ? 0x20FFFFFF : 0x10FFFFFF;
            c.fill(listX + 2, ey, listX + listW - 2, ey + LIST_ENTRY_H - 1, rowBg);

            // Player head (16x16)
            int headX = listX + 6;
            int headSize = LIST_ENTRY_H - 4;
            c.head(name, headX, ey + 2, headSize);

            // Player name
            c.text((isLocal ? "§a" : "§f") + name, headX + headSize + 4, ey + 2, 0xFFFFFF);

            // Cape name + mini cape icon
            if (cape != null) {
                c.text("§7" + cape, headX + headSize + 4, ey + 10, 0xAAAAAA);
                if (CapeRegistry.isValidName(cape)) {
                    c.cape(cape, listX + listW - 20, ey + 1, 8, 14);
                }
            }
        }

        c.endScissor();

        // Scrollbar
        if (maxPlayerScroll > 0) {
            int barX = listX + listW - 4;
            int thumbH = Math.max(10, contentH * contentH / (players.size() * LIST_ENTRY_H));
            int thumbY = contentY + (int) ((float) playerScroll / maxPlayerScroll * (contentH - thumbH));
            c.fill(barX, contentY, barX + 3, contentY + contentH, 0xFF111111);
            c.fill(barX, thumbY, barX + 3, thumbY + thumbH, 0xFF555555);
        }
    }

    // ==================== HELPERS ====================

    private void drawPanel(int x, int y, int w, int h) {
        c.fill(x, y, x + w, y + h, COL_PANEL);
        // Borders
        c.fill(x, y, x + w, y + 1, COL_BORDER);
        c.fill(x, y + h - 1, x + w, y + h, COL_BORDER);
        c.fill(x, y, x + 1, y + h, COL_BORDER);
        c.fill(x + w - 1, y, x + w, y + h, COL_BORDER);
    }

    private List<Map.Entry<String, String>> getPlayerList() {
        List<Map.Entry<String, String>> list = new ArrayList<>();
        String localName = CapeAll.getPlatform().localPlayerName();

        // Local player first
        if (localName != null) {
            list.add(new AbstractMap.SimpleEntry<>(localName, ModConfig.selectedCape));
        }

        // Other mod players
        for (Map.Entry<String, String> e : CapeManager.getAllPlayerCapes().entrySet()) {
            if (e.getKey().equals(localName)) continue;
            list.add(e);
        }

        return list;
    }

    private boolean isInArea(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ==================== INPUT ====================

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return;

        // Cape grid click
        if (isInArea(mouseX, mouseY, gridX, gridContentY, gridW, gridH - 18)) {
            int cellW = THUMB_W + THUMB_PAD;
            int cellH = THUMB_H + THUMB_PAD;
            int startX = gridX + THUMB_PAD;

            int col = (mouseX - startX) / cellW;
            int row = (mouseY - gridContentY) / cellH + capeScroll;
            int index = row * gridCols + col;

            if (mouseX >= startX && col < gridCols && index >= 0 && index < capeNames.length) {
                selectedCape = capeNames[index];
                ModConfig.saveSelectedCape(selectedCape);
                CapeAll.getCapeManager().forceBroadcast();
            }
        }

        // Preview drag start
        if (isInArea(mouseX, mouseY, previewX, previewY, previewW, previewH)) {
            dragging = true;
            dragLastX = mouseX;
        }
    }

    public void mouseReleased() {
        dragging = false;
    }

    public void mouseDragged(int mouseX) {
        if (dragging) {
            previewYaw += (mouseX - dragLastX) * 1.5f;
            dragLastX = mouseX;
        }
    }

    public void mouseScrolled(int mouseX, int mouseY, double scroll) {
        if (scroll == 0) return;

        if (isInArea(mouseX, mouseY, gridX, gridY, gridW, gridH)) {
            capeScroll += (scroll > 0) ? -1 : 1;
            capeScroll = Math.max(0, Math.min(maxCapeScroll, capeScroll));
        } else if (isInArea(mouseX, mouseY, listX, listY, listW, listH)) {
            playerScroll += (scroll > 0) ? -1 : 1;
            playerScroll = Math.max(0, Math.min(maxPlayerScroll, playerScroll));
        }
    }

    public void onClose() {
        CapeManager.setPreviewCape(null);
    }
}
