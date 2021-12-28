package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.render.OFTessellator;
import net.minecraft.client.render.Tessellator;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.impl.client.texture.StationBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(StationBlockRenderer.class)
public abstract class MixinStationBlockRenderer {
    // TODO: fast lighting better grass

    @Inject(
            method = "lambda$endMeshRender$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;draw()V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void setRenderingInChunkFalse(Atlas atlas, CallbackInfo ci, Tessellator tessellator) {
        OFTessellator.of(tessellator).setRenderingChunk(false);
    }

    @Redirect(
            method = "lambda$endMeshRender$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;setOffset(DDD)V"
            )
    )
    private static void stopOffset1(Tessellator instance, double d1, double d2, double v) {}

    @Inject(
            method = "prepareTessellator(Lnet/modificationstation/stationapi/api/client/texture/atlas/Atlas;)Lnet/minecraft/client/render/Tessellator;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;start()V",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void setRenderingInChunkTrue(Atlas atlas, CallbackInfoReturnable<Tessellator> cir, Tessellator tessellator) {
        OFTessellator.of(tessellator).setRenderingChunk(true);
    }

    @Redirect(
            method = "prepareTessellator(Lnet/modificationstation/stationapi/api/client/texture/atlas/Atlas;)Lnet/minecraft/client/render/Tessellator;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;setOffset(DDD)V"
            )
    )
    private void stopOffset2(Tessellator instance, double d1, double d2, double v) {}

    //TODO: fix stuff below

    @Redirect(
            method = "prepareTessellator",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/modificationstation/stationapi/mixin/render/client/TessellatorAccessor;getDrawing()Z",
                    ordinal = 1
            )
    )
    private boolean endIfRunning(net.modificationstation.stationapi.mixin.render.client.TessellatorAccessor instance) {
        capturedDrawing = instance.getDrawing();
        return false;
    }

    @Unique
    private boolean capturedDrawing;

    @Redirect(
            method = "prepareTessellator",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;start()V",
                    ordinal = 1
            )
    )
    private void start(Tessellator instance) {
        if (!capturedDrawing)
            instance.start();
    }
}
