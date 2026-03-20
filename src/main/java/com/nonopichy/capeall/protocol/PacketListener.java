package com.nonopichy.capeall.protocol;
//
public interface PacketListener {

    void onPacketReceived(String playerName, SkinLayerPacket packet);
}
