package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.render.OFCamera;
import net.mine_diver.fabrifine.render.OFClippingHelper;
import net.minecraft.class_573;
import net.minecraft.class_84;
import net.minecraft.util.maths.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(class_573.class)
public abstract class Mixinclass_573 implements OFCamera {

    @Shadow private class_84 field_2465;

    @Shadow private double field_2466;

    @Shadow private double field_2467;

    @Shadow private double field_2468;

    @Unique
    public boolean isBoxInFrustumFully(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        return OFClippingHelper.of(this.field_2465).isBoxInFrustumFully(minX - this.field_2466, minY - this.field_2467, minZ - this.field_2468, maxX - this.field_2466, maxY - this.field_2467, maxZ - this.field_2468);
    }

    @Unique
    @Override
    public boolean isBoundingBoxInFrustumFully(final Box aab) {
        return this.isBoxInFrustumFully(aab.minX, aab.minY, aab.minZ, aab.maxX, aab.maxY, aab.maxZ);
    }
}
