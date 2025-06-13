package com.elfmcys.yesstevemodel.geckolib3.util;

import com.elfmcys.yesstevemodel.geckolib3.core.IAnimatable;
import com.elfmcys.yesstevemodel.geckolib3.core.manager.AnimationFactory;
import com.elfmcys.yesstevemodel.geckolib3.core.manager.InstancedAnimationFactory;
import com.elfmcys.yesstevemodel.geckolib3.core.manager.SingletonAnimationFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GeckoLibUtil {
    public static AnimationFactory createFactory(IAnimatable animatable) {
        return createFactory(animatable, !(animatable instanceof Entity) && !(animatable instanceof BlockEntity));
    }

    public static AnimationFactory createFactory(IAnimatable animatable, boolean singletonObject) {
        return singletonObject ? new SingletonAnimationFactory(animatable) : new InstancedAnimationFactory(animatable);
    }
}
