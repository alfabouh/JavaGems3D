package ru.BouH.engine.render.scene.gui.ui;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.gui.font.GuiFont;

public class ButtonUI extends InteractiveUI {
    private final String text;
    private final TextUI textUI;
    private final Model<Format2D> buttonModel;
    private final GuiFont guiFont;
    private Vector2f fontOffset;
    private Action onEntered;
    private Action onLeft;
    private Action onClick;
    private Action onInside;

    public ButtonUI(String text, GuiFont guiFont, Vector3f position, Vector2f size) {
        super(position, size);
        this.text = text;
        this.guiFont = guiFont;
        this.buttonModel = MeshHelper.generatePlane2DModel(new Vector2f(0.0f), new Vector2f(size.x, size.y), this.getPosition().z);
        this.textUI = new TextUI(text, guiFont, 0xffffff, new Vector3f(0.0f, 0.0f, this.getPosition().z + 0.1f));
        this.fontOffset = this.getFontPos(text, size);

        this.onEntered = null;
        this.onLeft = null;
        this.onClick = null;
        this.onInside = null;
    }

    private Vector2f getFontPos(String text, Vector2f buttonSize) {
        float posX = buttonSize.x / 2.0f - BasicUI.getTextWidth(this.guiFont, text) / 2.0f;
        float posY = buttonSize.y / 2.0f - BasicUI.getFontHeight(this.guiFont) / 2.0f;
        return new Vector2f(posX, posY);
    }

    @Override
    public void render(double partialTicks) {
        if (!this.isVisible()) {
            return;
        }
        super.render(partialTicks);
        this.buttonModel.getFormat().setPosition(new Vector2d(this.getPosition().x, this.getPosition().y));
        this.textUI.setPosition(new Vector3f(this.getPosition().x + this.fontOffset.x, this.getPosition().y + this.fontOffset.y, this.getPosition().z + 0.1f));
        ShaderManager shaderManager = this.getCurrentShader();
        shaderManager.bind();
        shaderManager.getUtils().performProjectionMatrix2d(this.buttonModel);
        shaderManager.performUniform("background_color", new Vector4d(0.25d, 0.0d, 0.15d, 0.8d));
        shaderManager.performUniform("selected", this.isSelected() ? 1 : 0);
        Scene.renderModel(this.buttonModel, GL30.GL_TRIANGLES);
        shaderManager.unBind();
        this.textUI.render(partialTicks);
    }

    @Override
    public void clear() {
        this.textUI.clear();
        this.buttonModel.clean();
    }

    public void setOnEntered(Action onEntered) {
        this.onEntered = onEntered;
    }

    public void setOnClick(Action onClick) {
        this.onClick = onClick;
    }

    public void setOnInside(Action onInside) {
        this.onInside = onInside;
    }

    public void setOnLeft(Action onLeft) {
        this.onLeft = onLeft;
    }

    @Override
    public ShaderManager getCurrentShader() {
        return ResourceManager.shaderAssets.gui_button;
    }

    @Override
    public void onMouseInside() {
        if (this.onInside != null) {
            this.onInside.action();
        }
    }

    @Override
    public void onMouseEntered() {
        if (this.onEntered != null) {
            this.onEntered.action();
        }
    }

    @Override
    public void onMouseLeft() {
        if (this.onLeft != null) {
            this.onLeft.action();
        }
    }

    @Override
    public void onClicked() {
        if (this.onClick != null) {
            this.onClick.action();
        }
    }

    @Override
    public void setSize(Vector2f size) {
        super.setSize(size);
        this.fontOffset = this.getFontPos(text, size);
    }

    public String getText() {
        return this.text;
    }

    @FunctionalInterface
    public interface Action {
        void action();
    }
}
