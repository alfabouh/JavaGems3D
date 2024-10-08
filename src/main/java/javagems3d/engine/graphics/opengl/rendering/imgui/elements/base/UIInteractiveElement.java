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

package javagems3d.engine.graphics.opengl.rendering.imgui.elements.base;

import org.joml.Vector2f;
import javagems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.engine.system.controller.objects.IController;
import javagems3d.engine.system.controller.objects.MouseKeyboardController;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class UIInteractiveElement extends UIElement {
    private boolean selected;
    private boolean isMLKPressedOutsideButton;
    private boolean wasClickedButton;

    public UIInteractiveElement(JGemsShaderManager currentShader, float zValue) {
        super(currentShader, zValue);
    }

    public void render(float frameDeltaTicks) {
        this.handleInput();
    }

    protected void handleInput() {
        IController controller = this.getController();
        if (controller instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
            boolean flag = mouseKeyboardController.getMouseAndKeyboard().isLeftKeyPressed();
            if (!flag) {
                this.isMLKPressedOutsideButton = false;
            }

            Vector2f mouseCoordinates = mouseKeyboardController.getMouseAndKeyboard().getCursorCoordinatesV2F();
            if (mouseCoordinates.x >= this.getPosition().x && mouseCoordinates.x <= this.getPosition().x + this.getSize().x && mouseCoordinates.y >= this.getPosition().y && mouseCoordinates.y <= this.getPosition().y + this.getSize().y) {
                this.selected = true;
                this.onMouseEntered();
                this.onMouseInside(new Vector2f(mouseCoordinates));
            } else {
                if (flag) {
                    this.isMLKPressedOutsideButton = true;
                }
                if (this.selected) {
                    this.selected = false;
                    this.onMouseLeft();
                }
            }

            if (flag) {
                if (!this.isMLKPressedOutsideButton || (this.wasClickedButton && this.handleClickOutsideBorder())) {
                    this.selected = true;
                    this.onClicked(new Vector2f(mouseCoordinates));
                    this.wasClickedButton = true;
                    if (this.interruptMouseAfterClick()) {
                        JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptLMB();
                        JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptRMB();
                        JGemsControllerDispatcher.mouseKeyboardController.getMouseAndKeyboard().forceInterruptMMB();
                    }
                }
            } else if (this.wasClickedButton) {
                this.onUnClicked(new Vector2f(mouseCoordinates));
                this.wasClickedButton = false;
            }
        }
    }

    protected abstract void onMouseInside(Vector2f mouseCoordinates);

    protected abstract void onMouseEntered();

    protected abstract void onMouseLeft();

    protected abstract void onClicked(Vector2f mouseCoordinates);

    protected abstract void onUnClicked(Vector2f mouseCoordinates);

    public boolean isSelected() {
        return this.selected;
    }

    protected boolean handleClickOutsideBorder() {
        return false;
    }

    protected boolean interruptMouseAfterClick() {
        return true;
    }
}
