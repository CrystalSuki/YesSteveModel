package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.util.Md5Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.function.Supplier;

public class SendModelFile {
    private final byte[] data;

    public SendModelFile(byte[] data) {
        this.data = data;
    }

    public static void encode(SendModelFile message, FriendlyByteBuf buf) {
        buf.writeByteArray(message.data);
    }

    public static SendModelFile decode(FriendlyByteBuf buf) {
        return new SendModelFile(buf.readByteArray());
    }

    public static void handle(SendModelFile message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                if (message.data.length == 48) {
                    ClientModelManager.PASSWORD = message.data;
                } else {
                    String fileName = Md5Utils.md5Hex(message.data).toUpperCase(Locale.US);
                    File file = ServerModelManager.CACHE_CLIENT.resolve(fileName).toFile();
                    try {
                        FileUtils.writeByteArrayToFile(file, message.data);
                        RequestLoadModel.loadModel(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}
