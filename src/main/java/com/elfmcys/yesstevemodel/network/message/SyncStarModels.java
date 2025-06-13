package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.capability.StarModelsCapabilityProvider;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

public class SyncStarModels {
    private final Set<ResourceLocation> starModels;

    public SyncStarModels(Set<ResourceLocation> starModels) {
        this.starModels = starModels;
    }

    public static void encode(SyncStarModels message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.starModels.size());
        for (ResourceLocation modelId : message.starModels) {
            buf.writeResourceLocation(modelId);
        }
    }

    public static SyncStarModels decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Set<ResourceLocation> tmp = Sets.newHashSet();
        for (int i = 0; i < size; i++) {
            tmp.add(buf.readResourceLocation());
        }
        return new SyncStarModels(tmp);
    }

    public static void handle(SyncStarModels message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> handleCapability(message));
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleCapability(SyncStarModels message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP).ifPresent(cap -> cap.setStarModels(message.starModels));
        }
    }
}
