package com.nonopichy.capeall;

import com.nonopichy.capeall.cape.CapeManager;
import com.nonopichy.capeall.cape.CapeRegistry;
import com.nonopichy.capeall.config.ModConfig;
import com.nonopichy.capeall.protocol.SkinLayerProtocol;

import java.nio.file.Path;

public final class CapeAll {
    public static final String MODID = "capeall";

    private static Platform platform;
    private static SkinLayerProtocol protocol;
    private static CapeManager capeManager;
    private static boolean inWorld = false;

    private CapeAll() {
    }

    /** Called once by the loader entrypoint. */
    public static void init(Platform platform, Path configDir) {
        CapeAll.platform = platform;
        ModConfig.load(configDir.resolve("capeall.properties"));
        protocol = new SkinLayerProtocol(platform, ModConfig.ticksPerFrame);
        capeManager = new CapeManager(protocol, ModConfig.broadcastInterval);
    }

    /** Called at the start of every client tick. */
    public static void tick() {
        if (platform.localPlayerName() == null) {
            if (inWorld) {
                inWorld = false;
                protocol.reset();
                CapeManager.clearAll();
            }
            return;
        }

        protocol.tick();
        capeManager.tick();

        if (!inWorld) {
            inWorld = true;
            platform.chat("§7[CapeAll] §aMod carregado! Tecla: §e" + platform.menuKeyName() + " §7| Capa: §e" + ModConfig.selectedCape);
        }
    }

    /** Cape texture name to draw on this player, or null to keep the vanilla cape. */
    public static String capeFor(String playerName) {
        if (playerName.equals(platform.localPlayerName())) {
            String preview = CapeManager.getPreviewCape();
            if (preview != null) return preview;
            if (CapeRegistry.isValidName(ModConfig.selectedCape)) return ModConfig.selectedCape;
        }
        return CapeManager.getCape(playerName);
    }

    public static Platform getPlatform() {
        return platform;
    }

    public static CapeManager getCapeManager() {
        return capeManager;
    }
}
