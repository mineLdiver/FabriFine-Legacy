package net.mine_diver.fabrifine.mixin;

import lombok.Getter;
import lombok.Setter;
import net.mine_diver.fabrifine.config.Config;
import net.mine_diver.fabrifine.config.OFConfig;
import net.mine_diver.fabrifine.config.OptionsListener;
import net.minecraft.block.BlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.level.chunk.Chunk;
import net.minecraft.level.source.LevelSource;
import org.lwjgl.opengl.Display;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.*;
import java.util.*;

@Getter
@Setter
@Mixin(GameOptions.class)
public class MixinGameOptions implements OFConfig {

    @Shadow public boolean ao;
    @Shadow protected Minecraft minecraft;
    @Shadow public boolean advancedOpengl;
    @Shadow public int fpsLimit;
    @Unique
    private boolean
            ofFogFancy;
    @Unique
    private float
            ofFogStart;
    @Unique
    private int
            ofMipmapLevel;
    @Unique
    private boolean
            ofMipmapLinear,
            ofLoadFar;
    @Unique
    private int
            ofPreloadedChunks;
    @Unique
    private boolean
            ofOcclusionFancy,
            ofSmoothFps,
            ofSmoothInput;
    @Unique
    private float
            ofBrightness,
            ofAoLevel;
    @Unique
    private int
            ofClouds;
    @Unique
    private float
            ofCloudsHeight;
    @Unique
    private int
            ofTrees,
            ofGrass,
            ofRain,
            ofWater,
            ofBetterGrass,
            ofAutoSaveTicks;
    @Unique
    private boolean
            ofFastDebugInfo,
            ofWeather,
            ofSky,
            ofStars;
    @Unique
    private int
            ofChunkUpdates;
    @Unique
    private boolean
            ofChunkUpdatesDynamic,
            ofFarView;
    @Unique
    private int
            ofTime;
    @Unique
    private boolean
            ofClearWater;
    @Unique
    private int
            ofAnimatedWater,
            ofAnimatedLava;
    @Unique
    private boolean
            ofAnimatedFire,
            ofAnimatedPortal,
            ofAnimatedRedstone,
            ofAnimatedExplosion,
            ofAnimatedFlame,
            ofAnimatedSmoke;
    @Unique
    private KeyBinding
            ofKeyBindZoom;

    @Inject(
            method = "load()V",
            at = @At("HEAD")
    )
    private void onLoad(CallbackInfo ci) {
        ofFogFancy = false;
        ofFogStart = 0.8f;
        ofMipmapLevel = 0;
        ofMipmapLinear = false;
        ofLoadFar = false;
        ofPreloadedChunks = 0;
        ofOcclusionFancy = false;
        ofSmoothFps = false;
        ofSmoothInput = false;
        ofBrightness = 0.0f;
        ofAoLevel = 0.0f;
        ofClouds = 0;
        ofCloudsHeight = 0.0f;
        ofTrees = 0;
        ofGrass = 0;
        ofRain = 0;
        ofWater = 0;
        ofBetterGrass = 3;
        ofAutoSaveTicks = 4000;
        ofFastDebugInfo = false;
        ofWeather = true;
        ofSky = true;
        ofStars = true;
        ofChunkUpdates = 1;
        ofChunkUpdatesDynamic = true;
        ofFarView = false;
        ofTime = 0;
        ofClearWater = false;
        ofAnimatedWater = 0;
        ofAnimatedLava = 0;
        ofAnimatedFire = true;
        ofAnimatedPortal = true;
        ofAnimatedRedstone = true;
        ofAnimatedExplosion = true;
        ofAnimatedFlame = true;
        ofAnimatedSmoke = true;
    }

    @Inject(
            method = "method_1228(Lnet/minecraft/client/options/Option;F)V",
            at = @At("RETURN")
    )
    private void setFloatProperty(Option arg, float f, CallbackInfo ci) {
        if (arg == OptionsListener.getBRIGHTNESS()) {
            this.ofBrightness = f;
            this.updateWorldLightLevels();
        }
        if (arg == OptionsListener.getCLOUD_HEIGHT()) this.ofCloudsHeight = f;
        if (arg == OptionsListener.getAO_LEVEL()) {
            this.ofAoLevel = f;
            this.ao = (this.ofAoLevel > 0.0f);
            this.minecraft.worldRenderer.method_1537();
        }
    }

    @Unique
    private void updateWorldLightLevels() {
//        if (this.minecraft.gameRenderer != null) this.minecraft.gameRenderer.updateWorldLightLevels();
        if (this.minecraft.worldRenderer != null) this.minecraft.worldRenderer.method_1537();
    }

    @Unique
    private void updateWaterOpacity() {
        int opacity = 3;
        if (this.ofClearWater) opacity = 1;
        ((BlockBaseAccessor) BlockBase.STILL_WATER).invokeSetLightOpacity(opacity);
        ((BlockBaseAccessor) BlockBase.FLOWING_WATER).invokeSetLightOpacity(opacity);
        if (this.minecraft.level == null) return;
        final LevelSource cp = this.minecraft.level.getCache();
        if (cp == null) return;
        for (int x = -512; x < 512; ++x)
            for (int z = -512; z < 512; ++z)
                if (cp.isChunkLoaded(x, z)) {
                    final Chunk c = cp.getChunk(x, z);
                    if (c != null) {
                        final byte[] data = c.field_958.field_2103;
                        Arrays.fill(data, (byte) 0);
                        c.generateHeightmap();
                    }
                }
        this.minecraft.worldRenderer.method_1537();
    }

    @Redirect(
            method = "changeOption(Lnet/minecraft/client/options/Option;I)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;advancedOpengl:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void redirectAo(GameOptions gameOptions, boolean value) {
        if (!Config.isOcclusionAvailable()) {
            this.ofOcclusionFancy = false;
            this.advancedOpengl = false;
        }
        else if (!this.advancedOpengl) {
            this.advancedOpengl = true;
            this.ofOcclusionFancy = false;
        }
        else if (!this.ofOcclusionFancy) this.ofOcclusionFancy = true;
        else {
            this.ofOcclusionFancy = false;
            this.advancedOpengl = false;
        }
    }

    @Redirect(
            method = "changeOption(Lnet/minecraft/client/options/Option;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1537()V",
                    ordinal = 0
            )
    )
    private void redirectRenderRefresh(WorldRenderer worldRenderer) {
//        worldRenderer.setAllRenderesVisible();
    }

    @Redirect(
            method = "changeOption(Lnet/minecraft/client/options/Option;I)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fpsLimit:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void redirectFramerateLimit(GameOptions gameOptions, int value) {
        this.fpsLimit = (this.fpsLimit + value) % 4;
        Display.setVSyncEnabled(fpsLimit == 3);
    }

    @Inject(
            method = "changeOption(Lnet/minecraft/client/options/Option;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/options/GameOptions;saveOptions()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void changeOther(Option option, int i, CallbackInfo ci) {
        if (option == OptionsListener.getFOG_FANCY()) if (!Config.isFancyFogAvailable()) this.ofFogFancy = false;
        else this.ofFogFancy = !this.ofFogFancy;
        if (option == OptionsListener.getFOG_START()) {
            this.ofFogStart += 0.2f;
            if (this.ofFogStart > 0.81f) this.ofFogStart = 0.2f;
        }
        if (option == OptionsListener.getMIPMAP_LEVEL()) {
            ++this.ofMipmapLevel;
            if (this.ofMipmapLevel > 4) this.ofMipmapLevel = 0;
            this.minecraft.textureManager.reloadTexturesFromTexturePack();
        }
        if (option == OptionsListener.getMIPMAP_TYPE()) {
            this.ofMipmapLinear = !this.ofMipmapLinear;
            this.minecraft.textureManager.reloadTexturesFromTexturePack();
        }
        if (option == OptionsListener.getLOAD_FAR()) {
            this.ofLoadFar = !this.ofLoadFar;
            this.minecraft.worldRenderer.method_1537();
        }
        if (option == OptionsListener.getPRELOADED_CHUNKS()) {
            this.ofPreloadedChunks += 2;
            if (this.ofPreloadedChunks > 8) this.ofPreloadedChunks = 0;
            this.minecraft.worldRenderer.method_1537();
        }
        if (option == OptionsListener.getSMOOTH_FPS()) this.ofSmoothFps = !this.ofSmoothFps;
        if (option == OptionsListener.getSMOOTH_INPUT()) this.ofSmoothInput = !this.ofSmoothInput;
        if (option == OptionsListener.getCLOUDS()) {
            ++this.ofClouds;
            if (this.ofClouds > 3) this.ofClouds = 0;
        }
        if (option == OptionsListener.getTREES()) {
            ++this.ofTrees;
            if (this.ofTrees > 2) this.ofTrees = 0;
            this.minecraft.worldRenderer.method_1537();
        }
        if (option == OptionsListener.getGRASS()) {
            ++this.ofGrass;
            if (this.ofGrass > 2) this.ofGrass = 0;
            BlockRenderer.fancyGraphics = Config.isGrassFancy();
            this.minecraft.worldRenderer.method_1537();
        }
        if (option == OptionsListener.getRAIN()) {
            ++this.ofRain;
            if (this.ofRain > 3) this.ofRain = 0;
        }
        if (option == OptionsListener.getWATER()) {
            ++this.ofWater;
            if (this.ofWater > 2) this.ofWater = 0;
        }
        if (option == OptionsListener.getANIMATED_WATER()) {
            ++this.ofAnimatedWater;
            if (this.ofAnimatedWater > 2) this.ofAnimatedWater = 0;
            this.minecraft.textureManager.reloadTexturesFromTexturePack();
        }
        if (option == OptionsListener.getANIMATED_LAVA()) {
            ++this.ofAnimatedLava;
            if (this.ofAnimatedLava > 2) this.ofAnimatedLava = 0;
            this.minecraft.textureManager.reloadTexturesFromTexturePack();
        }
        if (option == OptionsListener.getANIMATED_FIRE()) {
            this.ofAnimatedFire = !this.ofAnimatedFire;
            this.minecraft.textureManager.reloadTexturesFromTexturePack();
        }
        if (option == OptionsListener.getANIMATED_PORTAL()) {
            this.ofAnimatedPortal = !this.ofAnimatedPortal;
            this.minecraft.textureManager.reloadTexturesFromTexturePack();
        }
        if (option == OptionsListener.getANIMATED_REDSTONE()) this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
        if (option == OptionsListener.getANIMATED_EXPLOSION()) this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
        if (option == OptionsListener.getANIMATED_FLAME()) this.ofAnimatedFlame = !this.ofAnimatedFlame;
        if (option == OptionsListener.getANIMATED_SMOKE()) this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
        if (option == OptionsListener.getFAST_DEBUG_INFO()) this.ofFastDebugInfo = !this.ofFastDebugInfo;
        if (option == OptionsListener.getAUTOSAVE_TICKS()) {
            this.ofAutoSaveTicks *= 10;
            if (this.ofAutoSaveTicks > 40000) this.ofAutoSaveTicks = 40;
        }
        if (option == OptionsListener.getBETTER_GRASS()) {
            ++this.ofBetterGrass;
            if (this.ofBetterGrass > 3) this.ofBetterGrass = 1;
            this.minecraft.worldRenderer.method_1537();
        }
        if (option == OptionsListener.getWEATHER()) this.ofWeather = !this.ofWeather;
        if (option == OptionsListener.getSKY()) this.ofSky = !this.ofSky;
        if (option == OptionsListener.getSTARS()) this.ofStars = !this.ofStars;
        if (option == OptionsListener.getCHUNK_UPDATES()) {
            ++this.ofChunkUpdates;
            if (this.ofChunkUpdates > 5) this.ofChunkUpdates = 1;
        }
        if (option == OptionsListener.getCHUNK_UPDATES_DYNAMIC())
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        if (option == OptionsListener.getFAR_VIEW()) {
            this.ofFarView = !this.ofFarView;
            this.minecraft.worldRenderer.method_1537();
        }
        if (option == OptionsListener.getTIME()) {
            ++this.ofTime;
            if (this.ofTime > 2) this.ofTime = 0;
        }
        if (option == OptionsListener.getCLEAR_WATER()) {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }
    }

    @Inject(
            method = "getFloatValue(Lnet/minecraft/client/options/Option;)F",
            at = @At("TAIL"),
            cancellable = true
    )
    private void getFloatProperty(Option arg, CallbackInfoReturnable<Float> cir) {
        if (arg == OptionsListener.getBRIGHTNESS()) {
            cir.setReturnValue(ofBrightness);
            return;
        }
        if (arg == OptionsListener.getCLOUD_HEIGHT()) {
            cir.setReturnValue(ofCloudsHeight);
            return;
        }
        if (arg == OptionsListener.getAO_LEVEL()) cir.setReturnValue(ofAoLevel);
    }

    @Inject(
            method = "getTranslatedValue(Lnet/minecraft/client/options/Option;)Ljava/lang/String;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/options/Option;isToggle()Z",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void translateAo(Option arg, CallbackInfoReturnable<String> cir, TranslationStorage var2, String s) {
        if (arg == Option.ADVANCED_OPENGL) {
            if (!this.advancedOpengl) {
                cir.setReturnValue(s + "OFF");
                return;
            }
            if (this.ofOcclusionFancy) {
                cir.setReturnValue(s + "Fancy");
                return;
            }
            cir.setReturnValue(s + "Fast");
        }
    }

    @Inject(
            method = "getTranslatedValue(Lnet/minecraft/client/options/Option;)Ljava/lang/String;",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;performanceTranslationKeys:[Ljava/lang/String;",
                    opcode = Opcodes.GETSTATIC,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void translateFpsLimit(Option arg, CallbackInfoReturnable<String> cir, TranslationStorage var2, String s) {
        if (this.fpsLimit == 3)
            cir.setReturnValue(s + "VSync");
    }

    @Inject(
            method = "getTranslatedValue(Lnet/minecraft/client/options/Option;)Ljava/lang/String;",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/Option;GRAPHICS:Lnet/minecraft/client/options/Option;",
                    opcode = Opcodes.GETSTATIC
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void translateOther(Option arg, CallbackInfoReturnable<String> cir, TranslationStorage var2, String s) {
        if (arg == OptionsListener.getFOG_FANCY()) if (this.ofFogFancy) cir.setReturnValue(s + "Fancy");
        else cir.setReturnValue(s + "Fast");
        else if (arg == OptionsListener.getFOG_START()) cir.setReturnValue(s + this.ofFogStart);
        else if (arg == OptionsListener.getMIPMAP_LEVEL()) cir.setReturnValue(s + this.ofMipmapLevel);
        else if (arg == OptionsListener.getMIPMAP_TYPE()) if (this.ofMipmapLinear) cir.setReturnValue(s + "Linear");
        else cir.setReturnValue(s + "Nearest");
        else if (arg == OptionsListener.getLOAD_FAR()) if (this.ofLoadFar) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getPRELOADED_CHUNKS())
            if (this.ofPreloadedChunks == 0) cir.setReturnValue(s + "OFF");
            else cir.setReturnValue(s + this.ofPreloadedChunks);
        else if (arg == OptionsListener.getSMOOTH_FPS()) if (this.ofSmoothFps) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getSMOOTH_INPUT()) if (this.ofSmoothInput) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getCLOUDS()) switch (this.ofClouds) {
            case 1:
                cir.setReturnValue(s + "Fast");
                break;
            case 2:
                cir.setReturnValue(s + "Fancy");
                break;
            case 3:
                cir.setReturnValue(s + "OFF");
                break;
            default:
                cir.setReturnValue(s + "Default");
                break;
        }
        else if (arg == OptionsListener.getTREES()) switch (this.ofTrees) {
            case 1:
                cir.setReturnValue(s + "Fast");
                break;
            case 2:
                cir.setReturnValue(s + "Fancy");
                break;
            default:
                cir.setReturnValue(s + "Default");
                break;
        }
        else if (arg == OptionsListener.getGRASS()) switch (this.ofGrass) {
            case 1:
                cir.setReturnValue(s + "Fast");
                break;
            case 2:
                cir.setReturnValue(s + "Fancy");
                break;
            default:
                cir.setReturnValue(s + "Default");
                break;
        }
        else if (arg == OptionsListener.getRAIN()) switch (this.ofRain) {
            case 1:
                cir.setReturnValue(s + "Fast");
                break;
            case 2:
                cir.setReturnValue(s + "Fancy");
                break;
            case 3:
                cir.setReturnValue(s + "OFF");
                break;
            default:
                cir.setReturnValue(s + "Default");
                break;
        }
        else if (arg == OptionsListener.getWATER()) switch (this.ofWater) {
            case 1:
                cir.setReturnValue(s + "Fast");
                break;
            case 2:
                cir.setReturnValue(s + "Fancy");
                break;
            case 3:
                cir.setReturnValue(s + "OFF");
                break;
            default:
                cir.setReturnValue(s + "Default");
                break;
        }
        else if (arg == OptionsListener.getANIMATED_WATER()) switch (this.ofAnimatedWater) {
            case 1:
                cir.setReturnValue(s + "Dynamic");
                break;
            case 2:
                cir.setReturnValue(s + "OFF");
                break;
            default:
                cir.setReturnValue(s + "ON");
                break;
        }
        else if (arg == OptionsListener.getANIMATED_LAVA()) switch (this.ofAnimatedLava) {
            case 1:
                cir.setReturnValue(s + "Dynamic");
                break;
            case 2:
                cir.setReturnValue(s + "OFF");
                break;
            default:
                cir.setReturnValue(s + "ON");
                break;
        }
        else if (arg == OptionsListener.getANIMATED_FIRE()) if (this.ofAnimatedFire) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getANIMATED_PORTAL()) if (this.ofAnimatedPortal) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getANIMATED_REDSTONE())
            if (this.ofAnimatedRedstone) cir.setReturnValue(s + "ON");
            else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getANIMATED_EXPLOSION())
            if (this.ofAnimatedExplosion) cir.setReturnValue(s + "ON");
            else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getANIMATED_FLAME()) if (this.ofAnimatedFlame) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getANIMATED_SMOKE()) if (this.ofAnimatedSmoke) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getFAST_DEBUG_INFO()) if (this.ofFastDebugInfo) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getAUTOSAVE_TICKS())
            if (this.ofAutoSaveTicks <= 40) cir.setReturnValue(s + "Default (2s)");
            else if (this.ofAutoSaveTicks <= 400) cir.setReturnValue(s + "20s");
            else if (this.ofAutoSaveTicks <= 4000) cir.setReturnValue(s + "3min");
            else cir.setReturnValue(s + "30min");
        else if (arg == OptionsListener.getBETTER_GRASS()) switch (this.ofBetterGrass) {
            case 1:
                cir.setReturnValue(s + "Fast");
                break;
            case 2:
                cir.setReturnValue(s + "Fancy");
                break;
            default:
                cir.setReturnValue(s + "OFF");
                break;
        }
        else if (arg == OptionsListener.getWEATHER()) if (this.ofWeather) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getSKY()) if (this.ofSky) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getSTARS()) if (this.ofStars) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getCHUNK_UPDATES()) cir.setReturnValue(s + this.ofChunkUpdates);
        else if (arg == OptionsListener.getCHUNK_UPDATES_DYNAMIC())
            if (this.ofChunkUpdatesDynamic) cir.setReturnValue(s + "ON");
            else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getFAR_VIEW()) if (this.ofFarView) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
        else if (arg == OptionsListener.getTIME()) if (this.ofTime == 1) cir.setReturnValue(s + "Day Only");
        else if (this.ofTime == 2) cir.setReturnValue(s + "Night Only");
        else cir.setReturnValue(s + "Default");
        else if (arg == OptionsListener.getCLEAR_WATER()) if (this.ofClearWater) cir.setReturnValue(s + "ON");
        else cir.setReturnValue(s + "OFF");
    }

    @Inject(
            method = "load()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;ao:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void setAoLevel(CallbackInfo ci) {
        if (this.ao) this.ofAoLevel = 1.0f;
        else this.ofAoLevel = 0.0f;
    }

    @Inject(
            method = "load()V",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=0",
                    shift = At.Shift.BEFORE,
                    ordinal = 15
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void loadConfig(CallbackInfo ci, BufferedReader var1, String var2, String[] as) {
        if (as[0].equals("ofFogFancy") && as.length >= 2) this.ofFogFancy = as[1].equals("true");
        if (as[0].equals("ofFogStart") && as.length >= 2) {
            this.ofFogStart = Float.parseFloat(as[1]);
            if (this.ofFogStart < 0.2f) this.ofFogStart = 0.2f;
            if (this.ofFogStart > 0.81f) this.ofFogStart = 0.8f;
        }
        if (as[0].equals("ofMipmapLevel") && as.length >= 2) {
            this.ofMipmapLevel = Integer.parseInt(as[1]);
            if (this.ofMipmapLevel < 0) this.ofMipmapLevel = 0;
            if (this.ofMipmapLevel > 4) this.ofMipmapLevel = 4;
        }
        if (as[0].equals("ofMipmapLinear") && as.length >= 2) this.ofMipmapLinear = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofLoadFar") && as.length >= 2) this.ofLoadFar = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofPreloadedChunks") && as.length >= 2) {
            this.ofPreloadedChunks = Integer.parseInt(as[1]);
            if (this.ofPreloadedChunks < 0) this.ofPreloadedChunks = 0;
            if (this.ofPreloadedChunks > 8) this.ofPreloadedChunks = 8;
        }
        if (as[0].equals("ofOcclusionFancy") && as.length >= 2) this.ofOcclusionFancy = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofSmoothFps") && as.length >= 2) this.ofSmoothFps = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofSmoothInput") && as.length >= 2) this.ofSmoothInput = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofBrightness") && as.length >= 2) {
            this.ofBrightness = Float.parseFloat(as[1]);
            this.ofBrightness = Config.limit(this.ofBrightness, 0.0f, 1.0f);
            this.updateWorldLightLevels();
        }
        if (as[0].equals("ofAoLevel") && as.length >= 2) {
            this.ofAoLevel = Float.parseFloat(as[1]);
            this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0f, 1.0f);
            this.ao = (this.ofAoLevel > 0.0f);
        }
        if (as[0].equals("ofClouds") && as.length >= 2) {
            this.ofClouds = Integer.parseInt(as[1]);
            this.ofClouds = Config.limit(this.ofClouds, 0, 3);
        }
        if (as[0].equals("ofCloudsHeight") && as.length >= 2) {
            this.ofCloudsHeight = Float.parseFloat(as[1]);
            this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0f, 1.0f);
        }
        if (as[0].equals("ofTrees") && as.length >= 2) {
            this.ofTrees = Integer.parseInt(as[1]);
            this.ofTrees = Config.limit(this.ofTrees, 0, 2);
        }
        if (as[0].equals("ofGrass") && as.length >= 2) {
            this.ofGrass = Integer.parseInt(as[1]);
            this.ofGrass = Config.limit(this.ofGrass, 0, 2);
        }
        if (as[0].equals("ofRain") && as.length >= 2) {
            this.ofRain = Integer.parseInt(as[1]);
            this.ofRain = Config.limit(this.ofRain, 0, 3);
        }
        if (as[0].equals("ofWater") && as.length >= 2) {
            this.ofWater = Integer.parseInt(as[1]);
            this.ofWater = Config.limit(this.ofWater, 0, 3);
        }
        if (as[0].equals("ofAnimatedWater") && as.length >= 2) {
            this.ofAnimatedWater = Integer.parseInt(as[1]);
            this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
        }
        if (as[0].equals("ofAnimatedLava") && as.length >= 2) {
            this.ofAnimatedLava = Integer.parseInt(as[1]);
            this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
        }
        if (as[0].equals("ofAnimatedFire") && as.length >= 2) this.ofAnimatedFire = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofAnimatedPortal") && as.length >= 2) this.ofAnimatedPortal = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofAnimatedRedstone") && as.length >= 2) this.ofAnimatedRedstone = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofAnimatedExplosion") && as.length >= 2)
            this.ofAnimatedExplosion = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofAnimatedFlame") && as.length >= 2) this.ofAnimatedFlame = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofAnimatedSmoke") && as.length >= 2) this.ofAnimatedSmoke = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofFastDebugInfo") && as.length >= 2) this.ofFastDebugInfo = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofAutoSaveTicks") && as.length >= 2) {
            this.ofAutoSaveTicks = Integer.parseInt(as[1]);
            this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
        }
        if (as[0].equals("ofBetterGrass") && as.length >= 2) {
            this.ofBetterGrass = Integer.parseInt(as[1]);
            this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
        }
        if (as[0].equals("ofWeather") && as.length >= 2) this.ofWeather = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofSky") && as.length >= 2) this.ofSky = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofStars") && as.length >= 2) this.ofStars = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofChunkUpdates") && as.length >= 2) {
            this.ofChunkUpdates = Integer.parseInt(as[1]);
            this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
        }
        if (as[0].equals("ofChunkUpdatesDynamic") && as.length >= 2)
            this.ofChunkUpdatesDynamic = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofFarView") && as.length >= 2) this.ofFarView = Boolean.parseBoolean(as[1]);
        if (as[0].equals("ofTime") && as.length >= 2) {
            this.ofTime = Integer.parseInt(as[1]);
            this.ofTime = Config.limit(this.ofTime, 0, 2);
        }
        if (!as[0].equals("ofClearWater") || as.length < 2) return;
        this.ofClearWater = Boolean.parseBoolean(as[1]);
        this.updateWaterOpacity();
    }

    @Inject(
            method = "saveOptions()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/io/PrintWriter;close()V",
                    remap = false
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void saveConfig(CallbackInfo ci, PrintWriter writer) {
        writer.println("ofFogFancy:" + ofFogFancy);
        writer.println("ofFogStart:" + ofFogStart);
        writer.println("ofMipmapLevel:" + ofMipmapLevel);
        writer.println("ofMipmapLinear:" + ofMipmapLinear);
        writer.println("ofLoadFar:" + ofLoadFar);
        writer.println("ofPreloadedChunks:" + ofPreloadedChunks);
        writer.println("ofOcclusionFancy:" + ofOcclusionFancy);
        writer.println("ofSmoothFps:" + ofSmoothFps);
        writer.println("ofSmoothInput:" + ofSmoothInput);
        writer.println("ofBrightness:" + ofBrightness);
        writer.println("ofAoLevel:" + ofAoLevel);
        writer.println("ofClouds:" + ofClouds);
        writer.println("ofCloudsHeight:" + ofCloudsHeight);
        writer.println("ofTrees:" + ofTrees);
        writer.println("ofGrass:" + ofGrass);
        writer.println("ofRain:" + ofRain);
        writer.println("ofWater:" + ofWater);
        writer.println("ofAnimatedWater:" + ofAnimatedWater);
        writer.println("ofAnimatedLava:" + ofAnimatedLava);
        writer.println("ofAnimatedFire:" + ofAnimatedFire);
        writer.println("ofAnimatedPortal:" + ofAnimatedPortal);
        writer.println("ofAnimatedRedstone:" + ofAnimatedRedstone);
        writer.println("ofAnimatedExplosion:" + ofAnimatedExplosion);
        writer.println("ofAnimatedFlame:" + ofAnimatedFlame);
        writer.println("ofAnimatedSmoke:" + ofAnimatedSmoke);
        writer.println("ofFastDebugInfo:" + ofFastDebugInfo);
        writer.println("ofAutoSaveTicks:" + ofAutoSaveTicks);
        writer.println("ofBetterGrass:" + ofBetterGrass);
        writer.println("ofWeather:" + ofWeather);
        writer.println("ofSky:" + ofSky);
        writer.println("ofStars:" + ofStars);
        writer.println("ofChunkUpdates:" + ofChunkUpdates);
        writer.println("ofChunkUpdatesDynamic:" + ofChunkUpdatesDynamic);
        writer.println("ofFarView:" + ofFarView);
        writer.println("ofTime:" + ofTime);
        writer.println("ofClearWater:" + ofClearWater);
    }
}
