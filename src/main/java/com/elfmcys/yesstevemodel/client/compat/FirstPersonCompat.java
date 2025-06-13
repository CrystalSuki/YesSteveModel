package com.elfmcys.yesstevemodel.client.compat;

import com.elfmcys.yesstevemodel.client.model.CustomPlayerModel;
import com.elfmcys.yesstevemodel.geckolib3.core.processor.IBone;
import dev.tr7zw.firstperson.api.FirstPersonAPI;
import dev.tr7zw.firstperson.api.PlayerOffsetHandler;
import net.minecraft.world.phys.Vec3;

public class FirstPersonCompat {
    public static void hideHead(IBone head) {
        head.setHidden(FirstPersonAPI.isRenderingPlayer());
    }

    public static void registerOffset() {
        FirstPersonAPI.registerPlayerHandler((PlayerOffsetHandler) (entity, delta, original, current) ->
                new Vec3(current.x(), 1.5 - CustomPlayerModel.FIRST_PERSON_HEAD_POS / 16, current.z()));
    }

    public static boolean isHeadHide() {
        return FirstPersonAPI.isRenderingPlayer();
    }
}