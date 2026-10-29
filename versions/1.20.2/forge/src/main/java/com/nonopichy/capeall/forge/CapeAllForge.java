package com.nonopichy.capeall.forge;

import com.nonopichy.capeall.CapeAll;
import com.nonopichy.capeall.CapeAllClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(CapeAll.MODID)
public class CapeAllForge {

    public CapeAllForge() {
        if (FMLEnvironment.dist != Dist.CLIENT) return;
        CapeAllClient.init(FMLPaths.CONFIGDIR.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener((RegisterKeyMappingsEvent event) -> event.register(CapeAllClient.KEY));
    }
}
