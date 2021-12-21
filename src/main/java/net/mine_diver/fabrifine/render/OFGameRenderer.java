package net.mine_diver.fabrifine.render;

import net.minecraft.sortme.GameRenderer;

public interface OFGameRenderer {

    static OFGameRenderer of(GameRenderer gameRenderer) {
        return (OFGameRenderer) gameRenderer;
    }

    void updateWorldLightLevels();
}
