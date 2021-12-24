package net.mine_diver.fabrifine.render;

import net.minecraft.class_68;
import net.minecraft.util.maths.Box;

public interface OFCamera extends class_68 {

    static OFCamera of(class_68 camera) {
        return (OFCamera) camera;
    }

    boolean isBoundingBoxInFrustumFully(final Box p0);
}
