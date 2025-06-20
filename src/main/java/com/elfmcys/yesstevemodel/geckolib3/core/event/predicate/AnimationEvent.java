package com.elfmcys.yesstevemodel.geckolib3.core.event.predicate;

import com.elfmcys.yesstevemodel.geckolib3.core.IAnimatable;
import com.elfmcys.yesstevemodel.geckolib3.core.controller.AnimationController;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

public class AnimationEvent<T extends IAnimatable> {
    private final T animatable;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float partialTick;
    private final boolean isMoving;
    private final List<Object> extraData;
    public double animationTick;
    protected AnimationController<T> controller;

    public AnimationEvent(T animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving,
                          List<Object> extraData) {
        this.animatable = animatable;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.partialTick = partialTick;
        this.isMoving = isMoving;
        this.extraData = extraData;
    }

    /**
     * 以动画控制的状态，获取当前动画时间，或者过渡动画时间
     */
    public double getAnimationTick() {
        return animationTick;
    }

    public T getAnimatable() {
        return animatable;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public AnimationController<T> getController() {
        return controller;
    }

    public void setController(AnimationController<T> controller) {
        this.controller = controller;
    }

    public List<Object> getExtraData() {
        return extraData;
    }

    public <D> List<D> getExtraDataOfType(Class<D> type) {
        ObjectArrayList<D> matches = new ObjectArrayList<>();
        for (Object obj : this.extraData) {
            if (type.isAssignableFrom(obj.getClass())) {
                matches.add((D) obj);
            }
        }
        return matches;
    }
}
