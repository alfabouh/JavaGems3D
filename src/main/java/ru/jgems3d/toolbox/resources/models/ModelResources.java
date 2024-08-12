package ru.jgems3d.toolbox.resources.models;

import ru.jgems3d.engine.system.service.file.JGemsPath;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.toolbox.resources.TBoxResourceManager;

public class ModelResources {
    public MeshDataGroup xyz;
    public MeshDataGroup cubic;
    public MeshDataGroup pointer;
    public MeshDataGroup player;

    public void init() {
        this.cubic = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/cubic/cubic.obj"));
        this.xyz = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/xyz/xyz.obj"));
        this.pointer = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/pointer/pointer.obj"));
        this.player = TBoxResourceManager.createModel(new JGemsPath("/assets/toolbox/models/player/player.obj"));
    }
}
