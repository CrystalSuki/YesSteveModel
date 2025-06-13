package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.command.sub.ManageCommand;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class RefreshModelManage {
    public RefreshModelManage() {
    }

    public static void encode(RefreshModelManage message, FriendlyByteBuf buf) {
    }

    public static RefreshModelManage decode(FriendlyByteBuf buf) {
        return new RefreshModelManage();
    }

    public static void handle(RefreshModelManage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer() && context.getSender() != null && context.getSender().hasPermissions(4)) {
            context.enqueueWork(() -> {
                List<RequestServerModelInfo.Info> customInfo = ManageCommand.getFilesInfo(ServerModelManager.CUSTOM);
                List<RequestServerModelInfo.Info> authInfo = ManageCommand.getFilesInfo(ServerModelManager.AUTH);
                NetworkHandler.sendToClientPlayer(new RequestServerModelInfo(customInfo, authInfo), context.getSender());
            });
        }
        context.setPacketHandled(true);
    }
}
