package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.liquids.ILiquid;
import ru.alfabouh.jgems3d.engine.render.opengl.frustum.ICullable;
import ru.alfabouh.jgems3d.engine.render.opengl.frustum.RenderABB;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderLiquidData;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;

public final class LiquidObject implements ICullable {
    private final RenderLiquidData renderLiquidData;
    private final ILiquid liquid;
    private final Model<Format3D> model;
    private final Vector2f textureScaling;

    public LiquidObject(ILiquid iLiquid, RenderLiquidData renderLiquidData) {
        this.renderLiquidData = renderLiquidData;
        this.liquid = iLiquid;
        this.textureScaling = new Vector2f(1.0f);
        this.model = this.constructModel(iLiquid);
    }

    private Model<Format3D> constructModel(ILiquid liquid) {
        Vector3f location = liquid.getZone().getLocation();
        Vector3f size = new Vector3f(liquid.getZone().getSize()).mul(0.5f);
        double y = location.y + size.y / 2.0f;
        Vector3f v1 = new Vector3f((float) (location.x - size.x), (float) y, (float) (location.z - size.z));
        Vector3f v2 = new Vector3f((float) (location.x - size.x), (float) y, (float) (location.z + size.z));
        Vector3f v3 = new Vector3f((float) (location.x + size.x), (float) y, (float) (location.z - size.z));
        Vector3f v4 = new Vector3f((float) (location.x + size.x), (float) y, (float) (location.z + size.z));
        if (size.x > size.z) {
            this.textureScaling.set(new Vector2f(size.x / size.z, 1.0f));
        } else if (size.x < size.z) {
            this.textureScaling.set(new Vector2f(1.0f, size.z / size.x));
        }
        float sizeBound = 10.0f;
        if (size.x > sizeBound) {
            this.textureScaling.mul(size.x / sizeBound, 1.0f);
        }
        if (size.z > sizeBound) {
            this.textureScaling.mul(1.0f, size.z / sizeBound);
        }
        return MeshHelper.generatePlane3DModel(v1, v2, v3, v4);
    }

    public Vector2f getTextureScaling() {
        return new Vector2f(this.textureScaling);
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public RenderLiquidData getRenderLiquidData() {
        return this.renderLiquidData;
    }

    public ILiquid getLiquid() {
        return this.liquid;
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }

    @Override
    public RenderABB getRenderABB() {
        return new RenderABB(this.getLiquid().getZone().getLocation(), this.getLiquid().getZone().getSize());
    }
}
