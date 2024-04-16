package ru.alfabouh.engine.game.resources;

import ru.alfabouh.engine.audio.sound.SoundBuffer;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.game.logger.GameLogging;
import ru.alfabouh.engine.game.resources.assets.*;
import ru.alfabouh.engine.game.resources.assets.materials.textures.TextureSample;
import ru.alfabouh.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.engine.game.resources.assets.shaders.loader.ShaderLoader;
import ru.alfabouh.engine.game.resources.assets.utils.ModelLoader;
import ru.alfabouh.engine.game.resources.cache.GameCache;
import ru.alfabouh.engine.game.resources.cache.ICached;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceManager {
    public static ShaderLoader shaderAssets = null;
    public static TextureAssetsLoader renderAssets = null;
    public static ModelAssetsLoader modelAssets = null;
    public static RenderDataLoader renderDataAssets = null;
    public static SoundAssetsLoader soundAssetsLoader = null;
    private final List<IAssetsLoader> assetsObjects;
    private final GameCache gameCache;

    public ResourceManager() {
        this.gameCache = new GameCache();
        this.assetsObjects = new ArrayList<>();
        ResourceManager.shaderAssets = new ShaderLoader();
    }

    public static void reloadShaders() {
        ResourceManager.shaderAssets.loadAllShaders();
    }

    public static Font createFontFromJAR(String font) {
        Font font1;
        try {
            font1 = Font.createFont(Font.TRUETYPE_FONT, Game.loadFileJar("/assets/" + font));
        } catch (FontFormatException | IOException e) {
            throw new GameException(e);
        }
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font1);
        return font1;
    }

    public static SoundBuffer createSoundBuffer(String soundName, int soundFormat) {
        return SoundBuffer.createSoundBuffer(Game.getGame().getResourceManager().getGameCache(), soundName, soundFormat);
    }

    public static MeshDataGroup createMesh(String modelPath, String modelName) {
        return ModelLoader.createMesh(Game.getGame().getResourceManager().getGameCache(), modelPath, modelName);
    }

    public static TextureSample createTextureOutsideJar(String fullPath, boolean interpolate, int wrapping) {
        return TextureSample.createTextureOutsideJar(Game.getGame().getResourceManager().getGameCache(), fullPath, interpolate, wrapping);
    }

    public static TextureSample createTexture(String path, boolean interpolate, int wrapping) {
        return TextureSample.createTexture(Game.getGame().getResourceManager().getGameCache(), path, interpolate, wrapping);
    }

    public static TextureSample createTextureIS(String id, InputStream inputStream, boolean interpolate, int wrapping) {
        return TextureSample.createTextureIS(id, inputStream, interpolate, wrapping);
    }

    public static TextureSample createTextureIS(InputStream inputStream, boolean interpolate, int wrapping) {
        return TextureSample.createTextureIS("#inputstream", inputStream, interpolate, wrapping);
    }

    public static ICached getResource(String key) {
        return Game.getGame().getResourceManager().getGameCache().getCachedObject(key);
    }

    public static TextureSample getTextureResource(String key) {
        return Game.getGame().getResourceManager().getGameCache().getCachedTexture(key);
    }

    public static MeshDataGroup getMeshDataGroupResource(String key) {
        return Game.getGame().getResourceManager().getGameCache().getCachedMeshDataGroup(key);
    }

    public void init() {
        ResourceManager.renderAssets = new TextureAssetsLoader();
        ResourceManager.modelAssets = new ModelAssetsLoader();
        ResourceManager.renderDataAssets = new RenderDataLoader();
        ResourceManager.soundAssetsLoader = new SoundAssetsLoader();
        this.addAssetLoader(ResourceManager.renderAssets);
        this.addAssetLoader(ResourceManager.modelAssets);
        this.addAssetLoader(ResourceManager.renderDataAssets);
        this.addAssetLoader(ResourceManager.soundAssetsLoader);
    }

    public void destroy() {
        ResourceManager.shaderAssets.destroyShaders();
        this.getGameCache().cleanCache();
    }

    public GameCache getGameCache() {
        return this.gameCache;
    }

    public List<IAssetsLoader> getAssetsObjects() {
        return this.assetsObjects;
    }

    private void addAssetLoader(IAssetsLoader asset) {
        this.assetsObjects.add(asset);
    }

    private Set<Thread> initAssets() {
        Set<Thread> set = new HashSet<>();
        Iterator<IAssetsLoader> assetsIterator = this.assetsObjects.iterator();
        while (assetsIterator.hasNext()) {
            IAssetsLoader assets = assetsIterator.next();
            if (assets.loadMode() == IAssetsLoader.LoadMode.PARALLEL) {
                Thread thread = new Thread(() -> {
                    try {
                        assets.load(this.getGameCache());
                    } catch (Exception e) {
                        Game.getGame().getLogManager().error(e);
                        GameLogging.showExceptionDialog("An exception occurred inside the game. Open the logs folder for details.");
                    }
                });
                set.add(thread);
                assetsIterator.remove();
            }
        }
        return set;
    }

    public void loadAllAssets() {
        this.assetsObjects.sort(Comparator.comparingInt(IAssetsLoader::loadOrder));
        Set<Thread> threads = this.initAssets();
        threads.forEach(Thread::start);
        List<IAssetsLoader> preLoad = this.assetsObjects.stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.PRE).collect(Collectors.toList());
        List<IAssetsLoader> postLoad = this.assetsObjects.stream().filter(e -> e.loadMode() == IAssetsLoader.LoadMode.POST).collect(Collectors.toList());
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException ex) {
                throw new GameException(ex);
            }
        });
        for (IAssetsLoader assets : preLoad) {
            assets.load(this.getGameCache());
        }
        for (IAssetsLoader assets : postLoad) {
            assets.load(this.getGameCache());
        }
    }
}
