package ru.alfabouh.jgems3d.engine.system.map.legacy.loader;

import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.map.MapInfo;
import ru.alfabouh.jgems3d.mapsys.file.save.objects.MapProperties;

public interface IMapLoader {
    void createMap(World world);
    MapInfo getLevelInfo();
}
