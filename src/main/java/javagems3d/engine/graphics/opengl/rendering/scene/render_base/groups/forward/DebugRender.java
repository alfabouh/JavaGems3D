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

package javagems3d.engine.graphics.opengl.rendering.scene.render_base.groups.forward;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import javagems3d.engine.JGems3D;
import javagems3d.engine.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.engine.system.resources.assets.models.Model;
import javagems3d.engine.system.resources.assets.models.formats.Format3D;
import javagems3d.engine.system.resources.assets.models.helper.MeshHelper;
import javagems3d.engine.system.resources.assets.shaders.UniformString;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;

public class DebugRender extends SceneRenderBase {
    private final JGemsShaderManager debugShaders;

    public DebugRender(JGemsOpenGLRenderer sceneRender) {
        super(1, sceneRender, new RenderGroup("DEBUG_FORWARD"));
        this.debugShaders = JGemsResourceManager.globalShaderAssets.debug;
    }

    public void onRender(FrameTicking frameTicking) {
        GL30.glHint(GL30.GL_LINE_SMOOTH_HINT, GL30.GL_NICEST);
        GL30.glEnable(GL30.GL_LINE_SMOOTH);
        if (JGemsDebugGlobalConstants.SHOW_DEBUG_LINES) {
            this.debugShaders.bind();
            this.debugShaders.getUtils().performPerspectiveMatrix();
            this.debugShaders.getUtils().performViewMatrix(JGemsSceneUtils.getMainCameraViewMatrix());

            JGemsDebugGlobalConstants.linesDebugDraw.drawAABBLines(this.debugShaders, JGems3D.get().getPhysicsWorld().getDynamics());
            JGemsDebugGlobalConstants.linesDebugDraw.drawNavMeshLines(this.debugShaders);

            this.renderDebugSunDirection();

            this.debugShaders.unBind();
        }
    }

    public void onStartRender() {
        super.onStartRender();
        JGemsDebugGlobalConstants.linesDebugDraw.setupBuffers();
    }

    public void onStopRender() {
        super.onStopRender();
        JGemsDebugGlobalConstants.linesDebugDraw.cleanup();
    }

    private void renderDebugSunDirection() {
        try (Model<Format3D> model = MeshHelper.generateVector3DModel3f(new Vector3f(0.0f), new Vector3f(this.getSceneWorld().getEnvironment().getSky().getSunPos()).mul(1000.0f))) {
            this.debugShaders.performUniform(new UniformString("colour"), new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
            JGemsSceneUtils.renderModel(model, GL30.GL_LINES);
        }
    }
}