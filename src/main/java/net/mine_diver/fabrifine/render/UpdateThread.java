package net.mine_diver.fabrifine.render;

import net.mine_diver.fabrifine.config.Config;
import net.mine_diver.fabrifine.mixin.TessellatorAccessor;
import net.minecraft.class_66;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.Pbuffer;

import java.util.*;

public class UpdateThread extends Thread {
    private final Pbuffer pbuffer;
    private final Object lock;
    private final List<class_66> updateList;
    private final List<class_66> updatedList;
    private int updateCount;
    private final Tessellator mainTessellator;
    private final Tessellator threadTessellator;
    private boolean working;
    private class_66 currentRenderer;
    private boolean canWork;
    private boolean canWorkToEndOfUpdate;
    private static final int MAX_UPDATE_CAPACITY = 10;
    
    public UpdateThread(final Pbuffer pbuffer) {
        super("UpdateThread");
        this.lock = new Object();
        this.updateList = new LinkedList<>();
        this.updatedList = new LinkedList<>();
        this.updateCount = 0;
        this.mainTessellator = Tessellator.INSTANCE;
        this.threadTessellator = TessellatorAccessor.invokeCor(262144);
        this.working = false;
        this.currentRenderer = null;
        this.canWork = false;
        this.canWorkToEndOfUpdate = false;
        this.pbuffer = pbuffer;
    }
    
    public void addRendererToUpdate(final class_66 wr, final boolean first) {
        synchronized (this.lock) {
//            if (wr.isUpdating) {
//                throw new IllegalArgumentException("Renderer already updating");
//            }
            if (first) {
                this.updateList.add(0, wr);
            }
            else {
                this.updateList.add(wr);
            }
//            wr.isUpdating = true;
            this.lock.notifyAll();
        }
    }
    
    private class_66 getRendererToUpdate() {
        synchronized (this.lock) {
            while (this.updateList.size() <= 0) {
                try {
                    this.lock.wait();
                }
                catch (InterruptedException ignored) {}
            }
            this.currentRenderer = this.updateList.remove(0);
            this.lock.notifyAll();
            return this.currentRenderer;
        }
    }
    
    public boolean hasWorkToDo() {
        synchronized (this.lock) {
            return this.updateList.size() > 0 || this.currentRenderer != null || this.working;
        }
    }
    
    public int getUpdateCapacity() {
        synchronized (this.lock) {
            if (this.updateList.size() > 10) {
                return 0;
            }
            return 10 - this.updateList.size();
        }
    }
    
    private void rendererUpdated(final class_66 wr) {
        synchronized (this.lock) {
            this.updatedList.add(wr);
            ++this.updateCount;
            this.currentRenderer = null;
            this.working = false;
            this.lock.notifyAll();
        }
    }
    
    private void finishUpdatedRenderers() {
        synchronized (this.lock) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < this.updatedList.size(); ++i) {
                final class_66 wr = this.updatedList.get(i);
//                wr.finishUpdate();
//                wr.isUpdating = false;
            }
            this.updatedList.clear();
        }
    }
    
    @Override
    public void run() {
        try {
            this.pbuffer.makeCurrent();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        final IUpdateListener updateListener = () -> {
            TessellatorAccessor.setINSTANCE(UpdateThread.this.mainTessellator);
            UpdateThread.this.checkCanWork();
            TessellatorAccessor.setINSTANCE(UpdateThread.this.threadTessellator);
        };
        while (!Thread.interrupted()) {
            try {
                final class_66 wr = this.getRendererToUpdate();
                this.checkCanWork();
                try {
                    TessellatorAccessor.setINSTANCE(this.threadTessellator);
//                    wr.updateRenderer(updateListener);
                }
                finally {
                    TessellatorAccessor.setINSTANCE(this.mainTessellator);
                }
                this.rendererUpdated(wr);
            }
            catch (Exception e2) {
                e2.printStackTrace();
                if (this.currentRenderer != null) {
//                    this.currentRenderer.isUpdating = false;
//                    this.currentRenderer.u = true;
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
                }
                catch (InterruptedException ignored) {}
            }
            this.finishUpdatedRenderers();
        }
    }
    
    public void unpause() {
        synchronized (this.lock) {
            if (this.working) {
                Config.dbg("UpdateThread still working in unpause()!!!");
            }
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
                }
                catch (InterruptedException ignored) {}
            }
            this.pause();
        }
    }
    
    private void checkCanWork() {
        Thread.yield();
        synchronized (this.lock) {
            while (true) {
                while (!this.canWork) {
                    if (this.canWorkToEndOfUpdate && this.currentRenderer != null) {
                        this.working = true;
                        this.lock.notifyAll();
                        return;
                    }
                    this.working = false;
                    this.lock.notifyAll();
                    try {
                        this.lock.wait();
                    }
                    catch (InterruptedException ignored) {}
                }
            }
        }
    }
    
    public void clearAllUpdates() {
        synchronized (this.lock) {
            this.unpauseToEndOfUpdate();
            this.updateList.clear();
            this.lock.notifyAll();
        }
    }
    
    public int getPendingUpdatesCount() {
        synchronized (this.lock) {
            int count = this.updateList.size();
            if (this.currentRenderer != null) {
                ++count;
            }
            return count;
        }
    }
    
    public int resetUpdateCount() {
        synchronized (this.lock) {
            final int count = this.updateCount;
            this.updateCount = 0;
            return count;
        }
    }
}
