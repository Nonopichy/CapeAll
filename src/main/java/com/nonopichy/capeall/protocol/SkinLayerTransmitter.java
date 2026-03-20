package com.nonopichy.capeall.protocol;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SkinLayerTransmitter {

    private static final int PRE_SYNC = 0x00;
    private static final int SYNC = 0x55;
    private static final int END = 0x7F;

    private final int ticksPerFrame;
    private final Queue<SkinLayerPacket> queue = new ConcurrentLinkedQueue<>();

    private TransmitState state = TransmitState.IDLE;
    private int tickCounter = 0;
    private int originalSkinLayers = 0x7F;
    private SkinLayerPacket currentPacket;
    private int dataFrameIndex;
    private boolean transmitting = false;

    public SkinLayerTransmitter(int ticksPerFrame) {
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
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) {
            reset();
            return;
        }

        if (state == TransmitState.IDLE) {
            currentPacket = queue.poll();
            if (currentPacket == null) return;

            transmitting = true;
            originalSkinLayers = getCurrentSkinLayers();
            state = TransmitState.PRE_SYNC;
            tickCounter = 0;
            setSkinLayers(PRE_SYNC);
            return;
        }

        tickCounter++;
        if (tickCounter < ticksPerFrame) return;
        tickCounter = 0;

        switch (state) {
            case PRE_SYNC:
                setSkinLayers(SYNC);
                state = TransmitState.SYNC;
                break;

            case SYNC:
                setSkinLayers(currentPacket.getChannel() & 0x7F);
                state = TransmitState.HEADER_CHANNEL;
                break;

            case HEADER_CHANNEL:
                setSkinLayers(currentPacket.getDataLength() & 0x7F);
                state = TransmitState.HEADER_LENGTH;
                dataFrameIndex = 0;
                break;

            case HEADER_LENGTH:
                if (currentPacket.getDataLength() > 0) {
                    setSkinLayers(currentPacket.getValue(0));
                    dataFrameIndex = 1;
                    state = TransmitState.DATA;
                } else {
                    setSkinLayers(END);
                    state = TransmitState.END;
                }
                break;

            case DATA:
                if (dataFrameIndex < currentPacket.getDataLength()) {
                    setSkinLayers(currentPacket.getValue(dataFrameIndex));
                    dataFrameIndex++;
                } else {
                    setSkinLayers(END);
                    state = TransmitState.END;
                }
                break;

            case END:
                setSkinLayers(originalSkinLayers);
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
        transmitting = false;
        state = TransmitState.IDLE;
        tickCounter = 0;
        currentPacket = null;
        dataFrameIndex = 0;
    }

    private int getCurrentSkinLayers() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return 0x7F;
        return mc.thePlayer.getDataWatcher().getWatchableObjectByte(10) & 0x7F;
    }

    private void setSkinLayers(int value) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.gameSettings == null) return;

        Set<EnumPlayerModelParts> currentParts = mc.gameSettings.getModelParts();
        for (EnumPlayerModelParts part : EnumPlayerModelParts.values()) {
            boolean shouldBeEnabled = (value & part.getPartMask()) != 0;
            boolean isEnabled = currentParts.contains(part);
            if (shouldBeEnabled != isEnabled) {
                mc.gameSettings.switchModelPartEnabled(part);
            }
        }
    }

    private enum TransmitState {
        IDLE, PRE_SYNC, SYNC, HEADER_CHANNEL, HEADER_LENGTH, DATA, END, RESTORE
    }
}
