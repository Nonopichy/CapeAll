package com.nonopichy.capeall.protocol;

import com.nonopichy.capeall.Platform;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkinLayerTransmitter {

    private static final int PRE_SYNC = 0x00;
    private static final int SYNC = 0x55;
    private static final int END = 0x7F;

    private final Platform platform;
    private final int ticksPerFrame;
    private final Queue<SkinLayerPacket> queue = new ConcurrentLinkedQueue<>();

    private TransmitState state = TransmitState.IDLE;
    private int tickCounter = 0;
    private int originalSkinLayers = 0x7F;
    private SkinLayerPacket currentPacket;
    private int dataFrameIndex;
    private boolean transmitting = false;

    public SkinLayerTransmitter(Platform platform, int ticksPerFrame) {
        this.platform = platform;
        this.ticksPerFrame = ticksPerFrame;
    }

    public void enqueue(SkinLayerPacket packet) {
        queue.add(packet);
    }

    public boolean isTransmitting() {
        return transmitting;
    }

    public int getOriginalSkinLayers() {
        return originalSkinLayers;
    }

    public void tick() {
        if (platform.localPlayerName() == null) {
            reset();
            return;
        }

        if (state == TransmitState.IDLE) {
            currentPacket = queue.poll();
            if (currentPacket == null) return;

            transmitting = true;
            originalSkinLayers = platform.getSkinLayers();
            state = TransmitState.PRE_SYNC;
            tickCounter = 0;
            platform.setSkinLayers(PRE_SYNC);
            return;
        }

        tickCounter++;
        if (tickCounter < ticksPerFrame) return;
        tickCounter = 0;

        switch (state) {
            case PRE_SYNC:
                platform.setSkinLayers(SYNC);
                state = TransmitState.SYNC;
                break;

            case SYNC:
                platform.setSkinLayers(currentPacket.getChannel() & 0x7F);
                state = TransmitState.HEADER_CHANNEL;
                break;

            case HEADER_CHANNEL:
                platform.setSkinLayers(currentPacket.getDataLength() & 0x7F);
                state = TransmitState.HEADER_LENGTH;
                dataFrameIndex = 0;
                break;

            case HEADER_LENGTH:
                if (currentPacket.getDataLength() > 0) {
                    platform.setSkinLayers(currentPacket.getValue(0));
                    dataFrameIndex = 1;
                    state = TransmitState.DATA;
                } else {
                    platform.setSkinLayers(END);
                    state = TransmitState.END;
                }
                break;

            case DATA:
                if (dataFrameIndex < currentPacket.getDataLength()) {
                    platform.setSkinLayers(currentPacket.getValue(dataFrameIndex));
                    dataFrameIndex++;
                } else {
                    platform.setSkinLayers(END);
                    state = TransmitState.END;
                }
                break;

            case END:
                platform.setSkinLayers(originalSkinLayers);
                state = TransmitState.RESTORE;
                break;

            case RESTORE:
                reset();
                break;

            default:
                break;
        }
    }

    public void reset() {
        // Interrupted mid-transmission (disconnect): give the player their real skin layers back.
        if (transmitting) platform.setSkinLayers(originalSkinLayers);
        transmitting = false;
        state = TransmitState.IDLE;
        tickCounter = 0;
        currentPacket = null;
        dataFrameIndex = 0;
    }

    private enum TransmitState {
        IDLE, PRE_SYNC, SYNC, HEADER_CHANNEL, HEADER_LENGTH, DATA, END, RESTORE
    }
}
