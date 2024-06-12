package ru.alfabouh.jgems3d.engine.physics.entities.enemy.ai;

import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;

public interface AI extends IWorldDynamic {
    WorldItem target();

    boolean isActive();
}
