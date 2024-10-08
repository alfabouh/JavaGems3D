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

package javagems3d.engine.system.resources.assets.models;

import org.jetbrains.annotations.NotNull;
import javagems3d.engine.system.resources.assets.material.Material;
import javagems3d.engine.system.resources.assets.models.formats.IFormat;
import javagems3d.engine.system.resources.assets.models.mesh.Mesh;
import javagems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.engine.system.resources.assets.models.mesh.ModelNode;

import java.io.Serializable;

public final class Model<T extends IFormat> implements Serializable, AutoCloseable {
    private static final long serialVersionUID = -228L;
    private final T format;
    private MeshDataGroup meshDataGroup;

    @SuppressWarnings("unchecked")
    public Model(Model<?> model) {
        this.format = (T) model.getFormat().copy();
        this.meshDataGroup = model.getMeshDataGroup();
    }

    public Model(Model<?> model, T format) {
        this.format = format;
        this.meshDataGroup = model.getMeshDataGroup();
    }

    public Model(@NotNull T t, MeshDataGroup meshDataGroup) {
        this.format = t;
        this.meshDataGroup = meshDataGroup;
    }

    public Model(@NotNull T t, ModelNode modelNode) {
        this.format = t;
        this.meshDataGroup = new MeshDataGroup();
        this.meshDataGroup.putNode(modelNode);
    }

    public Model(@NotNull T t, Mesh mesh, Material material) {
        this.format = t;
        this.meshDataGroup = new MeshDataGroup();
        this.meshDataGroup.putNode(new ModelNode(mesh, material));
    }

    public Model(@NotNull T t, Mesh mesh) {
        this.format = t;
        this.meshDataGroup = new MeshDataGroup(mesh);
    }

    public Model(@NotNull T t) {
        this.format = t;
        this.meshDataGroup = null;
    }

    public boolean isValid() {
        return this.getMeshDataGroup() != null;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public int totalMeshGroups() {
        return this.getMeshDataGroup().getModelNodeList().size();
    }

    public T getFormat() {
        return this.format;
    }

    public void clean() {
        if (this.getMeshDataGroup() == null) {
            return;
        }
        this.getMeshDataGroup().clean();
        this.meshDataGroup = null;
    }

    @Override
    public void close() {
        this.clean();
    }
}
