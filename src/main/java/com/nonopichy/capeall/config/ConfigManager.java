package com.nonopichy.capeall.config;

import net.minecraftforge.common.config.Configuration;

public class ConfigManager {

    private static Configuration config;

    public static void loadConfig(Configuration config) {
        ConfigManager.config = config;

        ModConfig.selectedCape = config.getString(
                "selectedCape", Configuration.CATEGORY_GENERAL,
                ModConfig.selectedCape,
                "Nome da capa selecionada (ex: mojang, nyan, classic)"
        );

        ModConfig.ticksPerFrame = config.getInt(
                "ticksPerFrame", Configuration.CATEGORY_GENERAL,
                ModConfig.ticksPerFrame, 3, 10,
                "Ticks por frame do protocolo (maior = mais confiavel, menor = mais rapido)"
        );

        ModConfig.broadcastInterval = config.getInt(
                "broadcastInterval", Configuration.CATEGORY_GENERAL,
                ModConfig.broadcastInterval, 100, 2400,
                "Intervalo de broadcast da capa em ticks (600 = 30s)"
        );

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void saveSelectedCape(String cape) {
        if (config != null) {
            config.get(Configuration.CATEGORY_GENERAL, "selectedCape", "mojang").set(cape);
            config.save();
        }
    }
}
