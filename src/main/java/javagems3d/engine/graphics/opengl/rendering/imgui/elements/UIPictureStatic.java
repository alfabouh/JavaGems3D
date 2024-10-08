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

package javagems3d.engine.graphics.opengl.rendering.imgui.elements;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import javagems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.engine.graphics.opengl.rendering.imgui.elements.base.UIElement;
import javagems3d.engine.system.resources.assets.material.samples.base.ITextureSample;
import javagems3d.engine.system.resources.assets.models.Model;
import javagems3d.engine.system.resources.assets.models.formats.Format2D;
import javagems3d.engine.system.resources.assets.models.helper.MeshHelper;
import javagems3d.engine.system.resources.assets.shaders.UniformString;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;

public class UIPictureStatic extends UIElement {
    protected final ITextureSample iImageSample;
    private final Vector2i position;
    private final Vector2i size;
    private final Vector2f textureXY;
    private final Vector2f textureWH;
    protected Model<Format2D> imageModel;

    public UIPictureStatic(@NotNull ITextureSample iImageSample, @NotNull Vector2i position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(JGemsResourceManager.globalShaderAssets.gui_image, zValue);
        this.iImageSample = iImageSample;
        this.position = position;
        this.textureXY = textureXY;
        this.textureWH = textureWH;
        this.size = new Vector2i((int) this.textureWH.x, (int) this.textureWH.y);
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.imageModel.getFormat().setPosition(new Vector2f(this.getPosition()));
        this.imageModel.getFormat().setScale(new Vector2f(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(this.imageModel);
        GL30.glActiveTexture(GL13.GL_TEXTURE0);
        this.iImageSample.bindTexture();
        shaderManager.performUniform(new UniformString("texture_sampler"), 0);
        JGemsSceneUtils.renderModel(this.imageModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
    }

    @Override
    public void buildUI() {
        this.imageModel = this.constructModel(iImageSample.size());
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
