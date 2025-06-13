package com.elfmcys.yesstevemodel.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class GeneralConfig {
    public static ForgeConfigSpec.BooleanValue DISCLAIMER_SHOW;
    public static ForgeConfigSpec.BooleanValue PRINT_ANIMATION_ROULETTE_MSG;
    public static ForgeConfigSpec.BooleanValue DISABLE_SELF_MODEL;
    public static ForgeConfigSpec.BooleanValue DISABLE_OTHER_MODEL;
    public static ForgeConfigSpec.BooleanValue DISABLE_SELF_HANDS;
    public static ForgeConfigSpec.ConfigValue<String> DEFAULT_MODEL_ID;
    public static ForgeConfigSpec.ConfigValue<String> DEFAULT_MODEL_TEXTURE;

    public static ForgeConfigSpec init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        init(builder);
        ExtraPlayerScreenConfig.init(builder);
        return builder.build();
    }

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("general");

        builder.comment("Whether to display disclaimer GUI");
        DISCLAIMER_SHOW = builder.define("DisclaimerShow", true);

        builder.comment("Whether to print animation roulette play message");
        PRINT_ANIMATION_ROULETTE_MSG = builder.define("PrintAnimationRouletteMsg", true);

        builder.comment("Prevents rendering of self player's model");
        DISABLE_SELF_MODEL = builder.define("DisableSelfModel", false);

        builder.comment("Prevents rendering of other player's model");
        DISABLE_OTHER_MODEL = builder.define("DisableOtherModel", false);

        builder.comment("Prevents rendering of self player's hand");
        DISABLE_SELF_HANDS = builder.define("DisableSelfHands", false);

        builder.comment("The default model ID when a player first enters the game");
        DEFAULT_MODEL_ID = builder.define("DefaultModelId", "default");

        builder.comment("The default model texture when a player first enters the game");
        DEFAULT_MODEL_TEXTURE = builder.define("DefaultModelTexture", "default.png");

        builder.pop();
    }
}
