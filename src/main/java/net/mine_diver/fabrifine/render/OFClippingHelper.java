package net.mine_diver.fabrifine.render;

import net.minecraft.class_84;

public interface OFClippingHelper {

    static OFClippingHelper of(class_84 clippingHelper) {
        return (OFClippingHelper) clippingHelper;
    }

    boolean isBoxInFrustumFully(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ);
}
