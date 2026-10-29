package com.nonopichy.capeall.protocol;

import com.nonopichy.capeall.Platform;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkinLayerProtocolTest {

    @Test
    void capeIdSurvivesNetworkDelayAndJitter() {
        for (int seed = 0; seed < 200; seed++) {
            assertEquals(List.of(42), transmit(42, new Random(seed)), "seed " + seed);
        }
    }

    /**
     * Player A sends one value; player B sees A's skin layers 1-3 ticks late (in order, like TCP).
     * Returns the values B decoded.
     */
    private static List<Integer> transmit(int value, Random random) {
        List<Integer> sent = new ArrayList<>();    // A's layers at the end of each tick
        List<Integer> arrival = new ArrayList<>(); // tick each of those reaches B
        int[] layers = {0x7F};
        int[] tick = {0};

        Platform a = new FakePlatform("A") {
            @Override
            public int getSkinLayers() {
                return layers[0];
            }

            @Override
            public void setSkinLayers(int bits) {
                layers[0] = bits;
            }
        };
        Platform b = new FakePlatform("B") {
            @Override
            public Map<String, Integer> otherPlayersSkinLayers() {
                int visible = 0x7F;
                for (int i = 0; i < sent.size() && arrival.get(i) <= tick[0]; i++) visible = sent.get(i);
                return Map.of("A", visible);
            }
        };

        SkinLayerTransmitter transmitter = new SkinLayerTransmitter(a, 5);
        SkinLayerReceiver receiver = new SkinLayerReceiver(b, 5);
        List<Integer> decoded = new ArrayList<>();
        receiver.registerListener((player, packet) -> decoded.add(packet.getFirstValue()));

        transmitter.enqueue(SkinLayerPacket.single(0, value));
        for (tick[0] = 0; tick[0] < 120; tick[0]++) {
            transmitter.tick();
            sent.add(layers[0]);
            int previous = arrival.isEmpty() ? 0 : arrival.get(arrival.size() - 1);
            arrival.add(Math.max(previous, tick[0] + 1 + random.nextInt(3)));
            receiver.tick();
        }

        assertEquals(0x7F, layers[0], "A's real skin layers are restored");
        return decoded;
    }

    private static class FakePlatform implements Platform {
        private final String name;

        FakePlatform(String name) {
            this.name = name;
        }

        @Override
        public String localPlayerName() {
            return name;
        }

        @Override
        public Map<String, Integer> otherPlayersSkinLayers() {
            return Collections.emptyMap();
        }

        @Override
        public int getSkinLayers() {
            return 0x7F;
        }

        @Override
        public void setSkinLayers(int bits) {
        }

        @Override
        public void chat(String message) {
        }

        @Override
        public String menuKeyName() {
            return "I";
        }
    }
}
