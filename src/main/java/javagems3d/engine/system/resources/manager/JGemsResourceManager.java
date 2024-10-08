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

package javagems3d.engine.system.resources.manager;

import javagems3d.engine.JGems3D;
import javagems3d.engine.api_bridge.APIContainer;
import javagems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.engine.graphics.opengl.rendering.fabric.inventory.table.InventoryRenderTable;
import javagems3d.engine.graphics.opengl.rendering.imgui.elements.base.font.GuiFont;
import javagems3d.engine.system.inventory.items.InventoryItem;
import javagems3d.engine.system.resources.assets.loaders.*;
import javagems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.engine.system.resources.cache.ResourceCache;
import javagems3d.engine.system.service.exceptions.JGemsIOException;
import javagems3d.engine.system.service.path.JGemsPath;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public final class JGemsResourceManager {
    public static final InventoryRenderTable inventoryItemRenderTable = new InventoryRenderTable();

    public static ShadersAssetsLoader globalShaderAssets = null;
    public static TextureAssetsLoader globalTextureAssets = null;
    public static ModelAssetsLoader globalModelAssets = null;
    public static RenderDataLoader globalRenderDataAssets = null;
    public static SoundAssetsLoader globalSoundAssets = null;

    private final GameResources globalResources;
    private final GameResources localResources;

    public JGemsResourceManager() {
        JGemsResourceManager.globalShaderAssets = new ShadersAssetsLoader();
        this.globalResources = new GameResources(new ResourceCache("Global"));
        this.localResources = new GameResources(new ResourceCache("Local"));
    }

    public static void addInventoryItemRenderer(Class<? extends InventoryItem> itemClass, InventoryItemRenderData inventoryItemRenderData) {
        JGemsResourceManager.inventoryItemRenderTable.addItem(itemClass, inventoryItemRenderData);
    }

    public static void createShaders() {
        JGemsResourceManager.globalShaderAssets.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersLoader<JGemsShaderManager> shadersLoader : APIContainer.get().getAppResourceLoader().getShadersLoaders()) {
            shadersLoader.createShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public static void reloadShaders() {
        JGemsResourceManager.globalShaderAssets.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        for (ShadersLoader<JGemsShaderManager> shadersLoader : APIContainer.get().getAppResourceLoader().getShadersLoaders()) {
            shadersLoader.reloadShaders(JGemsResourceManager.getGlobalGameResources().getResourceCache());
        }
    }

    public static Font createFontFromJAR(JGemsPath path) {
        Font font1;
        try {
            try (InputStream inputStream = JGems3D.loadFileFromJar(path)) {
                font1 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            }
        } catch (FontFormatException | IOException e) {
            throw new JGemsIOException(e);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font1);
        return font1;
    }

    public static GameResources getLocalGameResources() {
        return JGems3D.get().getResourceManager().getLocalResources();
    }

    public static GameResources getGlobalGameResources() {
        return JGems3D.get().getResourceManager().getGlobalResources();
    }

    public void destroy() {
        GuiFont.allCreatedFonts.forEach(GuiFont::cleanUp);
        this.cleanAllCaches();
    }

    public void loadGlobalResources() {
        this.getGlobalResources().loadResources();
    }

    public void loadLocalResources() {
        this.getLocalResources().loadResources();
    }

    public void initGlobalResources() {
        JGemsResourceManager.globalTextureAssets = new TextureAssetsLoader();
        JGemsResourceManager.globalModelAssets = new ModelAssetsLoader();
        JGemsResourceManager.globalRenderDataAssets = new RenderDataLoader();
        JGemsResourceManager.globalSoundAssets = new SoundAssetsLoader();
        this.getGlobalResources().addAssetsLoaders(JGemsResourceManager.globalTextureAssets, JGemsResourceManager.globalModelAssets, JGemsResourceManager.globalRenderDataAssets, JGemsResourceManager.globalSoundAssets);
        this.getGlobalResources().addAssetsLoaders(APIContainer.get().getAppResourceLoader().getAssetsLoaderSet());
    }

    public void initLocalResources() {
    }

    public void destroyLocalResources() {
        this.getLocalResources().destroy();
    }

    public void cleanGlobalCache() {
        this.getGlobalResources().cleanCache();
    }

    public void cleanLocalCache() {
        this.getLocalResources().cleanCache();
    }

    public void cleanAllCaches() {
        this.cleanLocalCache();
        this.cleanGlobalCache();
    }

    public void reloadTexturesInGlobalCache() {
        this.getGlobalResources().reloadTexturesInCache();
    }

    public void reloadTexturesInLocalCache() {
        this.getLocalResources().reloadTexturesInCache();
    }

    public void recreateTexturesInAllCaches() {
        this.reloadTexturesInGlobalCache();
        this.reloadTexturesInLocalCache();
    }

    public GameResources getLocalResources() {
        return this.localResources;
    }

    public GameResources getGlobalResources() {
        return this.globalResources;
    }
}
