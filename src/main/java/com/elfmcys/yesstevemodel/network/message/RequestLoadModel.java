package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.data.EncryptTools;
import com.elfmcys.yesstevemodel.data.ModelData;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.util.ThreadTools;
import com.elfmcys.yesstevemodel.util.UuidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

public class RequestLoadModel {
    private final String fileName;

    public RequestLoadModel(String fileName) {
        this.fileName = fileName;
    }

    public static void encode(RequestLoadModel message, FriendlyByteBuf buf) {
        buf.writeUtf(message.fileName);
    }

    public static RequestLoadModel decode(FriendlyByteBuf buf) {
        return new RequestLoadModel(buf.readUtf());
    }

    public static void handle(RequestLoadModel message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                ClientModelManager.CACHE_MD5.add(message.fileName);
                loadModel(message.fileName);
            });
        }
        context.setPacketHandled(true);
    }

    public static void loadModel(String fileName) {
        ThreadTools.THREAD_POOL.submit(() -> {
            try {
                while (ClientModelManager.PASSWORD == null) {
                    Thread.sleep(500);
                }
                if (Minecraft.getInstance().player != null) {
                    UUID uuid = Minecraft.getInstance().player.getUUID();
                    Path modelFile = ServerModelManager.CACHE_CLIENT.resolve(fileName);
                    byte[] fileBytes = FileUtils.readFileToByteArray(modelFile.toFile());
                    ModelData data = EncryptTools.decryptModel(UuidUtils.asBytes(uuid), ClientModelManager.PASSWORD, fileBytes);
                    if (data != null) {
                        Minecraft.getInstance().tell(() -> ClientModelManager.registerAll(data));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
