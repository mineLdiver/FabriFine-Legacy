package net.mine_diver.fabrifine.render;

import net.minecraft.client.render.Tessellator;

public interface OFTessellator {

    static OFTessellator of(Tessellator tessellator) {
        return (OFTessellator) tessellator;
    }

    void setRenderingChunk(boolean renderingChunk);
}
