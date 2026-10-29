package com.nonopichy.capeall.protocol;

public class SkinLayerPacket {

    private final int channel;
    private final int[] data;

    public SkinLayerPacket(int channel, int[] data) {
        this.channel = channel;
        this.data = data;
    }

    public static SkinLayerPacket single(int channel, int value) {
        return new SkinLayerPacket(channel, new int[]{value & 0x7F});
    }

    public int getChannel() {
        return channel;
    }

    public int[] getData() {
        return data;
    }

    public int getDataLength() {
        return data.length;
    }

    public int getValue(int index) {
        return data[index];
    }

    public int getFirstValue() {
        return data.length > 0 ? data[0] : 0;
    }
}
