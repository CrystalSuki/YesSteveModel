package com.elfmcys.yesstevemodel.geckolib3.model.provider;

import com.elfmcys.yesstevemodel.util.Keep;
import net.minecraft.resources.ResourceLocation;

public interface IAnimatableModelProvider<E> {
    @Keep
    ResourceLocation getAnimationFileLocation(E animatable);
}