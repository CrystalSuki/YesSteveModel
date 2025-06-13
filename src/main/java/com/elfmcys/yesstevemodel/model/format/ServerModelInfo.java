package com.elfmcys.yesstevemodel.model.format;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Optional;
import java.util.Set;

public class ServerModelInfo {
    @SerializedName("textures")
    @Expose
    private final Set<String> textures;
    @SerializedName("need_auth")
    @Expose
    private final boolean needAuth;
    @Expose(serialize = false, deserialize = false)
    private final Type type;
    @Expose(serialize = false, deserialize = false)
    private String md5;

    public ServerModelInfo(Set<String> textures, boolean needAuth, Type type) {
        this.textures = textures;
        this.needAuth = needAuth;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Set<String> getTextures() {
        return textures;
    }

    public Optional<String> getTexture() {
        return textures.stream().findFirst();
    }

    public boolean isNeedAuth() {
        return needAuth;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
