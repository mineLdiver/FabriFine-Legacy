package net.mine_diver.fabrifine.gui;

import net.mine_diver.fabrifine.config.OptionsListener;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.gui.widgets.OptionButton;
import net.minecraft.client.gui.widgets.Slider;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.util.ScreenScaler;

public class GuiAnimationSettingsOF extends ScreenBase {

    private final ScreenBase prevScreen;
    protected String title;
    private final GameOptions settings;
    private static Option[] enumOptions;
    
    public GuiAnimationSettingsOF(final ScreenBase guiscreen, final GameOptions gamesettings) {
        this.title = "Animation Settings";
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    @Override
    public void init() {
        final TranslationStorage stringtranslate = TranslationStorage.getInstance();
        int i = 0;
        for (final Option enumoptions : GuiAnimationSettingsOF.enumOptions) {
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
    public void render(final int i, final int j, final float f) {
        this.renderBackground();
        this.drawTextWithShadowCentred(this.textManager, this.title, this.width / 2, 20, 16777215);
        super.render(i, j, f);
    }
    
    static {
        GuiAnimationSettingsOF.enumOptions = new Option[] { OptionsListener.getANIMATED_WATER(), OptionsListener.getANIMATED_LAVA(), OptionsListener.getANIMATED_FIRE(), OptionsListener.getANIMATED_PORTAL(), OptionsListener.getANIMATED_REDSTONE(), OptionsListener.getANIMATED_EXPLOSION(), OptionsListener.getANIMATED_FLAME(), OptionsListener.getANIMATED_SMOKE() };
    }
}
