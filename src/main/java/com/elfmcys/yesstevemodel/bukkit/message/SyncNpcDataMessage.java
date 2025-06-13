package com.elfmcys.yesstevemodel.bukkit.message;

import com.elfmcys.yesstevemodel.bukkit.client.NPCData;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncNpcDataMessage {
    private final Map<UUID, Pair<ResourceLocation, ResourceLocation>> data;

    public SyncNpcDataMessage(Map<UUID, Pair<ResourceLocation, ResourceLocation>> data) {
        this.data = data;
    }

    public static void encode(SyncNpcDataMessage message, FriendlyByteBuf buf) {
    }

    public static SyncNpcDataMessage decode(FriendlyByteBuf buf) {
        Map<UUID, Pair<ResourceLocation, ResourceLocation>> map = Maps.newHashMap();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            ResourceLocation modelId = new ResourceLocation(buf.readUtf());
            ResourceLocation textureId = new ResourceLocation(buf.readUtf());
            map.put(uuid, Pair.of(modelId, textureId));
        }
        return new SyncNpcDataMessage(map);
    }

    public static void handle(SyncNpcDataMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> handleMessage(message));
        }
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleMessage(SyncNpcDataMessage message) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            NPCData.addAll(message.data);
        }
    }
}
