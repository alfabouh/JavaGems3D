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

package javagems3d.engine.graphics.opengl.screen.timer;

import javagems3d.engine.JGems3D;

public final class JGemsTimer {
    private boolean shouldBeErased;
    private double lastTime;
    private float deltaTime;
    private double accumulatedTime;

    JGemsTimer() {
        this.lastTime = JGems3D.glfwTime();
        this.shouldBeErased = false;
    }

    public void update() {
        double currentTime = JGems3D.glfwTime();
        this.deltaTime = (float) (currentTime - this.lastTime);
        this.lastTime = currentTime;
        this.accumulatedTime += this.deltaTime;
    }

    public void reset() {
        this.lastTime = JGems3D.glfwTime();
        this.accumulatedTime = 0.0f;
    }

    public void dispose() {
        this.shouldBeErased = true;
    }

    public boolean resetTimerAfterReachedSeconds(double seconds) {
        if (this.accumulatedTime >= seconds) {
            this.accumulatedTime = 0.0d;
            return true;
        }
        return false;
    }

    public boolean isShouldBeErased() {
        return this.shouldBeErased;
    }

    public double getLastTime() {
        return this.lastTime;
    }

    public float getDeltaTime() {
        return this.deltaTime;
    }

    public double getAccumulatedTime() {
        return this.accumulatedTime;
    }
}
