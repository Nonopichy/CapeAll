package com.nonopichy.capeall.protocol;

import com.nonopichy.capeall.Platform;

import java.util.*;

public class SkinLayerReceiver {

    private static final int SYNC = 0x55;
    private static final int END = 0x7F;
    private static final int TIMEOUT_TICKS = 60;

    private final Platform platform;
    private final int ticksPerFrame;
    private final Map<String, PlayerState> playerStates = new HashMap<>();
    private final List<PacketListener> globalListeners = new ArrayList<>();
    private final Map<Integer, List<PacketListener>> channelListeners = new HashMap<>();

    public SkinLayerReceiver(Platform platform, int ticksPerFrame) {
        this.platform = platform;
        this.ticksPerFrame = ticksPerFrame;
    }

    public void registerListener(PacketListener listener) {
        globalListeners.add(listener);
    }

    public void registerListener(int channel, PacketListener listener) {
        channelListeners.computeIfAbsent(channel, k -> new ArrayList<>()).add(listener);
    }

    public void tick() {
        if (platform.localPlayerName() == null) {
            playerStates.clear();
            return;
        }

        Map<String, Integer> players = platform.otherPlayersSkinLayers();

        for (Map.Entry<String, Integer> player : players.entrySet()) {
            String name = player.getKey();
            int currentValue = player.getValue() & 0x7F;

            PlayerState ps = playerStates.computeIfAbsent(name, k -> new PlayerState());

            if (ps.state != ReceiveState.IDLE) {
                ps.ticksSinceSync++;
                if (ps.ticksSinceSync > TIMEOUT_TICKS) {
                    ps.reset();
                }
            }

            switch (ps.state) {
                case IDLE:
                    if (currentValue == SYNC && currentValue != ps.lastValue) {
                        ps.state = ReceiveState.WAIT_CHANNEL;
                        // Sample each frame in its middle, not on its first tick: a frame that arrives
                        // a tick late (network jitter) would otherwise be read as the previous one.
                        ps.tickCounter = -(ticksPerFrame / 2);
                        ps.ticksSinceSync = 0;
                    }
                    break;

                case WAIT_CHANNEL:
                    ps.tickCounter++;
                    if (ps.tickCounter >= ticksPerFrame) {
                        ps.channel = currentValue;
                        ps.state = ReceiveState.WAIT_LENGTH;
                        ps.tickCounter = 0;
                    }
                    break;

                case WAIT_LENGTH:
                    ps.tickCounter++;
                    if (ps.tickCounter >= ticksPerFrame) {
                        ps.dataLength = currentValue;
                        if (ps.dataLength > 127 || ps.dataLength < 0) {
                            ps.reset();
                            break;
                        }
                        ps.dataFrames = new int[ps.dataLength];
                        ps.frameIndex = 0;
                        ps.state = ps.dataLength > 0 ? ReceiveState.WAIT_DATA : ReceiveState.WAIT_END;
                        ps.tickCounter = 0;
                    }
                    break;

                case WAIT_DATA:
                    ps.tickCounter++;
                    if (ps.tickCounter >= ticksPerFrame) {
                        ps.dataFrames[ps.frameIndex++] = currentValue;
                        ps.tickCounter = 0;
                        if (ps.frameIndex >= ps.dataLength) {
                            ps.state = ReceiveState.WAIT_END;
                        }
                    }
                    break;

                case WAIT_END:
                    ps.tickCounter++;
                    if (ps.tickCounter >= ticksPerFrame) {
                        if (currentValue == END) {
                            SkinLayerPacket packet = new SkinLayerPacket(ps.channel, ps.dataFrames);
                            notifyListeners(name, packet);
                        }
                        ps.reset();
                    }
                    break;
            }

            ps.lastValue = currentValue;
        }

        playerStates.keySet().retainAll(players.keySet());
    }

    public void clear() {
        playerStates.clear();
    }

    private void notifyListeners(String playerName, SkinLayerPacket packet) {
        for (PacketListener listener : globalListeners) {
            listener.onPacketReceived(playerName, packet);
        }
        List<PacketListener> listeners = channelListeners.get(packet.getChannel());
        if (listeners != null) {
            for (PacketListener listener : listeners) {
                listener.onPacketReceived(playerName, packet);
            }
        }
    }

    private static class PlayerState {
        ReceiveState state = ReceiveState.IDLE;
        int tickCounter = 0;
        int ticksSinceSync = 0;
        int lastValue = -1;
        int channel;
        int dataLength;
        int[] dataFrames;
        int frameIndex;

        void reset() {
            state = ReceiveState.IDLE;
            tickCounter = 0;
            ticksSinceSync = 0;
            channel = 0;
            dataLength = 0;
            dataFrames = null;
            frameIndex = 0;
        }
    }

    private enum ReceiveState {
        IDLE, WAIT_CHANNEL, WAIT_LENGTH, WAIT_DATA, WAIT_END
    }
}
