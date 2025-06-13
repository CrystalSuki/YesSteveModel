package com.elfmcys.yesstevemodel.command.sub;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.capability.AuthModelsCapabilityProvider;
import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.command.argument.ModelsArgument;
import com.elfmcys.yesstevemodel.command.argument.TexturesArgument;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.model.format.ServerModelInfo;
import com.elfmcys.yesstevemodel.util.ModelIdUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static com.elfmcys.yesstevemodel.model.ServerModelManager.*;

public class ModelCommand {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
    private static final String MODEL_NAME = "model";
    private static final String RELOAD_NAME = "reload";
    private static final String SET_NAME = "set";
    private static final String TARGETS_NAME = "targets";
    private static final String MODEL_ID_NAME = "model_id";
    private static final String TEXTURE_ID_NAME = "texture_id";
    private static final String IGNORE_AUTH_NAME = "ignore_auth";
    private static final String EXPORT_NAME = "export";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> model = Commands.literal(MODEL_NAME);
        LiteralArgumentBuilder<CommandSourceStack> reload = Commands.literal(RELOAD_NAME);
        model.then(reload.executes(ModelCommand::reloadAllPack));

        LiteralArgumentBuilder<CommandSourceStack> set = Commands.literal(SET_NAME);
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> targets = Commands.argument(TARGETS_NAME, EntityArgument.players());
        RequiredArgumentBuilder<CommandSourceStack, String> modelId = Commands.argument(MODEL_ID_NAME, ModelsArgument.ids());
        RequiredArgumentBuilder<CommandSourceStack, String> textureId = Commands.argument(TEXTURE_ID_NAME, TexturesArgument.ids());
        RequiredArgumentBuilder<CommandSourceStack, Boolean> ignoreAuth = Commands.argument(IGNORE_AUTH_NAME, BoolArgumentType.bool());

        model.then(set.then(targets.then(modelId.then(textureId.executes(context -> setModel(context, false))))));
        model.then(set.then(targets.then(modelId.then(textureId.then(ignoreAuth.executes(ModelCommand::setModelIgnoreAuth))))));

        LiteralArgumentBuilder<CommandSourceStack> export = Commands.literal(EXPORT_NAME);
        model.then(export.executes(ModelCommand::exportAllPackInfo));
        return model;
    }

    private static int setModelIgnoreAuth(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        boolean ignoreAuth = BoolArgumentType.getBool(context, IGNORE_AUTH_NAME);
        return setModel(context, ignoreAuth);
    }

    private static int setModel(CommandContext<CommandSourceStack> context, boolean ignoreAuth) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, TARGETS_NAME);
        String modelName = ModelsArgument.getModel(context, MODEL_ID_NAME);
        String textureName = TexturesArgument.getTexture(context, TEXTURE_ID_NAME);
        if (!ServerModelManager.CACHE_NAME_INFO.containsKey(modelName)) {
            context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.export.not_exist",
                    modelName), true);
            return Command.SINGLE_SUCCESS;
        }

        ServerModelInfo info = ServerModelManager.CACHE_NAME_INFO.get(modelName);
        if (info.getTexture().isEmpty()) {
            return Command.SINGLE_SUCCESS;
        }

        ResourceLocation modelId = new ResourceLocation(YesSteveModel.MOD_ID, modelName);
        ResourceLocation textureId = ModelIdUtil.getSubModelId(modelId, textureName);

        if (ignoreAuth) {
            targets.forEach(player -> player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).ifPresent(cap -> {
                cap.setModelAndTexture(modelId, textureId);
                context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.set.success",
                        modelName, player.getScoreboardName()), true);
            }));
            return Command.SINGLE_SUCCESS;
        }

        targets.forEach(player -> player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).ifPresent(cap ->
                player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP).ifPresent(authCap -> {
                    if (!ServerModelManager.AUTH_MODELS.contains(modelName) || authCap.containModel(modelId)) {
                        cap.setModelAndTexture(modelId, textureId);
                        context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.set.success",
                                modelName, player.getScoreboardName()), true);
                    } else {
                        context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.set.need_auth",
                                modelName, player.getScoreboardName()), true);
                    }
                })));
        return Command.SINGLE_SUCCESS;
    }

    private static int exportAllPackInfo(CommandContext<CommandSourceStack> context) {
        String infoText = GSON.toJson(ServerModelManager.CACHE_NAME_INFO);
        context.getSource().sendSuccess(() -> Component.literal(infoText), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int reloadAllPack(CommandContext<CommandSourceStack> context) {
        StopWatch watch = StopWatch.createStarted();
        checkModelFiles(context, CUSTOM);
        checkModelFiles(context, AUTH);
        ServerModelManager.reloadPacks();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ServerModelManager::sendRequestSyncModelMessage);
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            ServerModelManager.sendRequestSyncModelMessage(context.getSource().getServer().getPlayerList());
        }
        context.getSource().getLevel().players().forEach(player -> player.getCapability(AuthModelsCapabilityProvider.AUTH_MODELS_CAP).ifPresent(ownModelsCap -> {
            player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).ifPresent(modelIdCap -> {
                if (ServerModelManager.AUTH_MODELS.contains(modelIdCap.getModelId().getPath()) && !ownModelsCap.containModel(modelIdCap.getModelId())) {
                    ResourceLocation defaultModelId = new ResourceLocation(YesSteveModel.MOD_ID, "default");
                    ResourceLocation defaultTextureId = new ResourceLocation(YesSteveModel.MOD_ID, "default/default.png");
                    modelIdCap.setModelAndTexture(defaultModelId, defaultTextureId);
                }
            });
        }));
        watch.stop();
        context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.reload.info", watch.getTime(TimeUnit.MICROSECONDS) / 1000.0), true);
        return Command.SINGLE_SUCCESS;
    }

    private static void checkModelFiles(CommandContext<CommandSourceStack> context, Path rootPath) {
        Collection<File> dirs = FileUtils.listFiles(rootPath.toFile(), DirectoryFileFilter.INSTANCE, null);
        for (File dir : dirs) {
            String dirName = dir.getName();
            if (!ResourceLocation.isValidResourceLocation(dirName)) {
                context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.reload.error.dir_name", dirName), true);
            }
            boolean noMainModelFile = true;
            boolean noArmModelFile = true;
            boolean noTextureFile = true;
            Collection<File> files = FileUtils.listFiles(rootPath.resolve(dirName).toFile(), FileFileFilter.INSTANCE, null);
            for (File file : files) {
                String fileName = file.getName();
                if (MAIN_MODEL_FILE_NAME.equals(fileName) && isNotBlankFile(file)) {
                    noMainModelFile = false;
                }
                if (ARM_MODEL_FILE_NAME.equals(fileName) && isNotBlankFile(file)) {
                    noArmModelFile = false;
                }
                if (fileName.endsWith(".png")) {
                    noTextureFile = false;
                    String name = file.getName();
                    name = name.substring(0, name.length() - 4);
                    if (!ResourceLocation.isValidResourceLocation(name)) {
                        String showName = String.format("%s/%s.png", dirName, name);
                        context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.reload.error.texture_name", showName), true);
                    }
                }
            }
            if (noMainModelFile) {
                context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.reload.error.no_main_file", dirName), true);
            }
            if (noArmModelFile) {
                context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.reload.error.no_arm_file", dirName), true);
            }
            if (noTextureFile) {
                context.getSource().sendSuccess(() -> Component.translatable("message.yes_steve_model.model.reload.error.no_texture_file", dirName), true);
            }
        }
    }

    private static boolean isNotBlankFile(File file) {
        try {
            String fileText = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return StringUtils.isNoneBlank(fileText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
