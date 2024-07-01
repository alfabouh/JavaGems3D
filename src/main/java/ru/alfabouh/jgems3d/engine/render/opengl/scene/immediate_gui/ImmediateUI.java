package ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.font.GuiFont;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.Window;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.UIButton;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.UIPictureSizable;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.UIPictureStatic;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.UIText;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.base.UIElement;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.optionsUI.UICarousel;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.elements.optionsUI.UISlider;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.system.map.legacy.Map02;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.IImageSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.engine.system.settings.basic.SettingSlot;
import ru.alfabouh.jgems3d.engine.system.settings.objects.SettingFloatBar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImmediateUI {
    public static int MAX_TICKS_TO_REMOVE_UNUSED_UI = 3;
    public static int GLOBAL_UI_SCALING = 0;
    public static boolean AUTO_SCREEN_SCALING = false;
    private boolean requestCleanFrame;
    private final Map<Integer, UIElement> uiFrameCache;
    private PanelUI currentPanel;
    private float partialTicks;
    private final RenderUIData renderUIData;

    public ImmediateUI() {
        this.uiFrameCache = new HashMap<>();
        this.currentPanel = null;
        this.requestCleanFrame = false;
        this.renderUIData = new RenderUIData();
    }

    public static Vector3f HEX2RGB(int hex) {
        int r = (hex & 0xFFFFFF) >> 16;
        int g = (hex & 0xFFFF) >> 8;
        int b = hex & 0xFF;
        return new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
    }

    public static int getFontHeight(GuiFont fontTexture) {
        return fontTexture.getHeight();
    }

    public static int getTextWidth(GuiFont fontTexture, String text) {
        char[] chars = text.toCharArray();
        int startX = 0;
        for (final char aChar : chars) {
            GuiFont.CharInfo charInfo = fontTexture.getCharInfo(aChar);
            startX += charInfo.getWidth();
        }
        return startX;
    }

    public void setPanel(PanelUI panelUI) {
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onDestruct(this);
        }
        this.currentPanel = panelUI;
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onConstruct(this);
        }
        this.setRequestCleanFrame();
    }

    public void removePanel() {
        this.setPanel(null);
    }

    public void renderFrame(float partialTicks) {
        this.partialTicks = partialTicks;

        if (this.requestCleanFrame) {
            this.cleanFrame();
            this.requestCleanFrame = false;
        }

        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().drawPanel(this, this.partialTicks);
        }

        this.getUiFrameCache().values().forEach(UIElement::incrementUnusedTicks);
        Iterator<UIElement> uiElementIterator = this.getUiFrameCache().values().iterator();
        while (uiElementIterator.hasNext()) {
            UIElement element = uiElementIterator.next();
            if (element.getUnUsedTicks() > ImmediateUI.MAX_TICKS_TO_REMOVE_UNUSED_UI) {
                element.cleanData();
                uiElementIterator.remove();
            }
        }
    }

    public void defaultScale() {
        this.renderUIData.setScaling(null);
    }

    public void defaultShader() {
        this.renderUIData.setShaderManager(null);
    }

    public void scale(Vector2f scaling) {
        this.renderUIData.setScaling(scaling);
    }

    public void shader(JGemsShaderManager shaderManager) {
        this.renderUIData.setShaderManager(shaderManager);
    }

    public UIText textUI(String text, GuiFont guiFont, Vector2i position, int hexColor, float zValue) {
        return this.checkUIInCacheAndRender(UIText.class, new UIText(text, guiFont, hexColor, position, zValue));
    }

    public UIPictureStatic imageUI(IImageSample iImageSample, Vector2i position, Vector2f textureXY, Vector2f textureWH, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureStatic.class, new UIPictureStatic(iImageSample, position, textureXY, textureWH, zValue));
    }

    public UIPictureSizable imageUI(IImageSample iImageSample, Vector2i position, Vector2i size, float zValue) {
        return this.checkUIInCacheAndRender(UIPictureSizable.class, new UIPictureSizable(iImageSample, position, size, zValue));
    }

    public UIButton buttonUI(String text, GuiFont guiFont, Vector2i position, Vector2i size, int textColorHex, float zValue) {
        return this.checkUIInCacheAndRender(UIButton.class, new UIButton(text, guiFont, position, size, textColorHex, zValue));
    }

    public UISlider settingSliderUI(String text, GuiFont guiFont, int hexColor, Vector2i position, SettingFloatBar settingFloatBar, float zValue) {
        return this.checkUIInCacheAndRender(UISlider.class, new UISlider(text, guiFont, hexColor, position, settingFloatBar, zValue));
    }

    public UICarousel settingCarouselUI(String text, GuiFont guiFont, int hexColor, Vector2i position, SettingSlot settingIntSlots, float zValue) {
        return this.checkUIInCacheAndRender(UICarousel.class, new UICarousel(text, guiFont, hexColor, position, settingIntSlots, zValue));
    }

    public UIElement customUI(UIElement uiElement) {
        return this.checkUIInCacheAndRender(UIElement.class, uiElement);
    }

    private <T extends UIElement> T checkUIInCacheAndRender(Class<T> clazz, UIElement uiElement) {
        if (this.renderUIData.getShaderManager() != null) {
            uiElement.setCurrentShader(this.renderUIData.getShaderManager());
        } else {
            uiElement.setDefaultShader();
        }
        if (this.renderUIData.getScaling() != null) {
            uiElement.setScaling(this.renderUIData.getScaling());
        } else {
            uiElement.setDefaultScaling();
        }
        T ui = this.addUIInCache(clazz, uiElement);
        ui.render(this.partialTicks);
        return ui;
    }

    private <T extends UIElement> T addUIInCache(Class<T> clazz, UIElement uiElement) {
        uiElement.buildUI();
        int hash = uiElement.hashCode();
        if (this.getUiFrameCache().containsKey(hash)) {
            UIElement cachedUiElement = this.getUiFrameCache().get(hash);
            if (uiElement.equals(cachedUiElement)) {
                cachedUiElement.zeroUnusedTicks();
                uiElement.cleanData();
                return clazz.cast(cachedUiElement);
            }
        } else {
            //System.out.println(hash);
            this.getUiFrameCache().put(uiElement.hashCode(), uiElement);
        }
        return clazz.cast(uiElement);
    }

    public void onWindowResize(Vector2i dim) {
        this.setRequestCleanFrame();
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onWindowResize(dim);
        }
    }

    public void setRequestCleanFrame() {
        this.requestCleanFrame = true;
    }

    public void destroyUI() {
        this.cleanFrame();
        if (this.getCurrentPanel() != null) {
            this.getCurrentPanel().onDestruct(this);
            this.currentPanel = null;
        }
    }

    protected void cleanFrame() {
        this.getUiFrameCache().forEach((key, value) -> value.cleanData());
        this.getUiFrameCache().clear();
    }

    public static int CALC_INT_WITH_GLOBAL_UI_SCALING(float in) {
        return (int) (in * ImmediateUI.GET_GLOBAL_UI_SCALING());
    }

    public static int CALC_INT_WITH_SCREEN_NORMALIZED_UI_SCALING(float in) {
        return (int) (in * ImmediateUI.GET_SCREEN_NORMALIZED_SCALING());
    }

    public static float GET_GLOBAL_UI_SCALING() {
        if (!ImmediateUI.AUTO_SCREEN_SCALING) {
            return (float) (1.0f / Math.pow(2.0f, ImmediateUI.GLOBAL_UI_SCALING));
        }
        return GET_SCREEN_NORMALIZED_SCALING();
    }

    public static float GET_SCREEN_NORMALIZED_SCALING() {
        double width = JGems.get().getScreen().getWindowDimensions().x;
        double height = JGems.get().getScreen().getWindowDimensions().y;
        float f1 = (float) (width / Window.defaultW);
        float f2 = (float) (height / Window.defaultH);
        float f1_r = (float) Math.max(Math.ceil(f1 * 2.0f) / 2.0f, 1.0f);
        float f2_r = (float) Math.max(Math.ceil(f2 * 2.0f) / 2.0f, 1.0f);
        return Math.min(f1_r, f2_r);
    }

    public PanelUI getCurrentPanel() {
        return this.currentPanel;
    }

    public Map<Integer, UIElement> getUiFrameCache() {
        return this.uiFrameCache;
    }

    private static class RenderUIData {
        private JGemsShaderManager shaderManager;
        private Vector2f scaling;

        public RenderUIData() {
        }

        public void reset() {
            this.scaling = null;
            this.shaderManager = null;
        }

        public void setScaling(Vector2f scaling) {
            this.scaling = scaling;
        }

        public void setShaderManager(JGemsShaderManager shaderManager) {
            this.shaderManager = shaderManager;
        }

        public Vector2f getScaling() {
            return this.scaling;
        }

        public JGemsShaderManager getShaderManager() {
            return this.shaderManager;
        }
    }
}
