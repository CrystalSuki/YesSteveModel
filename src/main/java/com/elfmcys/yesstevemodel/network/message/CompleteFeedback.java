package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.client.upload.UploadManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompleteFeedback {
    public CompleteFeedback() {
    }

    public static void encode(CompleteFeedback message, FriendlyByteBuf buf) {
    }

    public static CompleteFeedback decode(FriendlyByteBuf buf) {
        return new CompleteFeedback();
    }

    public static void handle(CompleteFeedback message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(UploadManager::finishUpload);
        }
        context.setPacketHandled(true);
    }
}
