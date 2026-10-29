package com.nonopichy.capeall.fabric;

import com.nonopichy.capeall.CapeAllClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

public class CapeAllFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CapeAllClient.init(FabricLoader.getInstance().getConfigDir());
        KeyMapping.Category.register(CapeAllClient.CATEGORY.id());
        KeyMappingHelper.registerKeyMapping(CapeAllClient.KEY);
    }
}
