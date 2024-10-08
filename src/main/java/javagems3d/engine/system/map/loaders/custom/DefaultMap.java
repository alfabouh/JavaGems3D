/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.map.loaders.custom;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.engine.graphics.opengl.world.SceneWorld;
import javagems3d.engine.physics.entities.BtStaticMeshBody;
import javagems3d.engine.physics.entities.ai.CubeAI;
import javagems3d.engine.physics.entities.collectabes.EntityCollectableItem;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.physics.world.triggers.Zone;
import javagems3d.engine.physics.world.triggers.liquids.Water;
import javagems3d.engine.system.graph.Graph;
import javagems3d.engine.system.inventory.items.ItemZippo;
import javagems3d.engine.system.map.MapInfo;
import javagems3d.engine.system.map.loaders.IMapLoader;
import javagems3d.engine.system.resources.manager.GameResources;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;
import javagems3d.engine.system.service.path.JGemsPath;
import javagems3d.toolbox.map_sys.save.objects.MapProperties;
import javagems3d.toolbox.map_sys.save.objects.map_prop.FogProp;
import javagems3d.toolbox.map_sys.save.objects.map_prop.SkyProp;

public class DefaultMap implements IMapLoader {
    public DefaultMap() {
    }

    @Override
    public void createMap(GameResources globalResources, GameResources localResources, PhysicsWorld world, SceneWorld sceneWorld) {
        world.setMapNavGraph(Graph.readFromFile(new JGemsPath("/assets/jgems/nav.mesh")));

        BtStaticMeshBody worldModeledBrush = (BtStaticMeshBody) new BtStaticMeshBody(JGemsResourceManager.globalModelAssets.ground2, world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
        JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new RenderEntityData(JGemsResourceManager.globalRenderDataAssets.ground, JGemsResourceManager.globalModelAssets.ground2));
        worldModeledBrush.setPosition(new Vector3f(0, -5, 0));

        Water water = new Water(new Zone(new Vector3f(14.0f, -10.0f, 10.0f), new Vector3f(20.0f, 8.0f, 18.0f)));
        JGemsHelper.WORLD.addLiquid(water, JGemsResourceManager.globalRenderDataAssets.water);

        CubeAI cubeAI = new CubeAI(world, new Vector3f(0.0f), "grass");
        JGemsHelper.WORLD.addItemInWorld(cubeAI, new RenderEntityData(JGemsResourceManager.globalRenderDataAssets.entityCube, JGemsResourceManager.globalModelAssets.cube));

        EntityCollectableItem collectableItem = new EntityCollectableItem(world, new ItemZippo(), new Vector3f(3.0f, -4.5f, 0.0f), "zippo");
        JGemsHelper.WORLD.addItemInWorld(collectableItem, JGemsResourceManager.globalRenderDataAssets.zippo_world);
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
    }

    @Override
    public void preLoad(PhysicsWorld world, SceneWorld sceneWorld) {
    }

    @Override
    public @NotNull MapInfo getLevelInfo() {
        return new MapInfo(new MapProperties("default", new SkyProp(), new FogProp()));
    }
}
