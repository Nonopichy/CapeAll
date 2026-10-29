package com.nonopichy.capeall.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** config/capeall.properties - plain java.util.Properties, so it works the same on every loader. */
public class ModConfig {

    public static String selectedCape = "mojang";
    public static int ticksPerFrame = 5;
    public static int broadcastInterval = 20 * 30; // 30 segundos

    private static Path file;

    public static void load(Path path) {
        file = path;
        Properties p = new Properties();
        if (Files.exists(path)) {
            try (Reader r = Files.newBufferedReader(path)) {
                p.load(r);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        selectedCape = p.getProperty("selectedCape", selectedCape).trim();
        ticksPerFrame = getInt(p, "ticksPerFrame", ticksPerFrame, 3, 10);
        broadcastInterval = getInt(p, "broadcastInterval", broadcastInterval, 100, 2400);
        save();
    }

    public static void saveSelectedCape(String cape) {
        selectedCape = cape;
        save();
    }

    private static void save() {
        Properties p = new Properties();
        p.setProperty("selectedCape", selectedCape);
        p.setProperty("ticksPerFrame", String.valueOf(ticksPerFrame));
        p.setProperty("broadcastInterval", String.valueOf(broadcastInterval));
        try {
            Files.createDirectories(file.getParent());
            try (Writer w = Files.newBufferedWriter(file)) {
                p.store(w, "CapeAll | selectedCape: nome da capa (ex: mojang, nyan, classic)"
                        + " | ticksPerFrame: 3-10 | broadcastInterval: 100-2400 ticks (600 = 30s)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getInt(Properties p, String key, int def, int min, int max) {
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(p.getProperty(key, "").trim())));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
