package ru.alfabouh.engine.render.scene.scene_render.groups;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;
import ru.alfabouh.engine.render.scene.scene_render.RenderGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldTransparentRender extends SceneRenderBase {
    public static List<IModeledSceneObject> transparentRenderObjects;

    public WorldTransparentRender(SceneRender sceneRenderConveyor) {
        super(99, sceneRenderConveyor, new RenderGroup("WORLD_TRANSPARENT"));
        WorldTransparentRender.transparentRenderObjects = new ArrayList<>();
    }

    public void onRender(double partialTicks) {
        WorldTransparentRender.transparentRenderObjects.sort(Comparator.comparing(e -> -e.getModel3D().getFormat().getPosition().distance(this.getCamera().getCamPosition())));
        GL30.glDepthMask(false);
        this.render(partialTicks, WorldTransparentRender.transparentRenderObjects);
        GL30.glDepthMask(true);
        WorldTransparentRender.transparentRenderObjects.clear();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void render(double partialTicks, List<IModeledSceneObject> renderObjects) {
        for (IModeledSceneObject entityItem : renderObjects) {
            if (entityItem.hasRender()) {
                if (entityItem.isVisible()) {
                    entityItem.getModelRenderParams().getShaderManager().bind();
                    entityItem.getModelRenderParams().getShaderManager().getUtils().performProjectionMatrix();
                    entityItem.renderFabric().onRender(partialTicks, this, entityItem);
                    entityItem.getModelRenderParams().getShaderManager().unBind();
                }
            }
        }
    }
}