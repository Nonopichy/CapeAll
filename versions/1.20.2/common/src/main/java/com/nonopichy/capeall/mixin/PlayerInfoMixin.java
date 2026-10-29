package com.nonopichy.capeall.mixin;

import com.nonopichy.capeall.CapeAll;
import com.nonopichy.capeall.CapeAllClient;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void capeall$cape(CallbackInfoReturnable<PlayerSkin> cir) {
        // Plain call instead of @Shadow: reobfuscation (SRG versions) remaps it like any other call.
        String cape = CapeAll.capeFor(((PlayerInfo) (Object) this).getProfile().getName());
        if (cape == null) return;

        PlayerSkin skin = cir.getReturnValue();
        cir.setReturnValue(new PlayerSkin(skin.texture(), skin.textureUrl(), CapeAllClient.capeTexture(cape),
                skin.elytraTexture(), skin.model(), skin.secure()));
    }
}
