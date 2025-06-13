package com.elfmcys.yesstevemodel.geckolib3.geo.render;

import com.elfmcys.yesstevemodel.geckolib3.geo.raw.pojo.ModelProperties;
import com.elfmcys.yesstevemodel.geckolib3.geo.raw.tree.RawBoneGroup;
import com.elfmcys.yesstevemodel.geckolib3.geo.raw.tree.RawGeometryTree;
import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoBone;
import com.elfmcys.yesstevemodel.geckolib3.geo.render.built.GeoModel;
import com.elfmcys.yesstevemodel.util.Keep;

public interface IGeoBuilder {
    @Keep
    GeoModel constructGeoModel(RawGeometryTree geometryTree);

    @Keep
    GeoBone constructBone(RawBoneGroup bone, ModelProperties properties, GeoBone parent);
}
