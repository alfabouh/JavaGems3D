package ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class FreeCamera extends Camera {
    private IController controller;
    private float speed;

    public FreeCamera(IController controller, Vector3f pos, Vector3f rot) {
        super(pos, rot);
        SystemLogging.get().getLogManager().log("Created free camera at: " + pos);
        this.controller = controller;
        this.speed = this.camDefaultSpeed();
    }

    public void setCameraPos(Vector3f Vector3f) {
        super.setCameraPos(Vector3f);
    }

    public void setCameraRot(Vector3f Vector3f) {
        super.setCameraRot(Vector3f);
    }

    @Override
    public void updateCamera(float deltaTicks) {
        if (this.getController() != null) {
            this.moveCamera(this.moveCameraPosInput().mul(deltaTicks));
            this.moveCameraRot(this.moveCameraRotInput());
        }
        if (this.camRotation.x > 90) {
            this.camRotation.x = 90;
        }
        if (this.camRotation.x < -90) {
            this.camRotation.x = -90;
        }
    }

    protected Vector3f moveCameraPosInput() {
        Vector3f Vector3f = JGemsControllerDispatcher.getNormalizedPositionInput(this.getController());
        if (Vector3f.length() != 0.0f) {
            Vector3f.normalize();
        }
        return Vector3f;
    }

    protected Vector2f moveCameraRotInput() {
        return JGemsControllerDispatcher.getNormalizedRotationInput(this.getController());
    }

    public void addCameraPos(Vector3f Vector3f) {
        super.setCameraPos(this.getCamPosition().add(Vector3f));
    }

    public void addCameraRot(Vector3f Vector3f) {
        super.setCameraRot(this.getCamRotation().add(Vector3f));
    }

    public IController getController() {
        return this.controller;
    }

    public void setController(IController controller) {
        this.controller = controller;
    }

    protected void moveCameraRot(Vector2f xy) {
        this.addCameraRot(new Vector3f(xy, 0));
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float camSpeed() {
        return this.speed;
    }

    protected float camDefaultSpeed() {
        return 5.0f;
    }

    protected void moveCamera(Vector3f direction) {
        float[] motion = new float[3];
        float[] input = new float[3];
        input[0] = direction.x;
        input[1] = direction.y;
        input[2] = direction.z;
        if (input[2] != 0) {
            motion[0] += (float) (Math.sin(Math.toRadians(this.getCamRotation().y)) * -1.0f * input[2]);
            motion[2] += (float) (Math.cos(Math.toRadians(this.getCamRotation().y)) * input[2]);
        }
        if (input[0] != 0) {
            motion[0] += (float) (Math.sin(Math.toRadians(this.getCamRotation().y - 90)) * -1.0f * input[0]);
            motion[2] += (float) (Math.cos(Math.toRadians(this.getCamRotation().y - 90)) * input[0]);
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        this.addCameraPos(new Vector3f(motion[0], motion[1], motion[2]).mul(this.camSpeed()));
    }
}
