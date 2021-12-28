package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.config.Config;
import net.mine_diver.fabrifine.config.OFConfig;
import net.mine_diver.fabrifine.render.OFMeshRenderer;
import net.mine_diver.fabrifine.render.OFTessellatorFields;
import net.mine_diver.fabrifine.render.OFWorldRenderer;
import net.mine_diver.fabrifine.render.UpdateThread;
import net.minecraft.class_66;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Living;
import net.minecraft.util.maths.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.*;
import java.util.*;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements OFWorldRenderer {

    @Shadow private int field_1782;
    @Shadow private Minecraft client;

    @Shadow public abstract void method_1537();

    @Shadow protected abstract void method_1553(int i, int j, int k);

    @Shadow
    IntBuffer field_1797;
    @Shadow private class_66[] field_1808;
    @Shadow private boolean field_1817;

    @Shadow protected abstract int method_1542(int i, int j, int k, double d);

    @Shadow private int field_1810;
    @Shadow private List field_1807;
    @Shadow private class_66[] field_1809;

    private static final double CLOUD_OFFSET = 0.02;

    @Unique
    private long lastMovedTime = System.currentTimeMillis();
    @Unique
    private final IntBuffer field_22019_aY = BufferUtils.createIntBuffer(65536);
    @Unique
    double
            prevReposX,
            prevReposY,
            prevReposZ;
    @Unique
    private float timePerUpdateMs = 10.0f;
    @Unique
    private long updateStartTimeNs;
    @Unique
    private boolean firstUpdate = true;

    @Inject(
            method = "method_1537()V",
            at = @At("HEAD")
    )
    private void clearUpdates(CallbackInfo ci) {
        final UpdateThread ut = Config.getUpdateThread();
        if (ut != null) {
            ut.clearAllUpdates();
        }
        this.firstUpdate = true;
    }

    @Redirect(
            method = "method_1537()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fancyGraphics:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean redirectFancyTrees(GameOptions instance) {
        return Config.isTreesFancy();
    }

    @ModifyVariable(
            method = "method_1537()V",
            index = 1,
            at = @At(
                    value = "LOAD",
                    ordinal = 3
            )
    )
    private int modifyRenderDistance(int numBlocks) {
        numBlocks = 64 << 3 - this.field_1782;
        if (Config.isLoadChunksFar()) {
            numBlocks = 512;
        }
        if (Config.isFarView()) {
            if (numBlocks < 512) {
                numBlocks *= 3;
            }
            else {
                numBlocks *= 2;
            }
        }
        numBlocks += Config.getPreloadedChunks() * 2 * 16;
        if (!Config.isFarView() && numBlocks > 400) {
            numBlocks = 400;
        }
        this.prevReposX = -9999.0;
        this.prevReposY = -9999.0;
        this.prevReposZ = -9999.0;
        return numBlocks;
    }

    @Redirect(
            method = "method_1537()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_66;field_249:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void nullCheck(class_66 instance, boolean value) {
        if (instance != null)
            instance.field_249 = value;
    }

    @Redirect(
            method = "method_1537()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_66;field_243:Z",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void replaceBoolean(class_66 instance, boolean value) {
        instance.field_243 = false;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
            method = "method_1537()V",
            index = 4,
            at = @At("STORE")
    )
    private Living nullSafe(Living value) {
        if (value == null)
            value = client.player;
        return value;
    }

    @ModifyConstant(
            method = "method_1548(Lnet/minecraft/entity/Living;ID)I",
            constant = @Constant(intValue = 10)
    )
    private int stopIfSizeGreaterThanOrEqualToTen(int constant) {
        return field_1807.size() < 10 ? constant : 0;
    }

    @Redirect(
            method = "method_1548(Lnet/minecraft/entity/Living;ID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1537()V"
            )
    )
    private void redirectLoadedChunks(WorldRenderer instance) {
        if (!Config.isLoadChunksFar())
            method_1537();
    }

    @Redirect(
            method = "method_1548(Lnet/minecraft/entity/Living;ID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;method_1553(III)V"
            )
    )
    private void redirectPreloadedChunks(WorldRenderer instance, int j, int k, int i, Living arg, int i1, double d) {
        final int preloadedBlocks = Config.getPreloadedChunks() * 16;
        final double dReposX = arg.x - this.prevReposX;
        final double dReposY = arg.y - this.prevReposY;
        final double dReposZ = arg.z - this.prevReposZ;
        final double distSqRepos = dReposX * dReposX + dReposY * dReposY + dReposZ * dReposZ;
        if (distSqRepos > preloadedBlocks * preloadedBlocks + 16.0) {
            this.prevReposX = arg.x;
            this.prevReposY = arg.y;
            this.prevReposZ = arg.z;
            this.method_1553(MathHelper.floor(arg.x), MathHelper.floor(arg.y), MathHelper.floor(arg.z));
        }
    }

    @Inject(
            method = "method_1548(Lnet/minecraft/entity/Living;ID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderHelper;disableLighting()V"
            ),
            cancellable = true
    )
    private void redirectUpdateThread(Living player, int renderPass, double partialTicks, CallbackInfoReturnable<Integer> cir) {
        double partialX = player.prevRenderX + (player.x - player.prevRenderX) * partialTicks;
        double partialY = player.prevRenderY + (player.y - player.prevRenderY) * partialTicks;
        double partialZ = player.prevRenderZ + (player.z - player.prevRenderZ) * partialTicks;
        int s = (int)player.x;
        int e = (int)player.z;
        char x = 'ߐ';

        if ((Math.abs(s - OFTessellatorFields.chunkOffsetX) > x) || (Math.abs(e - OFTessellatorFields.chunkOffsetZ) > x)) {
            OFTessellatorFields.chunkOffsetX = s;
            OFTessellatorFields.chunkOffsetZ = e;
            method_1537();
        }
        int l = 0;
        UpdateThread updatethread = Config.getUpdateThread();
        if (updatethread != null) {
            if (updateStartTimeNs == 0L) {
                updateStartTimeNs = System.nanoTime();
            }
            if (updatethread.hasWorkToDo()) {
                l = Config.getUpdatesPerFrame();
                if ((Config.isDynamicUpdates()) && (!isMoving(player))) {
                    l *= 3;
                }
                l = Math.min(l, updatethread.getPendingUpdatesCount());
                if (l > 0) {
                    updatethread.unpause();
                }
            }
        }
        if (OFConfig.of(client.options).isOfSmoothFps() && renderPass == 0) {
            GL11.glFinish();
        }
        if (OFConfig.of(client.options).isOfSmoothInput() && renderPass == 0) {
            Config.sleep(1L);
        }
        int i1 = 0;
        int j1 = 0;
        if ((field_1817) && (client.options.advancedOpengl) && (!client.options.anaglyph3d) && (renderPass == 0)) {
            int k1 = 0;
            byte byte1 = 20;
            checkOcclusionQueryResult(k1, byte1, player.x, player.y, player.z);
            for (int i2 = k1; i2 < byte1; i2++) {
                field_1808[i2].field_252 = true;
            }

            i1 += method_1542(k1, byte1, renderPass, partialTicks);
            int j2 = byte1;
            int l2 = 0;
            byte byte2 = 30;
            int j3 = field_1810 / 2;
            while (j2 < field_1808.length) {
                byte byte3 = (byte)j2;
                if (l2 < j3) {
                    l2++;
                } else {
                    l2--;
                }
                j2 = byte3 + l2 * byte2;
                if (j2 <= byte3) {
                    j2 = byte3 + 10;
                }
                if (j2 > field_1808.length) {
                    j2 = field_1808.length;
                }
                GL11.glDisable(3553);
                GL11.glDisable(2896);
                GL11.glDisable(3008);
                GL11.glDisable(2912);
                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                checkOcclusionQueryResult(byte3, j2, player.x, player.y, player.z);
                GL11.glPushMatrix();
                float f4 = 0.0F;
                float f5 = 0.0F;
                float f6 = 0.0F;
                for (int k3 = byte3; k3 < j2; k3++) {
                    class_66 worldrenderer1 = field_1808[k3];
                    if (worldrenderer1.method_304()) {
                        worldrenderer1.field_243 = false;
                    }
                    else if (worldrenderer1.field_243) {
                        if (OFMeshRenderer.of(worldrenderer1).isUpdating()) {
                            worldrenderer1.field_252 = true;
                        }
                        else if ((Config.isOcclusionFancy()) && (!OFMeshRenderer.of(worldrenderer1).isInFrustrumFully())) {
                            worldrenderer1.field_252 = true;
                        }
                        else if ((worldrenderer1.field_243) && (!worldrenderer1.field_253)) {
                            if (OFMeshRenderer.of(worldrenderer1).isVisibleFromPosition()) {
                                float f7 = Math.abs((float)(OFMeshRenderer.of(worldrenderer1).getVisibleFromX() - player.x));
                                float f9 = Math.abs((float)(OFMeshRenderer.of(worldrenderer1).getVisibleFromY() - player.y));
                                float f11 = Math.abs((float)(OFMeshRenderer.of(worldrenderer1).getVisibleFromZ() - player.z));
                                float f13 = f7 + f9 + f11;
                                if (f13 < 10.0D + k3 / 1000.0D) {
                                    worldrenderer1.field_252 = true;
                                } else
                                    OFMeshRenderer.of(worldrenderer1).setVisibleFromPosition(false);
                            } else {
                                float f8 = (float)(worldrenderer1.field_237 - partialX);
                                float f10 = (float)(worldrenderer1.field_238 - partialY);
                                float f12 = (float)(worldrenderer1.field_239 - partialZ);
                                float f14 = f8 - f4;
                                float f15 = f10 - f5;
                                float f16 = f12 - f6;
                                if ((f14 != 0.0F) || (f15 != 0.0F) || (f16 != 0.0F)) {
                                    GL11.glTranslatef(f14, f15, f16);
                                    f4 += f14;
                                    f5 += f15;
                                    f6 += f16;
                                }
                                ARBOcclusionQuery.glBeginQueryARB(35092, worldrenderer1.field_254);
                                worldrenderer1.method_303();
                                ARBOcclusionQuery.glEndQueryARB(35092);
                                worldrenderer1.field_253 = true;
                                j1++;
                            }
                        }
                    }
                }
                GL11.glPopMatrix();
                GL11.glColorMask(true, true, true, true);
                GL11.glDepthMask(true);
                GL11.glEnable(3553);
                GL11.glEnable(3008);
                GL11.glEnable(2912);
                i1 += method_1542(byte3, j2, renderPass, partialTicks);
            }
        } else {
            i1 += method_1542(0, field_1808.length, renderPass, partialTicks);
        }
        if (updatethread != null) {
            float f = 0.0F;
            if (l > 0) {
                long l1 = System.nanoTime() - updateStartTimeNs;
                float f3 = timePerUpdateMs * (1.0F + (l - 1) / 2.0F);
                float f1 = f3 - (float)l1 / 1000000.0F;
                if (f1 > 0.0F) {
                    int i3 = (int)f1;
                    Config.sleep(i3);
                }
            }
            updatethread.pause();
            float f2 = 0.2F;
            if (l > 0) {
                int k2 = updatethread.resetUpdateCount();
                if (k2 < l) {
                    timePerUpdateMs += f2;
                }
                if (k2 > l) {
                    timePerUpdateMs -= f2;
                }
                if (k2 == l) {
                    timePerUpdateMs -= f2;
                }
            } else {
                timePerUpdateMs -= f2 / 5.0F;
            }
            if (timePerUpdateMs < 0.0F) {
                timePerUpdateMs = 0.0F;
            }
            updateStartTimeNs = System.nanoTime();
        }
        cir.setReturnValue(i1);
    }

    @Unique
    private void checkOcclusionQueryResult(final int startIndex, final int endIndex, final double px, final double py, final double pz) {
        for (int k = startIndex; k < endIndex; ++k) {
            final class_66 wr = this.field_1808[k];
            if (wr.field_253) {
                this.field_1797.clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(wr.field_254, 34919, this.field_1797);
                if (this.field_1797.get(0) != 0) {
                    wr.field_253 = false;
                    this.field_1797.clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(wr.field_254, 34918, this.field_1797);
                    final boolean wasVisible = wr.field_252;
                    wr.field_252 = (this.field_1797.get(0) > 0);
                    if (wasVisible && wr.field_252) {
                        OFMeshRenderer.of(wr).setVisibleFromPosition(true);
                        OFMeshRenderer.of(wr).setVisibleFromX(px);
                        OFMeshRenderer.of(wr).setVisibleFromY(py);
                        OFMeshRenderer.of(wr).setVisibleFromZ(pz);
                    }
                }
            }
        }
    }

    @Redirect(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;clear()V",
                    remap = false
            )
    )
    private void redirectClear(List instance) {
        field_22019_aY.clear();
    }

    @Inject(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    remap = false
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void captureCallList(int j, int k, int d, double par4, CallbackInfoReturnable<Integer> cir, int var6, int var7, int var8) {
        capturedCallList = var8;
    }

    @Unique
    private int capturedCallList;

    @Redirect(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    remap = false
            )
    )
    private <E> boolean redirectPut(List<E> instance, E e) {
        field_22019_aY.put(capturedCallList);
        return true;
    }

    @Inject(
            method = "method_1542(IIID)I",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;viewEntity:Lnet/minecraft/entity/Living;",
                    opcode = Opcodes.GETFIELD
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void cancelTail(int j, int k, int d, double partialTicks, CallbackInfoReturnable<Integer> cir, int l) {
        this.field_22019_aY.flip();
        final Living entityliving = this.client.viewEntity;
        final double partialX = entityliving.prevRenderX + (entityliving.x - entityliving.prevRenderX) * partialTicks - OFTessellatorFields.chunkOffsetX;
        final double partialY = entityliving.prevRenderY + (entityliving.y - entityliving.prevRenderY) * partialTicks;
        final double partialZ = entityliving.prevRenderZ + (entityliving.z - entityliving.prevRenderZ) * partialTicks - OFTessellatorFields.chunkOffsetZ;
        GL11.glTranslatef((float)(-partialX), (float)(-partialY), (float)(-partialZ));
        GL11.glCallLists(this.field_22019_aY);
        GL11.glTranslatef((float)partialX, (float)partialY, (float)partialZ);
        cir.setReturnValue(l);
    }

    /**
     * @reason I literally need to remove the method.
     * @author mine_diver
     */
    @Overwrite
    public void method_1540(int i, double d) {}

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    ordinal = 0,
                    remap = false
            )
    )
    private void cancelSky1(int list) {
        if (Config.isSkyEnabled())
            GL11.glCallList(list);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    ordinal = 1,
                    remap = false
            )
    )
    private void cancelStars(int list) {
        if (Config.isStarsEnabled())
            GL11.glCallList(list);
    }

    @Redirect(
            method = "renderSky(F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    ordinal = 2,
                    remap = false
            )
    )
    private void cancelSky2(int list) {
        if (Config.isSkyEnabled())
            GL11.glCallList(list);
    }

    @Inject(
            method = "method_1552(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fancyGraphics:Z",
                    opcode = Opcodes.GETFIELD
            ),
            cancellable = true
    )
    private void checkCloudSettings(float par1, CallbackInfo ci) {
        if (OFConfig.of(client.options).getOfClouds() == 3)
            ci.cancel();
    }

    @Redirect(
            method = "method_1552(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fancyGraphics:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean checkCloudSettings(GameOptions instance) {
        return Config.isCloudsFancy();
    }

    @ModifyVariable(
            method = "method_1552(F)V",
            index = 17,
            at = @At("STORE")
    )
    private float modifyCloudHeight(float value) {
        return value + OFConfig.of(client.options).getOfCloudsHeight() * 25;
    }

    @ModifyVariable(
            method = "renderClouds(F)V",
            index = 10,
            at = @At("STORE")
    )
    private float modifyFastCloudHeight(float value) {
        return value + OFConfig.of(client.options).getOfCloudsHeight() * 25;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 8
            )
    )
    private double modifyCloudSize1(double original) {
        return original + CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 9
            )
    )
    private double modifyCloudSize2(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 10
            )
    )
    private double modifyCloudSize3(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 11
            )
    )
    private double modifyCloudSize4(double original) {
        return original + CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 12
            )
    )
    private double modifyCloudSize5(double original) {
        return original + CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 13
            )
    )
    private double modifyCloudSize6(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 14
            )
    )
    private double modifyCloudSize7(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 15
            )
    )
    private double modifyCloudSize8(double original) {
        return original + CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 16
            )
    )
    private double modifyCloudSize9(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 17
            )
    )
    private double modifyCloudSize10(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 18
            )
    )
    private double modifyCloudSize11(double original) {
        return original + CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 19
            )
    )
    private double modifyCloudSize12(double original) {
        return original + CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 20
            )
    )
    private double modifyCloudSize13(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 21
            )
    )
    private double modifyCloudSize14(double original) {
        return original - CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 22
            )
    )
    private double modifyCloudSize15(double original) {
        return original + CLOUD_OFFSET;
    }

    @ModifyArg(
            method = "renderClouds(F)V",
            index = 1,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;vertex(DDDDD)V",
                    ordinal = 23
            )
    )
    private double modifyCloudSize16(double original) {
        return original + CLOUD_OFFSET;
    }

    /**
     * @reason Notch code.
     * @author mine_diver
     */
    @Overwrite
    public boolean method_1549(Living living, boolean flag) {
        UpdateThread updateThread = Config.getUpdateThread();
        if (updateThread == null && Config.isBackgroundChunkLoading()) {
            updateThread = Config.createUpdateThread(Display.getDrawable());
            updateThread.pause();
        }
        if (this.field_1807.size() <= 0) {
            return true;
        }
        int num = 0;
        final int NOT_IN_FRUSTRUM_MUL = 4;
        int numValid = 0;
        class_66 wrBest = null;
        float distSqBest = Float.MAX_VALUE;
        int indexBest = -1;
        for (int i = 0; i < this.field_1807.size(); ++i) {
            final class_66 wr = (class_66) this.field_1807.get(i);
            if (wr != null) {
                ++numValid;
                if (!OFMeshRenderer.of(wr).isUpdating()) {
                    if (!wr.field_249) {
                        //noinspection unchecked
                        this.field_1807.set(i, null);
                    } else {
                        float distSq = wr.method_299(living);
                        if (distSq <= 256.0f) {
                            if (this.isActingNow() || this.firstUpdate) {
                                if (updateThread != null) {
                                    updateThread.unpauseToEndOfUpdate();
                                }
                                wr.method_296();
                                wr.field_249 = false;
                                //noinspection unchecked
                                this.field_1807.set(i, null);
                                ++num;
                                continue;
                            }
                            if (updateThread != null) {
                                updateThread.addRendererToUpdate(wr, true);
                                wr.field_249 = false;
                                //noinspection unchecked
                                this.field_1807.set(i, null);
                                ++num;
                                continue;
                            }
                        }
                        if (!wr.field_243) {
                            distSq *= NOT_IN_FRUSTRUM_MUL;
                        }
                        if (wrBest == null) {
                            wrBest = wr;
                            distSqBest = distSq;
                            indexBest = i;
                        } else if (distSq < distSqBest) {
                            wrBest = wr;
                            distSqBest = distSq;
                            indexBest = i;
                        }
                    }
                }
            }
        }
        int maxUpdateNum = Config.getUpdatesPerFrame();
        boolean turboMode = false;
        if (Config.isDynamicUpdates() && !this.isMoving(living)) {
            maxUpdateNum *= 3;
            turboMode = true;
        }
        if (updateThread != null) {
            maxUpdateNum = updateThread.getUpdateCapacity();
            if (maxUpdateNum <= 0) {
                return true;
            }
        }
        if (wrBest != null) {
            this.updateRenderer(wrBest);
            //noinspection unchecked
            this.field_1807.set(indexBest, null);
            ++num;
            final float maxDiffDistSq = distSqBest / 5.0f;
            for (int j = 0; j < this.field_1807.size(); ++j) {
                if (num >= maxUpdateNum) {
                    break;
                }
                final class_66 wr2 = (class_66) this.field_1807.get(j);
                if (wr2 != null) {
                    if (!OFMeshRenderer.of(wr2).isUpdating()) {
                        float distSq2 = wr2.method_299(living);
                        if (!wr2.field_243) {
                            distSq2 *= NOT_IN_FRUSTRUM_MUL;
                        }
                        final float diffDistSq = Math.abs(distSq2 - distSqBest);
                        if (diffDistSq < maxDiffDistSq) {
                            this.updateRenderer(wr2);
                            //noinspection unchecked
                            this.field_1807.set(j, null);
                            ++num;
                        }
                    }
                }
            }
        }
        if (numValid == 0) {
            this.field_1807.clear();
        }
        if (this.field_1807.size() > 100 && numValid < this.field_1807.size() * 4 / 5) {
            int dstIndex = 0;
            for (int srcIndex = 0; srcIndex < this.field_1807.size(); ++srcIndex) {
                final Object wr3 = this.field_1807.get(srcIndex);
                if (wr3 != null) {
                    if (srcIndex != dstIndex) {
                        //noinspection unchecked
                        this.field_1807.set(dstIndex, wr3);
                        ++dstIndex;
                    }
                }
            }
            //noinspection ListRemoveInLoop
            for (int j = this.field_1807.size() - 1; j >= dstIndex; --j) {
                this.field_1807.remove(j);
            }
        }
        this.firstUpdate = false;
        return true;
    }

    @Unique
    private void updateRenderer(final class_66 wr) {
        final UpdateThread ut = Config.getUpdateThread();
        if (ut != null) {
            ut.addRendererToUpdate(wr, false);
            wr.field_249 = false;
            return;
        }
        wr.method_296();
        wr.field_249 = false;
        OFMeshRenderer.of(wr).setUpdating(false);
    }

    @Unique
    private boolean isMoving(final Living entityliving) {
        final boolean moving = this.isMovingNow(entityliving);
        if (moving) {
            this.lastMovedTime = System.currentTimeMillis();
            return true;
        }
        return System.currentTimeMillis() - this.lastMovedTime < 2000L;
    }

    @Unique
    private boolean isMovingNow(final Living entityliving) {
        final double maxDiff = 0.001;
        return ((LivingAccessor) entityliving).getJumping() || entityliving.method_1373() || entityliving.lastHandSwingProgress > maxDiff || this.client.field_2767.field_2586 != 0 || this.client.field_2767.field_2587 != 0 || Math.abs(entityliving.x - entityliving.prevX) > maxDiff || Math.abs(entityliving.y - entityliving.prevY) > maxDiff || Math.abs(entityliving.z - entityliving.prevZ) > maxDiff;
    }

    @Unique
    private boolean isActingNow() {
        return Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
    }

    @Redirect(
            method = "method_1550(Lnet/minecraft/class_68;F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_66;field_243:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean redirectToResumeIf(class_66 instance) {
        return false;
    }

    @Inject(
            method = "method_1148()V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelIfNull(CallbackInfo ci) {
        if (field_1809 == null)
            ci.cancel();
    }

    @Override
    @Unique
    public void setAllRenderesVisible() {
        if (this.field_1809 == null) {
            return;
        }
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < this.field_1809.length; ++i) {
            this.field_1809[i].field_252 = true;
        }
    }

    @Inject(
            method = "addParticle(Ljava/lang/String;DDDDDD)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/WorldRenderer;level:Lnet/minecraft/level/Level;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1
            ),
            cancellable = true
    )
    private void cancelSmoke(String d, double d1, double d2, double d3, double d4, double d5, double par7, CallbackInfo ci) {
        if (!Config.isAnimatedSmoke())
            ci.cancel();
    }

    @Inject(
            method = "addParticle(Ljava/lang/String;DDDDDD)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/WorldRenderer;level:Lnet/minecraft/level/Level;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 4
            ),
            cancellable = true
    )
    private void cancelExplosion(String d, double d1, double d2, double d3, double d4, double d5, double par7, CallbackInfo ci) {
        if (!Config.isAnimatedExplosion())
            ci.cancel();
    }

    @Inject(
            method = "addParticle(Ljava/lang/String;DDDDDD)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/WorldRenderer;level:Lnet/minecraft/level/Level;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 5
            ),
            cancellable = true
    )
    private void cancelFire(String d, double d1, double d2, double d3, double d4, double d5, double par7, CallbackInfo ci) {
        if (!Config.isAnimatedFlame())
            ci.cancel();
    }

    @Inject(
            method = "addParticle(Ljava/lang/String;DDDDDD)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/WorldRenderer;level:Lnet/minecraft/level/Level;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 9
            ),
            cancellable = true
    )
    private void cancelLargeSmoke(String d, double d1, double d2, double d3, double d4, double d5, double par7, CallbackInfo ci) {
        if (!Config.isAnimatedSmoke())
            ci.cancel();
    }

    @Inject(
            method = "addParticle(Ljava/lang/String;DDDDDD)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/WorldRenderer;level:Lnet/minecraft/level/Level;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 10
            ),
            cancellable = true
    )
    private void cancelRedstone(String d, double d1, double d2, double d3, double d4, double d5, double par7, CallbackInfo ci) {
        if (!Config.isAnimatedRedstone())
            ci.cancel();
    }

    @Override
    @Unique
    public int renderAllSortedRenderers(final int renderPass, final double partialTicks) {
        return this.method_1542(0, this.field_1808.length, renderPass, partialTicks);
    }
}
