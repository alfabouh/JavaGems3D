package ru.BouH.engine.render.environment.light;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.lwjgl.system.MemoryUtil;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.world.SceneWorld;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LightManager implements ILightManager {
    public static final int MAX_POINT_LIGHTS = 1024;
    private List<PointLight> pointLightList;
    private List<PointLight> activeLights;
    private final Environment environment;

    public LightManager(Environment environment) {
        this.environment = environment;
        this.initCollections();
    }

    private void initCollections() {
        this.pointLightList = new ArrayList<>(LightManager.MAX_POINT_LIGHTS);
        this.activeLights = new ArrayList<>();
        for (int i = 0; i < LightManager.MAX_POINT_LIGHTS; i++) {
            this.getPointLightList().add(new PointLight());
        }
    }

    public void addLight(Light light) {
        if ((light.lightCode() & Light.POINT_LIGHT) != 0) {
            int i = getActiveLights().size();
            if (i >= LightManager.MAX_POINT_LIGHTS) {
                Game.getGame().getLogManager().error("Reached point lights limit: " + LightManager.MAX_POINT_LIGHTS);
                return;
            }
            this.getPointLightList().set(i, (PointLight) light);
        }
    }

    public void removeLight(Light light) {
        light.disable();
    }

    public float calcAmbientLight() {
        float ambient = 0.5f;
        return Math.max(ambient * this.environment.getSky().getSunBrightness(), 5.0e-2f);
    }

    public List<PointLight> getPointLightList() {
        return this.pointLightList;
    }

    public List<PointLight> getActiveLights() {
        return this.activeLights;
    }

    public static Vector3d passVectorInViewSpace(Vector3d in, Matrix4d view, double w) {
        Vector4d aux = new Vector4d(in, w);
        aux.mul(view);
        return new Vector3d(aux.x, aux.y, aux.z);
    }

    public static Vector3f passVectorInViewSpace(Vector3f in, Matrix4d view, float w) {
        Vector4d aux = new Vector4d(in, w);
        aux.mul(view);
        return new Vector3f((float) aux.x, (float) aux.y, (float) aux.z);
    }

    @Override
    public void updateBuffers(SceneWorld sceneWorld, Matrix4d viewMatrix) {
        this.getActiveLights().forEach(e -> e.onUpdate(sceneWorld));
        this.updateSunUbo(viewMatrix);
        this.updatePointLightsUbo(viewMatrix);
        this.activeLights = this.getPointLightList().stream().filter(Light::isEnabled).collect(Collectors.toList());
    }

    private void updateSunUbo(Matrix4d viewMatrix) {
        Vector3f angle = LightManager.passVectorInViewSpace(this.environment.getSky().getSunAngle(), viewMatrix, 0.0f);
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(5);

        value1Buffer.put(this.calcAmbientLight());
        value1Buffer.put(this.environment.getSky().getSunBrightness());
        value1Buffer.put(angle.x);
        value1Buffer.put(angle.y);
        value1Buffer.put(angle.z);
        value1Buffer.flip();

        Game.getGame().getScreen().getScene().getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.SunLight, value1Buffer);
        MemoryUtil.memFree(value1Buffer);
    }

    private void updatePointLightsUbo(Matrix4d viewMatrix) {
        FloatBuffer value1Buffer = MemoryUtil.memAllocFloat(7 * LightManager.MAX_POINT_LIGHTS);
        List<PointLight> pointLightList = this.getActiveLights();
        int activeLights = pointLightList.size();
        for (int i = 0; i < activeLights; i++) {
            PointLight pointLight = pointLightList.get(i);
            Vector3f pos = LightManager.passVectorInViewSpace(new Vector3f((float) pointLight.getLightPos().x, (float) pointLight.getLightPos().y, (float) pointLight.getLightPos().z), viewMatrix, 1.0f);
            value1Buffer.put(pos.x);
            value1Buffer.put(pos.y);
            value1Buffer.put(pos.z);
            value1Buffer.put((float) pointLight.getLightColor().x);
            value1Buffer.put((float) pointLight.getLightColor().y);
            value1Buffer.put((float) pointLight.getLightColor().z);
            value1Buffer.put(!pointLight.isEnabled() ? -1.0f : pointLight.getBrightness());
            value1Buffer.flip();
            Game.getGame().getScreen().getScene().getGameUboShader().performUniformBuffer(ResourceManager.shaderAssets.PointLights, i * 32, value1Buffer);
        }
        MemoryUtil.memFree(value1Buffer);
    }
}
