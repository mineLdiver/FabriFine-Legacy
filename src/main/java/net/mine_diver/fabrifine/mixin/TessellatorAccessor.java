package net.mine_diver.fabrifine.mixin;

import net.minecraft.client.render.Tessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Tessellator.class)
public interface TessellatorAccessor {

    @Invoker("<init>")
    static Tessellator invokeCor(int bufferSize) {
        throw new AssertionError("Mixin!");
    }

    @Accessor
    static void setINSTANCE(Tessellator instance) {
        throw new AssertionError("Mixin!");
    }

    @Accessor
    boolean getDrawing();
}
