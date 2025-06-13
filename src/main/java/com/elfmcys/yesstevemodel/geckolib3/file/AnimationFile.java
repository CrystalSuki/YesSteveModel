package com.elfmcys.yesstevemodel.geckolib3.file;

import com.elfmcys.yesstevemodel.geckolib3.core.builder.Animation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collection;
import java.util.Map;

public record AnimationFile(Map<String, Animation> animations) {
    public AnimationFile() {
        this(new Object2ObjectOpenHashMap<>());
    }

    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public Collection<Animation> getAllAnimations() {
        return this.animations.values();
    }

    public void putAnimation(String name, Animation animation) {
        this.animations.put(name, animation);
    }
}