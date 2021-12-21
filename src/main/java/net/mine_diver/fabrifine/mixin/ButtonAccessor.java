package net.mine_diver.fabrifine.mixin;

import net.minecraft.client.gui.widgets.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Button.class)
public interface ButtonAccessor {

    @Accessor
    int getWidth();

    @Accessor
    int getHeight();
}
