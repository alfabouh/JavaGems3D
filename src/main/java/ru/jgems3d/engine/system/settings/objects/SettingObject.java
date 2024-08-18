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

package ru.jgems3d.engine.system.settings.objects;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;

import java.io.Serializable;

public abstract class SettingObject <T extends Serializable> {
    private final String name;
    private final T defaultValue;
    private T value;

    public SettingObject(String name, @NotNull T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public abstract T tryParseFromString(String string);

    @SuppressWarnings("all")
    public void setValue(Object value) {
        if (!value.getClass().isAssignableFrom(this.value.getClass())) {
            throw new JGemsRuntimeException("Couldn't cast value");
        }
        this.value = (T) value;
    }

    public void setDefault() {
        this.value = this.getDefaultValue();
    }

    public void setValue(T value) {
        this.value = value;
    }

    public @NotNull T getDefaultValue() {
        return this.defaultValue;
    }

    public @NotNull T getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }
}
