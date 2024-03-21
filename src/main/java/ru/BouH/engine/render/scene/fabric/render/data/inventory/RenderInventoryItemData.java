package ru.BouH.engine.render.scene.fabric.render.data.inventory;

import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderInventoryItem;

public class RenderInventoryItemData {
    private final ShaderManager shaderManager;
    private final IRenderInventoryItem renderFabric;

    public RenderInventoryItemData(ShaderManager shaderManager, IRenderInventoryItem renderFabric) {
        this.shaderManager = shaderManager;
        this.renderFabric = renderFabric;
    }

    public IRenderInventoryItem getRenderFabric() {
        return this.renderFabric;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }
}
