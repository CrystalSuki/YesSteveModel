package com.elfmcys.yesstevemodel.client.gui.button;

import com.elfmcys.yesstevemodel.capability.ModelInfoCapabilityProvider;
import com.elfmcys.yesstevemodel.client.ClientModelManager;
import com.elfmcys.yesstevemodel.util.Keep;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextureCountButton extends FlatColorButton {
    public TextureCountButton(int x, int y) {
        super(x, y, 20, 20, Component.empty(), (b) -> {
        });
    }

    @Override
    @Keep
    public Component getMessage() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            return player.getCapability(ModelInfoCapabilityProvider.MODEL_INFO_CAP).map(cap -> {
                ResourceLocation modelId = cap.getModelId();
                if (ClientModelManager.MODELS.containsKey(modelId)) {
                    String countText = String.valueOf(ClientModelManager.MODELS.get(modelId).size());
                    return Component.literal(countText);
                }
                return super.getMessage();
            }).orElse(super.getMessage());
        }
        return super.getMessage();
    }
}
