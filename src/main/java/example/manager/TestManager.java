package example.manager;

import example.TestTBoxApp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.entities.BtDynamicMeshBody;
import ru.jgems3d.engine.physics.entities.BtStaticMeshBody;
import ru.jgems3d.engine.physics.entities.player.SimpleKinematicPlayer;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.core.player.IPlayerConstructor;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine_api.app.tbox.containers.TRenderContainer;
import ru.jgems3d.engine_api.configuration.AppConfiguration;
import ru.jgems3d.engine_api.manager.AppManager;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.map_table.object.ObjectCategory;

public class TestManager extends AppManager {
    public TestManager(@Nullable AppConfiguration appConfiguration) {
        super(appConfiguration);
    }

    @Override
    public void putIncomingObjectOnMap(SceneWorld sceneWorld, PhysicsWorld physicsWorld, GameResources localGameResources, String id, ObjectCategory objectCategory, AttributeContainer attributeContainer, TRenderContainer renderContainer) {
        try {
            Vector3f pos = attributeContainer.tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
            Vector3f rot = attributeContainer.tryGetValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
            Vector3f scale = attributeContainer.tryGetValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);

            MeshDataGroup meshDataGroup = localGameResources.createMesh(renderContainer.getPathToRenderModel());
            JGemsShaderManager shaderManager = localGameResources.getResource(renderContainer.getPathToRenderShader());

            RenderObjectData renderObjectData = new RenderObjectData(renderContainer.getRenderFabricClass().newInstance(), renderContainer.getSceneEntityClass(), new MeshRenderData(renderContainer.getMeshRenderAttributes(), shaderManager));

            if (objectCategory.equals(TestTBoxApp.PHYSICS_OBJECT)) {
                boolean isStatic = attributeContainer.tryGetValueFromAttributeByID(AttributeID.IS_STATIC, Boolean.class);
                JGemsHelper.tryCreateMeshCollisionData(meshDataGroup);
                if (isStatic) {
                    BtStaticMeshBody worldModeledBrush = new BtStaticMeshBody(meshDataGroup, physicsWorld, pos, id);
                    JGemsHelper.addItem(worldModeledBrush, new RenderObjectData(renderObjectData, meshDataGroup));
                    worldModeledBrush.setCanBeDestroyed(false);
                    worldModeledBrush.setRotation(new Vector3f(rot).negate());
                    worldModeledBrush.setScaling(scale);
                } else {
                    BtDynamicMeshBody worldModeledBrush = new BtDynamicMeshBody(meshDataGroup, physicsWorld, pos, id);
                    JGemsHelper.addItem(worldModeledBrush, new RenderObjectData(renderObjectData, meshDataGroup));
                    worldModeledBrush.setCanBeDestroyed(false);
                    worldModeledBrush.setRotation(new Vector3f(rot).negate());
                    worldModeledBrush.setScaling(scale);
                }
            } else if (objectCategory.equals(TestTBoxApp.PROP_OBJECT)) {
                MeshRenderData meshRenderData = new MeshRenderData(renderContainer.getMeshRenderAttributes(), shaderManager);
                IRenderObjectFabric renderFabric = renderObjectData.getRenderFabric();

                Model<Format3D> model = new Model<>(new Format3D(), meshDataGroup);
                model.getFormat().setPosition(pos);
                model.getFormat().setRotation(rot);
                model.getFormat().setScaling(scale);
                JGemsHelper.addPropInScene(new SceneProp(renderFabric, model, meshRenderData));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull IPlayerConstructor createPlayer(String mapName) {
        return (SimpleKinematicPlayer::new);
    }
}
