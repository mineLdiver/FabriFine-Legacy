package net.mine_diver.fabrifine.mixin;

import lombok.Getter;
import lombok.Setter;
import net.mine_diver.fabrifine.config.Config;
import net.mine_diver.fabrifine.render.IUpdateListener;
import net.mine_diver.fabrifine.render.OFCamera;
import net.mine_diver.fabrifine.render.OFMeshRenderer;
import net.mine_diver.fabrifine.render.OFTessellator;
import net.minecraft.class_66;
import net.minecraft.class_68;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.ItemRenderer;
import net.minecraft.level.Level;
import net.minecraft.tileentity.TileEntityBase;
import net.minecraft.util.maths.Box;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(class_66.class)
public abstract class Mixinclass_66 implements OFMeshRenderer {

    @Shadow public abstract void method_296();

    @Shadow private int field_225;
    @Shadow public int field_240;
    @Shadow public int field_242;
    @Shadow public int field_241;
    @Shadow public boolean[] field_244;

    @Shadow public abstract boolean method_304();

    @Shadow public int field_234;
    @Shadow public int field_235;
    @Shadow public int field_236;
    @Shadow private static Tessellator tesselator;
    @Shadow public boolean field_252;
    @Shadow public boolean field_243;
    @Shadow public Box field_250;
    @Shadow public boolean field_249;
    @Unique
    @Getter @Setter
    private boolean isVisibleFromPosition;
    @Unique
    @Getter @Setter
    private double visibleFromX;
    @Unique
    @Getter @Setter
    private double visibleFromY;
    @Unique
    @Getter @Setter
    private double visibleFromZ;
    @Unique
    private boolean needsBoxUpdate;
    @Unique
    @Getter @Setter
    private boolean isInFrustrumFully;
    @Unique
    @Getter @Setter
    private volatile boolean isUpdating;
    @Unique
    private int glRenderListStable;
    @Unique
    private int glRenderListBoundingBox;

    @Inject(
            method = "<init>(Lnet/minecraft/level/Level;Ljava/util/List;IIIII)V",
            at = @At("RETURN")
    )
    private void onCor(Level list, List<TileEntityBase> i, int j, int k, int i1, int j1, int par7, CallbackInfo ci) {
        this.glRenderListStable = this.field_225 + 393216;
        this.glRenderListBoundingBox = this.field_225 + 2;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
            method = "method_298(III)V",
            index = 4,
            at = @At("STORE")
    )
    private float modifyFloat(float value) {
        return 0;
    }

    @Redirect(
            method = "method_298(III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glNewList(II)V",
                    remap = false
            )
    )
    private void stopNewList(int list, int mode) {
        needsBoxUpdate = true;
    }

    @Redirect(
            method = "method_298(III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/ItemRenderer;method_2024(Lnet/minecraft/util/maths/Box;)V"
            )
    )
    private void stopItemRenderer(Box box) {}

    @Redirect(
            method = "method_298(III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEndList()V",
                    remap = false
            )
    )
    private void stopEndList() {}

    @Inject(
            method = "method_298(III)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_66;method_305()V",
                    shift = At.Shift.AFTER
            )
    )
    private void setIsVisibleFromPosition(int j, int k, int par3, CallbackInfo ci) {
        isVisibleFromPosition = true;
    }

    @Inject(
            method = "method_296()V",
            at = @At("RETURN")
    )
    private void checkIfDefaultMethod(CallbackInfo ci) {
        if (customUpdateListener == null)
            finishUpdate();
    }

    private IUpdateListener customUpdateListener;

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_66;field_249:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean redirectNeedsUpdate(class_66 instance) {
        field_249 = false;
        return true;
    }

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPushMatrix()V",
                    remap = false
            )
    )
    private void stopPushMatrix() {}

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_66;method_306()V"
            )
    )
    private void stopMethod_306(class_66 instance) {}

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
                    remap = false
            )
    )
    private void stopGlTranslatef(float x, float y, float z) {}

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V",
                    remap = false
            )
    )
    private void stopGlScalef(float x, float y, float z) {}

    @Inject(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;start()V"
            )
    )
    private void setRenderingChunk(CallbackInfo ci) {
        OFTessellator.of(Tessellator.INSTANCE).setRenderingChunk(true);
    }

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_66;tesselator:Lnet/minecraft/client/render/Tessellator;",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private Tessellator redirectTessellator() {
        return Tessellator.INSTANCE;
    }

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;setOffset(DDD)V"
            )
    )
    private void stopSetOffset(Tessellator instance, double d1, double d2, double v) {}

    @Inject(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Tessellator;draw()V"
            )
    )
    private void updateListener(CallbackInfo ci) {
        if (customUpdateListener != null)
            customUpdateListener.updating();
    }

    @Redirect(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V",
                    remap = false
            )
    )
    private void stopGlPopMatrix() {}

    @Inject(
            method = "method_296()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEndList()V",
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void setRenderingChunk2(CallbackInfo ci) {
        OFTessellator.of(Tessellator.INSTANCE).setRenderingChunk(false);
    }

    @Inject(
            method = "method_296()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_66;field_227:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void onReturn(CallbackInfo ci) {
        field_252 = true;
        isVisibleFromPosition = true;
    }

    @Override
    @Unique
    public void updateRenderer(IUpdateListener updateListener) {
        customUpdateListener = updateListener;
        method_296();
        customUpdateListener = null;
    }

    @Redirect(
            method = "method_297(I)I",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/class_66;field_225:I",
                    opcode = Opcodes.GETFIELD
            )
    )
    private int redirectRenderListId(class_66 instance) {
        return glRenderListStable;
    }

    @Inject(
            method = "method_300(Lnet/minecraft/class_68;)V",
            at = @At("RETURN")
    )
    private void isInFrustrum(class_68 par1, CallbackInfo ci) {
        this.isInFrustrumFully = this.field_243 && Config.isOcclusionEnabled() && Config.isOcclusionFancy() && OFCamera.of(par1).isBoundingBoxInFrustumFully(this.field_250);
    }

    @ModifyArg(
            method = "method_303()V",
            index = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glCallList(I)V",
                    remap = false
            )
    )
    private int redirectRenderListIdBox(int original) {
        return glRenderListBoundingBox;
    }

    @Override
    @Unique
    public void finishUpdate() {
        final int temp = this.field_225;
        this.field_225 = this.glRenderListStable;
        this.glRenderListStable = temp;
        for (int i = 0; i < 2; ++i) {
            if (!this.field_244[i]) {
                GL11.glNewList(this.field_225 + i, 4864);
                GL11.glEndList();
            }
        }
        if (this.needsBoxUpdate && !this.method_304()) {
            final float f = 0.0f;
            GL11.glNewList(this.glRenderListBoundingBox, 4864);
            ItemRenderer.method_2024(Box.createButWasteMemory(this.field_240 - f, this.field_241 - f, this.field_242 - f, this.field_240 + this.field_234 + f, this.field_241 + this.field_235 + f, this.field_242 + this.field_236 + f));
            GL11.glEndList();
        }
    }
}
