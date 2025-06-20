package com.elfmcys.yesstevemodel.geckolib3.core.molang;

import com.elfmcys.yesstevemodel.mclib.math.Variable;
import com.elfmcys.yesstevemodel.util.Keep;

import java.util.function.DoubleSupplier;

/**
 * 变量延迟计算，在需要时才会进行计算 <br>
 * 这样能优化渲染，只会在需要这个数值时才会进行计算
 */
public class LazyVariable extends Variable {
    private DoubleSupplier valueSupplier;

    @Deprecated
    public LazyVariable(String name, double value) {
        this(name, () -> value);
    }

    public LazyVariable(String name, DoubleSupplier valueSupplier) {
        super(name, 0);
        this.valueSupplier = valueSupplier;
    }

    public static LazyVariable from(Variable variable) {
        return new LazyVariable(variable.getName(), variable.get());
    }

    @Override
    @Keep
    public void set(double value) {
        this.valueSupplier = () -> value;
    }

    public void set(DoubleSupplier valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    @Override
    @Keep
    public double get() {
        return this.valueSupplier.getAsDouble();
    }
}
