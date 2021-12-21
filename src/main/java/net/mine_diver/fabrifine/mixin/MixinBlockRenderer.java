package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.config.Config;
import net.minecraft.block.BlockBase;
import net.minecraft.client.render.block.BlockRenderer;
import net.minecraft.level.BlockView;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderer.class)
public abstract class MixinBlockRenderer {

    @Shadow private float brightnessBottomNorthWest;

    @Shadow private float brightnessBottomNorth;

    @Shadow private float brightnessBottomWest;

    @Shadow private float brightnessBottom;

    @Shadow private float brightnessBottomSouthWest;

    @Shadow private float brightnessBottomSouth;

    @Shadow private float brightnessBottomEast;

    @Shadow private float brightnessBottomSouthEast;

    @Shadow private float brightnessBottomNorthEast;

    @Shadow private float brightnessTopNorthWest;

    @Shadow private float brightnessTopNorth;

    @Shadow private float brightnessTopWest;

    @Shadow private float brightnessTopSouthWest;

    @Shadow private float brightnessTopSouth;

    @Shadow private float brightnessTopEast;

    @Shadow private float brightnessTopSouthEast;

    @Shadow private float brightnessTopNorthEast;

    @Shadow private float brightnessTop;

    @Shadow private float brightnessNorthEast;

    @Shadow private float brightnessSouthEast;

    @Shadow private float brightnessEast;

    @Shadow private float brightnessNorthWest;

    @Shadow private float brightnessSouthWest;

    @Shadow private float brightnessWest;

    @Shadow private float brightnessNorth;

    @Shadow private float brightnessSouth;

    @Shadow public abstract boolean render(BlockBase block, int blockX, int blockY, int blockZ);

    @Shadow private BlockView blockView;

    @Shadow private float colourRed00;

    @Shadow private float colourRed01;

    @Shadow private float colurRed11;

    @Shadow private float colourRed10;

    @Shadow private float colourGreen00;

    @Shadow private float colourGreen01;

    @Shadow private float colourGreen11;

    @Shadow private float colourGreen10;

    @Shadow private float colourBlue00;

    @Shadow private float colourBlue01;

    @Shadow private float colourBlue11;

    @Shadow private float colourBlue10;

    @Inject(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;brightnessBottomNorthWest:F",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            )
    )
    private void fixAoBottom(BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getAmbientOcclusionLevel() > 0.0F) {
            this.brightnessBottomNorthWest = Config.fixAoLight(this.brightnessBottomNorthWest, this.brightnessBottom);
            this.brightnessBottomNorth = Config.fixAoLight(this.brightnessBottomNorth, this.brightnessBottom);
            this.brightnessBottomWest = Config.fixAoLight(this.brightnessBottomWest, this.brightnessBottom);
            this.brightnessBottomSouthWest = Config.fixAoLight(this.brightnessBottomSouthWest, this.brightnessBottom);
            this.brightnessBottomSouth = Config.fixAoLight(this.brightnessBottomSouth, this.brightnessBottom);
            this.brightnessBottomEast = Config.fixAoLight(this.brightnessBottomEast, this.brightnessBottom);
            this.brightnessBottomSouthEast = Config.fixAoLight(this.brightnessBottomSouthEast, this.brightnessBottom);
            this.brightnessBottomNorthEast = Config.fixAoLight(this.brightnessBottomNorthEast, this.brightnessBottom);
        }
    }

    @Inject(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;brightnessTopNorthWest:F",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            )
    )
    private void fixAoTop(BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getAmbientOcclusionLevel() > 0.0F) {
            this.brightnessTopNorthWest = Config.fixAoLight(this.brightnessTopNorthWest, this.brightnessTop);
            this.brightnessTopNorth = Config.fixAoLight(this.brightnessTopNorth, this.brightnessTop);
            this.brightnessTopWest = Config.fixAoLight(this.brightnessTopWest, this.brightnessTop);
            this.brightnessTopSouthWest = Config.fixAoLight(this.brightnessTopSouthWest, this.brightnessTop);
            this.brightnessTopSouth = Config.fixAoLight(this.brightnessTopSouth, this.brightnessTop);
            this.brightnessTopEast = Config.fixAoLight(this.brightnessTopEast, this.brightnessTop);
            this.brightnessTopSouthEast = Config.fixAoLight(this.brightnessTopSouthEast, this.brightnessTop);
            this.brightnessTopNorthEast = Config.fixAoLight(this.brightnessTopNorthEast, this.brightnessTop);
        }
    }

    @Inject(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;brightnessNorthEast:F",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 2,
                    shift = At.Shift.BEFORE
            )
    )
    private void fixAoEast(BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getAmbientOcclusionLevel() > 0.0F) {
            this.brightnessNorthEast = Config.fixAoLight(this.brightnessNorthEast, this.brightnessEast);
            this.brightnessTopNorthEast = Config.fixAoLight(this.brightnessTopNorthEast, this.brightnessEast);
            this.brightnessTopEast = Config.fixAoLight(this.brightnessTopEast, this.brightnessEast);
            this.brightnessSouthEast = Config.fixAoLight(this.brightnessSouthEast, this.brightnessEast);
            this.brightnessTopSouthEast = Config.fixAoLight(this.brightnessTopSouthEast, this.brightnessEast);
            this.brightnessBottomEast = Config.fixAoLight(this.brightnessBottomEast, this.brightnessEast);
            this.brightnessBottomSouthEast = Config.fixAoLight(this.brightnessBottomSouthEast, this.brightnessEast);
            this.brightnessBottomNorthEast = Config.fixAoLight(this.brightnessBottomNorthEast, this.brightnessEast);
        }
    }

    @Inject(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;brightnessNorthWest:F",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 2,
                    shift = At.Shift.BEFORE
            )
    )
    private void fixAoWest(BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getAmbientOcclusionLevel() > 0.0F) {
            this.brightnessNorthWest = Config.fixAoLight(this.brightnessNorthWest, this.brightnessWest);
            this.brightnessTopNorthWest = Config.fixAoLight(this.brightnessTopNorthWest, this.brightnessWest);
            this.brightnessTopWest = Config.fixAoLight(this.brightnessTopWest, this.brightnessWest);
            this.brightnessSouthWest = Config.fixAoLight(this.brightnessSouthWest, this.brightnessWest);
            this.brightnessTopSouthWest = Config.fixAoLight(this.brightnessTopSouthWest, this.brightnessWest);
            this.brightnessBottomWest = Config.fixAoLight(this.brightnessBottomWest, this.brightnessWest);
            this.brightnessBottomSouthWest = Config.fixAoLight(this.brightnessBottomSouthWest, this.brightnessWest);
            this.brightnessBottomNorthWest = Config.fixAoLight(this.brightnessBottomNorthWest, this.brightnessWest);
        }
    }

    @Inject(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;brightnessBottomNorth:F",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 4,
                    shift = At.Shift.BEFORE
            )
    )
    private void fixAoNorth(BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getAmbientOcclusionLevel() > 0.0F) {
            this.brightnessBottomNorth = Config.fixAoLight(this.brightnessBottomNorth, this.brightnessNorth);
            this.brightnessBottomNorthWest = Config.fixAoLight(this.brightnessBottomNorthWest, this.brightnessNorth);
            this.brightnessNorthWest = Config.fixAoLight(this.brightnessNorthWest, this.brightnessNorth);
            this.brightnessTopNorth = Config.fixAoLight(this.brightnessTopNorth, this.brightnessNorth);
            this.brightnessTopNorthWest = Config.fixAoLight(this.brightnessTopNorthWest, this.brightnessNorth);
            this.brightnessNorthEast = Config.fixAoLight(this.brightnessNorthEast, this.brightnessNorth);
            this.brightnessTopNorthEast = Config.fixAoLight(this.brightnessTopNorthEast, this.brightnessNorth);
            this.brightnessBottomNorthEast = Config.fixAoLight(this.brightnessBottomNorthEast, this.brightnessNorth);
        }
    }

    @Inject(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;brightnessBottomSouth:F",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 4,
                    shift = At.Shift.BEFORE
            )
    )
    private void fixAoSouth(BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue, CallbackInfoReturnable<Boolean> cir) {
        if (Config.getAmbientOcclusionLevel() > 0.0F) {
            this.brightnessBottomSouth = Config.fixAoLight(this.brightnessBottomSouth, this.brightnessSouth);
            this.brightnessBottomSouthWest = Config.fixAoLight(this.brightnessBottomSouthWest, this.brightnessSouth);
            this.brightnessSouthWest = Config.fixAoLight(this.brightnessSouthWest, this.brightnessSouth);
            this.brightnessTopSouth = Config.fixAoLight(this.brightnessTopSouth, this.brightnessSouth);
            this.brightnessTopSouthWest = Config.fixAoLight(this.brightnessTopSouthWest, this.brightnessSouth);
            this.brightnessSouthEast = Config.fixAoLight(this.brightnessSouthEast, this.brightnessSouth);
            this.brightnessTopSouthEast = Config.fixAoLight(this.brightnessTopSouthEast, this.brightnessSouth);
            this.brightnessBottomSouthEast = Config.fixAoLight(this.brightnessBottomSouthEast, this.brightnessSouth);
        }
    }

    @ModifyVariable(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            index = 19,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;renderEastFace(Lnet/minecraft/block/BlockBase;DDDI)V",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = -2
            )
    )
    private int betterGrassEast(int texture, BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue) {
        if (Config.isBetterGrass()) {
            if (texture == 3) {
                texture = Config.getSideGrassTexture(this.blockView, x, y, z, 2);
                if (texture == 0) {
                    this.colourRed00 *= colourMultiplierRed;
                    this.colourRed01 *= colourMultiplierRed;
                    this.colurRed11 *= colourMultiplierRed;
                    this.colourRed10 *= colourMultiplierRed;
                    this.colourGreen00 *= colourMultiplierGreen;
                    this.colourGreen01 *= colourMultiplierGreen;
                    this.colourGreen11 *= colourMultiplierGreen;
                    this.colourGreen10 *= colourMultiplierGreen;
                    this.colourBlue00 *= colourMultiplierBlue;
                    this.colourBlue01 *= colourMultiplierBlue;
                    this.colourBlue11 *= colourMultiplierBlue;
                    this.colourBlue10 *= colourMultiplierBlue;
                }
            }
            if (texture == 68) {
                texture = Config.getSideSnowGrassTexture(this.blockView, x, y, z, 2);
            }
        }
        return texture;
    }

    @ModifyVariable(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            index = 19,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;renderWestFace(Lnet/minecraft/block/BlockBase;DDDI)V",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = -2
            )
    )
    private int betterGrassWest(int texture, BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue) {
        if (Config.isBetterGrass()) {
            if (texture == 3) {
                texture = Config.getSideGrassTexture(this.blockView, x, y, z, 3);
                if (texture == 0) {
                    this.colourRed00 *= colourMultiplierRed;
                    this.colourRed01 *= colourMultiplierRed;
                    this.colurRed11 *= colourMultiplierRed;
                    this.colourRed10 *= colourMultiplierRed;
                    this.colourGreen00 *= colourMultiplierGreen;
                    this.colourGreen01 *= colourMultiplierGreen;
                    this.colourGreen11 *= colourMultiplierGreen;
                    this.colourGreen10 *= colourMultiplierGreen;
                    this.colourBlue00 *= colourMultiplierBlue;
                    this.colourBlue01 *= colourMultiplierBlue;
                    this.colourBlue11 *= colourMultiplierBlue;
                    this.colourBlue10 *= colourMultiplierBlue;
                }
            }
            if (texture == 68) {
                texture = Config.getSideSnowGrassTexture(this.blockView, x, y, z, 3);
            }
        }
        return notchCodeFixer = texture;
    }

    @Unique
    private int notchCodeFixer;

    @Redirect(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockBase;getTextureForSide(Lnet/minecraft/level/BlockView;IIII)I",
                    ordinal = 4
            )
    )
    private int redirectNotchCode(BlockBase blockBase, BlockView tileView, int x, int y, int z, int meta) {
        return notchCodeFixer;
    }

    @ModifyVariable(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            index = 19,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;renderNorthFace(Lnet/minecraft/block/BlockBase;DDDI)V",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = -2
            )
    )
    private int betterGrassNorth(int texture, BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue) {
        if (Config.isBetterGrass()) {
            if (texture == 3) {
                texture = Config.getSideGrassTexture(this.blockView, x, y, z, 4);
                if (texture == 0) {
                    this.colourRed00 *= colourMultiplierRed;
                    this.colourRed01 *= colourMultiplierRed;
                    this.colurRed11 *= colourMultiplierRed;
                    this.colourRed10 *= colourMultiplierRed;
                    this.colourGreen00 *= colourMultiplierGreen;
                    this.colourGreen01 *= colourMultiplierGreen;
                    this.colourGreen11 *= colourMultiplierGreen;
                    this.colourGreen10 *= colourMultiplierGreen;
                    this.colourBlue00 *= colourMultiplierBlue;
                    this.colourBlue01 *= colourMultiplierBlue;
                    this.colourBlue11 *= colourMultiplierBlue;
                    this.colourBlue10 *= colourMultiplierBlue;
                }
            }
            if (texture == 68) {
                texture = Config.getSideSnowGrassTexture(this.blockView, x, y, z, 4);
            }
        }
        return texture;
    }

    @ModifyVariable(
            method = "renderSmooth(Lnet/minecraft/block/BlockBase;IIIFFF)Z",
            index = 19,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;renderSouthFace(Lnet/minecraft/block/BlockBase;DDDI)V",
                    ordinal = 0,
                    shift = At.Shift.BY,
                    by = -2
            )
    )
    private int betterGrassSouth(int texture, BlockBase block, int x, int y, int z, float colourMultiplierRed, float colourMultiplierGreen, float colourMultiplierBlue) {
        if (Config.isBetterGrass()) {
            if (texture == 3) {
                texture = Config.getSideGrassTexture(this.blockView, x, y, z, 5);
                if (texture == 0) {
                    this.colourRed00 *= colourMultiplierRed;
                    this.colourRed01 *= colourMultiplierRed;
                    this.colurRed11 *= colourMultiplierRed;
                    this.colourRed10 *= colourMultiplierRed;
                    this.colourGreen00 *= colourMultiplierGreen;
                    this.colourGreen01 *= colourMultiplierGreen;
                    this.colourGreen11 *= colourMultiplierGreen;
                    this.colourGreen10 *= colourMultiplierGreen;
                    this.colourBlue00 *= colourMultiplierBlue;
                    this.colourBlue01 *= colourMultiplierBlue;
                    this.colourBlue11 *= colourMultiplierBlue;
                    this.colourBlue10 *= colourMultiplierBlue;
                }
            }
            if (texture == 68) {
                texture = Config.getSideSnowGrassTexture(this.blockView, x, y, z, 5);
            }
        }
        return texture;
    }
}
