package com.nonopichy.capeall;

import com.nonopichy.capeall.config.ConfigManager;
import com.nonopichy.capeall.config.ModConfig;
import com.nonopichy.capeall.util.CapeConnection;
import com.nonopichy.capeall.util.ChatUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = CapeAll.MODID, name = CapeAll.NAME, version = CapeAll.VERSION)
public class CapeAll {
    public static final String MODID = "capeall";
    public static final String NAME = "CapeAll - Nonopichy";
    public static final String VERSION = "1.0";

    private int tickCape = 0;
    private static boolean updatingCape = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        ConfigManager.loadConfig(config);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side.isClient()) {
            if (tickCape >= 20 * 60) {
                tickCape = 0;
                ChatUtil.enviarMensagemColorida("&7[CapeAll] Tentando atualizar capas...");
                try {
                    CapeConnection.updatePlayerData();
                } catch (Exception e){
                    updatingCape = false;
                    e.printStackTrace();
                }
            }
            if (!updatingCape) {
                tickCape++;
            }
        }
    }

}
