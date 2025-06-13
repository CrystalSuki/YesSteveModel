/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.elfmcys.yesstevemodel.geckolib3.core.event;

import com.elfmcys.yesstevemodel.geckolib3.core.controller.AnimationController;

public class SoundKeyframeEvent<T> extends KeyframeEvent<T> {
    public final String sound;

    @SuppressWarnings("rawtypes")
    public SoundKeyframeEvent(T entity, double animationTick, String sound, AnimationController controller) {
        super(entity, animationTick, controller);
        this.sound = sound;
    }
}
