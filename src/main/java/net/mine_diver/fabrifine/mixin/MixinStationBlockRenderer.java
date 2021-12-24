package net.mine_diver.fabrifine.mixin;

import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.impl.client.texture.StationBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StationBlockRenderer.class)
public abstract class MixinStationBlockRenderer {
    // TODO: fast lighting better grass

    @Redirect(
            method = "lambda$endMeshRender$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;setOffset(DDD)V"
            )
    )
    private static void stopOffset1(Tessellator instance, double d1, double d2, double v) {}

    @Redirect(
            method = "prepareTessellator(Lnet/modificationstation/stationapi/api/client/texture/atlas/Atlas;)Lnet/minecraft/client/render/Tessellator;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;setOffset(DDD)V"
            )
    )
    private void stopOffset2(Tessellator instance, double d1, double d2, double v) {}
}
