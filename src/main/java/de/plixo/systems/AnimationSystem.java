package de.plixo.systems;

import de.plixo.animation.Ease;
import de.plixo.animation.Job;
import de.plixo.event.AssetServer;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostRenderEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class AnimationSystem {

     CopyOnWriteArrayList<Job> jobs = new CopyOnWriteArrayList<>();

     long lastMs = System.currentTimeMillis();

    private void step() {
        float delta = (System.currentTimeMillis() - lastMs) / 1000f;
        jobs.forEach(ref -> {
            ref.addTime(delta);
            if(ref.interpolate()) {
                ref.kill();
            }
        });
        lastMs = System.currentTimeMillis();
    }

    private Job animate_(Consumer<Float> setter, float getter, float target, float duration, Ease ease) {
       Job job = new Job(setter, () -> getter, target, duration, ease) {
            @Override
            public void kill() {
                jobs.remove(this);
            }
        };
        jobs.add(job);
        return job;
    }

    private Job wait_(float time , Runnable action) {
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
    void animate(@NotNull PostRenderEvent event) {
        step();
    }

    public static Job animate(Consumer<Float> setter, float getter, float target, float duration, Ease ease) {
        return AssetServer.get(AnimationSystem.class).animate_(setter,getter,target,duration,ease);
    }
    public static Job wait(float time , Runnable action) {
        return AssetServer.get(AnimationSystem.class).wait_(time,action);
    }

}

