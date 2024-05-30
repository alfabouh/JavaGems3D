package ru.alfabouh.engine.render.scene.fabric.render.inventory;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.Material;
import ru.alfabouh.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.system.resources.assets.models.mesh.Mesh;
import ru.alfabouh.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.engine.system.resources.assets.models.mesh.ModelNode;
import ru.alfabouh.engine.inventory.items.InventoryItem;
import ru.alfabouh.engine.inventory.items.ItemZippo;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.fabric.render.RenderPlayerSP;
import ru.alfabouh.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;

public class InventoryZippoObject extends InventoryItemObject {
    private final MeshDataGroup model1;
    private final MeshDataGroup model2;

    public InventoryZippoObject() {
        Mesh mesh = MeshHelper.generatePlane3DMesh(new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f));
        Material material1 = Material.createDefault();
        Material material2 = Material.createDefault();
        material1.setDiffuse(ResourceManager.renderAssets.zippo1);
        material1.setEmissive(ResourceManager.renderAssets.zippo1_emission);
        material2.setDiffuse(ResourceManager.renderAssets.zippo2);
        ModelNode modelNode1 = new ModelNode(mesh, material1);
        ModelNode modelNode2 = new ModelNode(mesh, material2);
        this.model1 = new MeshDataGroup(modelNode1);
        this.model2 = new MeshDataGroup(modelNode2);
    }

    @Override
    public void onRender(double partialTicks, SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        ItemZippo itemZippo = (ItemZippo) inventoryItem;
        double d1 = Math.cos(RenderPlayerSP.stepBobbing * 0.1f) * 0.051f;
        super.performTransformations(new Vector3d(0.1f, -1.0f + d1, -1.4f), new Vector3d(0.0f, Math.toRadians(20.0f), 0.0f), new Vector3d(1.0d), renderInventoryItemData);
        renderInventoryItemData.getShaderManager().performUniform("use_emission", itemZippo.isOpened() ? 1 : 0);
        super.renderInventoryModel(itemZippo.isOpened() ? this.model1 : this.model2, renderInventoryItemData.getShaderManager());
    }

    @Override
    public void preRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        super.preRender(sceneRenderBase, inventoryItem, renderInventoryItemData);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void postRender(SceneRenderBase sceneRenderBase, InventoryItem inventoryItem, RenderInventoryItemData renderInventoryItemData) {
        super.postRender(sceneRenderBase, inventoryItem, renderInventoryItemData);
        GL30.glDisable(GL30.GL_BLEND);
    }
}
