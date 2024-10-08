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

package javagems3d.engine.system.core.player;

import org.joml.Vector3f;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.engine.physics.entities.player.Player;
import javagems3d.engine.physics.world.PhysicsWorld;
import javagems3d.engine.system.service.collections.Pair;

@FunctionalInterface
public interface IPlayerConstructor {
    Pair<Player, RenderEntityData> constructPlayer(PhysicsWorld world, Vector3f startPos, Vector3f startRot);
}
