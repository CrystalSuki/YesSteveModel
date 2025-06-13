package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.client.ClientModelManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestSyncModel {
    public RequestSyncModel() {
    }

    public static void encode(RequestSyncModel message, FriendlyByteBuf buf) {
    }

    public static RequestSyncModel decode(FriendlyByteBuf buf) {
        return new RequestSyncModel();
    }

    public static void handle(RequestSyncModel message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(ClientModelManager::sendSyncModelMessage);
        }
        context.setPacketHandled(true);
    }
}
