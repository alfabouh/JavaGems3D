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

package ru.jgems3d.engine_api.app;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JGemsGameEntry {
    @NotNull String gameTitle();
    @NotNull String gameVersion();
    @NotNull DevStage devStage();

    enum DevStage {
        PRE_ALPHA,
        ALPHA,
        PRE_BETA,
        BETA,
        PRE_RELEASE,
        RELEASE
    }
}
