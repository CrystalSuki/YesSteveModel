package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetPlayAnimation {
    private static final int STOP = -1;
    private final int extraAnimationId;

    public SetPlayAnimation(int extraAnimationId) {
        this.extraAnimationId = extraAnimationId;
    }

    public static SetPlayAnimation stop() {
        return new SetPlayAnimation(STOP);
    }

    public static void encode(SetPlayAnimation message, FriendlyByteBuf buf) {
        buf.writeInt(message.extraAnimationId);
    }

    public static SetPlayAnimation decode(FriendlyByteBuf buf) {
        return new SetPlayAnimation(buf.readInt());
    }

    public static void handle(SetPlayAnimation message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = context.getSender();
                if (sender == null) {
                    return;
                }
                if (STOP <= message.extraAnimationId && message.extraAnimationId < 8) {
                    handleCapability(message, sender);
                }
            });
        }
        context.setPacketHandled(true);
    }

    private static void handleCapability(SetPlayAnimation message, ServerPlayer sender) {
        sender.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).ifPresent(modelIdCap -> {
            if (message.extraAnimationId == STOP) {
                modelIdCap.stopAnimation();
            } else {
                modelIdCap.playAnimation("extra" + message.extraAnimationId);
            }
        });
    }
}
