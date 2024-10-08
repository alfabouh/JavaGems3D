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

package javagems3d.engine_api.manager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import javagems3d.engine.system.controller.binding.BindingManager;
import javagems3d.engine.system.core.player.IPlayerConstructor;
import javagems3d.engine.system.map.loaders.IMapLoader;
import javagems3d.engine_api.configuration.AppConfiguration;

/**
 * This class contains controls/initializations for various important game systems.
 */
public abstract class AppManager {
    private final AppConfiguration appConfiguration;

    /**
     * Instantiates a new App manager.
     *
     * @param appConfiguration the app configuration
     */
    public AppManager(@Nullable AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration == null ? AppConfiguration.createDefaultAppConfiguration() : appConfiguration;
    }

    /**
     * @return The BindingManager, that contains key bindings for different actions.
     */
    public abstract @NotNull BindingManager createBindingManager();

    /**
     * @return instance of the game character constructor that will be created each time the map is launched.
     */
    public abstract @NotNull IPlayerConstructor createPlayer(IMapLoader mapLoader);

    /**
     * @return Panel UI instance of the game's main menu.
     */
    public abstract @NotNull PanelUI gameMainMenuPanel();

    /**
     * Gets app configuration.
     *
     * @return the app configuration
     */
    public @NotNull AppConfiguration getAppConfiguration() {
        return this.appConfiguration;
    }
}
