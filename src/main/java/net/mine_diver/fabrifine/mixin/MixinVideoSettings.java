package net.mine_diver.fabrifine.mixin;

import net.mine_diver.fabrifine.config.OptionsListener;
import net.mine_diver.fabrifine.gui.GuiAnimationSettingsOF;
import net.mine_diver.fabrifine.gui.GuiDetailSettingsOF;
import net.mine_diver.fabrifine.gui.GuiOtherSettingsOF;
import net.mine_diver.fabrifine.gui.GuiWorldSettingsOF;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.screen.menu.VideoSettings;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;
import net.minecraft.client.gui.widgets.Slider;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.TranslationStorage;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(VideoSettings.class)
public class MixinVideoSettings extends ScreenBase {

    @Shadow private GameOptions options;
    @Shadow private static Option[] OPTIONS;
    @Unique
    private int
            lastMouseX,
            lastMouseY;
    @Unique
    private long
            mouseStillTime;

    @Inject(
            method = "init()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/options/Option;isSlider()Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void captureLocals(CallbackInfo ci, TranslationStorage var1, int var2) {
        capturedIndex = var2;
    }

    @Unique
    private int capturedIndex;

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
            method = "init()V",
            at = @At(
                    value = "NEW",
                    target = "(IIILnet/minecraft/client/options/Option;Ljava/lang/String;)Lnet/minecraft/client/gui/widgets/OptionButton;"
            )
    )
    private OptionButton redirectOptionButtons(int id, int x, int y, Option option, String text) {
        return new OptionButton(id, x, this.height / 6 + 21 * (capturedIndex >> 1) - 10, option, text);
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(
            method = "init()V",
            at = @At(
                    value = "NEW",
                    target = "(IIILnet/minecraft/client/options/Option;Ljava/lang/String;F)Lnet/minecraft/client/gui/widgets/Slider;"
            )
    )
    private Slider redirectOptionButtons(int id, int x, int y, Option option, String text, float value) {
        return new Slider(id, x, this.height / 6 + 21 * (capturedIndex >> 1) - 10, option, text, value);
    }

    @Inject(
            method = "init()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    shift = At.Shift.BEFORE,
                    remap = false
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void addButtons(CallbackInfo ci, TranslationStorage var1, int i) {
        int y2 = this.height / 6 + 21 * (i / 2) - 10;
        int x2 = this.width / 2 - 155;
        //noinspection unchecked
        this.buttons.add(new OptionButton(100, x2, y2, "Animations..."));
        x2 = this.width / 2 - 155 + 160;
        //noinspection unchecked
        this.buttons.add(new OptionButton(101, x2, y2, "Details..."));
        y2 += 21;
        x2 = this.width / 2 - 155;
        //noinspection unchecked
        this.buttons.add(new OptionButton(102, x2, y2, "World..."));
        x2 = this.width / 2 - 155 + 160;
        //noinspection unchecked
        this.buttons.add(new OptionButton(103, x2, y2, "Other..."));
    }

    @ModifyConstant(
            method = "init()V",
            constant = @Constant(intValue = 168)
    )
    private int modifyButtonHeight(int original) {
        return original + 11;
    }

    @Inject(
            method = "buttonClicked(Lnet/minecraft/client/gui/widgets/Button;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/gui/screen/menu/VideoSettings;minecraft:Lnet/minecraft/client/Minecraft;",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 2,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void handleButtons(Button button, CallbackInfo ci) {
        if (button.id == 100) {
            this.minecraft.options.saveOptions();
            final GuiAnimationSettingsOF scr = new GuiAnimationSettingsOF(this, this.options);
            this.minecraft.openScreen(scr);
        }
        if (button.id == 101) {
            this.minecraft.options.saveOptions();
            final GuiDetailSettingsOF scr2 = new GuiDetailSettingsOF(this, this.options);
            this.minecraft.openScreen(scr2);
        }
        if (button.id == 102) {
            this.minecraft.options.saveOptions();
            final GuiWorldSettingsOF scr3 = new GuiWorldSettingsOF(this, this.options);
            this.minecraft.openScreen(scr3);
        }
        if (button.id == 103) {
            this.minecraft.options.saveOptions();
            final GuiOtherSettingsOF scr4 = new GuiOtherSettingsOF(this, this.options);
            this.minecraft.openScreen(scr4);
        }
        if (button.id == OptionsListener.getBRIGHTNESS().ordinal() || button.id == OptionsListener.getAO_LEVEL().ordinal()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render(IIF)V",
            at = @At("RETURN")
    )
    private void renderTooltips(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Math.abs(mouseX - this.lastMouseX) > 5 || Math.abs(mouseY - this.lastMouseY) > 5) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.mouseStillTime = System.currentTimeMillis();
            return;
        }
        final int activateDelay = 700;
        if (System.currentTimeMillis() < this.mouseStillTime + activateDelay) {
            return;
        }
        final int x2 = this.width / 2 - 150;
        int y2 = this.height / 6 - 5;
        if (mouseY <= y2 + 98) {
            y2 += 105;
        }
        final int x3 = x2 + 150 + 150;
        final int y3 = y2 + 84 + 10;
        final Button btn = this.getSelectedButton(mouseX, mouseY);
        if (btn != null) {
            final String s = this.getButtonName(btn.text);
            final String[] lines = this.getTooltipLines(s);
            if (lines == null) {
                return;
            }
            this.fillGradient(x2, y2, x3, y3, -536870912, -536870912);
            for (int i = 0; i < lines.length; ++i) {
                final String line = lines[i];
                this.textManager.drawTextWithShadow(line, x2 + 5, y2 + 5 + i * 11, 14540253);
            }
        }
    }

    @Unique
    private String[] getTooltipLines(final String btnName) {
        if (btnName.equals("Graphics")) {
            return new String[] { "Visual quality", "  Fast  - lower quality, faster", "  Fancy - higher quality, slower", "Changes the appearance of clouds, leaves, water,", "shadows and grass sides." };
        }
        if (btnName.equals("Render Distance")) {
            return new String[] { "Visible distance", "  Far - 256m (slower)", "  Normal - 128m", "  Short - 64m (faster)", "  Tiny - 32m (fastest)" };
        }
        if (btnName.equals("Smooth Lighting")) {
            return new String[] { "Smooth lighting", "  OFF - no smooth lighting (faster)", "  1% - light smooth lighting (slower)", "  100% - dark smooth lighting (slower)" };
        }
        if (btnName.equals("Performance")) {
            return new String[] { "FPS Limit", "  Max FPS - no limit (fastest)", "  Balanced - limit 120 FPS (slower)", "  Power saver - limit 40 FPS (slowest)", "  VSync - limit to monitor framerate (60, 30, 20)", "Balanced and Power saver decrease the FPS even if", "the limit value is not reached." };
        }
        if (btnName.equals("3D Anaglyph")) {
            return new String[] { "3D mode used with red-cyan 3D glasses." };
        }
        if (btnName.equals("View Bobbing")) {
            return new String[] { "More realistic movement.", "When using mipmaps set it to OFF for best results." };
        }
        if (btnName.equals("GUI Scale")) {
            return new String[] { "GUI Scale", "Smaller GUI might be faster" };
        }
        if (btnName.equals("Advanced OpenGL")) {
            return new String[] { "Detect and render only visible geometry", "  OFF - all geometry is rendered (slower)", "  Fast - ony visible geometry is rendered (fastest)", "  Fancy - conservative, avoids visual artifacts (faster)", "The option is available only if it is supported by the ", "graphic card." };
        }
        if (btnName.equals("Fog")) {
            return new String[] { "Fog type", "  Fast - faster fog", "  Fancy - slower fog, looks better", "The fancy fog is available only if it is supported by the ", "graphic card." };
        }
        if (btnName.equals("Fog Start")) {
            return new String[] { "Fog start", "  0.2 - the fog starts near the player", "  0.8 - the fog starts far from the player", "This option usually does not affect the performance." };
        }
        if (btnName.equals("Mipmap Level")) {
            return new String[] { "Visual effect which makes distant objects look better", "by smoothing the texture details", "  OFF - no smoothing", "  1 - minimum smoothing", "  4 - maximum smoothing", "This option usually does not affect the performance." };
        }
        if (btnName.equals("Mipmap Type")) {
            return new String[] { "Visual effect which makes distant objects look better", "by smoothing the texture details", "  Nearest - rough smoothing", "  Linear - fine smoothing", "This option usually does not affect the performance." };
        }
        if (btnName.equals("Better Grass")) {
            return new String[] { "Better Grass", "  OFF - default side grass texture, fastest", "  Fast - full side grass texture, slower", "  Fancy - dynamic side grass texture, slowest" };
        }
        if (btnName.equals("Brightness")) {
            return new String[] { "Increases the brightness of darker objects", "  OFF - standard brightness", "  100% - maximum brightness for darker objects", "This options does not change the brightness of ", "fully black objects" };
        }
        return null;
    }

    @Unique
    private String getButtonName(final String displayString) {
        final int pos = displayString.indexOf(58);
        if (pos < 0) {
            return displayString;
        }
        return displayString.substring(0, pos);
    }

    @Unique
    private Button getSelectedButton(final int i, final int j) {
        //noinspection ForLoopReplaceableByForEach
        for (int k = 0; k < this.buttons.size(); ++k) {
            final Button btn = (Button) this.buttons.get(k);
            final boolean flag = i >= btn.x && j >= btn.y && i < btn.x + ((ButtonAccessor) btn).getWidth() && j < btn.y + ((ButtonAccessor) btn).getHeight();
            if (flag) {
                return btn;
            }
        }
        return null;
    }

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "<clinit>",
            at = @At("RETURN")
    )
    private static void initOptions(CallbackInfo ci) {
        for (int i = 0; i < OPTIONS.length; i++) {
            if (OPTIONS[i] == Option.AMBIENT_OCCLUSION)
                OPTIONS[i] = OptionsListener.getAO_LEVEL();
        }
        OPTIONS = Arrays.copyOf(OPTIONS, OPTIONS.length + 6);
        OPTIONS[OPTIONS.length - 6] = OptionsListener.getFOG_FANCY();
        OPTIONS[OPTIONS.length - 5] = OptionsListener.getFOG_START();
        OPTIONS[OPTIONS.length - 4] = OptionsListener.getMIPMAP_LEVEL();
        OPTIONS[OPTIONS.length - 3] = OptionsListener.getMIPMAP_TYPE();
        OPTIONS[OPTIONS.length - 2] = OptionsListener.getBETTER_GRASS();
        OPTIONS[OPTIONS.length - 1] = OptionsListener.getBRIGHTNESS();
    }
}
