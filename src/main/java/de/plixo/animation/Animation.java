package de.plixo.animation;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostRenderEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Animation {

    static CopyOnWriteArrayList<Job> jobs = new CopyOnWriteArrayList<>();

    static long lastMs = System.currentTimeMillis();

    public static void step() {
        float delta = (System.currentTimeMillis() - lastMs) / 1000f;
        jobs.forEach(ref -> {
            ref.addTime(delta);
            if(ref.interpolate()) {
                ref.kill();
            }
        });
        lastMs = System.currentTimeMillis();
    }

    public static Job animate(Consumer<Float> setter, float getter, float target, float duration, Ease ease) {
       Job job = new Job(setter, () -> getter, target, duration, ease) {
            @Override
            public void kill() {
                jobs.remove(this);
            }
        };
        jobs.add(job);
        return job;
    }

    public static Job wait(float time , Runnable action) {
        Job job = new Job(ref -> {}, () -> 0f, 1f, time, Ease.Linear) {
            @Override
            public void kill() {
                action.run();
                jobs.remove(this);
            }
        };
        jobs.add(job);
        return job;
    }

    @SubscribeEvent
    static void animate(@NotNull PostRenderEvent event) {
        step();
    }

}

