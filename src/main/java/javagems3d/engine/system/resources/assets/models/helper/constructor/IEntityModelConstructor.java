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

package javagems3d.engine.system.resources.assets.models.helper.constructor;

import javagems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

@FunctionalInterface
public interface IEntityModelConstructor<T> {
    MeshDataGroup constructMeshDataGroup(T t);
}
