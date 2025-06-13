package com.elfmcys.yesstevemodel.bukkit.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetNpcModelAndTexture {
    private final ResourceLocation modelId;
    private final ResourceLocation selectTexture;
    private final int npcId;

    public SetNpcModelAndTexture(ResourceLocation modelId, ResourceLocation selectTexture, int npcId) {
        this.modelId = modelId;
        this.selectTexture = selectTexture;
        this.npcId = npcId;
    }

    public static void encode(SetNpcModelAndTexture message, FriendlyByteBuf buf) {
        buf.writeResourceLocation(message.modelId);
        buf.writeResourceLocation(message.selectTexture);
        buf.writeVarInt(message.npcId);
    }

    public static SetNpcModelAndTexture decode(FriendlyByteBuf buf) {
        return new SetNpcModelAndTexture(buf.readResourceLocation(), buf.readResourceLocation(), buf.readVarInt());
    }

    public static void handle(SetNpcModelAndTexture message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().setPacketHandled(true);
    }
}