package com.elfmcys.yesstevemodel.bukkit.message;

import com.elfmcys.yesstevemodel.bukkit.client.NPCData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UpdateNpcDataMessage {
    private final UUID uuid;
    private final ResourceLocation modelId;
    private final ResourceLocation textureId;

    public UpdateNpcDataMessage(UUID uuid, ResourceLocation modelId, ResourceLocation textureId) {
        this.uuid = uuid;
        this.modelId = modelId;
        this.textureId = textureId;
    }

    public static void encode(UpdateNpcDataMessage message, FriendlyByteBuf buf) {
    }

    public static UpdateNpcDataMessage decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        ResourceLocation modelId = new ResourceLocation(buf.readUtf());
        ResourceLocation textureId = new ResourceLocation(buf.readUtf());
        return new UpdateNpcDataMessage(uuid, modelId, textureId);
    }

    public static void handle(UpdateNpcDataMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> handleMessage(message));
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleMessage(UpdateNpcDataMessage message) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            NPCData.put(message.uuid, message.modelId, message.textureId);
        }
    }
}
