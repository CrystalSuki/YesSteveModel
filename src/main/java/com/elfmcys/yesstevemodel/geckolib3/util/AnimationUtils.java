/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package com.elfmcys.yesstevemodel.geckolib3.util;

import com.elfmcys.yesstevemodel.geckolib3.geo.IGeoRenderer;
import com.elfmcys.yesstevemodel.geckolib3.model.provider.GeoModelProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AnimationUtils {
    public static double convertTicksToSeconds(double ticks) {
        return ticks / 20;
    }

    public static double convertSecondsToTicks(double seconds) {
        return seconds * 20;
    }

    /**
     * 获取实体的 Renderer
     */
    public static <T extends Entity> EntityRenderer<T> getRenderer(T entity) {
        EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
        return (EntityRenderer<T>) renderManager.getRenderer(entity);
    }

    public static <T extends Entity> GeoModelProvider getGeoModelForEntity(T entity) {
        EntityRenderer<T> entityRenderer = getRenderer(entity);
        if (entityRenderer instanceof IGeoRenderer geoRenderer) {
            return geoRenderer.getGeoModelProvider();
        }
        return null;
    }
}