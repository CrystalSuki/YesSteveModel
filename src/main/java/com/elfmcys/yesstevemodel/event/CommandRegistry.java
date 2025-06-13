package com.elfmcys.yesstevemodel.event;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.command.RootCommand;
import com.elfmcys.yesstevemodel.command.argument.AnimationArgument;
import com.elfmcys.yesstevemodel.command.argument.ModelsArgument;
import com.elfmcys.yesstevemodel.command.argument.TexturesArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = YesSteveModel.MOD_ID)
public final class CommandRegistry {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, YesSteveModel.MOD_ID);
    public static final RegistryObject<SingletonArgumentInfo<ModelsArgument>> MODELS_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("models", () ->
            ArgumentTypeInfos.registerByClass(ModelsArgument.class, SingletonArgumentInfo.contextFree(ModelsArgument::ids)));
    public static final RegistryObject<SingletonArgumentInfo<AnimationArgument>> ANIMATIONS_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("animations", () ->
            ArgumentTypeInfos.registerByClass(AnimationArgument.class, SingletonArgumentInfo.contextFree(AnimationArgument::animations)));
    public static final RegistryObject<SingletonArgumentInfo<TexturesArgument>> TEXTURES_COMMAND_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("textures", () ->
            ArgumentTypeInfos.registerByClass(TexturesArgument.class, SingletonArgumentInfo.contextFree(TexturesArgument::ids)));

    @SubscribeEvent
    public static void onServerStaring(RegisterCommandsEvent event) {
        RootCommand.register(event.getDispatcher());
    }
}
