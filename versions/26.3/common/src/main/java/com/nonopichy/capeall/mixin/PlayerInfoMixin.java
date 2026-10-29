package com.nonopichy.capeall.mixin;

import com.mojang.authlib.GameProfile;
import com.nonopichy.capeall.CapeAll;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {

    @Shadow
    public abstract GameProfile getProfile();

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void capeall$cape(CallbackInfoReturnable<PlayerSkin> cir) {
        String cape = CapeAll.capeFor(getProfile().name());
        if (cape == null) return;

        PlayerSkin skin = cir.getReturnValue();
        // ResourceTexture("cape/x") -> minecraft:textures/cape/x.png
        ClientAsset.Texture texture = new ClientAsset.ResourceTexture(Identifier.withDefaultNamespace("cape/" + cape));
        cir.setReturnValue(new PlayerSkin(skin.body(), texture, skin.elytra(), skin.model(), skin.secure()));
    }
}
