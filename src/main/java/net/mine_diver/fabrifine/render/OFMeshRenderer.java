package net.mine_diver.fabrifine.render;

import net.minecraft.class_66;

public interface OFMeshRenderer {

    static OFMeshRenderer of(class_66 meshRenderer) {
        return (OFMeshRenderer) meshRenderer;
    }

    void updateRenderer(final IUpdateListener updateListener);

    void finishUpdate();

    boolean isVisibleFromPosition();

    double getVisibleFromX();

    double getVisibleFromY();

    double getVisibleFromZ();

    boolean isInFrustrumFully();

    boolean isUpdating();

    void setVisibleFromPosition(boolean isVisibleFromPosition);

    void setVisibleFromX(double visibleFromX);

    void setVisibleFromY(double visibleFromY);

    void setVisibleFromZ(double visibleFromZ);

    void setInFrustrumFully(boolean isInFrustrumFully);

    void setUpdating(boolean isUpdating);
}
