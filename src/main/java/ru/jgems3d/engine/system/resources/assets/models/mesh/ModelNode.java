package ru.jgems3d.engine.system.resources.assets.models.mesh;

import ru.jgems3d.engine.system.resources.assets.material.Material;

public class ModelNode {
    private final Mesh mesh;
    private final Material material;

    public ModelNode(Mesh mesh, Material material) {
        this.mesh = mesh;
        this.material = material;
    }

    public ModelNode(Mesh mesh) {
        this.mesh = mesh;
        this.material = null;
    }

    public void cleanMesh() {
        this.getMesh().clean();
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public Material getMaterial() {
        return this.material;
    }
}
