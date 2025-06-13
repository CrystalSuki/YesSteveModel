package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.client.gui.ModelManageScreen;
import com.elfmcys.yesstevemodel.model.format.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Supplier;

public class RequestServerModelInfo {
    private final List<Info> customModels;
    private final List<Info> authModels;

    public RequestServerModelInfo(List<Info> customModels, List<Info> authModels) {
        this.customModels = customModels;
        this.authModels = authModels;
    }

    public static void encode(RequestServerModelInfo message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.customModels.size());
        for (Info info : message.customModels) {
            infoToBuffer(buf, info);
        }
        buf.writeVarInt(message.authModels.size());
        for (Info info : message.authModels) {
            infoToBuffer(buf, info);
        }
    }

    public static RequestServerModelInfo decode(FriendlyByteBuf buf) {
        List<Info> outCustomModels = Lists.newArrayList();
        List<Info> outAuthModels = Lists.newArrayList();
        int customModelsSize = buf.readVarInt();
        for (int i = 0; i < customModelsSize; i++) {
            outCustomModels.add(bufferToInfo(buf));
        }
        int authModelsSize = buf.readVarInt();
        for (int i = 0; i < authModelsSize; i++) {
            outAuthModels.add(bufferToInfo(buf));
        }
        return new RequestServerModelInfo(outCustomModels, outAuthModels);
    }

    public static void handle(RequestServerModelInfo message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> openGui(message));
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void openGui(RequestServerModelInfo message) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ModelManageScreen(message.customModels, message.authModels));
    }

    private static void infoToBuffer(FriendlyByteBuf buf, Info info) {
        buf.writeUtf(info.fileName);
        buf.writeEnum(info.type);
        buf.writeLong(info.size);
    }

    private static Info bufferToInfo(FriendlyByteBuf buf) {
        return new Info(buf.readUtf(), buf.readEnum(Type.class), buf.readLong());
    }

    public static class Info {
        private String fileName;
        private Type type;
        private long size;

        public Info(String fileName, Type type, long size) {
            this.fileName = fileName;
            this.type = type;
            this.size = size;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }
}
