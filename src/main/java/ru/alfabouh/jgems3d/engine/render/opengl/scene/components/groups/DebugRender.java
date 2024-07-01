package ru.alfabouh.jgems3d.engine.render.opengl.scene.components.groups;

import org.joml.Vector3f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graph.Graph;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.debug.constants.GlobalRenderDebugConstants;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class DebugRender extends SceneRenderBase {
    private final JGemsShaderManager debugShaders;

    public DebugRender(JGemsSceneRender sceneRenderConveyor) {
        super(50, sceneRenderConveyor, new RenderGroup("DEBUG"));
        this.debugShaders = ResourceManager.shaderAssets.debug;
    }

    public void onRender(float partialTicks) {
        if (GlobalRenderDebugConstants.SHOW_DEBUG_LINES) {
            this.debugShaders.bind();
            this.renderDebugSunDirection(this);
            this.renderNavMesh(this);
            this.debugShaders.getUtils().performPerspectiveMatrix();
            this.debugShaders.unBind();

            if (!JGems.get().getPhysicsWorld().getDynamicsWorld().isNull()) {
                JGems.get().getPhysicsWorld().getDynamicsWorld().debugDrawWorld();
            }
        }
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void renderNavMesh(SceneRenderBase sceneRenderBase) {
        if (sceneRenderBase.getSceneWorld().getWorld().getGraph() == null) {
            return;
        }
        for (Graph.GVertex vertex : sceneRenderBase.getSceneWorld().getWorld().getGraph().getGraphContainer().keySet()) {
            if (JGems.get().getScreen().getCamera().getCamPosition().distance(new Vector3f(vertex.getX(), vertex.getY() + 0.1f, vertex.getZ())) > 5.0f) {
                continue;
            }
            Model<Format3D> model0 = MeshHelper.generateVector3fModel(new Vector3f((float) vertex.getX(), (float) vertex.getY(), (float) vertex.getZ()), new Vector3f((float) vertex.getX(), (float) (vertex.getY() + 1.0d), (float) vertex.getZ()));
            this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
            this.debugShaders.performUniform("colour", new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
            JGemsSceneUtils.renderModel(model0, GL30.GL_LINES);
            model0.clean();
            for (Graph.GEdge edge : sceneRenderBase.getSceneWorld().getWorld().getGraph().getNeighbors(vertex)) {
                Model<Format3D> model = MeshHelper.generateVector3fModel(new Vector3f((float) vertex.getX(), (float) (vertex.getY() + 0.1f), (float) vertex.getZ()), new Vector3f((float) edge.getTarget().getX(), (float) (edge.getTarget().getY() + 0.1f), (float) edge.getTarget().getZ()));
                this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
                this.debugShaders.performUniform("colour", new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
                //if (Map01.entityManiac != null && Map01.entityManiac.getNavigationAI().getPathToVertex() != null && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(vertex) && Map01.entityManiac.getNavigationAI().getPathToVertex().contains(edge.getTarget())) {
                //    this.debugShaders.performUniform("colour", news Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
                //}
                JGemsSceneUtils.renderModel(model, GL30.GL_LINES);
                model.clean();
            }
        }
    }

    private void renderDebugSunDirection(SceneRenderBase sceneRenderBase) {
        Model<Format3D> model = MeshHelper.generateVector3fModel(new Vector3f(0.0f), new Vector3f(sceneRenderBase.getSceneWorld().getEnvironment().getSky().getSunPos()).mul(1000.0f));
        this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());
        this.debugShaders.performUniform("colour", new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
        JGemsSceneUtils.renderModel(model, GL30.GL_LINES);
        model.clean();
    }
}