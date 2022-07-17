package de.plixo.animation;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Function;

/**
 * <a href="https://easings.net">...</a>
 */
public enum Ease {
    InOutSine(x -> -(Math.cos(Math.PI * x) - 1) / 2),
    InOutElastic(x -> {
        double c5 = (2 * Math.PI) / 4.5;
        double sin = Math.sin((20 * x - 11.125) * c5);
        return x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? -(Math.pow(2, 20 * x - 10) * sin) / 2
                : (Math.pow(2, -20 * x + 10) * sin) / 2 + 1;
    }),
    OutBounce(x -> {
        double n1 = 7.5625;
        double d1 = 2.75;
        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5 / d1) * x + 0.75;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25 / d1) * x + 0.9375;
        } else {
            return n1 * (x -= 2.625 / d1) * x + 0.984375;
        }
    }), OutElastic(x -> {
        double c4 = (2 * Math.PI) / 3;

        return x == 0
                ? 0
                : x == 1
                ? 1
                : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1;
    }),
    OutExpo(x -> (x == 1 ? 1 : 1 - Math.pow(2, -10 * x))),
    InOutCubic(x -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2),
    InOutQuint(x -> x < 0.5 ? 16 * x * x * x * x * x : 1 -  Math.pow(-2 * x + 2, 5) / 2),
    Linear(x -> x);


    @Getter
    @Accessors(fluent = true)
    final
    Function<Double, Double> function;

    Ease(Function<Double, Double> function) {
        this.function = function;
    }

    public float animate(float anim) {
        return function.apply((double) anim).floatValue();
    }
}
