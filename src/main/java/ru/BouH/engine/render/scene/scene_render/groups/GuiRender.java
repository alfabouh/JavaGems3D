package ru.BouH.engine.render.scene.scene_render.groups;

import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.render.scene.gui.InGameGUI;
import ru.BouH.engine.render.scene.gui.base.GameGUI;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;

public class GuiRender extends SceneRenderBase {
    public GuiRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(3, sceneRenderConveyor, new RenderGroup("GUI", false));
    }

    public void onRender(double partialTicks) {
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Game.getGame().getScreen().getScene().getGui().onRender(partialTicks);
        GL30.glDisable(GL30.GL_BLEND);
    }

    @Override
    public void onStartRender() {
    }

    @Override
    public void onStopRender() {
    }
}
