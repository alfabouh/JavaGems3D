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

package javagems3d.engine.graphics.opengl.rendering.items.props;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.graphics.opengl.environment.light.Light;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import javagems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.basic.IWorldObject;
import javagems3d.engine.physics.world.basic.IWorldTicked;
import javagems3d.engine.system.resources.assets.models.Model;
import javagems3d.engine.system.resources.assets.models.formats.Format3D;
import javagems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSceneProp implements IModeledSceneObject, IWorldObject, IWorldTicked {
    private final List<Light> lightList;
    private final IRenderObjectFabric renderFabric;
    private Model<Format3D> model;
    private MeshRenderData meshRenderData;
    private boolean isVisible;

    public AbstractSceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull MeshRenderData meshRenderData) {
        this.lightList = new ArrayList<>();

        this.renderFabric = renderFabric;
        this.model = model;
        this.meshRenderData = meshRenderData;
        this.isVisible = true;
    }

    public AbstractSceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, model, MeshRenderData.defaultMeshRenderData(shaderManager));
    }

    public void clearLights() {
        Iterator<Light> lightIterator = this.getLightsList().iterator();
        while (lightIterator.hasNext()) {
            Light l = lightIterator.next();
            l.stop();
            this.onRemoveLight(l);
            lightIterator.remove();
        }
    }

    public void addLight(Light light) {
        this.getLightsList().add(light);
        light.start();
        this.onAddLight(light);
    }

    public void removeLight(Light light) {
        this.getLightsList().remove(light);
        light.stop();
        this.onRemoveLight(light);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        JGemsHelper.getLogger().log("[ " + this + " ]" + " - PreRender");
        if (this.hasRender()) {
            this.renderFabric().onPreRender(this);
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        JGemsHelper.getLogger().log("[ " + this + " ]" + " - PostRender");
        if (this.hasRender()) {
            this.renderFabric().onPostRender(this);
        }
        this.clearLights();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.adjustLightsTranslation(this.getModel().getFormat().getPosition(), new Vector3f(0.0f));
    }

    protected void onAddLight(Light light) {
        JGemsHelper.getLogger().log("Add light to: " + this);
    }

    protected void onRemoveLight(Light light) {
        JGemsHelper.getLogger().log("Removed light from: " + this);
    }

    @Override
    public RenderSphere calcRenderSphere() {
        if (!this.getModel().isValid()) {
            return null;
        }
        return new RenderSphere(JGemsHelper.UTILS.calcDistanceToMostFarPoint(this.getModel().getMeshDataGroup(), this.getModel().getFormat().getScaling()), this.getModel().getFormat().getPosition());
    }

    public AbstractSceneProp setModelRenderConstraints(MeshRenderData meshRenderData) {
        this.meshRenderData = meshRenderData;
        return this;
    }

    public boolean canBeCulled() {
        return true;
    }

    @Override
    public List<Light> getLightsList() {
        return this.lightList;
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public AbstractSceneProp setModel(Model<Format3D> model) {
        this.model = model;
        return this;
    }

    public MeshRenderData getMeshRenderData() {
        return this.meshRenderData;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public IRenderObjectFabric renderFabric() {
        return this.renderFabric;
    }

    @Override
    public boolean hasRender() {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + this.getModel().getFormat().getPosition();
    }
}
