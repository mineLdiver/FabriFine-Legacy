package net.mine_diver.fabrifine.config;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import net.mine_diver.fabrifine.render.UpdateThread;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import net.minecraft.level.BlockView;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;

public class Config {

    private static GameOptions gameSettings;
    private static Minecraft minecraft;
    private static float[] lightLevels;
    private static int iconWidthTerrain;
    private static int iconWidthItems;
    private static Map<String, Class<?>> foundClassesMap;
    private static boolean fontRendererUpdated;
    private static UpdateThread updateThread;
    private static File logFile;
    public static final Boolean DEF_FOG_FANCY;
    public static final Float DEF_FOG_START;
    public static final Boolean DEF_OPTIMIZE_RENDER_DISTANCE;
    public static final Boolean DEF_OCCLUSION_ENABLED;
    public static final Integer DEF_MIPMAP_LEVEL;
    public static final Integer DEF_MIPMAP_TYPE;
    public static final Float DEF_ALPHA_FUNC_LEVEL;
    public static final Boolean DEF_LOAD_CHUNKS_FAR;
    public static final Integer DEF_PRELOADED_CHUNKS;
    public static final Integer DEF_CHUNKS_LIMIT;
    public static final Integer DEF_UPDATES_PER_FRAME;
    public static final Boolean DEF_DYNAMIC_UPDATES;
    
    private Config() {
    }
    
    private static String getVersion() {
        return "OptiFine_1.7.3_HD_MT_G";
    }
    
    private static void checkOpenGlCaps() {
        log("");
        log(getVersion());
        log("" + new Date());
        log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        log("LWJGL: " + Sys.getVersion());
        log("OpenGL: " + GL11.glGetString(7937) + " version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936));
        final int ver = getOpenGlVersion();
        final String verStr = "" + ver / 10 + "." + ver % 10;
        log("OpenGL Version: " + verStr);
        if (!GLContext.getCapabilities().OpenGL12)
            log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
        if (!GLContext.getCapabilities().GL_NV_fog_distance)
            log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
        if (!GLContext.getCapabilities().GL_ARB_occlusion_query)
            log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
    }
    
    public static boolean isFancyFogAvailable() {
        return GLContext.getCapabilities().GL_NV_fog_distance;
    }
    
    public static boolean isOcclusionAvailable() {
        return GLContext.getCapabilities().GL_ARB_occlusion_query;
    }
    
    private static int getOpenGlVersion() {
        if (!GLContext.getCapabilities().OpenGL11) return 10;
        if (!GLContext.getCapabilities().OpenGL12) return 11;
        if (!GLContext.getCapabilities().OpenGL13) return 12;
        if (!GLContext.getCapabilities().OpenGL14) return 13;
        if (!GLContext.getCapabilities().OpenGL15) return 14;
        if (!GLContext.getCapabilities().OpenGL20) return 15;
        if (!GLContext.getCapabilities().OpenGL21) return 20;
        if (!GLContext.getCapabilities().OpenGL30) return 21;
        if (!GLContext.getCapabilities().OpenGL31) return 30;
        if (!GLContext.getCapabilities().OpenGL32) return 31;
        if (!GLContext.getCapabilities().OpenGL33) return 32;
        if (!GLContext.getCapabilities().OpenGL40) return 33;
        return 40;
    }
    
    public static void setGameSettings(final GameOptions options) {
        if (Config.gameSettings == null) checkOpenGlCaps();
        Config.gameSettings = options;
    }
    
    public static boolean isUseMipmaps() {
        final int mipmapLevel = getMipmapLevel();
        return mipmapLevel > 0;
    }
    
    public static int getMipmapLevel() {
        if (Config.gameSettings == null) return Config.DEF_MIPMAP_LEVEL;
        return OFConfig.of(Config.gameSettings).getOfMipmapLevel();
    }
    
    public static int getMipmapType() {
        if (Config.gameSettings == null) return Config.DEF_MIPMAP_TYPE;
        if (OFConfig.of(Config.gameSettings).isOfMipmapLinear()) return 9986;
        return 9984;
    }
    
    public static boolean isUseAlphaFunc() {
        final float alphaFuncLevel = getAlphaFuncLevel();
        return alphaFuncLevel > Config.DEF_ALPHA_FUNC_LEVEL + 1.0E-5f;
    }
    
    public static float getAlphaFuncLevel() {
        return Config.DEF_ALPHA_FUNC_LEVEL;
    }
    
    public static boolean isFogFancy() {
        return GLContext.getCapabilities().GL_NV_fog_distance && Config.gameSettings != null && OFConfig.of(Config.gameSettings).isOfFogFancy();
    }
    
    public static float getFogStart() {
        if (Config.gameSettings == null) return Config.DEF_FOG_START;
        return OFConfig.of(Config.gameSettings).getOfFogStart();
    }
    
    public static boolean isOcclusionEnabled() {
        if (Config.gameSettings == null) return Config.DEF_OCCLUSION_ENABLED;
        return Config.gameSettings.advancedOpengl;
    }
    
    public static boolean isOcclusionFancy() {
        return isOcclusionEnabled() && Config.gameSettings != null && OFConfig.of(Config.gameSettings).isOfOcclusionFancy();
    }
    
    public static boolean isLoadChunksFar() {
        if (Config.gameSettings == null) return Config.DEF_LOAD_CHUNKS_FAR;
        return OFConfig.of(Config.gameSettings).isOfLoadFar();
    }
    
    public static int getPreloadedChunks() {
        if (Config.gameSettings == null) return Config.DEF_PRELOADED_CHUNKS;
        return OFConfig.of(Config.gameSettings).getOfPreloadedChunks();
    }
    
    public static void dbg(final String s) {
        System.out.println(s);
    }
    
    public static void log(final String s) {
        dbg(s);
        try {
            if (Config.logFile == null) {
                //noinspection ResultOfMethodCallIgnored
                (Config.logFile = new File(Minecraft.getGameDirectory(), "optifog.log")).delete();
                //noinspection ResultOfMethodCallIgnored
                Config.logFile.createNewFile();
            }
            final FileOutputStream fos = new FileOutputStream(Config.logFile, true);
            try (OutputStreamWriter logFileWriter = new OutputStreamWriter(fos, StandardCharsets.US_ASCII)) {
                logFileWriter.write(s);
                logFileWriter.write("\n");
                logFileWriter.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static int getUpdatesPerFrame() {
        if (Config.gameSettings != null) return OFConfig.of(Config.gameSettings).getOfChunkUpdates();
        return 1;
    }
    
    public static boolean isDynamicUpdates() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfChunkUpdatesDynamic();
    }
    
    public static boolean isRainFancy() {
        if (OFConfig.of(Config.gameSettings).getOfRain() == 0) return Config.gameSettings.fancyGraphics;
        return OFConfig.of(Config.gameSettings).getOfRain() == 2;
    }
    
    public static boolean isWaterFancy() {
        if (OFConfig.of(Config.gameSettings).getOfWater() == 0) return Config.gameSettings.fancyGraphics;
        return OFConfig.of(Config.gameSettings).getOfWater() == 2;
    }
    
    public static boolean isRainOff() {
        return OFConfig.of(Config.gameSettings).getOfRain() == 3;
    }
    
    public static boolean isCloudsFancy() {
        if (OFConfig.of(Config.gameSettings).getOfClouds() == 0) return Config.gameSettings.fancyGraphics;
        return OFConfig.of(Config.gameSettings).getOfClouds() == 2;
    }
    
    public static boolean isCloudsOff() {
        return OFConfig.of(Config.gameSettings).getOfClouds() == 3;
    }
    
    public static boolean isTreesFancy() {
        if (OFConfig.of(Config.gameSettings).getOfTrees() == 0) return Config.gameSettings.fancyGraphics;
        return OFConfig.of(Config.gameSettings).getOfTrees() == 2;
    }
    
    public static boolean isGrassFancy() {
        if (OFConfig.of(Config.gameSettings).getOfGrass() == 0) return Config.gameSettings.fancyGraphics;
        return OFConfig.of(Config.gameSettings).getOfGrass() == 2;
    }
    
    public static int limit(final int val, final int min, final int max) {
        return Ints.constrainToRange(val, min, max);
    }
    
    public static float limit(final float val, final float min, final float max) {
        return Floats.constrainToRange(val, min, max);
    }
    
    public static boolean isAnimatedWater() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).getOfAnimatedWater() != 2;
    }
    
    public static boolean isGeneratedWater() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).getOfAnimatedWater() == 1;
    }
    
    public static boolean isAnimatedPortal() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfAnimatedPortal();
    }
    
    public static boolean isAnimatedLava() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).getOfAnimatedLava() != 2;
    }
    
    public static boolean isGeneratedLava() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).getOfAnimatedLava() == 1;
    }
    
    public static boolean isAnimatedFire() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfAnimatedFire();
    }
    
    public static boolean isAnimatedRedstone() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfAnimatedRedstone();
    }
    
    public static boolean isAnimatedExplosion() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfAnimatedExplosion();
    }
    
    public static boolean isAnimatedFlame() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfAnimatedFlame();
    }
    
    public static boolean isAnimatedSmoke() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfAnimatedSmoke();
    }
    
    public static float getAmbientOcclusionLevel() {
        if (Config.gameSettings != null) return OFConfig.of(Config.gameSettings).getOfAoLevel();
        return 0.0f;
    }
    
    public static float fixAoLight(final float light, final float defLight) {
        if (Config.lightLevels == null) return light;
        final float level_0 = Config.lightLevels[0];
        final float level_2 = Config.lightLevels[1];
        if (light > level_0) return light;
        if (defLight <= level_2) return light;
        final float mul = 1.0f - getAmbientOcclusionLevel();
        return light + (defLight - light) * mul;
    }
    
    public static void setLightLevels(final float[] levels) {
        Config.lightLevels = levels;
    }
    
    public static boolean callBoolean(final String className, final String methodName, final Object[] params) {
        try {
            final Class<?> cls = getClass(className);
            if (cls == null) return false;
            final Method method = getMethod(cls, methodName, params);
            if (method == null) return false;
            return (Boolean)method.invoke(null, params);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void callVoid(final String className, final String methodName, final Object[] params) {
        try {
            final Class<?> cls = getClass(className);
            if (cls == null) return;
            final Method method = getMethod(cls, methodName, params);
            if (method == null) return;
            method.invoke(null, params);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static void callVoid(final Object obj, final String methodName, final Object[] params) {
        try {
            if (obj == null) return;
            final Class<?> cls = obj.getClass();
            if (cls == null) return;
            final Method method = getMethod(cls, methodName, params);
            if (method == null) return;
            method.invoke(obj, params);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    public static Object getFieldValue(final String className, final String fieldName) {
        try {
            final Class<?> cls = getClass(className);
            if (cls == null) return null;
            return cls.getDeclaredField(fieldName).get(null);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Object getFieldValue(final Object obj, final String fieldName) {
        try {
            if (obj == null) return null;
            final Class<?> cls = obj.getClass();
            if (cls == null) return null;
            return cls.getField(fieldName).get(obj);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static <T> Method getMethod(final Class<T> cls, final String methodName, final Object[] params) {
        final Method[] methods = cls.getMethods();
        for (final Method m : methods)
            if (m.getName().equals(methodName)) if (m.getParameterTypes().length == params.length) return m;
        dbg("No method found for: " + cls.getName() + "." + methodName + "(" + arrayToString(params) + ")");
        return null;
    }
    
    public static String arrayToString(final Object[] arr) {
        final StringBuilder buf = new StringBuilder(arr.length * 5);
        for (int i = 0; i < arr.length; ++i) {
            final Object obj = arr[i];
            if (i > 0) buf.append(", ");
            buf.append(obj);
        }
        return buf.toString();
    }
    
    public static boolean hasModLoader() {
        final Class<?> cls = getClass("ModLoader");
        return cls != null;
    }
    
    private static <T> Class<T> getClass(final String className) {
        //noinspection unchecked
        Class<T> cls = (Class<T>) Config.foundClassesMap.get(className);
        if (cls != null) return cls;
        if (Config.foundClassesMap.containsKey(className)) return null;
        try {
            //noinspection unchecked
            cls = (Class<T>) Class.forName(className);
        }
        catch (ClassNotFoundException e2) {
            log("Class not found: " + className);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        Config.foundClassesMap.put(className, cls);
        return cls;
    }
    
    public static void setMinecraft(final Minecraft mc) {
        Config.minecraft = mc;
    }
    
    public static Minecraft getMinecraft() {
        return Config.minecraft;
    }
    
    public static int getIconWidthTerrain() {
        return Config.iconWidthTerrain;
    }
    
    public static int getIconWidthItems() {
        return Config.iconWidthItems;
    }
    
    public static void setIconWidthItems(final int iconWidth) {
        Config.iconWidthItems = iconWidth;
    }
    
    public static void setIconWidthTerrain(final int iconWidth) {
        Config.iconWidthTerrain = iconWidth;
    }
    
    public static int getMaxDynamicTileWidth() {
        return 64;
    }
    
    public static int getSideGrassTexture(final BlockView blockAccess, int x, int y, int z, final int side) {
        if (!isBetterGrass()) return 3;
        if (isBetterGrassFancy()) {
            --y;
            switch (side) {
                case 2: {
                    --z;
                    break;
                }
                case 3: {
                    ++z;
                    break;
                }
                case 4: {
                    --x;
                    break;
                }
                case 5: {
                    ++x;
                    break;
                }
            }
            final int blockId = blockAccess.getTileId(x, y, z);
            if (blockId != 2) return 3;
        }
        return 0;
    }
    
    public static int getSideSnowGrassTexture(final BlockView blockAccess, int x, final int y, int z, final int side) {
        if (!isBetterGrass()) return 68;
        if (isBetterGrassFancy()) {
            switch (side) {
                case 2: {
                    --z;
                    break;
                }
                case 3: {
                    ++z;
                    break;
                }
                case 4: {
                    --x;
                    break;
                }
                case 5: {
                    ++x;
                    break;
                }
            }
            final int blockId = blockAccess.getTileId(x, y, z);
            if (blockId != 78 && blockId != 80) return 68;
        }
        return 66;
    }
    
    public static boolean isBetterGrass() {
        return Config.gameSettings != null && OFConfig.of(Config.gameSettings).getOfBetterGrass() != 3;
    }
    
    public static boolean isBetterGrassFancy() {
        return Config.gameSettings != null && OFConfig.of(Config.gameSettings).getOfBetterGrass() == 2;
    }
    
    public static boolean isFontRendererUpdated() {
        return Config.fontRendererUpdated;
    }
    
    public static void setFontRendererUpdated(final boolean fontRendererUpdated) {
        Config.fontRendererUpdated = fontRendererUpdated;
    }
    
    public static boolean isWeatherEnabled() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfWeather();
    }
    
    public static boolean isSkyEnabled() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfSky();
    }
    
    public static boolean isStarsEnabled() {
        return Config.gameSettings == null || OFConfig.of(Config.gameSettings).isOfStars();
    }
    
    public static UpdateThread getUpdateThread() {
        return Config.updateThread;
    }
    
    public static UpdateThread createUpdateThread(final Drawable displayDrawable) {
        if (Config.updateThread != null) throw new IllegalStateException("UpdateThread is already existing");
        try {
            final Pbuffer pbuffer = new Pbuffer(1, 1, new PixelFormat(), displayDrawable);
            (Config.updateThread = new UpdateThread(pbuffer)).setPriority(1);
            Config.updateThread.start();
            return Config.updateThread;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean isUpdateThread() {
        return Thread.currentThread() == Config.updateThread;
    }
    
    public static boolean isBackgroundChunkLoading() {
        return true;
    }
    
    public static boolean isFarView() {
        return Config.gameSettings != null && OFConfig.of(Config.gameSettings).isOfFarView();
    }
    
    public static void sleep(final long ms) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Thread.currentThread();
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isTimeDayOnly() {
        return Config.gameSettings != null && OFConfig.of(Config.gameSettings).getOfTime() == 1;
    }
    
    public static boolean isTimeNightOnly() {
        return Config.gameSettings != null && OFConfig.of(Config.gameSettings).getOfTime() == 2;
    }
    
    public static boolean isClearWater() {
        return Config.gameSettings != null && OFConfig.of(Config.gameSettings).isOfClearWater();
    }
    
    static {
        Config.gameSettings = null;
        Config.minecraft = null;
        Config.lightLevels = null;
        Config.iconWidthTerrain = 16;
        Config.iconWidthItems = 16;
        Config.foundClassesMap = new HashMap<>();
        Config.fontRendererUpdated = false;
        Config.updateThread = null;
        Config.logFile = null;
        DEF_FOG_FANCY = true;
        DEF_FOG_START = 0.2f;
        DEF_OPTIMIZE_RENDER_DISTANCE = false;
        DEF_OCCLUSION_ENABLED = false;
        DEF_MIPMAP_LEVEL = 0;
        DEF_MIPMAP_TYPE = 9984;
        DEF_ALPHA_FUNC_LEVEL = 0.1f;
        DEF_LOAD_CHUNKS_FAR = false;
        DEF_PRELOADED_CHUNKS = 0;
        DEF_CHUNKS_LIMIT = 25;
        DEF_UPDATES_PER_FRAME = 3;
        DEF_DYNAMIC_UPDATES = false;
    }
}
