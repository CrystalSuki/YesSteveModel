package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.data.EncryptTools;
import com.elfmcys.yesstevemodel.model.format.ServerModelInfo;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import com.elfmcys.yesstevemodel.util.ThreadTools;
import com.elfmcys.yesstevemodel.util.UuidUtils;
import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.elfmcys.yesstevemodel.model.ServerModelManager.*;

public class SyncModelFiles {
    private final String[] md5Info;

    public SyncModelFiles(String[] md5Info) {
        this.md5Info = md5Info;
    }

    public static void encode(SyncModelFiles message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.md5Info.length);
        for (String md5 : message.md5Info) {
            buf.writeUtf(md5);
        }
    }

    public static SyncModelFiles decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        String[] output = new String[count];
        for (int i = 0; i < count; i++) {
            output[i] = buf.readUtf();
        }
        return new SyncModelFiles(output);
    }

    public static void handle(SyncModelFiles message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer sender = context.getSender();
                if (sender == null) {
                    return;
                }
                sendPassword(sender);
                sendModelFiles(message.md5Info, sender);
            });
        }
        context.setPacketHandled(true);
    }

    private static void sendModelFiles(String[] md5Info, ServerPlayer sender) {
        Collection<String> cache = CACHE_NAME_INFO.values().stream().map(ServerModelInfo::getMd5).toList();
        List<String> output = Lists.newArrayList(cache);
        for (String md5 : md5Info) {
            if (cache.contains(md5)) {
                output.remove(md5);
                NetworkHandler.sendToClientPlayer(new RequestLoadModel(md5), sender);
            }
        }
        for (String md5 : output) {
            File modelFile = CACHE_SERVER.resolve(md5).toFile();
            try {
                byte[] modelBytes = FileUtils.readFileToByteArray(modelFile);
                ThreadTools.THREAD_POOL.submit(() -> NetworkHandler.sendToClientPlayer(new SendModelFile(modelBytes), sender));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendPassword(ServerPlayer sender) {
        try {
            byte[] password = FileUtils.readFileToByteArray(PASSWORD_FILE.toFile());
            byte[] uuid = UuidUtils.asBytes(sender.getUUID());
            byte[] output = EncryptTools.encryptPassword(uuid, password);
            ThreadTools.THREAD_POOL.submit(() -> NetworkHandler.sendToClientPlayer(new SendModelFile(output), sender));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
