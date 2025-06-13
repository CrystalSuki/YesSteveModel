package com.elfmcys.yesstevemodel.geckolib3.core.molang.expressions;

import com.elfmcys.yesstevemodel.geckolib3.core.molang.MolangParser;
import com.elfmcys.yesstevemodel.mclib.math.Constant;
import com.elfmcys.yesstevemodel.mclib.math.IValue;
import com.elfmcys.yesstevemodel.util.Keep;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class MolangValue extends MolangExpression {
    public IValue value;
    public boolean returns;

    public MolangValue(MolangParser context, IValue value) {
        super(context);
        this.value = value;
    }

    public MolangExpression addReturn() {
        this.returns = true;
        return this;
    }

    @Override
    @Keep
    public double get() {
        return this.value.get();
    }

    @Override
    @Keep
    public String toString() {
        return (this.returns ? MolangParser.RETURN : "") + this.value.toString();
    }

    @Override
    @Keep
    public JsonElement toJson() {
        if (this.value instanceof Constant) {
            return new JsonPrimitive(this.value.get());
        }
        return super.toJson();
    }
}
