package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.render.OFTessellator;
import net.minecraft.client.render.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Tessellator.class)
public class MixinTessellator {

    @ModifyArg(
            method = "addVertex(DDD)V",
            index = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Float;floatToRawIntBits(F)I",
                    ordinal = 2,
                    remap = false
            )
    )
    private float modifyX(float value) {
        return value - OFTessellator.chunkOffsetX;
    }

    @ModifyArg(
            method = "addVertex(DDD)V",
            index = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Float;floatToRawIntBits(F)I",
                    ordinal = 4,
                    remap = false
            )
    )
    private float modifyZ(float value) {
        return value - OFTessellator.chunkOffsetZ;
    }
}
