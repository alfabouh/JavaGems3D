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

package ru.jgems3d.engine.physics.world.basic;

import ru.jgems3d.engine.physics.world.IWorld;

public interface IWorldObject {
    void onSpawn(IWorld iWorld);

    void onDestroy(IWorld iWorld);

    boolean isDead();
}
