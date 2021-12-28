package net.mine_diver.fabrifine.mixin;

import lombok.Setter;
import net.mine_diver.fabrifine.render.OFTessellator;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.*;

import static net.mine_diver.fabrifine.render.OFTessellatorFields.chunkOffsetX;
import static net.mine_diver.fabrifine.render.OFTessellatorFields.chunkOffsetZ;

@Mixin(Tessellator.class)
public abstract class MixinTessellator implements OFTessellator {

    @Shadow private int vertexCount;
    @Shadow private double xOffset;
    @Shadow private double yOffset;
    @Shadow private double zOffset;
    @Shadow private IntBuffer intBuffer;
    @Shadow private int vertexAmount;
    @Shadow private int drawingMode;
    @Shadow private static boolean useTriangles;
    @Shadow private int field_2068;
    @Shadow private int colour;
    @Shadow private double textureX;
    @Shadow private double textureY;

    @Shadow public abstract void draw();

    @Shadow private boolean drawing;
    @Shadow private int bufferSize;
    @Unique
    @Setter
    private boolean renderingChunk;

    @Redirect(
            method = "draw()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;vertexCount:I",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0
            )
    )
    private int flushRenderIfNotInChunk(Tessellator instance) {
        if (renderingChunk) {
            return vertexCount;
        } else {
            GL11.glEnd();
            checkOpenGlError();
            return 0;
        }
    }

    @Redirect(
            method = "draw()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/nio/IntBuffer;clear()Ljava/nio/Buffer;",
                    remap = false
            )
    )
    private Buffer disableBufferClear(IntBuffer instance) {
        return instance;
    }

    @Redirect(
            method = "draw()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/nio/IntBuffer;put([III)Ljava/nio/IntBuffer;",
                    remap = false
            )
    )
    private IntBuffer disableBufferPut(IntBuffer instance, int[] ints, int i, int j) {
        return instance;
    }

    @Inject(
            method = "draw()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;useFloatBuffer:Z",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0
            )
    )
    private void enableStates(CallbackInfo ci) {
        GL11.glEnableClientState(32888 /*GL_TEXTURE_COORD_ARRAY_EXT*/);
        GL11.glEnableClientState(32886 /*GL_COLOR_ARRAY_EXT*/);
        GL11.glEnableClientState(32884 /*GL_VERTEX_ARRAY_EXT*/);
    }

    @ModifyConstant(
            method = "draw()V",
            constant = @Constant(intValue = 34962)
    )
    private int changeTarget(int constant) {
        return constant + 82;
    }

    @Redirect(
            method = "draw()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnableClientState(I)V",
                    remap = false
            )
    )
    private void stopEnabling(int cap) {}

    @Redirect(
            method = "draw()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisableClientState(I)V",
                    ordinal = 0,
                    remap = false
            )
    )
    private void stopDisablingVertexArray(int cap) {}

    @Redirect(
            method = {
                    "draw()V",
                    "addVertex(DDD)V"
            },
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;hasTexture:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean allowTextureByDefault(Tessellator instance) {
        return true;
    }

    @Redirect(
            method = {
                    "draw()V",
                    "addVertex(DDD)V"
            },
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;hasColour:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean allowColorByDefault(Tessellator instance) {
        return true;
    }

    @Redirect(
            method = {
                    "draw()V",
                    "addVertex(DDD)V"
            },
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;hasNormals:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean disableNormalsByDefault(Tessellator instance) {
        return false;
    }

    @Inject(
            method = "draw()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisableClientState(I)V",
                    shift = At.Shift.AFTER,
                    ordinal = 2,
                    remap = false
            )
    )
    private void disableVertexArrayAfterColor(CallbackInfo ci) {
        GL11.glDisableClientState(32884);
    }

    @Inject(
            method = "clear()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/nio/ByteBuffer;clear()Ljava/nio/Buffer;",
                    shift = At.Shift.AFTER
            )
    )
    private void clearBuffer(CallbackInfo ci) {
        intBuffer.clear();
    }

    @Inject(
            method = "start(I)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;drawing:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void doJniIfNotInChunk(int par1, CallbackInfo ci) {
        if(!renderingChunk) {
            GL11.glBegin(par1);
        }
    }

    @Inject(
            method = "setTextureXY(DD)V",
            at = @At("RETURN")
    )
    private void doJniIfNotInChunk(double d1, double par2, CallbackInfo ci) {
        if(!renderingChunk) {
            GL11.glTexCoord2f((float)d1, (float)par2);
        }
    }

    @Inject(
            method = "colour(IIII)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;hasColour:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void doJniIfNotInChunk(int j, int k, int i1, int par4, CallbackInfo ci) {
        if(!renderingChunk) {
            GL11.glColor4ub((byte)j, (byte)k, (byte)i1, (byte)par4);
            ci.cancel();
        }
    }

    /**
     * @reason mixins broke at array access redirect, so I have to temporarily overwrite until that issue is resolved.
     * @author mine_diver
     */
    @Overwrite
    public void addVertex(double d, double d1, double d2) {
        if(!renderingChunk) {
            GL11.glVertex3f((float)(d + xOffset), (float)(d1 + yOffset), (float)(d2 + zOffset));
            return;
        }
        ++this.vertexAmount;
        if (this.drawingMode == 7 && useTriangles && this.vertexAmount % 4 == 0) {
            for(int var7 = 0; var7 < 2; ++var7) {
                int var8 = 8 * (3 - var7);
                intBuffer.put(intBuffer.get(this.field_2068 - var8));
                intBuffer.put(intBuffer.get(this.field_2068 - var8 + 1));
                intBuffer.put(intBuffer.get(this.field_2068 - var8 + 2));
                intBuffer.put(intBuffer.get(this.field_2068 - var8 + 3));
                intBuffer.put(intBuffer.get(this.field_2068 - var8 + 4));
                intBuffer.put(intBuffer.get(this.field_2068 - var8 + 5));
                intBuffer.put(0);
                intBuffer.put(0);
                ++this.vertexCount;
                this.field_2068 += 8;
            }
        }

        intBuffer.put(Float.floatToRawIntBits((float)(d + this.xOffset) - chunkOffsetX));
        intBuffer.put(Float.floatToRawIntBits((float)(d1 + this.yOffset)));
        intBuffer.put(Float.floatToRawIntBits((float)(d2 + this.zOffset) - chunkOffsetZ));
        intBuffer.put(Float.floatToRawIntBits((float)this.textureX));
        intBuffer.put(Float.floatToRawIntBits((float)this.textureY));
        intBuffer.put(this.colour);
        intBuffer.put(0);
        intBuffer.put(0);
        this.field_2068 += 8;
        ++this.vertexCount;
        if (this.vertexCount % 4 == 0 && this.field_2068 >= this.bufferSize - 32) {
            this.draw();
            this.drawing = true;
        }
    }

//    @Inject(
//            method = "addVertex(DDD)V",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void doJniIfNotInChunk(double d1, double d2, double par3, CallbackInfo ci) {
//        if(!renderingChunk) {
//            GL11.glVertex3f((float)(d1 + xOffset), (float)(d2 + yOffset), (float)(par3 + zOffset));
//            ci.cancel();
//        }
//    }
//
//    @Unique
//    private int
//            capturedU, capturedV,
//            capturedColour;
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=13",
//                            "log=true"
//                    }
//            ),
//            slice = @Slice(
//                    from = @At(
//                            value = "FIELD",
//                            target = "Lnet/minecraft/client/render/Tessellator;hasTexture:Z",
//                            opcode = Opcodes.GETFIELD,
//                            ordinal = 1
//                    )
//            )
//    )
//    private void redirectBufferArraySetSlice(int[] array, int index, int value) {
//
//    }
//
//    @Unique
//    private void setBufferArray() {
//        switch (index & 7) {
//            case 0:
//            case 1:
//                intBuffer.put(value);
//                break;
//            case 2:
//                intBuffer.put(value);
//                intBuffer.put(capturedU);
//                intBuffer.put(capturedV);
//                intBuffer.put(capturedColour);
//                intBuffer.put(0);
//                intBuffer.put(0);
//                break;
//            case 3:
//                capturedU = value;
//                break;
//            case 4:
//                capturedV = value;
//                break;
//            case 5:
//                capturedColour = value;
//                break;
//        }
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=15",
//                            "log=true"
//                    },
//                    ordinal = 0
//            )
//    )
//    private void redirectBufferArrayU1(int[] array, int index, int value) {
//        capturedU = value;
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = "array=get",
//                    ordinal = 1
//            )
//    )
//    private int redirectBufferArrayGetU(int[] array, int index) {
//        return intBuffer.get(index);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 2
//            )
//    )
//    private void redirectBufferArrayV1(int[] array, int index, int value) {
//        capturedV = value;
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = "array=get",
//                    ordinal = 3
//            )
//    )
//    private int redirectBufferArrayGetV(int[] array, int index) {
//        return intBuffer.get(index);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 4
//            )
//    )
//    private void redirectBufferArrayColour1(int[] array, int index, int value) {
//        capturedColour = value;
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = "array=get",
//                    ordinal = 5
//            )
//    )
//    private int redirectBufferArrayGetColour(int[] array, int index) {
//        return intBuffer.get(index);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 6
//            )
//    )
//    private void redirectBufferArrayX1(int[] array, int index, int value) {
//        intBuffer.put(value);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = "array=get",
//                    ordinal = 7
//            )
//    )
//    private int redirectBufferArrayGetX(int[] array, int index) {
//        return intBuffer.get(index);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 8
//            )
//    )
//    private void redirectBufferArrayY1(int[] array, int index, int value) {
//        intBuffer.put(value);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = "array=get",
//                    ordinal = 9
//            )
//    )
//    private int redirectBufferArrayGetY(int[] array, int index) {
//        return intBuffer.get(index);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 10
//            )
//    )
//    private void redirectBufferArrayZ1(int[] array, int index, int value) {
//        intBuffer.put(value);
//        intBuffer.put(capturedU);
//        intBuffer.put(capturedV);
//        intBuffer.put(capturedColour);
//        intBuffer.put(0);
//        intBuffer.put(0);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = "array=get",
//                    ordinal = 11
//            )
//    )
//    private int redirectBufferArrayGetZ(int[] array, int index) {
//        return intBuffer.get(index);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 12
//            )
//    )
//    private void redirectBufferArrayU2(int[] array, int index, int value) {
//        capturedU = value;
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 13
//            )
//    )
//    private void redirectBufferArrayV2(int[] array, int index, int value) {
//        capturedV = value;
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 14
//            )
//    )
//    private void redirectBufferArrayColour2(int[] array, int index, int value) {
//        capturedColour = value;
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 16
//            )
//    )
//    private void redirectBufferArrayX2(int[] array, int index, int value) {
//        intBuffer.put(value);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 17
//            )
//    )
//    private void redirectBufferArrayY2(int[] array, int index, int value) {
//        intBuffer.put(value);
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;bufferArray:[I",
//                    args = {
//                            "array=set",
//                            "fuzz=14"
//                    },
//                    ordinal = 18
//            )
//    )
//    private void redirectBufferArrayZ2(int[] array, int index, int value) {
//        intBuffer.put(value);
//        intBuffer.put(capturedU);
//        intBuffer.put(capturedV);
//        intBuffer.put(capturedColour);
//        intBuffer.put(0);
//        intBuffer.put(0);
//    }
//
//    @ModifyArg(
//            method = "addVertex(DDD)V",
//            index = 0,
//            at = @At(
//                    value = "INVOKE",
//                    target = "Ljava/lang/Float;floatToRawIntBits(F)I",
//                    ordinal = 2,
//                    remap = false
//            )
//    )
//    private float modifyX(float value) {
//        return renderingChunk ? value - OFTessellatorFields.chunkOffsetX : value;
//    }
//
//    @ModifyArg(
//            method = "addVertex(DDD)V",
//            index = 0,
//            at = @At(
//                    value = "INVOKE",
//                    target = "Ljava/lang/Float;floatToRawIntBits(F)I",
//                    ordinal = 4,
//                    remap = false
//            )
//    )
//    private float modifyZ(float value) {
//        return renderingChunk ? value - OFTessellatorFields.chunkOffsetZ : value;
//    }
//
//    @Redirect(
//            method = "addVertex(DDD)V",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/render/Tessellator;vertexCount:I",
//                    opcode = Opcodes.GETFIELD,
//                    ordinal = 2
//            )
//    )
//    private int modifyIfRenderingInChunkThenDrawOverflown(Tessellator instance) {
//        return renderingChunk ? vertexCount : 1;
//    }

    @ModifyConstant(
            method = "setNormal(FFF)V",
            constant = @Constant(stringValue = "But..")
    )
    private String modifyErrorMessageBecauseNotchWasDepressed(String constant) {
        return "Error: Not drawing !!!";
    }

    @Inject(
            method = "setNormal(FFF)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;normal:I",
                    opcode = Opcodes.PUTFIELD
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void setNormal(float f1, float f2, float par3, CallbackInfo ci, int var4, int var5, int var6) {
        nx = (byte) var4;
        ny = (byte) var5;
        nz = (byte) var6;
    }

    @Unique
    private byte nx, ny, nz;

    @Inject(
            method = "setNormal(FFF)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/Tessellator;normal:I",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void setNormal(float f1, float f2, float par3, CallbackInfo ci) {
        if(!renderingChunk) {
            GL11.glNormal3b(nx, ny, nz);
        } else {
//            System.out.println("ERROR: NORMALS IN CHUNK MODE !!!");
        }
    }

    @Unique
    private void checkOpenGlError() {
        int i = GL11.glGetError();
        if(i != 0) {
            String s = "OpenGL Error: " + i + " " + Util.translateGLErrorString(i);
            Exception exception = new Exception(s);
            exception.printStackTrace();
        }
    }
}
