package com.elfmcys.yesstevemodel.command.sub;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.command.argument.ModelsArgument;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.util.YesModelUtils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.IOException;

public class ExportCommand {
    private static final String EXPORT_NAME = "export";
    private static final String MODEL_ID_NAME = "model_id";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> export = Commands.literal(EXPORT_NAME);
        RequiredArgumentBuilder<CommandSourceStack, String> modelId = Commands.argument(MODEL_ID_NAME, ModelsArgument.ids());
        export.then(modelId.executes(ExportCommand::exportModel));
        return export;
    }

    private static int exportModel(CommandContext<CommandSourceStack> context) {
        String modelName = ModelsArgument.getModel(context, MODEL_ID_NAME);
        File customFolder = ServerModelManager.CUSTOM.resolve(modelName).toFile();
        if (customFolder.isDirectory()) {
            try {
                YesModelUtils.export(customFolder);
                context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.export.success",
                        YesSteveModel.MOD_ID, modelName), false);
                return Command.SINGLE_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File authFolder = ServerModelManager.AUTH.resolve(modelName).toFile();
        if (authFolder.isDirectory()) {
            try {
                YesModelUtils.export(authFolder);
                context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.export.success",
                        YesSteveModel.MOD_ID, modelName), false);
                return Command.SINGLE_SUCCESS;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        context.getSource().sendSuccess(() -> Component.translatable("commands.yes_steve_model.export.not_exist",
                modelName), false);
        return Command.SINGLE_SUCCESS;
    }
}
