package com.nonopichy.capeall.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigManager {

    private static Configuration config;

    public static void loadConfig(File configFile) {
        config = new Configuration(configFile);

        ModConfig.serverURL = config.getString("serverURL", Configuration.CATEGORY_GENERAL, ModConfig.serverURL,
                "URL do servidor para a variavel SERVER_URL");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
