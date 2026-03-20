package com.nonopichy.capeall.mixin;

import com.mojang.authlib.GameProfile;
import com.nonopichy.capeall.cape.CapeManager;
import com.nonopichy.capeall.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo {

    @Shadow
    private GameProfile gameProfile;

    @Shadow
    private ResourceLocation locationCape;

    @Overwrite
    public ResourceLocation getLocationCape() {
        String playerName = gameProfile.getName();
        Minecraft mc = Minecraft.getMinecraft();

        // Local player: preview > selected > default
        if (mc.thePlayer != null && mc.thePlayer.getName().equals(playerName)) {
            String preview = CapeManager.getPreviewCape();
            if (preview != null) {
                return new ResourceLocation("minecraft:textures/cape/" + preview + ".png");
            }

            String selected = ModConfig.selectedCape;
            if (selected != null && !selected.isEmpty() && !selected.equals("none")) {
                return new ResourceLocation("minecraft:textures/cape/" + selected + ".png");
            }
        }
//
        // Other players: check protocol data
        String capeName = CapeManager.getCape(playerName);
        if (capeName != null) {
            return new ResourceLocation("minecraft:textures/cape/" + capeName + ".png");
        }

        if (this.locationCape == null) {
            this.loadPlayerTextures();
        }
        return this.locationCape;
    }

    @Shadow
    public abstract void loadPlayerTextures();
}
