package com.nonopichy.capeall.fabric;

import com.nonopichy.capeall.CapeAllClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;

public class CapeAllFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CapeAllClient.init(FabricLoader.getInstance().getConfigDir());
        KeyBindingHelper.registerKeyBinding(CapeAllClient.KEY);
    }
}
