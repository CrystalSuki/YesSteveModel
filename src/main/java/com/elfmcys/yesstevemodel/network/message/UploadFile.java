package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

public class UploadFile {
    private final String name;
    private final byte[] fileBytes;
    private final Dir dir;

    public UploadFile(String name, byte[] fileBytes, Dir dir) {
        this.name = name;
        this.fileBytes = fileBytes;
        this.dir = dir;
    }

    public static void encode(UploadFile message, FriendlyByteBuf buf) {
        buf.writeUtf(message.name);
        buf.writeByteArray(message.fileBytes);
        buf.writeEnum(message.dir);
    }

    public static UploadFile decode(FriendlyByteBuf buf) {
        String name = buf.readUtf();
        byte[] bytes = buf.readByteArray();
        Dir dirOut = buf.readEnum(Dir.class);
        return new UploadFile(name, bytes, dirOut);
    }

    public static void handle(UploadFile message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer() && context.getSender() != null && context.getSender().hasPermissions(4)) {
            context.enqueueWork(() -> writeFile(message, context.getSender()));
        }
        context.setPacketHandled(true);
    }

    private static void writeFile(UploadFile message, ServerPlayer player) {
        Path filePath;
        if (message.dir == Dir.CUSTOM) {
            filePath = ServerModelManager.CUSTOM.resolve(message.name);
        } else {
            filePath = ServerModelManager.AUTH.resolve(message.name);
        }
        try {
            FileUtils.writeByteArrayToFile(filePath.toFile(), message.fileBytes);
            NetworkHandler.sendToClientPlayer(new CompleteFeedback(), player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum Dir {
        CUSTOM,
        AUTH
    }
}
