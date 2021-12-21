package net.mine_diver.fabrifine.config;

import lombok.Getter;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.mod.entrypoint.EventBusPolicy;
import org.lwjgl.input.Keyboard;

import static net.modificationstation.stationapi.api.client.option.OptionFactory.create;

@Entrypoint(eventBus = @EventBusPolicy(registerInstance = false))
public class OptionsListener {

    @EventListener
    private static void registerKeyBindings(KeyBindingRegisterEvent event) {
        event.keyBindings.add(OF_KEY_BIND_ZOOM = new KeyBinding("Zoom", Keyboard.KEY_C));
        FOG_FANCY = create("FOG_FANCY", "Fog", false, false);
        FOG_START = create("FOG_FANCY", "Fog Start", false, false);
        MIPMAP_LEVEL = create("MIPMAP_LEVEL", "Mipmap Level", false, false);
        MIPMAP_TYPE = create("MIPMAP_TYPE", "Mipmap Type", false, false);
        LOAD_FAR = create("LOAD_FAR", "Load Far", false, false);
        PRELOADED_CHUNKS = create("PRELOADED_CHUNKS", "Preloaded Chunks", false, false);
        SMOOTH_FPS = create("SMOOTH_FPS", "Smooth FPS", false, false);
        BRIGHTNESS = create("BRIGHTNESS", "Brightness", true, false);
        CLOUDS = create("CLOUDS", "Clouds", false, false);
        CLOUD_HEIGHT = create("CLOUD_HEIGHT", "Cloud Height", true, false);
        TREES = create("TREES", "Trees", false, false);
        GRASS = create("GRASS", "Grass", false, false);
        RAIN = create("RAIN", "Rain & Snow", false, false);
        WATER = create("WATER", "Water", false, false);
        ANIMATED_WATER = create("ANIMATED_WATER", "Water Animated", false, false);
        ANIMATED_LAVA = create("ANIMATED_LAVA", "Lava Animated", false, false);
        ANIMATED_FIRE = create("ANIMATED_FIRE", "Fire Animated", false, false);
        ANIMATED_PORTAL = create("ANIMATED_PORTAL", "Portal Animated", false, false);
        AO_LEVEL = create("AO_LEVEL", "Smooth Lighting", true, false);
        FAST_DEBUG_INFO = create("FAST_DEBUG_INFO", "Fast Debug Info", false, false);
        AUTOSAVE_TICKS = create("AUTOSAVE_TICKS", "Autosave", false, false);
        BETTER_GRASS = create("BETTER_GRASS", "Better Grass", false, false);
        ANIMATED_REDSTONE = create("ANIMATED_REDSTONE", "Redstone Animated", false, false);
        ANIMATED_EXPLOSION = create("ANIMATED_EXPLOSION", "Explosion Animated", false, false);
        ANIMATED_FLAME = create("ANIMATED_FLAME", "Flame Animated", false, false);
        ANIMATED_SMOKE = create("ANIMATED_SMOKE", "Smoke Animated", false, false);
        WEATHER = create("WEATHER", "Weather", false, false);
        SKY = create("SKY", "Sky", false, false);
        STARS = create("STARS", "Stars", false, false);
        FAR_VIEW = create("FAR_VIEW", "Far View", false, false);
        CHUNK_UPDATES = create("CHUNK_UPDATES", "Chunk Updates", false, false);
        CHUNK_UPDATES_DYNAMIC = create("CHUNK_UPDATES_DYNAMIC", "Dynamic Updates", false, false);
        TIME = create("TIME", "Time", false, false);
        CLEAR_WATER = create("CLEAR_WATER", "Clear Water", false, false);
        SMOOTH_INPUT = create("SMOOTH_INPUT", "Smooth Input", false, false);
    }

    @Getter
    private static KeyBinding OF_KEY_BIND_ZOOM;
    @Getter
    private static Option
            FOG_FANCY,
            FOG_START,
            MIPMAP_LEVEL,
            MIPMAP_TYPE,
            LOAD_FAR,
            PRELOADED_CHUNKS,
            SMOOTH_FPS,
            BRIGHTNESS,
            CLOUDS,
            CLOUD_HEIGHT,
            TREES,
            GRASS,
            RAIN,
            WATER,
            ANIMATED_WATER,
            ANIMATED_LAVA,
            ANIMATED_FIRE,
            ANIMATED_PORTAL,
            AO_LEVEL,
            FAST_DEBUG_INFO,
            AUTOSAVE_TICKS,
            BETTER_GRASS,
            ANIMATED_REDSTONE,
            ANIMATED_EXPLOSION,
            ANIMATED_FLAME,
            ANIMATED_SMOKE,
            WEATHER,
            SKY,
            STARS,
            FAR_VIEW,
            CHUNK_UPDATES,
            CHUNK_UPDATES_DYNAMIC,
            TIME,
            CLEAR_WATER,
            SMOOTH_INPUT;
}