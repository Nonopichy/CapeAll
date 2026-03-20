package com.nonopichy.capeall;

import com.nonopichy.capeall.cape.CapeManager;
import com.nonopichy.capeall.config.ConfigManager;
import com.nonopichy.capeall.config.ModConfig;
import com.nonopichy.capeall.gui.CapeScreen;
import com.nonopichy.capeall.protocol.SkinLayerProtocol;
import com.nonopichy.capeall.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = CapeAll.MODID, name = CapeAll.NAME, version = CapeAll.VERSION)
public class CapeAll {
    public static final String MODID = "capeall";
    public static final String NAME = "CapeAll - Nonopichy";
    public static final String VERSION = "2.0";

    private static SkinLayerProtocol protocol;
    private static CapeManager capeManager;
    private static KeyBinding keyOpenGui;
    private boolean initialized = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        ConfigManager.loadConfig(config);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        protocol = new SkinLayerProtocol(ModConfig.ticksPerFrame);
        capeManager = new CapeManager(protocol, ModConfig.broadcastInterval);

        keyOpenGui = new KeyBinding("CapeAll Menu", Keyboard.KEY_P, "CapeAll");
        ClientRegistry.registerKeyBinding(keyOpenGui);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !event.side.isClient()) return;

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer != null && mc.theWorld != null) {
            protocol.tick();
            capeManager.tick();

            if (!initialized) {
                initialized = true;
                ChatUtil.enviarMensagemColorida(
                        "&7[CapeAll] &aMod carregado! Tecla: &eP &7| Capa: &e" + ModConfig.selectedCape
                );
            }
        }

        // Keybind
        if (mc.currentScreen == null && keyOpenGui.isPressed()) {
            mc.displayGuiScreen(new CapeScreen());
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        protocol.reset();
        CapeManager.clearAll();
        initialized = false;
    }

    public static SkinLayerProtocol getProtocol() {
        return protocol;
    }

    public static CapeManager getCapeManager() {
        return capeManager;
    }

    public static KeyBinding getKeyBind() {
        return keyOpenGui;
    }
}
