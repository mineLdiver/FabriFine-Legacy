package net.mine_diver.fabrifine.mixin;

import net.minecraft.block.BlockBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockBase.class)
public interface BlockBaseAccessor {

    @Invoker
    BlockBase invokeSetLightOpacity(int lightOpacity);
}
