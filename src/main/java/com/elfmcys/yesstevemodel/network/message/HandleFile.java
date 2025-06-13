package com.elfmcys.yesstevemodel.network.message;

import com.elfmcys.yesstevemodel.model.ServerModelManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public class HandleFile {
    private final String name;
    private final UploadFile.Dir dir;
    private final String action;
    private final String rename;

    public HandleFile(String name, UploadFile.Dir dir, String action, String rename) {
        this.name = name;
        this.dir = dir;
        this.action = action;
        this.rename = rename;
    }

    public static void encode(HandleFile message, FriendlyByteBuf buf) {
        buf.writeUtf(message.name);
        buf.writeEnum(message.dir);
        buf.writeUtf(message.action);
        buf.writeUtf(message.rename);
    }

    public static HandleFile decode(FriendlyByteBuf buf) {
        return new HandleFile(buf.readUtf(), buf.readEnum(UploadFile.Dir.class), buf.readUtf(), buf.readUtf());
    }

    public static void handle(HandleFile message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer() && context.getSender() != null && context.getSender().hasPermissions(4)) {
            context.enqueueWork(() -> {
                if (message.dir == UploadFile.Dir.CUSTOM) {
                    String actionIn = message.action;
                    File file = ServerModelManager.CUSTOM.resolve(message.name).toFile();
                    if (FileUtils.isRegularFile(file) || FileUtils.isDirectory(file)) {
                        if (actionIn.equals("delete")) {
                            FileUtils.deleteQuietly(file);
                        }
                        if (actionIn.equals("move")) {
                            File destFile = ServerModelManager.AUTH.resolve(message.name).toFile();
                            try {
                                if (FileUtils.isRegularFile(file)) {
                                    FileUtils.moveFile(file, destFile);
                                }
                                if (FileUtils.isDirectory(file)) {
                                    FileUtils.moveDirectory(file, destFile);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (actionIn.equals("rename") && StringUtils.isNotBlank(message.rename)) {
                            File destFile = ServerModelManager.CUSTOM.resolve(message.rename).toFile();
                            try {
                                if (FileUtils.isRegularFile(file)) {
                                    FileUtils.moveFile(file, destFile);
                                }
                                if (FileUtils.isDirectory(file)) {
                                    FileUtils.moveDirectory(file, destFile);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (message.dir == UploadFile.Dir.AUTH) {
                    String actionIn = message.action;
                    File file = ServerModelManager.AUTH.resolve(message.name).toFile();
                    if (FileUtils.isRegularFile(file) || FileUtils.isDirectory(file)) {
                        if (actionIn.equals("delete")) {
                            FileUtils.deleteQuietly(file);
                        }
                        if (actionIn.equals("move")) {
                            File destFile = ServerModelManager.CUSTOM.resolve(message.name).toFile();
                            try {
                                if (FileUtils.isRegularFile(file)) {
                                    FileUtils.moveFile(file, destFile);
                                }
                                if (FileUtils.isDirectory(file)) {
                                    FileUtils.moveDirectory(file, destFile);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (actionIn.equals("rename") && StringUtils.isNotBlank(message.rename)) {
                            File destFile = ServerModelManager.AUTH.resolve(message.rename).toFile();
                            try {
                                if (FileUtils.isRegularFile(file)) {
                                    FileUtils.moveFile(file, destFile);
                                }
                                if (FileUtils.isDirectory(file)) {
                                    FileUtils.moveDirectory(file, destFile);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
        context.setPacketHandled(true);
    }
}
