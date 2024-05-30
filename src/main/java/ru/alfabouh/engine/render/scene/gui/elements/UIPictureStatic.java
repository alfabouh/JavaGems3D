package ru.alfabouh.engine.render.scene.gui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.gui.elements.base.UIElement;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.textures.IImageSample;
import ru.alfabouh.engine.system.resources.assets.models.Model;
import ru.alfabouh.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.system.resources.assets.models.formats.Format2D;
import ru.alfabouh.engine.system.resources.assets.shaders.ShaderManager;

public class UIPictureStatic extends UIElement {
    protected final IImageSample iImageSample;
    protected final Model<Format2D> imageModel;
    private final Vector2i position;
    private final Vector2i size;
    private final Vector2f textureXY;
    private final Vector2f textureWH;

    public UIPictureStatic(@NotNull IImageSample iImageSample, @NotNull Vector2i position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(ResourceManager.shaderAssets.gui_image, zValue);
        this.iImageSample = iImageSample;
        this.position = position;
        this.textureXY = textureXY;
        this.textureWH = textureWH;
        this.size = new Vector2i((int) this.textureWH.x, (int) this.textureWH.y);

        this.imageModel = this.constructModel(iImageSample.size());
    }

    @Override
    public void render(double partialTicks) {
        this.imageModel.getFormat().setPosition(new Vector2d(this.getPosition()));
        this.imageModel.getFormat().setScale(new Vector2d(this.getScaling()));
        ShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performProjectionMatrix2d(this.imageModel);
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.iImageSample.bindTexture();
        shaderManager.performUniform("texture_sampler", 0);
        Scene.renderModel(this.imageModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }

    @Override
    public void cleanData() {
        this.imageModel.clean();
    }

    protected Model<Format2D> constructModel(Vector2i imageSize) {
        Vector2f tMin = new Vector2f(this.textureXY.x / (float) imageSize.x, this.textureXY.y / (float) imageSize.y);
        Vector2f tMax = new Vector2f((this.textureWH.x + this.textureXY.x) / (float) imageSize.x, (this.textureWH.y + this.textureXY.y) / (float) imageSize.y);
        return MeshHelper.generatePlane2DModel(new Vector2f(0.0f), this.getZValue(), tMin, tMax, new Vector2f(this.textureWH).mul(this.getScaling()));
    }

    public @NotNull Vector2i getPosition() {
        return this.position;
    }

    @Override
    public @NotNull Vector2i getSize() {
        return new Vector2i((int) (this.size.x * this.getScaling().x), (int) (this.size.y * this.getScaling().y));
    }

    @Override
    public int calcUIHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.iImageSample.hashCode();
        result = prime * result + this.textureXY.hashCode();
        result = prime * result + this.textureWH.hashCode();
        result = prime * result + this.getSize().hashCode();
        result = prime * result + this.getPosition().hashCode();
        return result;
    }
}
