package net.mine_diver.fabrifine.render;

import net.mine_diver.fabrifine.config.Config;
import net.mine_diver.fabrifine.mixin.TessellatorAccessor;
import net.minecraft.class_66;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.Pbuffer;

import java.util.*;

public class UpdateThread extends Thread {

    private Pbuffer pbuffer = null;
    private Object lock = new Object();
    private List updateList = new LinkedList();
    private List updatedList = new LinkedList();
    private int updateCount = 0;

    private Tessellator mainTessellator = Tessellator.INSTANCE;
    private Tessellator threadTessellator = TessellatorAccessor.invokeCor(262144);

    private boolean working = false;
    private class_66 currentRenderer = null;

    private boolean canWork = false;
    private boolean canWorkToEndOfUpdate = false;
    private static final int MAX_UPDATE_CAPACITY = 10;

    public UpdateThread(Pbuffer pbuffer) {
        super("UpdateThread");
        this.pbuffer = pbuffer;
    }

    public void addRendererToUpdate(class_66 wr, boolean first) {
        synchronized (this.lock) {
            if (OFMeshRenderer.of(wr).isUpdating()) {
                throw new IllegalArgumentException("Renderer already updating");
            }

            if (first)
                this.updateList.add(0, wr);
            else {
                this.updateList.add(wr);
            }
            OFMeshRenderer.of(wr).setUpdating(true);

            this.lock.notifyAll();
        }
    }

    private class_66 getRendererToUpdate() {
        synchronized (this.lock) {
            while (this.updateList.size() <= 0) {
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {}
            }

            this.currentRenderer = ((class_66)this.updateList.remove(0));

            this.lock.notifyAll();

            return this.currentRenderer;
        }
    }

    public boolean hasWorkToDo() {
        synchronized (this.lock) {
            if (this.updateList.size() > 0) {
                return true;
            }
            if (this.currentRenderer != null) {
                return true;
            }
            return this.working;
        }
    }

    public int getUpdateCapacity() {
        synchronized (this.lock) {
            if (this.updateList.size() > 10)
                return 0;
            return 10 - this.updateList.size();
        }
    }

    private void rendererUpdated(class_66 wr) {
        synchronized (this.lock) {
            this.updatedList.add(wr);

            this.updateCount += 1;
            this.currentRenderer = null;
            this.working = false;

            this.lock.notifyAll();
        }
    }

    private void finishUpdatedRenderers() {
        synchronized (this.lock) {
            for (int i = 0; i < this.updatedList.size(); i++) {
                class_66 wr = (class_66) this.updatedList.get(i);
                OFMeshRenderer.of(wr).finishUpdate();

                OFMeshRenderer.of(wr).setUpdating(false);
            }

            this.updatedList.clear();
        }
    }

    public void run() {
        try {
            this.pbuffer.makeCurrent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IUpdateListener updateListener = () -> {
            TessellatorAccessor.setINSTANCE(UpdateThread.this.mainTessellator);
            UpdateThread.this.checkCanWork();
            TessellatorAccessor.setINSTANCE(UpdateThread.this.threadTessellator);
        };
        while (!Thread.interrupted()) {
            try {
                class_66 wr = getRendererToUpdate();

                checkCanWork();
                try {
                    TessellatorAccessor.setINSTANCE(this.threadTessellator);
                    OFMeshRenderer.of(wr).updateRenderer(updateListener);
                } finally {
                    TessellatorAccessor.setINSTANCE(this.mainTessellator);
                }

                rendererUpdated(wr);
            } catch (Exception e) {
                e.printStackTrace();

                if (this.currentRenderer != null) {
                    OFMeshRenderer.of(this.currentRenderer).setUpdating(false);
                    this.currentRenderer.field_249 = true;
                }

                this.currentRenderer = null;
                this.working = false;
            }
        }
    }

    public void pause() {
        synchronized (this.lock) {
            this.canWork = false;
            this.canWorkToEndOfUpdate = false;

            this.lock.notifyAll();

            while (this.working) {
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {}

            }

            finishUpdatedRenderers();
        }
    }

    public void unpause() {
        synchronized (this.lock) {
            if (this.working)
                Config.dbg("UpdateThread still working in unpause()!!!");
            this.canWork = true;
            this.canWorkToEndOfUpdate = false;

            this.lock.notifyAll();
        }
    }

    public void unpauseToEndOfUpdate() {
        synchronized (this.lock) {
            if (this.working) {
                Config.dbg("UpdateThread still working in unpause()!!!");
            }
            while (this.currentRenderer != null) {
                this.canWork = false;
                this.canWorkToEndOfUpdate = true;

                this.lock.notifyAll();
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {}
            }

            pause();
        }
    }

    private void checkCanWork() {
        Thread.yield();

        synchronized (this.lock) {
            while (!this.canWork) {
                if ((this.canWorkToEndOfUpdate) && (this.currentRenderer != null)) {
                    break;
                }
                this.working = false;

                this.lock.notifyAll();
                try {
                    this.lock.wait();
                } catch (InterruptedException ignored) {}
            }

            this.working = true;

            this.lock.notifyAll();
        }
    }

    public void clearAllUpdates() {
        synchronized (this.lock) {
            unpauseToEndOfUpdate();
            this.updateList.clear();

            this.lock.notifyAll();
        }
    }

    public int getPendingUpdatesCount() {
        synchronized (this.lock) {
            int count = this.updateList.size();
            if (this.currentRenderer != null) {
                count++;
            }
            return count;
        }
    }

    public int resetUpdateCount() {
        synchronized (this.lock) {
            int count = this.updateCount;
            this.updateCount = 0;

            return count;
        }
    }
}