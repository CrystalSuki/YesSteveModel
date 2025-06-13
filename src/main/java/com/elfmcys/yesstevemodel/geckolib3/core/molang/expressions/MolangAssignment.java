package com.elfmcys.yesstevemodel.geckolib3.core.molang.expressions;

import com.elfmcys.yesstevemodel.geckolib3.core.molang.MolangParser;
import com.elfmcys.yesstevemodel.mclib.math.IValue;
import com.elfmcys.yesstevemodel.mclib.math.Variable;
import com.elfmcys.yesstevemodel.util.Keep;

public class MolangAssignment extends MolangExpression {
    public Variable variable;
    public IValue expression;

    public MolangAssignment(MolangParser context, Variable variable, IValue expression) {
        super(context);
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    @Keep
    public double get() {
        double value = this.expression.get();
        this.variable.set(value);
        return value;
    }

    @Override
    @Keep
    public String toString() {
        return this.variable.getName() + " = " + this.expression.toString();
    }
}
