package com.nonopichy.capeall.config;

import net.minecraftforge.common.config.Configuration;

public class ConfigManager {

    private static Configuration config;

    public static void loadConfig(Configuration config) {
        ConfigManager.config = config;
        ModConfig.serverURL = config.getString("Server URL", Configuration.CATEGORY_GENERAL, ModConfig.serverURL, "URL do servidor para a variável SERVER_URL");
        if (config.hasChanged()) {
            config.save();
        }
    }
}
