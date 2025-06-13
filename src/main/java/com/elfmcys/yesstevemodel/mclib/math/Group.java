package com.elfmcys.yesstevemodel.mclib.math;

import com.elfmcys.yesstevemodel.util.Keep;

public class Group implements IValue {
    private IValue value;

    public Group(IValue value) {
        this.value = value;
    }

    @Override
    @Keep
    public double get() {
        return this.value.get();
    }

    @Override
    @Keep
    public String toString() {
        return "(" + this.value.toString() + ")";
    }
}
