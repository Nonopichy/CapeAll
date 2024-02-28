package com.nonopichy.capeall.mixin;

import com.mojang.authlib.GameProfile;
import com.nonopichy.capeall.util.CapeConnection;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo
{
    @Shadow
    private final GameProfile gameProfile;
    @Shadow
    private ResourceLocation locationCape;

    public MixinNetworkPlayerInfo(GameProfile p_i46294_1_)
    {
        this.gameProfile = p_i46294_1_;
    }

    @Overwrite
    public ResourceLocation getLocationCape()
    {
        String capeCustom = CapeConnection.getCapeAll(gameProfile.getName());
        if(capeCustom!=null && !capeCustom.equalsIgnoreCase("default"))
            return new ResourceLocation("minecraft:textures/cape/"+capeCustom+".png");

        if (this.locationCape == null) {
            this.loadPlayerTextures();
        }

        return this.locationCape;
    }

    @Shadow
    public void loadPlayerTextures(){

    }

}
