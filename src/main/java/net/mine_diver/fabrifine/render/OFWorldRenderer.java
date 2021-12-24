package net.mine_diver.fabrifine.render;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Unique;

public interface OFWorldRenderer {

    static OFWorldRenderer of(WorldRenderer worldRenderer) {
        return (OFWorldRenderer) worldRenderer;
    }

    @Unique
    void setAllRenderesVisible();

    @Unique
    int renderAllSortedRenderers(int renderPass, double partialTicks);
}
