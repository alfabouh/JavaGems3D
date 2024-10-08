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

package javagems3d.engine.graphics.opengl.rendering.programs.shaders.unifroms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.system.service.exceptions.JGemsRuntimeException;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class UniformBufferProgram {
    private final int uboBlock;
    private final String name;
    private final int shaderId;
    private int binding;

    public UniformBufferProgram(int shaderId, String name) {
        this.uboBlock = GL20.glGenBuffers();
        this.shaderId = shaderId;
        if (this.getUboBlock() == 0) {
            throw new JGemsRuntimeException("Could not create uniform-buffer program!");
        }
        this.name = name;
    }

    public boolean createUniformBuffer(int binding, int bytes) {
        int uniformLocation = this.getLocation();
        this.binding = binding;
        if (uniformLocation < 0) {
            JGemsHelper.getLogger().warn("Could not find uniform-buffer " + this.getName());
            return false;
        }
        this.setupUniformBuffer(bytes, binding);
        return true;
    }

    private void setupUniformBuffer(int bytes, int binding) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL43.glBindBufferBase(GL43.GL_UNIFORM_BUFFER, binding, this.getUboBlock());
        GL43.glUniformBlockBinding(this.shaderId, this.getLocation(), this.getBinding());
        GL43.glBufferData(GL43.GL_UNIFORM_BUFFER, bytes, GL43.GL_DYNAMIC_DRAW);
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
    }

    public int getLocation() {
        return GL43.glGetUniformBlockIndex(this.shaderId, this.getName());
    }

    public void setUniformBufferData(int offset, ByteBuffer buffer) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL43.glBufferSubData(GL43.GL_UNIFORM_BUFFER, offset, buffer);
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
    }

    public void setUniformBufferData(int offset, FloatBuffer buffer) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL43.glBufferSubData(GL43.GL_UNIFORM_BUFFER, offset, buffer);
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
    }

    public void setUniformBufferData(int offset, IntBuffer buffer) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL43.glBufferSubData(GL43.GL_UNIFORM_BUFFER, offset, buffer);
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
    }

    public void setUniformBufferData(int offset, float[] values) {
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, this.getUboBlock());
        GL43.glBufferSubData(GL43.GL_UNIFORM_BUFFER, offset, values);
        GL43.glBindBuffer(GL43.GL_UNIFORM_BUFFER, 0);
    }

    public int getBinding() {
        return this.binding;
    }

    public String getName() {
        return this.name;
    }

    public int getUboBlock() {
        return this.uboBlock;
    }
}
