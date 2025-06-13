package com.elfmcys.yesstevemodel.command.sub;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.AuthModelsCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.command.argument.ModelsArgument;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import com.elfmcys.yesstevemodel.network.message.SyncAuthModels;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class AuthCommand {
    private static final String AUTH_NAME = "auth";
    private static final String ADD_NAME = "add";
    private static final String REMOVE_NAME = "remove";
    private static final String ALL_NAME = "all";
    private static final String CLEAR_NAME = "clear";
    private static final String TARGETS_NAME = "targets";
    private static final String MODEL_ID_NAME = "model_id";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> auth = Commands.literal(AUTH_NAME);
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> targets = Commands.argument(TARGETS_NAME, EntityArgument.players());
        RequiredArgumentBuilder<CommandSourceStack, String> modelId = Commands.argument(MODEL_ID_NAME, ModelsArgument.ids());

        auth.then(targets.then(Commands.literal(ADD_NAME).then(modelId.executes(AuthCommand::addAuthModel))));
        auth.then(targets.then(Commands.literal(REMOVE_NAME).then(modelId.executes(AuthCommand::removeAuthModel))));
        auth.then(targets.then(Commands.literal(ALL_NAME).executes(AuthCommand::addAllAuthModel)));
        auth.then(targets.then(Commands.literal(CLEAR_NAME).executes(AuthCommand::clearAuthModel)));
        return auth;
    }

    private static int addAuthModel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, TARGETS_NAME);
        String modelName = ModelsArgument.getModel(context, MODEL_ID_NAME);
        if (!ServerModelManager.CACHE_NAME_INFO.containsKey(modelName)) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.export.not_exist",
                    modelName), true);
            return Command.SINGLE_SUCCESS;
        }
        targets.forEach(player -> player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP).ifPresent(cap -> {
            ResourceLocation modelId = new ResourceLocation(YesSteveModel.MOD_ID, modelName);
            cap.addModel(modelId);
            NetworkHandler.sendToClientPlayer(new SyncAuthModels(cap.getAuthModels()), player);
            context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.auth_model.add.info",
                    modelId.getPath(), player.getScoreboardName()), true);
        }));
        return Command.SINGLE_SUCCESS;
    }

    private static int addAllAuthModel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, TARGETS_NAME);
        targets.forEach(player -> player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP).ifPresent(cap -> {
            ServerModelManager.CACHE_NAME_INFO.keySet().forEach(name -> cap.addModel(new ResourceLocation(YesSteveModel.MOD_ID, name)));
            NetworkHandler.sendToClientPlayer(new SyncAuthModels(cap.getAuthModels()), player);
            context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.auth_model.all.info",
                    player.getScoreboardName()), true);
        }));
        return Command.SINGLE_SUCCESS;
    }

    private static int removeAuthModel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, TARGETS_NAME);
        ResourceLocation modelId = new ResourceLocation(YesSteveModel.MOD_ID, ModelsArgument.getModel(context, MODEL_ID_NAME));
        targets.forEach(player -> player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP).ifPresent(ownModelsCap -> {
            ownModelsCap.removeModel(modelId);
            player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).ifPresent(modelIdCap -> {
                if (ServerModelManager.AUTH_MODELS.contains(modelIdCap.getModelId().getPath()) && !ownModelsCap.containModel(modelIdCap.getModelId())) {
                    ResourceLocation defaultModelId = new ResourceLocation(YesSteveModel.MOD_ID, "default");
                    ResourceLocation defaultTextureId = new ResourceLocation(YesSteveModel.MOD_ID, "default/default.png");
                    modelIdCap.setModelAndTexture(defaultModelId, defaultTextureId);
                }
            });
            NetworkHandler.sendToClientPlayer(new SyncAuthModels(ownModelsCap.getAuthModels()), player);
            context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.auth_model.remove.info",
                    modelId.getPath(), player.getScoreboardName()), true);
        }));
        return Command.SINGLE_SUCCESS;
    }

    private static int clearAuthModel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, TARGETS_NAME);
        targets.forEach(player -> player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP).ifPresent(ownModelCap -> {
            ownModelCap.clear();
            player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).ifPresent(modelIdCap -> {
                ResourceLocation defaultModelId = new ResourceLocation(YesSteveModel.MOD_ID, "default");
                ResourceLocation defaultTextureId = new ResourceLocation(YesSteveModel.MOD_ID, "default/default.png");
                modelIdCap.setModelAndTexture(defaultModelId, defaultTextureId);
            });
            NetworkHandler.sendToClientPlayer(new SyncAuthModels(ownModelCap.getAuthModels()), player);
            context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.auth_model.clear.info",
                    player.getScoreboardName()), true);
        }));
        return Command.SINGLE_SUCCESS;
    }
}
