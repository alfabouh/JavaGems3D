package ru.jgems3d.engine.system.resources.assets.loaders;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import ru.jgems3d.engine.system.service.misc.JGPath;
import ru.jgems3d.engine.system.resources.assets.loaders.base.ShadersLoader;
import ru.jgems3d.engine.system.resources.assets.shaders.ShaderContainer;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformBufferObject;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.cache.ResourceCache;

public class ShadersAssetsLoader extends ShadersLoader<JGemsShaderManager> {

    public UniformBufferObject SunLight;
    public UniformBufferObject PointLights;
    public UniformBufferObject Misc;
    public UniformBufferObject Fog;

    public JGemsShaderManager world_enemy;
    public JGemsShaderManager world_pickable;
    public JGemsShaderManager menu;
    public JGemsShaderManager gameUbo;
    public JGemsShaderManager gui_text;
    public JGemsShaderManager gui_noised;
    public JGemsShaderManager gui_image;
    public JGemsShaderManager gui_button;
    public JGemsShaderManager gui_image_selectable;
    public JGemsShaderManager blur5;
    public JGemsShaderManager blur9;
    public JGemsShaderManager blur13;
    public JGemsShaderManager blur_box;
    public JGemsShaderManager blur_ssao;
    public JGemsShaderManager hdr;
    public JGemsShaderManager scene_gluing;
    public JGemsShaderManager fxaa;
    public JGemsShaderManager skybox;
    public JGemsShaderManager world_gbuffer;
    public JGemsShaderManager world_ssao;
    public JGemsShaderManager world_deferred;
    public JGemsShaderManager weighted_oit;
    public JGemsShaderManager weighted_particle_oit;
    public JGemsShaderManager weighted_liquid_oit;
    public JGemsShaderManager simple;
    public JGemsShaderManager depth_sun;
    public JGemsShaderManager depth_plight;
    public JGemsShaderManager debug;
    public JGemsShaderManager world_selected_gbuffer;
    public JGemsShaderManager inventory_zippo;
    public JGemsShaderManager inventory_common_item;
    public JGemsShaderManager imgui;

    protected void initObjects(ResourceCache resourceCache) {
        this.SunLight = this.createUBO("SunLight", 0, 32);
        this.PointLights = this.createUBO("PointLights", 1, 32 * JGemsSceneGlobalConstants.MAX_POINT_LIGHTS + 4);
        this.Misc = this.createUBO("Misc", 2, 4);
        this.Fog = this.createUBO("Fog", 3, 16);

        this.world_enemy = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world/world_enemy"));
        this.world_pickable = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world/world_pickable"));
        this.debug = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "debug"));
        this.gui_text = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui/gui_text"));
        this.gui_noised = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui/gui_noised"));

        this.gui_button = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui/gui_button"));
        this.gui_image = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui/gui_image"));
        this.gui_image_selectable = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui/gui_image_selectable"));

        this.blur_ssao = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/blur_ssao"));
        this.blur5 = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/blur5"));
        this.blur9 = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/blur9"));
        this.blur13 = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/blur13"));
        this.blur_box = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/blur_box"));

        this.imgui = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui/imgui"));

        this.fxaa = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/fxaa"));
        this.hdr = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/hdr"));
        this.scene_gluing = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/scene_gluing"));

        this.skybox = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world/skybox")).attachUBOs(this.SunLight);

        this.world_ssao = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "post/screen_ssao"));

        this.weighted_liquid_oit = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "oit/weighted_liquid_oit")).setShaderRenderPass(RenderPass.TRANSPARENCY);
        this.weighted_oit = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "oit/weighted_oit")).setShaderRenderPass(RenderPass.TRANSPARENCY);
        this.weighted_particle_oit = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "oit/weighted_particle_oit")).setShaderRenderPass(RenderPass.TRANSPARENCY);

        this.world_gbuffer = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world/world_gbuffer")).setShaderRenderPass(RenderPass.DEFERRED);
        this.world_deferred = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world/world_deferred")).attachUBOs(this.SunLight, this.PointLights, this.Fog);

        this.menu = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gui/menu"));

        this.inventory_zippo = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "inventory/inventory_zippo"));
        this.inventory_common_item = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "inventory/inventory_common_item"));

        this.simple = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world/simple"));
        this.depth_sun = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "shadows/depth_sun"));

        this.world_selected_gbuffer = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "world/world_selected_gbuffer"));
        this.depth_plight = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "shadows/depth_plight"));

        this.gameUbo = this.createShaderManager(resourceCache, new JGPath(JGems3D.Paths.SHADERS, "gameubo")).attachUBOs(this.SunLight, this.Misc, this.PointLights, this.Fog);
    }

    @Override
    protected JGemsShaderManager createShaderObject(JGPath shaderPath) {
        return new JGemsShaderManager(new ShaderContainer(shaderPath));
    }
}