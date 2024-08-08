package ru.jgems3d.engine.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.system.service.exceptions.JGemsException;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.collision.MeshCollisionData;

public class StaticMeshCollider implements IColliderConstructor {
    private final MeshDataGroup meshDataGroup;

    public StaticMeshCollider(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        MeshCollisionData meshCollisionData = this.meshDataGroup.getMeshDataContainer(MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsException("Couldn't get mesh collision data! " + this.meshDataGroup);
        }
        return new MeshCollisionShape(true, meshCollisionData.getCompoundMesh());
    }
}