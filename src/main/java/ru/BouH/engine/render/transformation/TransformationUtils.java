package ru.BouH.engine.render.transformation;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format2D;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.render.scene.world.camera.ICamera;

public class TransformationUtils {
    private final Matrix4d viewMatrix;

    public TransformationUtils() {
        this.viewMatrix = new Matrix4d();
    }

    public final Matrix4d getModelMatrix(Model<Format3D> model, boolean invertRotations) {
        return this.getModelMatrix(model.getFormat(), invertRotations);
    }

    public final Matrix4d getModelMatrix(Format3D format3D, boolean invertRotations) {
        Vector3d rotation = new Vector3d(format3D.getRotation());
        Vector3d position = new Vector3d(format3D.getPosition());
        Matrix4d m1 = new Matrix4d();
        return m1.identity().translate(position).rotateXYZ(invertRotations ? rotation.x : -rotation.x, invertRotations ? rotation.y : -rotation.y, invertRotations ? rotation.z : -rotation.z).scale(format3D.getScale());
    }

    public final Matrix4d getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().perspective(fov, width / height, zNear, zFar);
    }

    public final Matrix4d getOrthographicMatrix(float left, float right, float bottom, float top) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setOrtho2D(left, right, bottom, top);
    }

    public final Matrix4d getLookAtMatrix(Vector3d eye, Vector3d up, Vector3d destination) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setLookAt(eye, destination, up);
    }

    public final Matrix4d getOrthographicMatrix(double left, double right, double bottom, double top, double zNear, double zFar, boolean zZeroToOne) {
        Matrix4d m1 = new Matrix4d();
        return m1.identity().setOrtho(left, right, bottom, top, zNear, zFar, zZeroToOne);
    }

    public Matrix4d getOrthoModelMatrix(Model<Format2D> model, Matrix4d orthoMatrix) {
        Vector2d rotation = model.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(new Vector3d(model.getFormat().getPosition(), 0.0d)).rotateX(-rotation.x).rotateY(-rotation.y).scaleXY(model.getFormat().getScale().x, model.getFormat().getScale().y);
        Matrix4d viewCurr = new Matrix4d(orthoMatrix);
        return viewCurr.mul(m1);
    }

    public Matrix4d getModelViewMatrix(Model<Format3D> model, Matrix4d viewMatrix) {
        Vector3d rotation = model.getFormat().getRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().translate(model.getFormat().getPosition()).rotateXYZ(-rotation.x, -rotation.y, -rotation.z).scale(model.getFormat().getScale());
        if (model.getFormat().isOrientedToViewMatrix()) {
            viewMatrix.transpose3x3(m1);
        }
        Matrix4d viewCurr = new Matrix4d(viewMatrix);
        return viewCurr.mul(m1);
    }

    public void updateViewMatrix(ICamera camera) {
        this.viewMatrix.set(this.buildViewMatrix(camera, null));
    }

    public Matrix4d buildViewMatrix(ICamera camera, Vector3d offset) {
        Vector3d cameraPos = camera.getCamPosition();
        Vector3d cameraRot = camera.getCamRotation();
        Matrix4d m1 = new Matrix4d();
        m1.identity().rotateXYZ(Math.toRadians(cameraRot.x), Math.toRadians(cameraRot.y), Math.toRadians(cameraRot.z)).translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        if (offset != null) {
            m1.translate(MathHelper.calcLookVector(cameraRot).mul(offset));
        }
        return m1;
    }

    public Matrix4d getMainCameraViewMatrix() {
        return new Matrix4d(this.viewMatrix);
    }
}
