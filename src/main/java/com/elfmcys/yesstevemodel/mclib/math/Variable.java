package com.elfmcys.yesstevemodel.mclib.math;

import com.elfmcys.yesstevemodel.util.Keep;

public class Variable implements IValue {
    private String name;
    private double value;

    public Variable(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    @Keep
    public double get() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    @Override
    @Keep
    public String toString() {
        return this.name;
    }
}
