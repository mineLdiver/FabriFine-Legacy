package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.config.Config;
import net.mine_diver.fabrifine.config.OFConfig;
import net.mine_diver.fabrifine.config.OptionsListener;
import net.mine_diver.fabrifine.render.OFGameRenderer;
import net.mine_diver.fabrifine.render.OFWorldRenderer;
import net.mine_diver.fabrifine.render.UpdateThread;
import net.minecraft.block.BlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.entity.Living;
import net.minecraft.level.Level;
import net.minecraft.level.dimension.Dimension;
import net.minecraft.sortme.GameRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer implements OFGameRenderer {

    @Shadow private Minecraft minecraft;

    @Shadow private float field_2350;
    @Unique
    private Dimension updatedWorldProvider;
    @Unique
    private boolean
            showDebugInfo,
            zoomMode;

    @ModifyVariable(
            method = "method_1848(F)F",
            index = 3,
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Living;health:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private float zoom(float f2) {
        if (Keyboard.isKeyDown(OptionsListener.getOF_KEY_BIND_ZOOM().key)) {
            if (!this.zoomMode) {
                this.zoomMode = true;
                this.minecraft.options.cinematicMode = true;
            }
            f2 /= 4.0f;
        }
        else if (this.zoomMode) {
            this.zoomMode = false;
            this.minecraft.options.cinematicMode = false;
        }
        return f2;
    }

    @Redirect(
            method = "method_1840(FI)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/sortme/GameRenderer;field_2350:F",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void fog(GameRenderer instance, float value) {
        this.field_2350 = (float)(32 << 3 - this.minecraft.options.viewDistance);
        if (Config.isFarView()) {
            if (this.field_2350 < 256.0f) {
                this.field_2350 *= 3.0f;
            }
            else {
                this.field_2350 *= 2.0f;
            }
        }
        if (Config.isFogFancy()) {
            this.field_2350 *= 0.95f;
        }
        else {
            this.field_2350 *= 0.83f;
        }
    }

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

    @Inject(
            method = "method_1844(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/InGame;renderHud(FZII)V"
            )
    )
    private void beforeDebugInfo(float par1, CallbackInfo ci) {
        if (OFConfig.of(this.minecraft.options).isOfFastDebugInfo()) {
            final Minecraft m = this.minecraft;
            if (Minecraft.isDebugHudEnabled()) {
                this.showDebugInfo = !this.showDebugInfo;
            }
            if (this.showDebugInfo) {
                this.minecraft.options.debugHud = true;
            }
        }
    }

    @Inject(
            method = "method_1844(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/InGame;renderHud(FZII)V",
                    shift = At.Shift.AFTER
            )
    )
    private void afterDebugInfo(float par1, CallbackInfo ci) {
        if (OFConfig.of(this.minecraft.options).isOfFastDebugInfo()) {
            this.minecraft.options.debugHud = false;
        }
    }

    @Redirect(
            method = "delta(FJ)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;viewDistance:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int redirectViewDistanceForFarView(GameOptions instance) {
        return Config.isFarView() ? 1 : instance.viewDistance;
    }

    @Inject(
            method = "delta(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderHelper;disableLighting()V",
                    shift = At.Shift.AFTER
            )
    )
    private void alphaFunc(float l, long par2, CallbackInfo ci) {
        if (Config.isUseAlphaFunc()) {
            GL11.glAlphaFunc(516, Config.getAlphaFuncLevel());
        }
    }

    @Inject(
            method = "delta(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void unpauseThread(float l, long par2, CallbackInfo ci) {
        final UpdateThread updateThread = Config.getUpdateThread();
        if (updateThread != null) {
            updateThread.unpause();
        }
    }

    @Redirect(
            method = "delta(FJ)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fancyGraphics:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean fancyWater(GameOptions instance) {
        return Config.isWaterFancy();
    }

    @Redirect(
            method = "delta(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1548(Lnet/minecraft/entity/Living;ID)I",
                    ordinal = 1
            )
    )
    private int redirectSortedRenderers1(WorldRenderer instance, Living i, int d, double v) {
        return OFWorldRenderer.of(instance).renderAllSortedRenderers(d, v);
    }

    @Redirect(
            method = "delta(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1540(ID)V"
            )
    )
    private void redirectSortedRenderers2(WorldRenderer instance, int d, double v) {
        OFWorldRenderer.of(instance).renderAllSortedRenderers(d, v);
    }

    @Inject(
            method = "delta(FJ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDepthMask(Z)V",
                    remap = false
            )
    )
    private void pauseThread(float l, long par2, CallbackInfo ci) {
        final UpdateThread updateThread = Config.getUpdateThread();
        if (updateThread != null) {
            updateThread.pause();
        }
    }

    @Redirect(
            method = "method_1846()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fancyGraphics:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean fancyRain(GameOptions instance) {
        return Config.isRainFancy();
    }

    @Inject(
            method = "method_1847(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;viewEntity:Lnet/minecraft/entity/Living;",
                    opcode = Opcodes.GETFIELD
            ),
            cancellable = true
    )
    private void cancelRainIfDisabled(float par1, CallbackInfo ci) {
        if (Config.isRainOff())
            ci.cancel();
    }

    @ModifyConstant(
            method = "method_1842(IF)V",
            constant = @Constant(
                    floatValue = 0.1F,
                    ordinal = 1
            )
    )
    private float changeWaterDensity(float constant) {
        return Config.isClearWater() ? 0.02F : constant;
    }

    @Redirect(
            method = "method_1842(IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V",
                    ordinal = 3,
                    remap = false
            )
    )
    private void stopFog1(int pname, float param, int i, float f) {
        if (GLContext.getCapabilities().GL_NV_fog_distance) {
            if (Config.isFogFancy()) {
                GL11.glFogi(34138, 34139);
            }
            else {
                GL11.glFogi(34138, 34140);
            }
        }
        float fogStart = Config.getFogStart();
        float fogEnd = 1.0f;
        if (i < 0) {
            fogStart = 0.0f;
            fogEnd = 0.8f;
        }
        if (this.minecraft.level.dimension.blocksCompassAndClock) {
            fogStart = 0.0f;
            fogEnd = 1.0f;
        }
        GL11.glFogf(2915, this.field_2350 * fogStart);
        GL11.glFogf(2916, this.field_2350 * fogEnd);
    }

    @Redirect(
            method = "method_1842(IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V",
                    ordinal = 4,
                    remap = false
            )
    )
    private void stopFog2(int pname, float param) {}

    @Redirect(
            method = "method_1842(IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V",
                    ordinal = 5,
                    remap = false
            )
    )
    private void stopFog3(int pname, float param) {}

    @Redirect(
            method = "method_1842(IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V",
                    ordinal = 6,
                    remap = false
            )
    )
    private void stopFog4(int pname, float param) {}

    @Redirect(
            method = "method_1842(IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glFogi(II)V",
                    ordinal = 4,
                    remap = false
            )
    )
    private void stopFog5(int pname, int param) {}

    @Redirect(
            method = "method_1842(IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glFogf(IF)V",
                    ordinal = 7,
                    remap = false
            )
    )
    private void stopFog6(int pname, float param) {}
}
