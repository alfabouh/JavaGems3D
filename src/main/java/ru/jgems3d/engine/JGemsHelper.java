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

package ru.jgems3d.engine;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.audio.SoundManager;
import ru.jgems3d.engine.graphics.opengl.camera.FreeCamera;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.jgems3d.engine.graphics.opengl.environment.fog.Fog;
import ru.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.jgems3d.engine.graphics.opengl.environment.light.PointLight;
import ru.jgems3d.engine.graphics.opengl.environment.sky.Sky;
import ru.jgems3d.engine.graphics.opengl.particles.ParticlesEmitter;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.graphics.opengl.particles.objects.SimpleColoredParticle;
import ru.jgems3d.engine.graphics.opengl.particles.objects.base.ParticleFX;
import ru.jgems3d.engine.graphics.opengl.particles.objects.SimpleTexturedParticle;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.items.props.SceneProp;
import ru.jgems3d.engine.graphics.opengl.screen.JGemsScreen;
import ru.jgems3d.engine.graphics.opengl.screen.timer.JGemsTimer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.physics.entities.properties.controller.IControllable;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import ru.jgems3d.engine.physics.world.triggers.liquids.base.Liquid;
import ru.jgems3d.engine.physics.world.triggers.zones.base.ITriggerZone;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.controller.objects.IController;
import ru.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.jgems3d.engine.system.graph.Graph;
import ru.jgems3d.engine.system.map.navigation.pathgen.MapNavGraphGenerator;
import ru.jgems3d.engine.system.resources.assets.material.samples.packs.ParticleTexturePack;
import ru.jgems3d.engine.system.resources.localisation.Lang;
import ru.jgems3d.engine.system.resources.localisation.Localisation;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.collision.MeshCollisionData;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.settings.JGemsSettings;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.logger.managers.LoggingManager;

import java.util.List;

/**
 * Using the JGemsHelper class, you can conveniently access most of the most important functions for managing the state of the engine. This utility class is divided into sections for easier navigation.
 * @see ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils
 * @see ru.jgems3d.engine.system.resources.assets.models.helper.MeshHelper
 */

@SuppressWarnings("all")
public abstract class JGemsHelper {
    public static JGemsTimer createTimer() {
        return JGemsHelper.getScreen().getTimerPool().createTimer();
    }

    public static Player getCurrentPlayer() {
        return JGems3D.get().getPlayer();
    }

    public static JGemsScreen getScreen() {
        return JGems3D.get().getScreen();
    }

    public static SceneWorld getSceneWorld() {
        return JGems3D.get().getSceneWorld();
    }

    public static PhysicsWorld getPhysicsWorld() {
        return JGems3D.get().getPhysicsWorld();
    }

    public static JGems3D getCoreObject() {
        return JGems3D.get();
    }

    public static SoundManager getSoundManager() {
        return JGems3D.get().getSoundManager();
    }

    public static LoggingManager getLogger() {
        return SystemLogging.get().getLogManager();
    }

    // section Resources
    public static abstract class RESOURCES {
        public static GameResources getGlobalResources() {
            return JGemsHelper.RESOURCES.getJGemsResourceManager().getGlobalResources();
        }

        public static GameResources getLocalResources() {
            return JGemsHelper.RESOURCES.getJGemsResourceManager().getLocalResources();
        }

        public static JGemsResourceManager getJGemsResourceManager() {
            return JGems3D.get().getResourceManager();
        }

        public static void reloadResources() {
            JGems3D.get().reloadResources();
        }
    }

    // section Camera
    public static abstract class CAMERA {
        public static ICamera getCurrentCamera() {
            return JGemsHelper.getScreen().getCamera();
        }

        public static void enableFreeCamera(IController controller, Vector3f pos, Vector3f rot) {
            JGemsHelper.getScreen().getScene().setRenderCamera(new FreeCamera(controller, pos, rot));
        }

        public static void enableAttachedCamera(WorldItem worldItem) {
            JGemsHelper.getScreen().getScene().setRenderCamera(JGemsHelper.getSceneWorld().createAttachedCamera(worldItem));
        }

        public static void enableAttachedCamera(AbstractSceneEntity abstractSceneEntity) {
            JGemsHelper.getScreen().getScene().enableAttachedCamera(abstractSceneEntity);
        }
    }

    // section Localisation
    public static abstract class LOCALISATION {
        public static Lang createLocalisation(String langName, JGemsPath path) {
            return Localisation.createLocalisation(langName, path);
        }

        public static void setLangLocalisationPath(Lang lang, JGemsPath path) {
            Localisation.setLangLocalisationPath(lang, path);
        }

        public static Localisation getLocalisation() {
            return JGems3D.get().getLocalisation();
        }
    }

    // section Particles
    public static abstract class PARTICLES {
        public static SimpleTexturedParticle createSimpleTexturedParticle(ParticleAttributes particleAttributes, ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
            return ParticlesEmitter.createSimpleTexturedParticle(JGemsHelper.getSceneWorld(), particleAttributes, particleTexturePack, pos, scaling);
        }

        public static SimpleColoredParticle createSimpleColoredParticle(ParticleAttributes particleAttributes, Vector3f color, Vector3f pos, Vector2f scaling) {
            return ParticlesEmitter.createSimpleColoredParticle(JGemsHelper.getSceneWorld(), particleAttributes, color, pos, scaling);
        }

        public static ParticleFX emitParticle(ParticleFX particleFX) {
            JGemsHelper.PARTICLES.getParticlesEmitter().emitParticle(particleFX);
            return particleFX;
        }

        public static ParticlesEmitter getParticlesEmitter() {
            return JGemsHelper.getScreen().getScene().getSceneWorld().getParticlesEmitter();
        }
    }

    // section Controller
    public static abstract class ENVIRONMENT {
        public static Sky getSky() {
            return JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSky();
        }

        public static Fog getFog() {
            return JGemsHelper.ENVIRONMENT.getWorldEnvironment().getFog();
        }

        public static Environment getWorldEnvironment() {
            return JGemsHelper.getSceneWorld().getEnvironment();
        }
    }

    // section Controller
    public static abstract class CONTROLLER {
        public static boolean setCursorInCenter() {
            IController controller = getCurrentController();
            if (controller instanceof MouseKeyboardController) {
                MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) controller;
                mouseKeyboardController.setCursorInCenter();
                return true;
            }
            JGemsHelper.getLogger().warn("Couldn't find cursor. Check your controller!");
            return false;
        }

        public static void attachControllerTo(IController controller, IControllable remoteController) {
            JGemsHelper.CONTROLLER.getControllerDispatcher().attachControllerTo(controller, remoteController);
        }

        public static JGemsControllerDispatcher getControllerDispatcher() {
            return JGemsHelper.getScreen().getControllerDispatcher();
        }

        public static IController getCurrentController() {
            return JGemsHelper.CONTROLLER.getControllerDispatcher().getCurrentController();
        }

        public static void detachController() {
            JGemsHelper.CONTROLLER.getControllerDispatcher().detachController();
        }

        public static BindingManager bindingManager() {
            return JGemsControllerDispatcher.bindingManager();
        }
    }

    // section Game
    public static abstract class GAME {
        public static void killItems() {
            JGemsHelper.getPhysicsWorld().killItems();
        }

        public static void zeroRenderTick() {
            JGems3D.get().getScreen().zeroRenderTick();
        }

        public static void lockController() {
            JGems3D.get().lockController();
        }

        public static void unLockController() {
            JGems3D.get().unLockController();
        }

        public static void pauseGameAndLockUnPausing(boolean pauseSounds) {
            JGems3D.get().pauseGameAndLockUnPausing(pauseSounds);
        }

        public static IMapLoader getCurrentMap() {
            return JGems3D.get().getEngineSystem().getMapLoader();
        }

        public static void unPauseGameAndUnLockUnPausing() {
            JGems3D.get().unPauseGameAndUnLockUnPausing();
        }

        public static void pauseGame(boolean pauseSounds) {
            JGems3D.get().pauseGame(pauseSounds);
        }

        public static void unPauseGame() {
            JGems3D.get().unPauseGame();
        }

        public static void loadMap(IMapLoader mapLoader) {
            JGems3D.get().loadMap(mapLoader);
        }

        public static void destroyMap() {
            JGems3D.get().destroyMap();
        }

        public static void destroyGame() {
            JGems3D.get().destroyGame();
        }

        public static JGemsSettings getGameSettings() {
            return JGems3D.get().getGameSettings();
        }
    }

    // section Window
    public static abstract class WINDOW {
        public static void setWindowFocus(boolean focus) {
            JGemsHelper.getScreen().getWindow().setInFocus(focus);
        }

        public static boolean isWindowActive() {
            return JGemsHelper.getScreen().getWindow().isWindowActive();
        }
    }

    // section UI
    public static abstract class UI {
        public static void removeUIPanel() {
            JGems3D.get().removeUIPanel();
        }

        public static void openUIPanel(PanelUI ui) {
            JGems3D.get().openUIPanel(ui);
        }
    }

    // section World
    public static abstract class WORLD {
        public static Graph genSimpleMapGraphFromStartPoint(Vector3f start) {
            return MapNavGraphGenerator.createGraphWithStartPoint(JGems3D.get().getPhysicThreadManager().getPhysicsTimer().getDynamicsSystem(), DynamicsUtils.convertV3F_JME(start));
        }

        public static void removePropFromScene(SceneProp sceneProp) {
            JGems3D.get().getScreen().getScene().getSceneWorld().removeObjectFromWorld(sceneProp);
        }

        public static void addPropInScene(SceneProp sceneProp) {
            JGems3D.get().getScreen().getScene().getSceneWorld().addObjectInWorld(sceneProp);
        }

        public static void removeItemFromWorld(WorldItem worldItem) {
            worldItem.setDead();
        }

        public static void addItemInWorld(WorldItem worldItem, RenderEntityData renderData) {
            JGemsHelper.getPhysicsWorld().addItem(worldItem);
            JGemsHelper.getSceneWorld().addItem(worldItem, renderData);
        }

        public static void addPointLight(WorldItem worldItem, PointLight light, int attachShadowScene) {
            JGemsHelper.WORLD.addLight(worldItem, light);
            JGemsHelper.getScreen().getScene().getSceneRenderer().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
        }

        public static void addPointLight(PointLight light, int attachShadowScene) {
            JGemsHelper.WORLD.addLight(light);
            JGemsHelper.getScreen().getScene().getSceneRenderer().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
        }

        public static void addLiquid(Liquid liquid, RenderLiquidData renderLiquidData) {
            JGemsHelper.getPhysicsWorld().addItem(liquid);
            JGemsHelper.getSceneWorld().addLiquid(liquid, renderLiquidData);
        }

        public static void addTriggerZone(ITriggerZone triggerZone) {
            JGemsHelper.getPhysicsWorld().addItem(triggerZone);
        }

        public static void addLight(WorldItem worldItem, Light light) {
            JGemsHelper.getSceneWorld().addWorldItemLight(worldItem, light);
        }

        public static void addLight(Light light) {
            light.start();
            JGemsHelper.ENVIRONMENT.getWorldEnvironment().getLightManager().addLight(light);
        }
    }

    public static abstract class MATH {

        public static float lerp(float a, float b, float f)
        {
            return a + f * (b - a);
        }

        public static int clamp(int d1, int d2, int d3) {
            return d1 < d2 ? d2 : (int) Math.min(d1, d3);
        }

        public static float clamp(float d1, float d2, float d3) {
            return d1 < d2 ? d2 : Math.min(d1, d3);
        }

        public static double clamp(double d1, double d2, double d3) {
            return d1 < d2 ? d2 : Math.min(d1, d3);
        }
    }

    //section Utils
    public static abstract class UTILS {
        public static float calcDistanceToMostFarPoint(MeshDataGroup meshDataGroup, Vector3f scaling) {
            float max = Float.MIN_VALUE;

            for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
                List<Float> floats = modelNode.getMesh().getAttributePositions();
                for (int i = 0; i < floats.size(); i += 3) {
                    float i1 = floats.get(i);
                    float i2 = floats.get(i + 1);
                    float i3 = floats.get(i + 2);

                    float scaledX = i1 * scaling.x;
                    float scaledY = i2 * scaling.y;
                    float scaledZ = i3 * scaling.z;

                    float length = (float) Math.sqrt(scaledX * scaledX + scaledY * scaledY + scaledZ * scaledZ);

                    if (length > max) {
                        max = length;
                    }
                }
            }
            return max;
        }

        public static int[] convertIntsArray(List<Integer> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            int[] a = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                a[i] = list.get(i);
            }
            return a;
        }

        public static float[] convertFloatsArray(List<Float> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            float[] a = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                a[i] = list.get(i);
            }
            return a;
        }

        public static float[] convertFloats3Array(List<Vector3f> list) {
            if (list == null || list.isEmpty()) {
                return null;
            }
            float[] a = new float[list.size() * 3];
            for (int i = 0; i < list.size(); i += 3) {
                a[i] = list.get(i).x;
                a[i + 1] = list.get(i).y;
                a[i + 2] = list.get(i).z;
            }
            return a;
        }

        public static Vector3f convertV3DV3F(Vector3f vector3f) {
            return new Vector3f(vector3f.x, vector3f.y, vector3f.z);
        }

        public static Vector3f convertV3FV3D(Vector3f vector3f) {
            return new Vector3f(vector3f);
        }

        public static Vector3f calcLookVector(Vector3f rotations) {
            float x = rotations.x;
            float y = rotations.y;
            float lX = Math.sin(y) * Math.cos(x);
            float lY = -Math.sin(x);
            float lZ = -Math.cos(y) * Math.cos(x);
            return new Vector3f(lX, lY, lZ);
        }

        @SuppressWarnings("all")
        public static boolean createMeshCollisionData(MeshDataGroup meshDataGroup) {
            if (meshDataGroup != null && meshDataGroup.getMeshUserData() == null) {
                meshDataGroup.setMeshUserData(new MeshCollisionData(meshDataGroup));
                return true;
            }
            return false;
        }
    }
}
