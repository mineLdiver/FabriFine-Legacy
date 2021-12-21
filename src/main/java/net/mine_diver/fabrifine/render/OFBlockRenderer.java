package net.mine_diver.fabrifine.render;

public class OFBlockRenderer {

    public static float[][] redstoneColors;
    static {
        redstoneColors = new float[16][];
        for (int i = 0; i < redstoneColors.length; ++i) {
            float f = i / 15.0f;
            final float f2 = f * 0.6f + 0.4f;
            if (i == 0) {
                f = 0.0f;
            }
            float f3 = f * f * 0.7f - 0.5f;
            float f4 = f * f * 0.6f - 0.7f;
            if (f3 < 0.0f) {
                f3 = 0.0f;
            }
            if (f4 < 0.0f) {
                f4 = 0.0f;
            }
            redstoneColors[i] = new float[] { f2, f3, f4 };
        }
    }
}
