package com.nonopichy.capeall.mixin;

import com.nonopichy.capeall.CapeAll;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.nonopichy.capeall.util.CapeConnection;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo {
    @Shadow
    Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = Maps.newEnumMap(MinecraftProfileTexture.Type.class);
    @Shadow
    private final GameProfile gameProfile;

    public MixinNetworkPlayerInfo(GameProfile p_i46294_1_) {
        this.gameProfile = p_i46294_1_;
    }

    @Overwrite
    @Nullable
    public ResourceLocation getLocationCape() {
        String capeCustom = CapeConnection.getCapeAll(gameProfile.getName());
        if(capeCustom!=null && !capeCustom.equalsIgnoreCase("default"))
            return new ResourceLocation("minecraft:textures/cape/"+capeCustom+".png");
        this.loadPlayerTextures();
        return (ResourceLocation)this.playerTextures.get(MinecraftProfileTexture.Type.CAPE);
    }

    @Shadow
    private void loadPlayerTextures() {

    }

}
