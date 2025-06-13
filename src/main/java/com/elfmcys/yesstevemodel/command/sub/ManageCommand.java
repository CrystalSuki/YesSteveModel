package com.elfmcys.yesstevemodel.command.sub;

import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.model.format.Type;
import com.elfmcys.yesstevemodel.network.NetworkHandler;
import com.elfmcys.yesstevemodel.network.message.RequestServerModelInfo;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class ManageCommand {
    private static final String MANAGE_NAME = "manage";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> manage = Commands.literal(MANAGE_NAME).requires(stack -> stack.hasPermission(4));
        manage.executes(ManageCommand::exportModel);
        return manage;
    }

    private static int exportModel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (context.getSource().hasPermission(4)) {
            ServerPlayer player = context.getSource().getPlayerOrException();
            List<RequestServerModelInfo.Info> customInfo = getFilesInfo(ServerModelManager.CUSTOM);
            List<RequestServerModelInfo.Info> authInfo = getFilesInfo(ServerModelManager.AUTH);
            NetworkHandler.sendToClientPlayer(new RequestServerModelInfo(customInfo, authInfo), player);
        }
        return Command.SINGLE_SUCCESS;
    }

    public static List<RequestServerModelInfo.Info> getFilesInfo(Path rootPath) {
        List<RequestServerModelInfo.Info> out = Lists.newArrayList();
        Collection<File> dirs = FileUtils.listFiles(rootPath.toFile(), DirectoryFileFilter.INSTANCE, null);
        for (File dir : dirs) {
            RequestServerModelInfo.Info info = new RequestServerModelInfo.Info(dir.getName(), Type.FOLDER, FileUtils.sizeOf(dir));
            out.add(info);
        }
        Collection<File> zipFiles = FileUtils.listFiles(rootPath.toFile(), new String[]{"zip"}, false);
        for (File zipFile : zipFiles) {
            RequestServerModelInfo.Info info = new RequestServerModelInfo.Info(zipFile.getName(), Type.ZIP, FileUtils.sizeOf(zipFile));
            out.add(info);
        }
        Collection<File> ysmFiles = FileUtils.listFiles(rootPath.toFile(), new String[]{"ysm"}, false);
        for (File ysmFile : ysmFiles) {
            RequestServerModelInfo.Info info = new RequestServerModelInfo.Info(ysmFile.getName(), Type.YSM, FileUtils.sizeOf(ysmFile));
            out.add(info);
        }
        return out;
    }
}
