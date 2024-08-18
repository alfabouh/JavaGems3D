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

package ru.jgems3d.toolbox.render.scene;

import javafx.util.Pair;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.FBOTexture2DProgram;
import ru.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import ru.jgems3d.engine.graphics.opengl.screen.window.IWindow;
import ru.jgems3d.engine.graphics.transformation.TransformationUtils;
import ru.jgems3d.engine.system.resources.assets.material.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.helper.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.service.exceptions.JGemsNullException;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.logger.managers.LoggingManager;
import ru.jgems3d.toolbox.map_sys.read.TBoxMapReader;
import ru.jgems3d.toolbox.map_sys.save.TBoxMapSaver;
import ru.jgems3d.toolbox.map_sys.save.container.TBoxMapContainer;
import ru.jgems3d.toolbox.map_sys.save.objects.MapProperties;
import ru.jgems3d.toolbox.map_sys.save.objects.SaveObject;
import ru.jgems3d.toolbox.map_table.TBoxMapTable;
import ru.jgems3d.toolbox.map_table.object.AbstractObjectData;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.Attribute;
import ru.jgems3d.toolbox.map_sys.save.objects.object_attributes.AttributeID;
import ru.jgems3d.toolbox.ToolBox;
import ru.jgems3d.toolbox.render.scene.camera.TBoxFreeCamera;
import ru.jgems3d.toolbox.render.scene.container.SceneContainer;
import ru.jgems3d.toolbox.render.scene.dear_imgui.DIMGuiRenderTBox;
import ru.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;
import ru.jgems3d.toolbox.render.scene.dear_imgui.content.ImGuiContent;
import ru.jgems3d.toolbox.render.scene.items.objects.TBoxObject;
import ru.jgems3d.toolbox.render.scene.items.objects.base.TBoxScene3DObject;
import ru.jgems3d.toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;
import ru.jgems3d.toolbox.render.scene.utils.TBoxSceneUtils;
import ru.jgems3d.toolbox.resources.TBoxResourceManager;
import ru.jgems3d.toolbox.resources.samples.TextureSample;
import ru.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TBoxScene {
    public static FBOTexture2DProgram sceneFbo;
    public static FBOTexture2DProgram previewItemFbo;
    private final SceneContainer sceneObjects;
    private final IWindow window;
    private final TransformationUtils transformationUtils;
    private DIMGuiRenderTBox dimGuiRenderTBox;
    private ICamera camera;

    public TBoxScene(TransformationUtils transformationUtils, IWindow window) {
        this.sceneObjects = new SceneContainer();
        this.transformationUtils = transformationUtils;
        this.window = window;
    }

    public void resetEditor() {
        SystemLogging.get().getLogManager().log("Resetting editor...");
        this.clear();
        this.setGUIEditor();
    }

    public void tryLoadMap(File file) {
        TBoxMapContainer mapContainer;
        try {
            if (file == null || !Files.exists(file.toPath())) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Read");
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int userSelection = jFileChooser.showOpenDialog(null);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    file = jFileChooser.getSelectedFile();
                }
            }
            if (file != null) {
                mapContainer = TBoxMapReader.readMapFolder(file);

                ToolBox.get().getTBoxSettings().recentPathOpen.setValue(new File(file.toString()).getAbsolutePath());

                MapProperties mapObjectProperties = mapContainer.getSaveMapProperties();
                if (mapObjectProperties == null) {
                    throw new JGemsNullException("Invalid deserialization!");
                }

                if (mapObjectProperties.getMapName() == null || mapObjectProperties.getMapName().isEmpty()) {
                    throw new JGemsNullException("Invalid path provided");
                }

                Set<SaveObject> saveObjects = mapContainer.getSaveObjectsSet();
                this.clear();

                if (saveObjects != null) {
                    for (SaveObject saveObject : saveObjects) {
                        try {
                            Vector3f savePos = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
                            Vector3f saveRot = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
                            Vector3f saveScale = saveObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);
                            if (savePos == null) {
                                SystemLogging.get().getLogManager().error("Deserialized object has NULL position!!");
                                continue;
                            }
                            if (saveRot != null) {
                                saveRot = new Vector3f(saveRot);
                            }
                            Format3D format3D = new Format3D(savePos, saveRot, saveScale);
                            AbstractObjectData mapObject = TBoxMapTable.INSTANCE.getObjectTable().getObjects().get(saveObject.getObjectId());
                            MeshDataGroup meshDataGroup = mapObject.meshDataGroup();
                            TBoxObject tBoxModelObject = new TBoxObject(saveObject.getObjectId(), new TBoxObjectRenderData(mapObject.getShaderManager(), mapObject.getObjectRenderer()), new Model<>(format3D, meshDataGroup));
                            tBoxModelObject.setAttributeContainer(saveObject.getAttributeContainer());
                            this.addObject(tBoxModelObject);
                        } catch (NullPointerException e) {
                            e.printStackTrace(System.err);
                        }
                    }
                } else {
                    SystemLogging.get().getLogManager().error("Couldn't read objects path from map!");
                    LoggingManager.showExceptionDialog("Couldn't read objects path from map!");
                }

                this.getSceneContainer().setMapProperties(mapObjectProperties);
            }
        } catch (Exception e) {
            SystemLogging.get().getLogManager().exception(e);
            LoggingManager.showExceptionDialog("Found errors, while reading map!");
        }
    }

    public void prepareMapToSave(File file) {
        if (this.getMapProperties().getMapName() == null || this.getMapProperties().getMapName().isEmpty()) {
            LoggingManager.showWindowInfo("Enter map name!");
            return;
        }

        TBoxMapContainer TBoxMapContainer = new TBoxMapContainer(this.getSceneContainer().getMapProperties());

        for (TBoxObject tBoxObject : this.getSceneContainer().getObjectsFromContainer(TBoxObject.class)) {
            TBoxMapContainer.addSaveObject(new SaveObject(tBoxObject.getAttributeContainer(), tBoxObject.objectId()));
        }

        try {
            if (file == null || !Files.exists(file.toPath())) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Save");
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int userSelection = jFileChooser.showSaveDialog(null);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    file = jFileChooser.getSelectedFile();
                }
            }
            if (file != null) {
                TBoxMapSaver.saveEditorToJSON(TBoxMapContainer, file);
                String path = new File(file.toString()).getAbsolutePath();
                ToolBox.get().getTBoxSettings().recentPathSave.setValue(path);
            }
        } catch (Exception e) {
            SystemLogging.get().getLogManager().exception(e);
            LoggingManager.showExceptionDialog("Found errors, while saving map!");
        }
    }

    public void preRender() {
        this.createFBOs(this.getWindow().getWindowDimensions());
        this.camera = new TBoxFreeCamera(ToolBox.get().getScreen().getControllerDispatcher().getCurrentController(), new Vector3f(-0.0f), new Vector3f(0.0f));
        this.setGUIEditor();
        SystemLogging.get().getLogManager().log("Pre-Scene Render");

        String recentStrOpen = ToolBox.get().getTBoxSettings().recentPathOpen.getValue();
        if (!recentStrOpen.isEmpty()) {
            this.tryLoadMap(new File(recentStrOpen));
        }
    }

    public void postRender() {
        SystemLogging.get().getLogManager().log("Post-Scene Render");
        this.clear();
        this.destroyFBOs();
        this.getDimGuiRenderTBox().cleanUp();
        ToolBox.get().getScreen().getResourceManager().destroy();
    }

    public void render(float deltaTime) {
        if (!this.isActiveScene()) {
            return;
        }
        this.getCamera().updateCamera(deltaTime);
        TBoxScene.sceneFbo.bindFBO();
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        if (this.getDimGuiRenderTBox().getCurrentContentToRender() instanceof EditorContent) {
            EditorContent editorContent = (EditorContent) this.getDimGuiRenderTBox().getCurrentContentToRender();
            this.getSceneContainer().render(deltaTime);
            Vector3f v3 = this.getMapProperties().getSkyProp().getSunPos();
            Model<Format3D> modelSun = MeshHelper.generateVector3DModel3f(new Vector3f(0.0f), new Vector3f(v3.x, v3.y, v3.z).mul(300.0f));
            TBoxResourceManager.shaderAssets.world_lines.bind();
            TBoxResourceManager.shaderAssets.world_lines.getUtils().performPerspectiveMatrix();
            TBoxResourceManager.shaderAssets.world_lines.getUtils().performViewMatrix(TBoxSceneUtils.getMainCameraViewMatrix());
            TBoxResourceManager.shaderAssets.world_lines.performUniform(new UniformString("colour"), new Vector4f(1.0f, 1.0f, 0.0f, 1.0f));
            TBoxSceneUtils.renderModel(modelSun, GL30.GL_LINES);
            TBoxResourceManager.shaderAssets.world_lines.unBind();
            modelSun.clean();
            if (editorContent.currentSelectedObject != null) {
                Model<Format3D> model = MeshHelper.generateWirebox3DModel(JGemsHelper.UTILS.convertV3DV3F(editorContent.currentSelectedObject.getLocalCollision().getAabb().getMin()), JGemsHelper.UTILS.convertV3DV3F(editorContent.currentSelectedObject.getLocalCollision().getAabb().getMax()));
                TBoxResourceManager.shaderAssets.world_lines.bind();
                TBoxResourceManager.shaderAssets.world_lines.getUtils().performPerspectiveMatrix();
                TBoxResourceManager.shaderAssets.world_lines.getUtils().performViewMatrix(TBoxSceneUtils.getMainCameraViewMatrix());
                TBoxResourceManager.shaderAssets.world_lines.performUniform(new UniformString("colour"), new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
                TBoxSceneUtils.renderModel(model, GL30.GL_LINES);
                TBoxResourceManager.shaderAssets.world_lines.unBind();
                model.clean();
            }
        }
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        this.showXYZ();
        TBoxScene.sceneFbo.unBindFBO();

        ImGuiContent content = this.getDimGuiRenderTBox().getCurrentContentToRender();
        if (content instanceof EditorContent) {
            EditorContent editorContent = (EditorContent) content;
            String s = editorContent.currentSelectedToPlaceID;
            if (s != null) {
                AbstractObjectData mapObject = TBoxMapTable.INSTANCE.getObjectTable().getObjects().get(s);
                TBoxScene.previewItemFbo.bindFBO();
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
                GL30.glEnable(GL30.GL_DEPTH_TEST);
                this.renderIsometricEditorItem(mapObject, editorContent.previewBorders[0]);
                GL30.glDisable(GL30.GL_DEPTH_TEST);
                TBoxScene.previewItemFbo.unBindFBO();
            }
        }

        this.getDimGuiRenderTBox().render(deltaTime);
    }

    private void showXYZ() {
        GL30.glClear(GL30.GL_DEPTH_BUFFER_BIT);
        GL30.glEnable(GL30.GL_DEPTH_TEST);
        Format3D format3D = new Format3D();
        Model<Format3D> model = new Model<>(format3D, ToolBox.get().getResourceManager().getModelResources().xyz);
        Quaternionf quaterniond = new Quaternionf();
        Matrix4f Matrix4f = TBoxSceneUtils.getMainCameraViewMatrix();
        Matrix4f.getNormalizedRotation(quaterniond);
        Matrix4f inversedView = new Matrix4f().identity().rotate(quaterniond);
        TBoxResourceManager.shaderAssets.world_xyz.bind();
        TBoxResourceManager.shaderAssets.world_xyz.getUtils().performOrthographicMatrix(this.getWindow().getWindowDimensions().x / (float) this.getWindow().getWindowDimensions().y, 36.0f);
        TBoxResourceManager.shaderAssets.world_xyz.getUtils().performModel3DMatrix(model);
        TBoxResourceManager.shaderAssets.world_xyz.performUniform(new UniformString("view_inversed"), inversedView);
        TBoxSceneUtils.renderModelTextured(TBoxResourceManager.shaderAssets.world_xyz, model, GL30.GL_TRIANGLES);
        TBoxResourceManager.shaderAssets.world_xyz.unBind();
        GL30.glDisable(GL30.GL_DEPTH_TEST);
    }

    private void renderIsometricEditorItem(AbstractObjectData mapObject, float borders) {
        GL30.glViewport(0, 0, 400, 400);
        TBoxShaderManager shaderManager = TBoxResourceManager.shaderAssets.world_isometric_object;
        shaderManager.bind();
        shaderManager.getUtils().performOrthographicMatrix(1.0f, borders);
        shaderManager.getUtils().performModel3DMatrix(new Matrix4f().identity().lookAt(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.0f), new Vector3f(0.0f, 1.0f, 0.0f)));
        TBoxScene.renderIsometricModel(shaderManager, mapObject.meshDataGroup(), GL30.GL_TRIANGLES);
        shaderManager.unBind();
        ToolBox.get().getScreen().normalizeViewPort();
    }

    @SuppressWarnings("all")
    public static void renderIsometricModel(TBoxShaderManager shaderManager, MeshDataGroup meshDataGroup, int code) {
        for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
            if (modelNode.getMaterial().getDiffuse() instanceof ColorSample) {
                shaderManager.performUniform(new UniformString("use_texture"), false);
            } else {
                shaderManager.performUniform(new UniformString("use_texture"), true);
                shaderManager.performUniformNoWarn(new UniformString("diffuse_map"), 0);
                GL30.glActiveTexture(GL30.GL_TEXTURE0);
                GL30.glBindTexture(GL11.GL_TEXTURE_2D, ((TextureSample) modelNode.getMaterial().getDiffuse()).getTextureId());
            }
            GL30.glBindVertexArray(modelNode.getMesh().getVao());
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glEnableVertexAttribArray(a);
            }
            GL30.glDrawElements(code, modelNode.getMesh().getTotalVertices(), GL30.GL_UNSIGNED_INT, 0);
            for (int a : modelNode.getMesh().getAttributePointers()) {
                GL30.glDisableVertexAttribArray(a);
            }
            GL30.glBindVertexArray(0);
        }
    }

    public boolean tryGrabObject(EditorContent editorContent) {
        Vector2f sceneFrameMin = editorContent.getSceneFrameMin();
        Vector2f sceneFrameMax = editorContent.getSceneFrameMax();
        Vector2f mouseCVector = new Vector2f(ToolBox.get().getScreen().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().getCursorCoordinatesV2F());

        Vector3f camTo = this.getMouseRay(mouseCVector, sceneFrameMin, sceneFrameMax).mul(100.0f);
        Vector3f camPos = this.getCamera().getCamPosition();

        Set<TBoxScene3DObject> intersectedAABBs = this.getSceneContainer().getSceneObjects().stream().filter(e -> e.getLocalCollision().isRayIntersectObjectAABB(camPos, camTo)).collect(Collectors.toSet());
        List<Pair<Vector3f, TBoxScene3DObject>> intersections = intersectedAABBs.stream().map(obj -> new Pair<>(obj.getLocalCollision().findClosesPointRayIntersectObjectMesh(obj.getModel().getFormat(), camPos, camTo), obj)).filter(pair -> pair.getKey() != null).sorted(Comparator.comparingDouble(pair -> pair.getKey().distance(camPos))).collect(Collectors.toList());

        if (intersections.isEmpty()) {
            editorContent.removeSelection();
            return false;
        }

        TBoxScene3DObject closestObject = intersections.get(0).getValue();
        if (!editorContent.trySelectObject(closestObject)) {
            throw new JGemsRuntimeException("Occurred error while trying to select NULL object");
        }

        return true;
    }

    public void placeObjectFromGUI(EditorContent editorContent, String nameId) {
        AbstractObjectData mapObject = TBoxMapTable.INSTANCE.getObjectTable().getObjects().get(nameId);
        MeshDataGroup meshDataGroup = mapObject.meshDataGroup();
        Vector3f camRot = this.getCamera().getCamRotation();
        Vector3f camPos = this.getCamera().getCamPosition();
        Vector3f camTo = JGemsHelper.UTILS.calcLookVector(camRot).normalize();

        Format3D format3D = new Format3D();
        format3D.setPosition(new Vector3f(camPos).add(JGemsHelper.UTILS.calcLookVector(camRot).mul(5.0f)));

        Set<TBoxScene3DObject> intersectedAABBs = this.getSceneContainer().getSceneObjects().stream().filter(obj -> obj.getLocalCollision().isRayIntersectObjectAABB(camPos, camTo)).collect(Collectors.toSet());
        List<Vector3f> intersections = intersectedAABBs.stream().map(obj -> obj.getLocalCollision().findClosesPointRayIntersectObjectMesh(obj.getModel().getFormat(), camPos, camTo)).filter(Objects::nonNull).filter(e -> e.distance(camPos) < 15.0f).sorted(Comparator.comparingDouble(e -> e.distance(camPos))).collect(Collectors.toList());

        if (!intersections.isEmpty()) {
            Vector3f closestObject = intersections.get(0);
            format3D.setPosition(closestObject);
        }

        TBoxObject tBoxObject = new TBoxObject(nameId, new TBoxObjectRenderData(mapObject.getShaderManager(), mapObject.getObjectRenderer()), new Model<>(format3D, meshDataGroup));
        tBoxObject.setAttributeContainer(mapObject.copyAttributeContainer());

        Attribute<Vector3f> attribute = tBoxObject.getAttributeContainer().tryGetAttributeByID(AttributeID.POSITION_XYZ, Vector3f.class);
        if (attribute == null) {
            throw new JGemsNullException("Caught attribute with NULL position!");
        }
        attribute.setValue(format3D.getPosition());

        Vector3f rot = tBoxObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.ROTATION_XYZ, Vector3f.class);
        if (rot != null) {
            format3D.setRotation(rot);
        }

        Vector3f scaling = tBoxObject.getAttributeContainer().tryGetValueFromAttributeByID(AttributeID.SCALING_XYZ, Vector3f.class);
        if (scaling != null) {
            format3D.setScaling(scaling);
        }

        tBoxObject.reCalcCollision();
        this.addObject(tBoxObject);
    }

    public Vector3f getMouseRay(Vector2f mouseCoordinates, Vector2f sceneFrameMin, Vector2f sceneFrameMax) {
        Matrix4f projectionMatrix = TBoxSceneUtils.getMainPerspectiveMatrix();
        Matrix4f viewMatrix = TBoxSceneUtils.getMainCameraViewMatrix();

        float normalizedX = 2.0f * (mouseCoordinates.x - sceneFrameMin.x) / (sceneFrameMax.x - sceneFrameMin.x) - 1.0f;
        float normalizedY = 1.0f - 2.0f * (mouseCoordinates.y - sceneFrameMin.y) / (sceneFrameMax.y - sceneFrameMin.y);

        Vector4f clipCoordinates = new Vector4f(normalizedX, normalizedY, -1.0f, 1.0f);

        Matrix4f invertedProjection = projectionMatrix.invert();
        Vector4f eyeCoordinates = invertedProjection.transform(clipCoordinates);
        eyeCoordinates.z = -1.0f;
        eyeCoordinates.w = 0.0f;

        Matrix4f invertedView = viewMatrix.invert();
        Vector4f worldCoordinates = invertedView.transform(eyeCoordinates);

        Vector3f mouseRay = new Vector3f(worldCoordinates.x, worldCoordinates.y, worldCoordinates.z);
        mouseRay.normalize();

        return mouseRay;
    }

    public boolean isActiveScene() {
        return this.getWindow().isWindowActive();
    }

    public void createGUI() {
        this.dimGuiRenderTBox = new DIMGuiRenderTBox(this.getWindow(), ToolBox.get().getResourceManager().getCache());
    }

    private void setGUIEditor() {
        this.getDimGuiRenderTBox().setCurrentContentToRender(new EditorContent(this));
    }

    private void createFBOs(Vector2i dim) {
        T2DAttachmentContainer fbo = new T2DAttachmentContainer() {{
            add(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_RGBA, GL30.GL_RGBA);
        }};
        TBoxScene.sceneFbo = new FBOTexture2DProgram(true);
        TBoxScene.previewItemFbo = new FBOTexture2DProgram(true);
        TBoxScene.sceneFbo.createFrameBuffer2DTexture(dim, fbo, true, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
        TBoxScene.previewItemFbo.createFrameBuffer2DTexture(new Vector2i(400, 400), fbo, false, GL30.GL_NEAREST, GL30.GL_COMPARE_REF_TO_TEXTURE, GL30.GL_LESS, GL30.GL_CLAMP_TO_BORDER, null);
    }

    private void destroyFBOs() {
        TBoxScene.sceneFbo.clearFBO();
        TBoxScene.previewItemFbo.clearFBO();
    }

    private void clear() {
        this.getSceneContainer().clear();
        this.getSceneContainer().createMapProperties();
    }

    public void removeObject(TBoxScene3DObject scene3DObject) {
        this.getSceneContainer().removeObject(scene3DObject);
    }

    public void addObject(TBoxScene3DObject scene3DObject) {
        this.getSceneContainer().addObject(scene3DObject);
    }

    public void onWindowResize(Vector2i dim) {
        this.getDimGuiRenderTBox().onResize(dim);

        this.destroyFBOs();
        this.createFBOs(dim);
    }

    public MapProperties getMapProperties() {
        return this.getSceneContainer().getMapProperties();
    }

    public DIMGuiRenderTBox getDimGuiRenderTBox() {
        return this.dimGuiRenderTBox;
    }

    public ICamera getCamera() {
        return this.camera;
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
    }

    public TransformationUtils getTransformationUtils() {
        return this.transformationUtils;
    }

    public IWindow getWindow() {
        return this.window;
    }

    public SceneContainer getSceneContainer() {
        return this.sceneObjects;
    }
}
