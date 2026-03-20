package com.nonopichy.capeall.cape;

import com.nonopichy.capeall.config.ModConfig;
import com.nonopichy.capeall.protocol.PacketListener;
import com.nonopichy.capeall.protocol.SkinLayerPacket;
import com.nonopichy.capeall.protocol.SkinLayerProtocol;
import com.nonopichy.capeall.util.ChatUtil;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//
public class CapeManager implements PacketListener {

    public static final int CHANNEL_CAPE = 0;

    private static final Map<String, String> playerCapes = new ConcurrentHashMap<>();
    private static volatile String previewCape = null;

    private final SkinLayerProtocol protocol;
    private int broadcastTimer = 0;
    private int broadcastInterval;

    public CapeManager(SkinLayerProtocol protocol, int broadcastIntervalTicks) {
        this.protocol = protocol;
        this.broadcastInterval = broadcastIntervalTicks;
        protocol.registerListener(CHANNEL_CAPE, this);
    }

    @Override
    public void onPacketReceived(String playerName, SkinLayerPacket packet) {
        int capeId = packet.getFirstValue();
        String capeName = CapeRegistry.getNameById(capeId);

        if (capeName != null) {
            String oldCape = playerCapes.get(playerName);
            playerCapes.put(playerName, capeName);

            if (oldCape == null || !oldCape.equals(capeName)) {
                ChatUtil.enviarMensagemColorida(
                        "&7[CapeAll] &aCapa detectada: &f" + playerName + " &7-> &e" + capeName
                );
            }
        }
    }

    public void tick() {
        broadcastTimer++;
        if (broadcastTimer >= broadcastInterval) {
            broadcastTimer = 0;
            broadcastOwnCape();
        }
    }

    public void broadcastOwnCape() {
        String capeName = ModConfig.selectedCape;
        if (capeName == null || capeName.isEmpty() || capeName.equals("none")) return;

        int capeId = CapeRegistry.getIdByName(capeName);
        if (capeId < 0) return;

        if (protocol.isTransmitting()) return;

        protocol.send(CHANNEL_CAPE, capeId);
    }

    public void forceBroadcast() {
        broadcastTimer = broadcastInterval;
    }

    public static String getCape(String playerName) {
        return playerCapes.get(playerName);
    }

    public static boolean hasCape(String playerName) {
        return playerCapes.containsKey(playerName);
    }

    public static Map<String, String> getAllPlayerCapes() {
        return Collections.unmodifiableMap(playerCapes);
    }

    public static void setPreviewCape(String cape) {
        previewCape = cape;
    }

    public static String getPreviewCape() {
        return previewCape;
    }

    public static void clearAll() {
        playerCapes.clear();
        previewCape = null;
    }

    public void setBroadcastInterval(int ticks) {
        this.broadcastInterval = ticks;
    }
}
