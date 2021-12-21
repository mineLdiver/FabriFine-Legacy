package net.mine_diver.fabrifine.mixin;

import net.modificationstation.stationapi.impl.client.texture.StationBlockRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StationBlockRenderer.class)
public abstract class MixinStationBlockRenderer {
    // TODO: fast lighting better grass
}
