package com.elfmcys.yesstevemodel.geckolib3.util;

import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoBone;
import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoCube;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class RenderUtils {
    public static void translateMatrixToBone(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.getPositionX() / 16f, bone.getPositionY() / 16f, bone.getPositionZ() / 16f);
    }

    public static void rotateMatrixAroundBone(PoseStack poseStack, GeoBone bone) {
        if (bone.getRotationZ() != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotation(bone.getRotationZ()));
        }
        if (bone.getRotationY() != 0.0F) {
            poseStack.mulPose(Axis.YP.rotation(bone.getRotationY()));
        }
        if (bone.getRotationX() != 0.0F) {
            poseStack.mulPose(Axis.XP.rotation(bone.getRotationX()));
        }
    }

    public static void rotateMatrixAroundCube(PoseStack poseStack, GeoCube cube) {
        Vector3f rotation = cube.rotation;
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, rotation.z()));
        poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float) rotation.y(), 0));
        poseStack.mulPose(new Quaternionf().rotationXYZ(rotation.x(), 0, 0));
    }

    public static void scaleMatrixForBone(PoseStack poseStack, GeoBone bone) {
        poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static void translateToPivotPoint(PoseStack poseStack, GeoCube cube) {
        Vector3f pivot = cube.pivot;
        poseStack.translate(pivot.x() / 16f, pivot.y() / 16f, pivot.z() / 16f);
    }

    public static void translateToPivotPoint(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(bone.rotationPointX / 16f, bone.rotationPointY / 16f, bone.rotationPointZ / 16f);
    }

    public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoCube cube) {
        Vector3f pivot = cube.pivot;
        poseStack.translate(-pivot.x() / 16f, -pivot.y() / 16f, -pivot.z() / 16f);
    }

    public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoBone bone) {
        poseStack.translate(-bone.rotationPointX / 16f, -bone.rotationPointY / 16f, -bone.rotationPointZ / 16f);
    }

    public static void translateAndRotateMatrixForBone(PoseStack poseStack, GeoBone bone) {
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
    }

    public static void prepMatrixForBone(PoseStack poseStack, GeoBone bone) {
        translateMatrixToBone(poseStack, bone);
        translateToPivotPoint(poseStack, bone);
        rotateMatrixAroundBone(poseStack, bone);
        scaleMatrixForBone(poseStack, bone);
        translateAwayFromPivotPoint(poseStack, bone);
    }

    public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
        inputMatrix = new Matrix4f(inputMatrix);
        inputMatrix.invert();
        inputMatrix.mul(baseMatrix);
        return inputMatrix;
    }
}
