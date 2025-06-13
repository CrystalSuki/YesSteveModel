package com.elfmcys.yesstevemodel.capability;

import com.elfmcys.yesstevemodel.util.Keep;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class StarModelsCapabilityProvider implements ICapabilitySerializable<ListTag> {
    public static Capability<StarModelsCapability> STAR_MODELS_CAP = CapabilityManager.get(new CapabilityToken<>() {
    });
    private StarModelsCapability instance = null;

    @NotNull
    @Override
    @Keep
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == STAR_MODELS_CAP) {
            return LazyOptional.of(this::createCapability).cast();
        }
        return LazyOptional.empty();
    }

    @NotNull
    @Override
    @Keep
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return getCapability(cap, null);
    }

    @Nonnull
    private StarModelsCapability createCapability() {
        if (instance == null) {
            this.instance = new StarModelsCapability();
        }
        return instance;
    }

    @Override
    @Keep
    public void deserializeNBT(ListTag nbt) {
        createCapability().deserializeNBT(nbt);
    }

    @Override
    @Keep
    public ListTag serializeNBT() {
        return createCapability().serializeNBT();
    }
}
