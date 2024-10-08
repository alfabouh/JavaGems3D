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

package javagems3d.engine.physics.entities.player;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.objects.infos.CharacterController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import javagems3d.engine.JGems3D;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.audio.sound.data.SoundType;
import javagems3d.engine.physics.entities.IBtEntity;
import javagems3d.engine.physics.entities.properties.collision.CollisionFilter;
import javagems3d.engine.physics.entities.properties.state.EntityState;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.physics.world.basic.IWorldTicked;
import javagems3d.engine.physics.world.thread.PhysicsThread;
import javagems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.engine.physics.world.triggers.ITriggerAction;
import javagems3d.engine.system.controller.objects.IController;
import javagems3d.engine.system.inventory.IInventoryOwner;
import javagems3d.engine.system.inventory.Inventory;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class SimpleKinematicPlayer extends Player implements IInventoryOwner, IWorldTicked, IBtEntity {
    private final Vector3f cameraRotation;
    private final Deque<Vector3f> inputMotion;
    protected float stepHeight = 0.3f;
    protected float maxSlope = 45.0f;
    private EntityState entityState;
    private IController controller;
    private Inventory inventory;
    private PhysicsCharacter physicsCharacter;
    private CharacterController characterController;

    public SimpleKinematicPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot, "player_sp");
        this.cameraRotation = new Vector3f(rot);
        this.inputMotion = new ArrayDeque<>();
        this.entityState = new EntityState();
        this.createInventory();
    }

    protected void createInventory() {
        this.inventory = new Inventory(this, 4);
    }

    public void createPlayer() {
        this.changeYStartPos();

        ConvexShape convexShape = new CapsuleCollisionShape(this.capsuleSize().x, this.capsuleSize().y);

        this.physicsCharacter = new PhysicsCharacter(convexShape, 0.0f);
        this.characterController = new CharacterController(this.getPhysicsCharacter());
        this.getPhysicsCharacter().setUserObject(this);
        this.getPhysicsCharacter().warp(DynamicsUtils.convertV3F_JME(this.startPos));

        this.getPhysicsCharacter().setStepHeight(this.stepHeight);
        this.getPhysicsCharacter().setMaxSlope((float) Math.toRadians(this.maxSlope));

        this.setCollisionGroup(CollisionFilter.PLAYER);
        this.setCollisionFilter(CollisionFilter.ALL);
    }

    protected void changeYStartPos() {
        this.startPos.y += this.height();
    }

    protected Vector2f capsuleSize() {
        return new Vector2f(0.3f, 0.8f);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        PhysicsWorld world = (PhysicsWorld) iWorld;
        this.createPlayer();
        world.getDynamics().addCollisionObject(this.getPhysicsCharacter());
    }

    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        PhysicsWorld world = (PhysicsWorld) iWorld;
        world.getDynamics().removeCollisionObject(this.getPhysicsCharacter());
    }

    @Override
    public final void onUpdate(IWorld iWorld) {
        this.onTick(iWorld);
        this.getEntityState().removeState(EntityState.Type.IN_LIQUID);
    }

    protected void onTick(IWorld iWorld) {
        final float speed = this.walkSpeed();

        if (this.getPosition().y < -50.0f) {
            this.resetWarp();
        }
        if (this.getEntityState().checkState(EntityState.Type.IN_LIQUID)) {
            this.swim(this.calcControllerMotion(), speed);
        } else {
            this.walk(this.calcControllerMotion(), speed);
        }
    }

    protected void swim(Vector3f dir, float speed) {
        com.jme3.math.Vector3f vDir = new com.jme3.math.Vector3f(0.0f, 0.0f, 0.0f);
        float vY = dir.y;
        if (dir.length() > 0.0f) {
            float factor1 = this.getLiquidSwimUpFactor();
            if (this.getPhysicsCharacter().onGround()) {
                float slow = 1.0f - Math.max(factor1, 0.0f);
                this.slowDownLinearVelocity(slow);
            }
            vDir = DynamicsUtils.convertV3F_JME(dir.normalize().mul(speed).mul(1, 0, 1));
            if (vY != 0) {
                if (factor1 > 0.0f) {
                    this.getPhysicsCharacter().jump(DynamicsUtils.createV3F_JME(0.0f, 3.0f * factor1, 0.0f));
                } else if (this.getPhysicsCharacter().onGround()) {
                    if (this.canJump()) {
                        this.getPhysicsCharacter().jump();
                    }
                }
            }
        }
        this.getPhysicsCharacter().setWalkDirection(vDir);
    }

    private float getLiquidSwimUpFactor() {
        Vector3f contactPoint = new Vector3f(0.0f);

        BoundingBox boundingBox = new BoundingBox();
        this.getPhysicsCharacter().boundingBox(boundingBox);

        this.getWorld().getDynamics().getPhysicsSpace().contactTest(this.getPhysicsCharacter(), (e) -> {
            PhysicsCollisionObject collisionObjectB = e.getObjectB();
            if (CollisionFilter.LIQUID.matchMask(collisionObjectB.getCollisionGroup())) {
                com.jme3.math.Vector3f vector3f = new com.jme3.math.Vector3f();
                e.getLocalPointB(vector3f);
                contactPoint.set(DynamicsUtils.convertV3F_JOML(vector3f)).add(DynamicsUtils.getObjectBodyPos(collisionObjectB));
            }
        });

        if (contactPoint.length() <= 0.0f) {
            return -1.0f;
        }

        {
            com.jme3.math.Vector3f rayFrom = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y, contactPoint.z);
            com.jme3.math.Vector3f rayTo = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y - boundingBox.getYExtent(), contactPoint.z);

            List<PhysicsRayTestResult> rayTestResults = this.getWorld().getDynamics().getPhysicsSpace().rayTestRaw(rayFrom, rayTo);
            for (PhysicsRayTestResult physicsRayTestResult : rayTestResults) {
                if (CollisionFilter.LIQUID.matchMask(physicsRayTestResult.getCollisionObject().getCollisionGroup())) {
                    return -1.0f;
                }
            }
        }

        com.jme3.math.Vector3f rayFrom = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y + boundingBox.getYExtent(), contactPoint.z);
        com.jme3.math.Vector3f rayTo = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y, contactPoint.z);
        List<PhysicsRayTestResult> rayTestResults = this.getWorld().getDynamics().getPhysicsSpace().rayTestRaw(rayFrom, rayTo);
        for (PhysicsRayTestResult physicsRayTestResult : rayTestResults) {
            if (CollisionFilter.LIQUID.matchMask(physicsRayTestResult.getCollisionObject().getCollisionGroup())) {
                com.jme3.math.Vector3f hitP = DynamicsUtils.lerp(rayFrom, rayTo, physicsRayTestResult.getHitFraction());
                float f1 = rayFrom.distance(rayTo);
                float f2 = rayFrom.distance(hitP);
                return (float) Math.pow(1.0f - (f2 / f1), 0.5f);
            }
        }
        return 1.0f;
    }

    protected void walk(Vector3f dir, float speed) {
        if (this.canJump()) {
            if (dir.y > 0 && this.getPhysicsCharacter().onGround()) {
                this.getPhysicsCharacter().jump();
            }
        }
        com.jme3.math.Vector3f vDir = new com.jme3.math.Vector3f(0.0f, 0.0f, 0.0f);
        if (dir.length() > 0.0f) {
            if (this.getPhysicsCharacter().onGround() && this.getTicksExisted() % 15 == 0) {
                JGemsHelper.getSoundManager().playLocalSound(JGemsResourceManager.globalSoundAssets.pl_step[JGems3D.random.nextInt(4)], SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
            }
            vDir = DynamicsUtils.convertV3F_JME(dir.normalize().mul(speed).mul(1, 0, 1));
        }
        this.getPhysicsCharacter().setWalkDirection(vDir);
    }

    public void slowDownLinearVelocity(float val) {
        com.jme3.math.Vector3f vector3f1 = new com.jme3.math.Vector3f();
        this.getPhysicsCharacter().getLinearVelocity(vector3f1);
        if (vector3f1.length() <= 0.0f) {
            return;
        }
        Vector3f newV = DynamicsUtils.convertV3F_JOML(vector3f1);
        newV.mul(val);
        this.getPhysicsCharacter().setLinearVelocity(DynamicsUtils.convertV3F_JME(newV));
    }

    public void addLinearVelocity(Vector3f val) {
        com.jme3.math.Vector3f vector3f1 = new com.jme3.math.Vector3f();
        this.getPhysicsCharacter().getLinearVelocity(vector3f1);
        Vector3f newV = DynamicsUtils.convertV3F_JOML(vector3f1);
        newV.add(new Vector3f(val));
        this.getPhysicsCharacter().setLinearVelocity(DynamicsUtils.convertV3F_JME(newV));
    }

    @Override
    public ITriggerAction onColliding() {
        return null;
    }

    public void setController(IController iController) {
        this.controller = iController;
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        if (!isFocused) {
            this.inputMotion.clear();
            return;
        }
        this.getCameraRotation().add(new Vector3f(rotationInput, 0.0f));
        if (this.inputMotion.size() < PhysicsThread.TICKS_PER_SECOND * 10) {
            this.inputMotion.addFirst(new Vector3f(xyzInput));
        }
        this.clampCameraRotation();
    }

    private Vector3f calcControllerMotion() {
        if (this.inputMotion.isEmpty()) {
            return new Vector3f(0.0f);
        }
        float[] motion = new float[3];
        float[] input = new float[3];
        Vector3f inputMotion = this.inputMotion.pop();
        input[0] = inputMotion.x;
        input[1] = inputMotion.y;
        input[2] = inputMotion.z;
        if (input[2] != 0) {
            motion[0] += (float) Math.sin(this.getRotation().y) * -1.0f * input[2];
            motion[2] += (float) Math.cos(this.getRotation().y) * input[2];
        }
        if (input[0] != 0) {
            motion[0] += (float) Math.sin(this.getRotation().y - (Math.PI / 2.0f)) * -1.0f * input[0];
            motion[2] += (float) Math.cos(this.getRotation().y - (Math.PI / 2.0f)) * input[0];
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        return new Vector3f(motion[0], motion[1], motion[2]);
    }

    private void clampCameraRotation() {
        if (this.getRotation().x > Math.toRadians(90.0f)) {
            this.getCameraRotation().set(new Vector3d(Math.toRadians(90.0f), this.getRotation().y, this.getRotation().z));
        }
        if (this.getRotation().x < -Math.toRadians(90.0f)) {
            this.getCameraRotation().set(new Vector3d(-Math.toRadians(90.0f), this.getRotation().y, this.getRotation().z));
        }
    }

    public boolean canJump() {
        return true;
    }

    protected float walkSpeed() {
        return 0.25f;
    }

    @Override
    public float getEyeHeight() {
        return 0.45f;
    }

    public float getScalarSpeed() {
        com.jme3.math.Vector3f vector3f = new com.jme3.math.Vector3f();
        this.getPhysicsCharacter().getLinearVelocity(vector3f);
        return vector3f.length();
    }

    public int getCollisionGroup() {
        return this.getPhysicsCharacter().getCollisionGroup();
    }

    public void setCollisionGroup(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getPhysicsCharacter().setCollisionGroup(i);
    }

    public int getCollisionFilter() {
        return this.getPhysicsCharacter().getCollideWithGroups();
    }

    public void setCollisionFilter(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getPhysicsCharacter().setCollideWithGroups(i);
    }

    @Override
    public Vector3f getPosition() {
        return DynamicsUtils.getObjectBodyPos(this.getPhysicsCharacter());
    }

    @Override
    public void setPosition(Vector3f vector3d) {
        this.getCharacterController().warp(DynamicsUtils.convertV3F_JME(vector3d));
    }

    @Override
    public Vector3f getRotation() {
        return this.getCameraRotation();
    }

    @Override
    public void setRotation(Vector3f vector3d) {
        this.cameraRotation.set(vector3d);
    }

    @Override
    public boolean canBeDestroyed() {
        return false;
    }

    public EntityState getEntityState() {
        return this.entityState;
    }

    public void setEntityState(@NotNull EntityState state) {
        this.entityState = state;
    }

    @Override
    public IController currentController() {
        return this.controller;
    }

    public Vector3f getCameraRotation() {
        return this.cameraRotation;
    }

    public PhysicsCharacter getPhysicsCharacter() {
        return this.physicsCharacter;
    }

    public CharacterController getCharacterController() {
        return this.characterController;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public float height() {
        return this.capsuleSize().y;
    }
}