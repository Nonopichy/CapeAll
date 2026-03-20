package com.nonopichy.capeall.protocol;

public class SkinLayerProtocol {

    private final SkinLayerTransmitter transmitter;
    private final SkinLayerReceiver receiver;

    public SkinLayerProtocol(int ticksPerFrame) {
        this.transmitter = new SkinLayerTransmitter(ticksPerFrame);
        this.receiver = new SkinLayerReceiver(ticksPerFrame);
    }

    public void registerListener(PacketListener listener) {
        receiver.registerListener(listener);
    }

    public void registerListener(int channel, PacketListener listener) {
        receiver.registerListener(channel, listener);
    }

    public void send(int channel, int[] data) {
        transmitter.enqueue(new SkinLayerPacket(channel, data));
    }
//
    public void send(int channel, int singleValue) {
        transmitter.enqueue(SkinLayerPacket.single(channel, singleValue));
    }

    public void tick() {
        transmitter.tick();
        receiver.tick();
    }

    public boolean isTransmitting() {
        return transmitter.isTransmitting();
    }

    public void reset() {
        transmitter.reset();
        receiver.clear();
    }

    public SkinLayerTransmitter getTransmitter() {
        return transmitter;
    }

    public SkinLayerReceiver getReceiver() {
        return receiver;
    }
}
