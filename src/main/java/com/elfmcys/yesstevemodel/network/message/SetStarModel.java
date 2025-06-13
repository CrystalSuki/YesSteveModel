package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.capability.StarModelsCapabilityProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetStarModel {
    private final ResourceLocation modelId;
    private final boolean isAdd;

    private SetStarModel(ResourceLocation modelId, boolean isAdd) {
        this.modelId = modelId;
        this.isAdd = isAdd;
    }

    public static SetStarModel add(ResourceLocation modelId) {
        return new SetStarModel(modelId, true);
    }

    public static SetStarModel remove(ResourceLocation modelId) {
        return new SetStarModel(modelId, false);
    }

    public static void encode(SetStarModel message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.modelId);
        buf.writeBoolean(message.isAdd);
    }

    public static SetStarModel decode(FriendlyByteBuf buf) {
        return new SetStarModel(buf.readResourceLocation(), buf.readBoolean());
    }

    public static void handle(SetStarModel message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = context.getSender();
                if (sender == null) {
                    return;
                }
                handleCapability(message, sender);
            });
        }
        context.setPacketHandled(true);
    }

    private static void handleCapability(SetStarModel message, ServerPlayer sender) {
        sender.getCapability(StarModelsCapabilityProvider.STAR_MODELS_CAP).ifPresent(cap -> {
            if (message.isAdd) {
                cap.addModel(message.modelId);
            } else {
                cap.removeModel(message.modelId);
            }
        });
    }
}
