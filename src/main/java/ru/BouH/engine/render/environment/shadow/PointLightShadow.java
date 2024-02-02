package ru.BouH.engine.render.environment.shadow;

import org.joml.Matrix4d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.programs.FBOCubeMapProgram;

import java.util.ArrayList;
import java.util.List;

public class PointLightShadow {
    private final FBOCubeMapProgram pointLightCubeMap;
    private final int id;
    private final Scene scene;
    private PointLight pointLight;
    private List<Matrix4d> shadowDirections;

    public PointLightShadow(int id, Scene scene) {
        this.id = id;
        this.scene = scene;
        this.shadowDirections = new ArrayList<>();
        this.pointLight = null;
        this.pointLightCubeMap = new FBOCubeMapProgram(false, false);
        this.pointLightCubeMap.createFrameBufferCubeMap(new Vector2i(ShadowScene.SHADOW_PLIGHT_MAP_SIZE), false, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_DEPTH_COMPONENT, GL30.GL_DEPTH_COMPONENT, GL30.GL_NEAREST, GL30.GL_CLAMP_TO_EDGE);
    }

    public void configureMatrices() {
        this.shadowDirections = RenderManager.instance.getAllDirectionViewSpaces(this.getPointLight().getLightPos(), this.nearPlane(), this.farPlane());
    }

    public int getId() {
        return this.id;
    }

    public float farPlane() {
        return 25.0f;
    }

    public float nearPlane() {
        return 0.1f;
    }

    public boolean isAttachedToLight() {
        return this.getPointLight() != null;
    }

    public void setPointLight(PointLight pointLight) {
        if (pointLight == null) {
            if (this.getPointLight() != null) {
                this.getPointLight().setAttachedShadowSceneId(-1);
            }
            this.pointLight = null;
        } else {
            this.pointLight = pointLight;
            this.pointLight.setAttachedShadowSceneId(this.getId());
        }
    }

    public PointLight getPointLight() {
        return this.pointLight;
    }

    public List<Matrix4d> getShadowDirections() {
        return this.shadowDirections;
    }

    public Scene getScene() {
        return this.scene;
    }

    public FBOCubeMapProgram getPointLightCubeMap() {
        return this.pointLightCubeMap;
    }
}
