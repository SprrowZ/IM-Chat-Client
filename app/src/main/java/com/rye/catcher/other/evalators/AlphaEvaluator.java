package com.rye.catcher.other.evalators;

import android.animation.TypeEvaluator;

/**
 * 自定义差值器---纯属练手，和IntEvaluator里一样
 */
public class AlphaEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        return (float)startValue+fraction*((float)endValue - (float)startValue);
    }
}
