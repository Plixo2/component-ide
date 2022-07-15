package de.plixo.animation;

import de.plixo.animation.Ease;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Job {

    Consumer<Float> floatConsumer;
    Supplier<Float> floatSupplier;
    float end , duration;
    float time = 0;
    Ease ease;
    public Job(Consumer<Float> floatConsumer, Supplier<Float> floatSupplier , float end , float duration,Ease ease) {
        this.floatConsumer = floatConsumer;
        this.floatSupplier = floatSupplier;
        this.end = end;
        this.duration = duration;
        this.ease = ease;
    }

    public boolean interpolate() {
        float start = floatSupplier.get();
        float normalizedNumber = end - start;
        double normalizedTime = time / duration;
        double valueOverTime = ease.function.apply(normalizedTime) * normalizedNumber;
        if(this.time >= duration) {
            valueOverTime = normalizedNumber;
        }
        floatConsumer.accept((float) (start + valueOverTime));
        return this.time >= duration;
    }

    public void addTime(float time) {
        this.time += time;
    }

    public abstract void kill();

}
