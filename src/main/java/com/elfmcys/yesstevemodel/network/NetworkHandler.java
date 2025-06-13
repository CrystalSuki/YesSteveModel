package com.elfmcys.yesstevemodel.network;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.bukkit.message.OpenModelGuiMessage;
import com.elfmcys.yesstevemodel.bukkit.message.SetNpcModelAndTexture;
import com.elfmcys.yesstevemodel.bukkit.message.SyncNpcDataMessage;
import com.elfmcys.yesstevemodel.bukkit.message.UpdateNpcDataMessage;
import com.elfmcys.yesstevemodel.network.message.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class NetworkHandler {
    private static final String VERSION = "1.0.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(YesSteveModel.MOD_ID, "network"),
            () -> VERSION, it -> it.equals(VERSION), it -> it.equals(VERSION));
    public static final int OPEN_NPC_MODEL_GUI = 93;
    public static final int SET_NPC_MODEL_ID = 94;
    public static final int SYNC_NPC_DATA = 95;
    public static final int UPDATE_NPC_DATA = 96;

    public static void init() {
        CHANNEL.registerMessage(0, SyncModelFiles.class, SyncModelFiles::encode, SyncModelFiles::decode, SyncModelFiles::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(1, SendModelFile.class, SendModelFile::encode, SendModelFile::decode, SendModelFile::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(2, RequestSyncModel.class, RequestSyncModel::encode, RequestSyncModel::decode, RequestSyncModel::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(3, RequestLoadModel.class, RequestLoadModel::encode, RequestLoadModel::decode, RequestLoadModel::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(4, SyncModelInfo.class, SyncModelInfo::encode, SyncModelInfo::decode, SyncModelInfo::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(5, SetModelAndTexture.class, SetModelAndTexture::encode, SetModelAndTexture::decode, SetModelAndTexture::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(6, SyncAuthModels.class, SyncAuthModels::encode, SyncAuthModels::decode, SyncAuthModels::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(7, SetPlayAnimation.class, SetPlayAnimation::encode, SetPlayAnimation::decode, SetPlayAnimation::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(8, SyncStarModels.class, SyncStarModels::encode, SyncStarModels::decode, SyncStarModels::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(9, SetStarModel.class, SetStarModel::encode, SetStarModel::decode, SetStarModel::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(10, RequestServerModelInfo.class, RequestServerModelInfo::encode, RequestServerModelInfo::decode, RequestServerModelInfo::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(11, UploadFile.class, UploadFile::encode, UploadFile::decode, UploadFile::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(12, CompleteFeedback.class, CompleteFeedback::encode, CompleteFeedback::decode, CompleteFeedback::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(13, RefreshModelManage.class, RefreshModelManage::encode, RefreshModelManage::decode, RefreshModelManage::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(14, HandleFile.class, HandleFile::encode, HandleFile::decode, HandleFile::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        initBukkit();
    }

    private static void initBukkit() {
        CHANNEL.registerMessage(OPEN_NPC_MODEL_GUI, OpenModelGuiMessage.class, OpenModelGuiMessage::encode, OpenModelGuiMessage::decode, OpenModelGuiMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(SET_NPC_MODEL_ID, SetNpcModelAndTexture.class, SetNpcModelAndTexture::encode, SetNpcModelAndTexture::decode, SetNpcModelAndTexture::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(SYNC_NPC_DATA, SyncNpcDataMessage.class, SyncNpcDataMessage::encode, SyncNpcDataMessage::decode, SyncNpcDataMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(UPDATE_NPC_DATA, UpdateNpcDataMessage.class, UpdateNpcDataMessage::encode, UpdateNpcDataMessage::decode, UpdateNpcDataMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToClientPlayer(Object message, Player player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }
}
