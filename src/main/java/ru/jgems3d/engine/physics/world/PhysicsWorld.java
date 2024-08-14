package ru.jgems3d.engine.physics.world;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.api_bridge.events.APIEventsPusher;
import ru.jgems3d.engine.graphics.opengl.rendering.debug.GlobalRenderDebugConstants;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine_api.events.bus.Events;

import java.util.*;

public final class PhysicsWorld implements IWorld {
    private final WorldObjectsContainer worldObjectsContainer;
    private Graph mapNavGraph;
    private int ticks;

    public PhysicsWorld() {
        this.mapNavGraph = null;
        this.worldObjectsContainer = new WorldObjectsContainer(this);
    }

    public void onWorldStart() {
        APIEventsPusher.pushEvent(new Events.PhysWorldStart(Events.Stage.PRE, this));
        this.ticks = 0;
        APIEventsPusher.pushEvent(new Events.PhysWorldStart(Events.Stage.POST, this));
    }

    public void onWorldUpdate() {
        if (!APIEventsPusher.pushEvent(new Events.PhysWorldTickPre(this)).isCancelled()) {
            this.getWorldObjectsContainer().onUpdate();
            this.ticks += 1;
        }
        APIEventsPusher.pushEvent(new Events.PhysWorldTickPost(this));
    }

    public void onWorldEnd() {
        APIEventsPusher.pushEvent(new Events.PhysWorldEnd(Events.Stage.PRE, this));
        this.removeNavGraph();
        this.cleanUp();
        APIEventsPusher.pushEvent(new Events.PhysWorldEnd(Events.Stage.POST, this));
    }

    public void killItems() {
        this.getWorldObjectsContainer().killItems();
    }

    public void cleanUp() {
        this.getWorldObjectsContainer().cleanUp();
    }

    public void addItem(IWorldObject worldObject) {
        this.getWorldObjectsContainer().addObjectInWorld(worldObject);
        APIEventsPusher.pushEvent(new Events.ItemSpawnedInPhysicsWorld(worldObject));
    }

    public void removeItem(IWorldObject worldObject) {
        APIEventsPusher.pushEvent(new Events.ItemDestroyedInPhysicsWorld(worldObject));
        this.getWorldObjectsContainer().removeObjectFromWorld(worldObject);
    }

    public void setMapNavGraph(Graph mapNavGraph) {
        synchronized (this) {
            this.mapNavGraph = mapNavGraph;
        }
        if (JGems3D.DEBUG_MODE) {
            if (mapNavGraph != null) {
                GlobalRenderDebugConstants.linesDebugDraw.constructNavMeshFloatBuffer(mapNavGraph);
            }
        }
    }

    public void removeNavGraph() {
        if (JGems3D.DEBUG_MODE) {
            if (this.mapNavGraph != null) {
                GlobalRenderDebugConstants.linesDebugDraw.destroyNavMeshFloatBuffer();
            }
        }
        synchronized (this) {
            this.mapNavGraph = null;
        }
    }

    public Graph getMapNavGraph() {
        synchronized (this) {
            return this.mapNavGraph;
        }
    }

    public WorldItem getItemByID(int id) {
        Optional<IWorldObject> worldItem = this.getWorldObjectsContainer().getWorldObjects().stream().filter(e -> (e instanceof WorldItem) && ((WorldItem) e).getItemId() == id).findFirst();
        IWorldObject worldObject = worldItem.orElse(null);
        if (worldObject == null) {
            return null;
        }
        return (WorldItem) worldObject;
    }

    public WorldObjectsContainer getWorldObjectsContainer() {
        return this.worldObjectsContainer;
    }

    public int countItems() {
        return this.getWorldObjectsContainer().getWorldObjects().size();
    }

    public int getTicks() {
        return this.ticks;
    }

    public DynamicsSystem getDynamics() {
        return JGems3D.get().getPhysicThreadManager().getPhysicsTimer().getDynamicsSystem();
    }
}
