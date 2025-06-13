package com.elfmcys.yesstevemodel.command.argument;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.model.ServerModelManager;
import com.elfmcys.yesstevemodel.util.Keep;
import com.elfmcys.yesstevemodel.util.ModelIdUtil;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class TexturesArgument implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Collections.singleton("default");

    private TexturesArgument() {
    }

    public static TexturesArgument ids() {
        return new TexturesArgument();
    }

    public static String getTexture(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    @Keep
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    @Keep
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> source, SuggestionsBuilder builder) {
        if (source.getSource() instanceof SharedSuggestionProvider) {
            String modelName = ModelsArgument.getModel((CommandContext<CommandSourceStack>) source, "model_id");
            if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
                if (ServerModelManager.CACHE_NAME_INFO.containsKey(modelName)) {
                    Set<String> textures = ServerModelManager.CACHE_NAME_INFO.get(modelName).getTextures();
                    return SharedSuggestionProvider.suggest(textures, builder);
                }
            } else {
                ResourceLocation modelId = new ResourceLocation(YesSteveModel.MOD_ID, modelName);
                if (ClientModelManager.MODELS.containsKey(modelId)) {
                    List<ResourceLocation> textures = ClientModelManager.MODELS.get(modelId);
                    Stream<String> stream = textures.stream().map(ModelIdUtil::getSubNameFromId).filter(StringUtils::isNoneBlank);
                    return SharedSuggestionProvider.suggest(stream, builder);
                }
            }
        }
        return Suggestions.empty();
    }

    @Override
    @Keep
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
