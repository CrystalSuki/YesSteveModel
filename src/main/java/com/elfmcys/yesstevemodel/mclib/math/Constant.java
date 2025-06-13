package com.elfmcys.yesstevemodel.mclib.math;

import com.elfmcys.yesstevemodel.util.Keep;

public class Constant implements IValue {
    private double value;

    public Constant(double value) {
        this.value = value;
    }

    @Override
    @Keep
    public double get() {
        return this.value;
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    @Keep
    public String toString() {
        return String.valueOf(this.value);
    }
}
