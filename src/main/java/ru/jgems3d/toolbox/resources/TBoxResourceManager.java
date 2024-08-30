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

package ru.jgems3d.toolbox.resources;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
import ru.jgems3d.engine.system.resources.cache.ICached;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.toolbox.ToolBox;
import ru.jgems3d.toolbox.resources.models.ModelResources;
import ru.jgems3d.toolbox.resources.samples.TextureSample;
import ru.jgems3d.toolbox.resources.shaders.ShaderResources;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;
import ru.jgems3d.toolbox.resources.utils.SimpleModelLoader;

public class TBoxResourceManager {
    private final ShaderResources shaderAssets;
    private final ModelResources modelResources;
    private final ResourceCache resourceCache;

    public TBoxResourceManager() {
        this.resourceCache = new ResourceCache("Global");
        this.shaderAssets = new ShaderResources();
        this.modelResources = new ModelResources();
    }

    public static void createShaders() {
        TBoxResourceManager.shaderResources().createShaders(ToolBox.get().getResourceManager().getCache());
    }

    public static ShaderResources shaderResources() {
        return ToolBox.get().getResourceManager().getShaderAssets();
    }

    public TBoxShaderManager createShaderManager(JGemsPath shaderPath) {
        if (ToolBox.get().getResourceManager().getCache().checkObjectInCache(shaderPath)) {
            JGemsHelper.getLogger().warn("Shader " + shaderPath + " already exists!");
            return (TBoxShaderManager) this.getCache().getCachedObject(shaderPath);
        }
        JGemsHelper.getLogger().log("Creating shader " + shaderPath + "...");
        TBoxShaderManager shaderManager = new TBoxShaderManager(new ShaderContainer(shaderPath));
        this.getCache().addObjectInBuffer(shaderPath, shaderManager);
        return shaderManager;
    }

    public TextureSample createTexture(String fullPath) {
        return TextureSample.createTexture(this.getCache(), fullPath);
    }

    public MeshDataGroup createModel(JGemsPath modelPath) {
        return SimpleModelLoader.createMesh(this, modelPath);
    }

    public ICached getResource(String key) {
        return ToolBox.get().getResourceManager().getCache().getCachedObject(key);
    }

    public void loadResources() {
        SystemLogging.get().getLogManager().log("Loading resources...");
        this.getModelResources().init(this);
    }

    public void destroy() {
        this.getCache().cleanCache();
    }

    public ShaderResources getShaderAssets() {
        return this.shaderAssets;
    }

    public ModelResources getModelResources() {
        return this.modelResources;
    }

    public ResourceCache getCache() {
        return this.resourceCache;
    }
}
