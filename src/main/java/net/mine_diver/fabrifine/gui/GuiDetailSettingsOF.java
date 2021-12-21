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

public class GuiDetailSettingsOF extends ScreenBase {
    
    private final ScreenBase prevScreen;
    protected String title;
    private final GameOptions settings;
    private static Option[] enumOptions;
    private int lastMouseX;
    private int lastMouseY;
    private long mouseStillTime;
    
    public GuiDetailSettingsOF(final ScreenBase guiscreen, final GameOptions gamesettings) {
        this.lastMouseX = 0;
        this.lastMouseY = 0;
        this.mouseStillTime = 0L;
        this.title = "Detail Settings";
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    @Override
    public void init() {
        final TranslationStorage stringtranslate = TranslationStorage.getInstance();
        int i = 0;
        for (final Option enumoptions : GuiDetailSettingsOF.enumOptions) {
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
        if (btnName.equals("Clouds")) {
            return new String[] { "Clouds", "  Default - as set by setting Graphics", "  Fast - lower quality, faster", "  Fancy - higher quality, slower", "  OFF - no clouds, fastest", "Fast clouds are rendered 2D.", "Fancy clouds are rendered 3D." };
        }
        if (btnName.equals("Cloud Height")) {
            return new String[] { "Cloud Height", "  OFF - default height", "  100% - above world height limit" };
        }
        if (btnName.equals("Trees")) {
            return new String[] { "Trees", "  Default - as set by setting Graphics", "  Fast - lower quality, faster", "  Fancy - higher quality, slower", "Fast trees have opaque leaves.", "Fancy trees have transparent leaves." };
        }
        if (btnName.equals("Grass")) {
            return new String[] { "Grass", "  Default - as set by setting Graphics", "  Fast - lower quality, faster", "  Fancy - higher quality, slower", "Fast grass uses default side texture.", "Fancy grass uses biome side texture." };
        }
        if (btnName.equals("Water")) {
            return new String[] { "Water", "  Default - as set by setting Graphics", "  Fast  - lower quality, faster", "  Fancy - higher quality, slower", "Fast water (1 pass) has some visual artifacts", "Fancy water (2 pass) has no visual artifacts" };
        }
        if (btnName.equals("Rain & Snow")) {
            return new String[] { "Rain & Snow", "  Default - as set by setting Graphics", "  Fast  - light rain/snow, faster", "  Fancy - heavy rain/snow, slower", "  OFF - no rain/snow, fastest", "When rain is OFF the splashes and rain sounds", "are still active." };
        }
        if (btnName.equals("Sky")) {
            return new String[] { "Sky", "  ON - sky is visible, slower", "  OFF  - sky is not visible, faster", "When sky is OFF the moon and sun are still visible." };
        }
        if (btnName.equals("Stars")) {
            return new String[] { "Stars", "  ON - stars are visible, slower", "  OFF  - stars are not visible, faster" };
        }
        if (btnName.equals("Autosave")) {
            return new String[] { "Autosave interval", "Default autosave interval (2s) is NOT RECOMMENDED.", "Autosave causes the famous Lag Spike of Death." };
        }
        if (btnName.equals("Fast Debug Info")) {
            return new String[] { "Fast Debug Info", " OFF - default debug info screen, slower", " ON - debug info screen without lagometer, faster", "Removes the lagometer from the debug screen (F3)." };
        }
        if (btnName.equals("Chunk Updates")) {
            return new String[] { "Chunk updates per frame", " 1 - (default) slower world loading, higher FPS", " 3 - faster world loading, lower FPS", " 5 - fastest world loading, lowest FPS" };
        }
        if (btnName.equals("Dynamic Updates")) {
            return new String[] { "Chunk updates per frame", " OFF - (default) standard chunk updates per frame", " ON - more updates while the player is standing still", "Dynamic updates force more chunk updates while", "the player is standing still to load the world faster." };
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
        GuiDetailSettingsOF.enumOptions = new Option[] { OptionsListener.getCLOUDS(), OptionsListener.getCLOUD_HEIGHT(), OptionsListener.getTREES(), OptionsListener.getGRASS(), OptionsListener.getWATER(), OptionsListener.getRAIN(), OptionsListener.getSKY(), OptionsListener.getSTARS(), OptionsListener.getCLEAR_WATER() };
    }
}
