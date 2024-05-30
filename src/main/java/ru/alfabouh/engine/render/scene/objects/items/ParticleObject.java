package ru.alfabouh.engine.render.scene.objects.items;

import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.assets.materials.Material;
import ru.alfabouh.engine.system.resources.assets.materials.textures.ParticleTexturePack;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderParticleD2Data;
import ru.alfabouh.engine.render.scene.world.SceneWorld;

public class ParticleObject extends EntityObject {
    private int currentTexture;
    private double lastUpdate = JGems.glfwTime();

    public ParticleObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
        this.currentTexture = 0;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        RenderParticleD2Data renderParticleD2Data = (RenderParticleD2Data) this.getRenderData();
        ParticleTexturePack particleTexturePack = renderParticleD2Data.getParticleTexturePack();
        if (particleTexturePack.getAnimationRate() <= 0.0f) {
            this.currentTexture = JGems.random.nextInt(particleTexturePack.getTexturesNum());
            Material material = new Material();
            material.setDiffuse(particleTexturePack.getiImageSample()[this.currentTexture]);
            renderParticleD2Data.setOverObjectMaterial(material);
        }
        this.getModel3D().getFormat().setOrientedToView(true);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        RenderParticleD2Data renderParticleD2Data = (RenderParticleD2Data) this.getRenderData();
        ParticleTexturePack particleTexturePack = renderParticleD2Data.getParticleTexturePack();
        double curr = JGems.glfwTime();
        if (particleTexturePack.getAnimationRate() > 0.0f) {
            if (curr - this.lastUpdate > 1.0d * particleTexturePack.getAnimationRate()) {
                this.currentTexture = (this.currentTexture + 1) % particleTexturePack.getTexturesNum();
                Material material = new Material();
                material.setDiffuse(particleTexturePack.getiImageSample()[this.currentTexture]);
                renderParticleD2Data.setOverObjectMaterial(material);
                this.lastUpdate = curr;
            }
        }
    }
}
