package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.programs.CubeMapProgram;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

public final class RenderLiquidData {
    private final TextureSample liquidTexture;
    private final TextureSample liquidNormals;
    private final JGemsShaderManager shaderManager;
    private final boolean reflections;

    public RenderLiquidData(TextureSample liquidNormals, @NotNull TextureSample liquidTexture, boolean reflections, JGemsShaderManager shaderManager) {
        this.liquidTexture = liquidTexture;
        if ((liquidNormals != null && !liquidNormals.isValid()) || !liquidTexture.isValid()) {
            throw new JGemsException("Wrong liquid samples!");
        }
        this.liquidNormals = liquidNormals;
        this.shaderManager = shaderManager;
        this.reflections = reflections;
    }

    public boolean reflections() {
        return this.reflections;
    }

    public CubeMapProgram getAmbient() {
        return JGems.get().getScreen().getRenderWorld().getEnvironment().getSky().getSkyBox().cubeMapTexture();
    }

    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public TextureSample getLiquidNormals() {
        return this.liquidNormals;
    }

    public TextureSample getLiquidTexture() {
        return this.liquidTexture;
    }
}