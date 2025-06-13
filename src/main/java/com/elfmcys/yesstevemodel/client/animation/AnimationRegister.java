package com.elfmcys.yesstevemodel.client.animation;

import com.elfmcys.yesstevemodel.client.compat.FirstPersonCompat;
import com.elfmcys.yesstevemodel.client.entity.CustomPlayerEntity;
import com.elfmcys.yesstevemodel.geckolib3.core.builder.ILoopType;
import com.elfmcys.yesstevemodel.geckolib3.core.event.predicate.AnimationEvent;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.LazyVariable;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.MolangParser;
import com.elfmcys.yesstevemodel.geckolib3.model.provider.data.EntityModelData;
import com.elfmcys.yesstevemodel.geckolib3.resource.GeckoLibCache;
import com.elfmcys.yesstevemodel.geckolib3.util.MolangUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;

import java.util.function.BiPredicate;

public class AnimationRegister {
    private static final double MIN_SPEED = 0.05;
    private static final String FIRST_PERSON_MOD_ID = "firstpersonmod";

    public static void registerAnimationState() {
        register("death", ILoopType.EDefaultLoopTypes.PLAY_ONCE, Priority.HIGHEST, (player, event) -> player.isDeadOrDying());
        register("riptide", Priority.HIGHEST, (player, event) -> player.isAutoSpinAttack());
        register("sleep", Priority.HIGHEST, (player, event) -> player.getPose() == Pose.SLEEPING);
        register("swim", Priority.HIGHEST, (player, event) -> player.isSwimming());
        register("climb", Priority.HIGHEST, (player, event) -> player.getPose() == Pose.SWIMMING && Math.abs(event.getLimbSwingAmount()) > MIN_SPEED);
        register("climbing", Priority.HIGHEST, (player, event) -> player.getPose() == Pose.SWIMMING);

        register("ride_pig", Priority.HIGH, (player, event) -> player.getVehicle() instanceof Pig);
        register("ride", Priority.HIGH, (player, event) -> player.getVehicle() instanceof Saddleable);
        register("boat", Priority.HIGH, (player, event) -> player.getVehicle() instanceof Boat);
        register("sit", Priority.HIGH, (player, event) -> player.isPassenger());

        register("fly", Priority.HIGH, (player, event) -> player.getAbilities().flying);
        register("elytra_fly", Priority.HIGH, (player, event) -> player.getPose() == Pose.FALL_FLYING && player.isFallFlying());

        register("swim_stand", Priority.NORMAL, (player, event) -> player.isInWater());
        register("attacked", ILoopType.EDefaultLoopTypes.PLAY_ONCE, Priority.NORMAL, (player, event) -> player.hurtTime > 0);
        register("jump", Priority.NORMAL, (player, event) -> !player.onGround() && !player.isInWater());
        register("sneak", Priority.NORMAL, (player, event) -> player.onGround() && player.getPose() == Pose.CROUCHING && Math.abs(event.getLimbSwingAmount()) > MIN_SPEED);
        register("sneaking", Priority.NORMAL, (player, event) -> player.onGround() && player.getPose() == Pose.CROUCHING);

        register("run", Priority.LOW, (player, event) -> player.onGround() && player.isSprinting());
        register("walk", Priority.LOW, (player, event) -> player.onGround() && event.getLimbSwingAmount() > MIN_SPEED);

        register("idle", Priority.LOWEST, (player, event) -> true);
    }

    @SuppressWarnings("deprecation")
    public static void registerVariables() {
        MolangParser parser = GeckoLibCache.getInstance().parser;

        parser.register(new LazyVariable("query.actor_count", 0));
        parser.register(new LazyVariable("query.anim_time", 0));

        parser.register(new LazyVariable("query.body_x_rotation", 0));
        parser.register(new LazyVariable("query.body_y_rotation", 0));
        parser.register(new LazyVariable("query.cardinal_facing_2d", 0));
        parser.register(new LazyVariable("query.distance_from_camera", 0));
        parser.register(new LazyVariable("query.equipment_count", 0));
        parser.register(new LazyVariable("query.eye_target_x_rotation", 0));
        parser.register(new LazyVariable("query.eye_target_y_rotation", 0));
        parser.register(new LazyVariable("query.ground_speed", 0));

        parser.register(new LazyVariable("query.has_cape", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.has_rider", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.head_x_rotation", 0));
        parser.register(new LazyVariable("query.head_y_rotation", 0));
        parser.register(new LazyVariable("query.health", 0));
        parser.register(new LazyVariable("query.hurt_time", 0));

        parser.register(new LazyVariable("query.is_eating", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_first_person", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_in_water", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_in_water_or_rain", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_jumping", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_on_fire", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_on_ground", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_playing_dead", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_riding", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_sleeping", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_sneaking", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_spectator", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_sprinting", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_swimming", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.is_using_item", MolangUtils.FALSE));
        parser.register(new LazyVariable("query.item_in_use_duration", 0));
        parser.register(new LazyVariable("query.item_max_use_duration", 0));
        parser.register(new LazyVariable("query.item_remaining_use_duration", 0));

        parser.register(new LazyVariable("query.life_time", 0));
        parser.register(new LazyVariable("query.max_health", 0));
        parser.register(new LazyVariable("query.modified_distance_moved", 0));
        parser.register(new LazyVariable("query.moon_phase", 0));

        parser.register(new LazyVariable("query.player_level", 0));
        parser.register(new LazyVariable("query.time_of_day", 0));
        parser.register(new LazyVariable("query.time_stamp", 0));
        parser.register(new LazyVariable("query.vertical_speed", 0));
        parser.register(new LazyVariable("query.walk_distance", 0));
        parser.register(new LazyVariable("query.yaw_speed", 0));

        parser.register(new LazyVariable("ysm.head_yaw", 0));
        parser.register(new LazyVariable("ysm.head_pitch", 0));
        parser.register(new LazyVariable("ysm.has_helmet", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_chest_plate", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_leggings", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_boots", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_mainhand", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.has_offhand", MolangUtils.FALSE));

        parser.register(new LazyVariable("ysm.has_elytra", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.elytra_rot_x", 0));
        parser.register(new LazyVariable("ysm.elytra_rot_y", 0));
        parser.register(new LazyVariable("ysm.elytra_rot_z", 0));

        parser.register(new LazyVariable("ysm.is_close_eyes", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.is_passenger", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.is_sleep", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.is_sneak", MolangUtils.FALSE));
        parser.register(new LazyVariable("ysm.is_riptide", MolangUtils.FALSE));

        parser.register(new LazyVariable("ysm.armor_value", 0));
        parser.register(new LazyVariable("ysm.hurt_time", 0));
        parser.register(new LazyVariable("ysm.food_level", 20));

        parser.register(new LazyVariable("ysm.first_person_mod_hide", MolangUtils.FALSE));
    }

    public static void setParserValue(AnimationEvent<CustomPlayerEntity> animationEvent, MolangParser parser, EntityModelData data, Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        parser.setValue("query.actor_count", () -> mc.level.getEntityCount());
        parser.setValue("query.body_x_rotation", player::getXRot);
        parser.setValue("query.body_y_rotation", () -> Mth.wrapDegrees(player.getYRot()));
        parser.setValue("query.cardinal_facing_2d", () -> player.getDirection().get3DDataValue());
        parser.setValue("query.distance_from_camera", () -> mc.gameRenderer.getMainCamera().getPosition().distanceTo(player.position()));
        parser.setValue("query.equipment_count", () -> getEquipmentCount(player));
        parser.setValue("query.eye_target_x_rotation", () -> player.getViewXRot(0));
        parser.setValue("query.eye_target_y_rotation", () -> player.getViewYRot(0));
        parser.setValue("query.ground_speed", () -> getGroundSpeed(player));

        parser.setValue("query.has_cape", () -> MolangUtils.booleanToFloat(hasCape(player)));
        parser.setValue("query.has_rider", () -> MolangUtils.booleanToFloat(player.isVehicle()));
        parser.setValue("query.head_x_rotation", () -> data.netHeadYaw);
        parser.setValue("query.head_y_rotation", () -> data.headPitch);
        parser.setValue("query.health", player::getHealth);
        parser.setValue("query.hurt_time", () -> player.hurtTime);

        parser.setValue("query.is_eating", () -> MolangUtils.booleanToFloat(player.getUseItem().getUseAnimation() == UseAnim.EAT));
        parser.setValue("query.is_first_person", () -> MolangUtils.booleanToFloat(mc.options.getCameraType() == CameraType.FIRST_PERSON));
        parser.setValue("query.is_in_water", () -> MolangUtils.booleanToFloat(player.isInWater()));
        parser.setValue("query.is_in_water_or_rain", () -> MolangUtils.booleanToFloat(player.isInWaterRainOrBubble()));
        parser.setValue("query.is_jumping", () -> MolangUtils.booleanToFloat(!player.getAbilities().flying && !player.isPassenger() && !player.onGround() && !player.isInWater()));
        parser.setValue("query.is_on_fire", () -> MolangUtils.booleanToFloat(player.isOnFire()));
        parser.setValue("query.is_on_ground", () -> MolangUtils.booleanToFloat(player.onGround()));
        parser.setValue("query.is_on_ground", () -> MolangUtils.booleanToFloat(player.onGround()));
        parser.setValue("query.is_playing_dead", () -> MolangUtils.booleanToFloat(player.isDeadOrDying()));
        parser.setValue("query.is_riding", () -> MolangUtils.booleanToFloat(player.isPassenger()));
        parser.setValue("query.is_sleeping", () -> MolangUtils.booleanToFloat(player.isSleeping()));
        parser.setValue("query.is_sneaking", () -> MolangUtils.booleanToFloat(player.onGround() && player.getPose() == Pose.CROUCHING));
        parser.setValue("query.is_spectator", () -> MolangUtils.booleanToFloat(player.isSpectator()));
        parser.setValue("query.is_sprinting", () -> MolangUtils.booleanToFloat(player.isSprinting()));
        parser.setValue("query.is_swimming", () -> MolangUtils.booleanToFloat(player.isSwimming()));
        parser.setValue("query.is_using_item", () -> MolangUtils.booleanToFloat(player.isUsingItem()));
        parser.setValue("query.item_in_use_duration", () -> player.getTicksUsingItem() / 20.0);
        parser.setValue("query.item_max_use_duration", () -> getMaxUseDuration(player) / 20.0);
        parser.setValue("query.item_remaining_use_duration", () -> player.getUseItemRemainingTicks() / 20.0);

        parser.setValue("query.max_health", player::getMaxHealth);
        parser.setValue("query.modified_distance_moved", () -> player.walkDist);
        parser.setValue("query.moon_phase", () -> mc.level.getMoonPhase());

        parser.setValue("query.player_level", () -> player.experienceLevel);
        parser.setValue("query.time_of_day", () -> MolangUtils.normalizeTime(mc.level.getDayTime()));
        parser.setValue("query.time_stamp", () -> mc.level.getDayTime());
        parser.setValue("query.vertical_speed", () -> getVerticalSpeed(player));
        parser.setValue("query.walk_distance", () -> player.moveDist);
        parser.setValue("query.yaw_speed", () -> getYawSpeed(animationEvent, player));

        parser.setValue("ysm.head_yaw", () -> data.netHeadYaw);
        parser.setValue("ysm.head_pitch", () -> data.headPitch);

        parser.setValue("ysm.has_helmet", () -> getSlotValue(player, EquipmentSlot.HEAD));
        parser.setValue("ysm.has_chest_plate", () -> getSlotValue(player, EquipmentSlot.CHEST));
        parser.setValue("ysm.has_leggings", () -> getSlotValue(player, EquipmentSlot.LEGS));
        parser.setValue("ysm.has_boots", () -> getSlotValue(player, EquipmentSlot.FEET));
        parser.setValue("ysm.has_mainhand", () -> getSlotValue(player, EquipmentSlot.MAINHAND));
        parser.setValue("ysm.has_offhand", () -> getSlotValue(player, EquipmentSlot.OFFHAND));

        parser.setValue("ysm.has_elytra", () -> MolangUtils.booleanToFloat(player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA));
        parser.setValue("ysm.elytra_rot_x", () -> {
            if (player instanceof LocalPlayer localPlayer) {
                return Math.toDegrees(localPlayer.elytraRotX);
            }
            return 0;
        });
        parser.setValue("ysm.elytra_rot_y", () -> {
            if (player instanceof LocalPlayer localPlayer) {
                return Math.toDegrees(localPlayer.elytraRotY);
            }
            return 0;
        });
        parser.setValue("ysm.elytra_rot_z", () -> {
            if (player instanceof LocalPlayer localPlayer) {
                return Math.toDegrees(localPlayer.elytraRotZ);
            }
            return 0;
        });

        parser.setValue("ysm.is_close_eyes", () -> getEyeCloseState(animationEvent, player));
        parser.setValue("ysm.is_passenger", () -> MolangUtils.booleanToFloat(player.isPassenger()));
        parser.setValue("ysm.is_sleep", () -> MolangUtils.booleanToFloat(player.getPose() == Pose.SLEEPING));
        parser.setValue("ysm.is_sneak", () -> MolangUtils.booleanToFloat(player.onGround() && player.getPose() == Pose.CROUCHING));
        parser.setValue("ysm.is_riptide", () -> MolangUtils.booleanToFloat(player.isAutoSpinAttack()));

        parser.setValue("ysm.armor_value", player::getArmorValue);
        parser.setValue("ysm.hurt_time", () -> player.hurtTime);
        parser.setValue("ysm.food_level", () -> player.getFoodData().getFoodLevel());

        if (ModList.get().isLoaded(FIRST_PERSON_MOD_ID)) {
            parser.setValue("ysm.first_person_mod_hide", () -> MolangUtils.booleanToFloat(FirstPersonCompat.isHeadHide()));
        }
    }

    private static boolean hasCape(Player player) {
        if (player instanceof AbstractClientPlayer clientPlayer) {
            return clientPlayer.isCapeLoaded() && !player.isInvisible() && clientPlayer.isModelPartShown(PlayerModelPart.CAPE) && clientPlayer.getCloakTextureLocation() != null;
        }
        return false;
    }

    private static int getEquipmentCount(Player player) {
        int count = 0;
        for (ItemStack s : player.getArmorSlots()) {
            if (!s.isEmpty()) {
                count += 1;
            }
        }
        return count;
    }

    private static double getMaxUseDuration(Player player) {
        ItemStack useItem = player.getUseItem();
        if (useItem.isEmpty()) {
            return 0.0;
        } else {
            return useItem.getUseDuration();
        }
    }

    private static float getYawSpeed(AnimationEvent<CustomPlayerEntity> animationEvent, Player player) {
        double seekTime = animationEvent.getAnimationTick();
        return player.getViewYRot((float) seekTime - player.getViewYRot((float) seekTime - 0.1f));
    }

    private static float getGroundSpeed(Player player) {
        Vec3 velocity = player.getDeltaMovement();
        return 20 * Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
    }

    private static float getVerticalSpeed(Player player) {
        return 20 * (float) (player.position().y - player.yo);
    }

    private static void register(String animationName, ILoopType loopType, int priority, BiPredicate<Player, AnimationEvent<CustomPlayerEntity>> predicate) {
        AnimationManager manager = AnimationManager.getInstance();
        manager.register(new AnimationState(animationName, loopType, priority, predicate));
    }

    private static void register(String animationName, int priority, BiPredicate<Player, AnimationEvent<CustomPlayerEntity>> predicate) {
        register(animationName, ILoopType.EDefaultLoopTypes.LOOP, priority, predicate);
    }

    private static double getEyeCloseState(AnimationEvent<CustomPlayerEntity> animationEvent, Player player) {
        double remainder = (animationEvent.getAnimationTick() + Math.abs(player.getUUID().getLeastSignificantBits()) % 10) % 90;
        boolean isBlinkTime = 85 < remainder && remainder < 90;
        return MolangUtils.booleanToFloat(player.isSleeping() || isBlinkTime);
    }

    private static double getSlotValue(Player player, EquipmentSlot slot) {
        return MolangUtils.booleanToFloat(!player.getItemBySlot(slot).isEmpty());
    }
}