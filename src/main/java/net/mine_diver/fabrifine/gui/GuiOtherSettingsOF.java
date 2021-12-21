package net.mine_diver.fabrifine.gui;

import net.mine_diver.fabrifine.config.OptionsListener;
import net.mine_diver.fabrifine.mixin.ButtonAccessor;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;
import net.minecraft.client.gui.widgets.Slider;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.ScreenScaler;

public class GuiOtherSettingsOF extends ScreenBase {

    private final ScreenBase prevScreen;
    protected String title;
    private final GameOptions settings;
    private static Option[] enumOptions;
    private int lastMouseX;
    private int lastMouseY;
    private long mouseStillTime;
    
    public GuiOtherSettingsOF(final ScreenBase guiscreen, final GameOptions gamesettings) {
        this.lastMouseX = 0;
        this.lastMouseY = 0;
        this.mouseStillTime = 0L;
        this.title = "Other Settings";
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    @Override
    public void init() {
        final TranslationStorage stringtranslate = TranslationStorage.getInstance();
        int i = 0;
        for (final Option enumoptions : GuiOtherSettingsOF.enumOptions) {
            final int x = this.width / 2 - 155 + i % 2 * 160;
            final int y = this.height / 6 + 21 * (i / 2) - 10;
            if (!enumoptions.isSlider()) {
                //noinspection unchecked
                this.buttons.add(new OptionButton(enumoptions.getId(), x, y, enumoptions, this.settings.getTranslatedValue(enumoptions)));
            }
            else {
                //noinspection unchecked
                this.buttons.add(new Slider(enumoptions.getId(), x, y, enumoptions, this.settings.getTranslatedValue(enumoptions), this.settings.getFloatValue(enumoptions)));
            }
            ++i;
        }
        //noinspection unchecked
        this.buttons.add(new Button(200, this.width / 2 - 100, this.height / 6 + 168 + 11, stringtranslate.translate("gui.done")));
    }

    @Override
    protected void buttonClicked(final Button guibutton) {
        if (!guibutton.active) {
            return;
        }
        if (guibutton.id < 100 && guibutton instanceof OptionButton) {
            this.settings.changeOption(((OptionButton)guibutton).getOption(), 1);
            guibutton.text = this.settings.getTranslatedValue(Option.getById(guibutton.id));
        }
        if (guibutton.id == 200) {
            this.minecraft.options.saveOptions();
            this.minecraft.openScreen(this.prevScreen);
        }
        if (guibutton.id != OptionsListener.getCLOUD_HEIGHT().ordinal()) {
            final ScreenScaler scaledresolution = new ScreenScaler(this.minecraft.options, this.minecraft.actualWidth, this.minecraft.actualHeight);
            final int i = scaledresolution.getScaledWidth();
            final int j = scaledresolution.getScaledHeight();
            this.init(this.minecraft, i, j);
        }
    }

    @Override
    public void render(final int x, final int y, final float f) {
        this.renderBackground();
        this.drawTextWithShadowCentred(this.textManager, this.title, this.width / 2, 20, 16777215);
        super.render(x, y, f);
        if (Math.abs(x - this.lastMouseX) > 5 || Math.abs(y - this.lastMouseY) > 5) {
            this.lastMouseX = x;
            this.lastMouseY = y;
            this.mouseStillTime = System.currentTimeMillis();
            return;
        }
        final int activateDelay = 700;
        if (System.currentTimeMillis() < this.mouseStillTime + activateDelay) {
            return;
        }
        final int x2 = this.width / 2 - 150;
        int y2 = this.height / 6 - 5;
        if (y <= y2 + 98) {
            y2 += 105;
        }
        final int x3 = x2 + 150 + 150;
        final int y3 = y2 + 84 + 10;
        final Button btn = this.getSelectedButton(x, y);
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
    
    private String[] getTooltipLines(final String btnName) {
        if (btnName.equals("Smooth FPS")) {
            return new String[] { "Stabilizes FPS by flushing the graphic driver buffers", "  OFF - no stabilization, FPS may fluctuate", "  ON - FPS stabilization", "This option is graphic driver dependant and its effect", "is not always visible" };
        }
        if (btnName.equals("Smooth Input")) {
            return new String[] { "Fixes stuck keys, slow input response and sound lag", "  OFF - no fix for stuck keys", "  ON - fixes stuck keys", "This option adds a small delay (1ms) to the game loop", "which fixes the stuck keys, slow input and sound lag." };
        }
        if (btnName.equals("Autosave")) {
            return new String[] { "Autosave interval", "Default autosave interval (2s) is NOT RECOMMENDED.", "Autosave causes the famous Lag Spike of Death." };
        }
        if (btnName.equals("Fast Debug Info")) {
            return new String[] { "Fast Debug Info", " OFF - default debug info screen, slower", " ON - debug info screen without lagometer, faster", "Removes the lagometer from the debug screen (F3)." };
        }
        return null;
    }
    
    private String getButtonName(final String displayString) {
        final int pos = displayString.indexOf(58);
        if (pos < 0) {
            return displayString;
        }
        return displayString.substring(0, pos);
    }
    
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
    
    static {
        GuiOtherSettingsOF.enumOptions = new Option[] { OptionsListener.getSMOOTH_FPS(), OptionsListener.getSMOOTH_INPUT(), OptionsListener.getAUTOSAVE_TICKS(), OptionsListener.getFAST_DEBUG_INFO() };
    }
}
