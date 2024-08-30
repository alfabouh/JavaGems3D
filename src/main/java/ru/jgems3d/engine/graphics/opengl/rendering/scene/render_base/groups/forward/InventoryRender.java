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

package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.forward;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.system.inventory.IInventoryOwner;
import ru.jgems3d.engine.system.inventory.items.InventoryItem;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class InventoryRender extends SceneRenderBase {
    public InventoryRender(JGemsOpenGLRenderer sceneRender) {
        super(-1, sceneRender, new RenderGroup("INVENTORY_FORWARD"));
    }

    public void onRender(FrameTicking frameTicking) {
        Player player = JGems3D.get().getPlayer();
        if (player instanceof IInventoryOwner && !(JGemsHelper.CAMERA.getCurrentCamera() instanceof FreeCamera)) {
            IInventoryOwner hasInventory = (IInventoryOwner) player;
            InventoryItem current = hasInventory.inventory().getCurrentItem();
            if (hasInventory.inventory().getCurrentItem() != null && JGemsResourceManager.inventoryItemRenderTable.hasRender(current)) {
                InventoryItemRenderData inventoryItemRenderData = JGemsResourceManager.inventoryItemRenderTable.getMap().get(current.getClass());
                if (inventoryItemRenderData.getRenderFabric() != null) {
                    inventoryItemRenderData.getRenderFabric().preRender(this, current, inventoryItemRenderData);
                    inventoryItemRenderData.getRenderFabric().onRender(frameTicking, this, current, inventoryItemRenderData);
                    inventoryItemRenderData.getRenderFabric().postRender(this, current, inventoryItemRenderData);
                }
            }
        }
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}