package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.render.OFClippingHelper;
import net.minecraft.class_84;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(class_84.class)
public class Mixinclass_84 implements OFClippingHelper {

    @Shadow public float[][] field_285;

    @Unique
    @Override
    public boolean isBoxInFrustumFully(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        for (int i = 0; i < 6; ++i) {
            final float minXf = (float)minX;
            final float minYf = (float)minY;
            final float minZf = (float)minZ;
            final float maxXf = (float)maxX;
            final float maxYf = (float)maxY;
            final float maxZf = (float)maxZ;
            if (i < 4) {
                if (this.field_285[i][0] * minXf + this.field_285[i][1] * minYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f || this.field_285[i][0] * maxXf + this.field_285[i][1] * minYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f || this.field_285[i][0] * minXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f || this.field_285[i][0] * maxXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f || this.field_285[i][0] * minXf + this.field_285[i][1] * minYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f || this.field_285[i][0] * maxXf + this.field_285[i][1] * minYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f || this.field_285[i][0] * minXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f || this.field_285[i][0] * maxXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f) {
                    return false;
                }
            }
            else if (this.field_285[i][0] * minXf + this.field_285[i][1] * minYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f && this.field_285[i][0] * maxXf + this.field_285[i][1] * minYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f && this.field_285[i][0] * minXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f && this.field_285[i][0] * maxXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * minZf + this.field_285[i][3] <= 0.0f && this.field_285[i][0] * minXf + this.field_285[i][1] * minYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f && this.field_285[i][0] * maxXf + this.field_285[i][1] * minYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f && this.field_285[i][0] * minXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f && this.field_285[i][0] * maxXf + this.field_285[i][1] * maxYf + this.field_285[i][2] * maxZf + this.field_285[i][3] <= 0.0f) {
                return false;
            }
        }
        return true;
    }
}
