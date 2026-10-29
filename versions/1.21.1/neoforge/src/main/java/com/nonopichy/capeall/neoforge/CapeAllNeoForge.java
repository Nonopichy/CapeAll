package com.nonopichy.capeall.neoforge;

import com.nonopichy.capeall.CapeAll;
import com.nonopichy.capeall.CapeAllClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = CapeAll.MODID, dist = Dist.CLIENT)
public class CapeAllNeoForge {

    public CapeAllNeoForge(IEventBus modBus) {
        CapeAllClient.init(FMLPaths.CONFIGDIR.get());
        modBus.addListener((RegisterKeyMappingsEvent event) -> event.register(CapeAllClient.KEY));
    }
}
