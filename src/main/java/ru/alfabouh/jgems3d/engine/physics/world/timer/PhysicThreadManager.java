package ru.alfabouh.jgems3d.engine.physics.world.timer;

import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.managers.JGemsLogging;

public class PhysicThreadManager {
    public static final Object locker = new Object();
    public static final int TICKS_PER_SECOND = 50;
    public static final int PHYS_THREADS = 1;
    private final PhysicsTimer physicsTimer;
    private final int tps;
    private Thread thread;

    public PhysicThreadManager(int tps) {
        this.tps = tps;
        this.physicsTimer = new PhysicsTimer();
    }

    public static double getFrameTime() {
        return 1.0d / PhysicThreadManager.TICKS_PER_SECOND;
    }

    public static long getTicksForUpdate(int TPS) {
        return 1000L / TPS;
    }

    public void initService() {
        this.thread = new Thread(() -> {
            try {
                this.getPhysicsTimer().updateTimer(this.getTps());
            } catch (Exception e) {
                SystemLogging.get().getLogManager().exception(e);
                JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
            }
        });
        this.thread.setName("physics");
        this.thread.start();
    }

    public Thread getThread() {
        return this.thread;
    }

    public int getTps() {
        return this.tps;
    }

    public final PhysicsTimer getPhysicsTimer() {
        return this.physicsTimer;
    }
}
