package net.mine_diver.fabrifine.config;

import net.minecraft.client.options.GameOptions;

public interface OFConfig {

    static OFConfig of(GameOptions gameOptions) {
        return (OFConfig) gameOptions;
    }

    boolean isOfFogFancy();

    float getOfFogStart();

    int getOfMipmapLevel();

    boolean isOfMipmapLinear();

    boolean isOfLoadFar();

    int getOfPreloadedChunks();

    boolean isOfOcclusionFancy();

    boolean isOfSmoothFps();

    boolean isOfSmoothInput();

    float getOfBrightness();

    float getOfAoLevel();

    int getOfClouds();

    float getOfCloudsHeight();

    int getOfTrees();

    int getOfGrass();

    int getOfRain();

    int getOfWater();

    int getOfBetterGrass();

    int getOfAutoSaveTicks();

    boolean isOfFastDebugInfo();

    boolean isOfWeather();

    boolean isOfSky();

    boolean isOfStars();

    int getOfChunkUpdates();

    boolean isOfChunkUpdatesDynamic();

    boolean isOfFarView();

    int getOfTime();

    boolean isOfClearWater();

    int getOfAnimatedWater();

    int getOfAnimatedLava();

    boolean isOfAnimatedFire();

    boolean isOfAnimatedPortal();

    boolean isOfAnimatedRedstone();

    boolean isOfAnimatedExplosion();

    boolean isOfAnimatedFlame();

    boolean isOfAnimatedSmoke();

    void setOfFogFancy(boolean ofFogFancy);

    void setOfFogStart(float ofFogStart);

    void setOfMipmapLevel(int ofMipmapLevel);

    void setOfMipmapLinear(boolean ofMipmapLinear);

    void setOfLoadFar(boolean ofLoadFar);

    void setOfPreloadedChunks(int ofPreloadedChunks);

    void setOfOcclusionFancy(boolean ofOcclusionFancy);

    void setOfSmoothFps(boolean ofSmoothFps);

    void setOfSmoothInput(boolean ofSmoothInput);

    void setOfBrightness(float ofBrightness);

    void setOfAoLevel(float ofAoLevel);

    void setOfClouds(int ofClouds);

    void setOfCloudsHeight(float ofCloudsHeight);

    void setOfTrees(int ofTrees);

    void setOfGrass(int ofGrass);

    void setOfRain(int ofRain);

    void setOfWater(int ofWater);

    void setOfBetterGrass(int ofBetterGrass);

    void setOfAutoSaveTicks(int ofAutoSaveTicks);

    void setOfFastDebugInfo(boolean ofFastDebugInfo);

    void setOfWeather(boolean ofWeather);

    void setOfSky(boolean ofSky);

    void setOfStars(boolean ofStars);

    void setOfChunkUpdates(int ofChunkUpdates);

    void setOfChunkUpdatesDynamic(boolean ofChunkUpdatesDynamic);

    void setOfFarView(boolean ofFarView);

    void setOfTime(int ofTime);

    void setOfClearWater(boolean ofClearWater);

    void setOfAnimatedWater(int ofAnimatedWater);

    void setOfAnimatedLava(int ofAnimatedLava);

    void setOfAnimatedFire(boolean ofAnimatedFire);

    void setOfAnimatedPortal(boolean ofAnimatedPortal);

    void setOfAnimatedRedstone(boolean ofAnimatedRedstone);

    void setOfAnimatedExplosion(boolean ofAnimatedExplosion);

    void setOfAnimatedFlame(boolean ofAnimatedFlame);

    void setOfAnimatedSmoke(boolean ofAnimatedSmoke);
}
