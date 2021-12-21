package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.config.Config;
import net.mine_diver.fabrifine.config.OFConfig;
import net.mine_diver.fabrifine.render.OFGameRenderer;
import net.minecraft.block.BlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.Level;
import net.minecraft.level.dimension.Dimension;
import net.minecraft.sortme.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements OFGameRenderer {

    @Shadow private Minecraft minecraft;

    @Unique
    private Dimension updatedWorldProvider;
    @Unique
    private boolean
            showDebugInfo,
            zoomMode;

    @Override
    @Unique
    public void updateWorldLightLevels() {
        if (this.minecraft == null) {
            return;
        }
        if (this.minecraft.level == null) {
            return;
        }
        if (this.minecraft.level.dimension == null) {
            return;
        }
        final float brightness = OFConfig.of(this.minecraft.options).getOfBrightness();
        final float[] lightLevels = this.minecraft.level.dimension.lightTable;
        float minLevel = 0.05f;
        if (this.minecraft.level.dimension.blocksCompassAndClock) {
            minLevel = 0.1f + brightness * 0.15f;
        }
        final float k = 3.0f * (1.0f - brightness);
        for (int i = 0; i <= 15; ++i) {
            final float f1 = 1.0f - i / 15.0f;
            lightLevels[i] = (1.0f - f1) / (f1 * k + 1.0f) * (1.0f - minLevel) + minLevel;
        }
        Config.setLightLevels(lightLevels);
    }

    @Inject(
            method = "method_1844(F)V",
            at = @At("HEAD")
    )
    private void deltaRender(float delta, CallbackInfo ci) {
        final Level world = this.minecraft.level;
        if (world != null && world.dimension != null && this.updatedWorldProvider != world.dimension) {
            this.updateWorldLightLevels();
            this.updatedWorldProvider = this.minecraft.level.dimension;
        }
        Minecraft.isPremiumCheckTime = 0L;
        BlockRenderer.fancyGraphics = Config.isGrassFancy();
        if (Config.isBetterGrassFancy()) {
            BlockRenderer.fancyGraphics = true;
        }
        BlockBase.LEAVES.method_991(Config.isTreesFancy());
        Config.setMinecraft(this.minecraft);
//        if (Config.getIconWidthTerrain() > 16 && !(this.c instanceof ItemRendererHD)) {
//            this.c = new ItemRendererHD(this.j);
//            th.a.f = this.c;
//        }
        if (world != null) {
            ((LevelAccessor) world).setField_212(OFConfig.of(this.minecraft.options).getOfAutoSaveTicks());
        }
        if (!Config.isWeatherEnabled() && world != null && world.getProperties() != null) {
            world.getProperties().setRaining(false);
        }
        if (world != null) {
            final long time = world.getLevelTime();
            final long timeOfDay = time % 24000L;
            if (Config.isTimeDayOnly()) {
                if (timeOfDay <= 1000L) {
                    world.setLevelTime(time - timeOfDay + 1001L);
                }
                if (timeOfDay >= 11000L) {
                    world.setLevelTime(time - timeOfDay + 24001L);
                }
            }
            if (Config.isTimeNightOnly()) {
                if (timeOfDay <= 14000L) {
                    world.setLevelTime(time - timeOfDay + 14001L);
                }
                if (timeOfDay >= 22000L) {
                    world.setLevelTime(time - timeOfDay + 24000L + 14001L);
                }
            }
        }
    }
}
