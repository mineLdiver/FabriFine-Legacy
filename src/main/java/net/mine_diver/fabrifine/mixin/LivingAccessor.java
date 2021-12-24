package net.mine_diver.fabrifine.mixin;

import net.minecraft.entity.Living;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Living.class)
public interface LivingAccessor {

    @Accessor
    boolean getJumping();
}
