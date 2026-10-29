package com.nonopichy.capeall;

import com.mojang.blaze3d.platform.InputConstants;
import com.nonopichy.capeall.gui.CapeScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelPart;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Minecraft 26.3 side of CapeAll: the {@link Platform}, the menu key and the tick hook. */
public final class CapeAllClient implements Platform {

    /** Registered by each loader entrypoint (NeoForge has its own event for it). */
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(CapeAll.MODID, "main"));
    public static final KeyMapping KEY = new KeyMapping("key.capeall.menu", InputConstants.KEY_I, CATEGORY);

    /** Called by the loader entrypoint. */
    public static void init(Path configDir) {
        CapeAll.init(new CapeAllClient(), configDir);
    }

    /** Start of every client tick (MinecraftMixin). */
    public static void tick() {
        CapeAll.tick();
        Minecraft mc = Minecraft.getInstance();
        while (KEY.consumeClick()) {
            if (mc.player != null && mc.gui.screen() == null) mc.gui.setScreen(new CapeScreen());
        }
    }

    public static Identifier capeTexture(String cape) {
        return Identifier.withDefaultNamespace("textures/cape/" + cape + ".png");
    }

    @Override
    public String localPlayerName() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.level != null ? mc.player.getGameProfile().name() : null;
    }

    @Override
    public Map<String, Integer> otherPlayersSkinLayers() {
        Minecraft mc = Minecraft.getInstance();
        Map<String, Integer> players = new HashMap<>();
        for (AbstractClientPlayer player : mc.level.players()) {
            if (player == mc.player) continue;
            int bits = 0;
            for (PlayerModelPart part : PlayerModelPart.values()) {
                if (player.isModelPartShown(part)) bits |= part.getMask();
            }
            players.put(player.getGameProfile().name(), bits);
        }
        return players;
    }

    @Override
    public int getSkinLayers() {
        Options options = Minecraft.getInstance().options;
        int bits = 0;
        for (PlayerModelPart part : PlayerModelPart.values()) {
            if (options.isModelPartEnabled(part)) bits |= part.getMask();
        }
        return bits;
    }

    @Override
    public void setSkinLayers(int bits) {
        Options options = Minecraft.getInstance().options;
        for (PlayerModelPart part : PlayerModelPart.values()) {
            options.setModelPart(part, (bits & part.getMask()) != 0);
        }
        options.broadcastOptions();
    }

    @Override
    public void chat(String message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) mc.player.sendSystemMessage(Component.literal(message));
    }

    @Override
    public String menuKeyName() {
        return KEY.getTranslatedKeyMessage().getString();
    }
}
