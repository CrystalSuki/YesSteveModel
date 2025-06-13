package com.elfmcys.yesstevemodel.mclib.math.functions.classic;

import com.elfmcys.yesstevemodel.mclib.math.IValue;
import com.elfmcys.yesstevemodel.mclib.math.functions.Function;
import com.elfmcys.yesstevemodel.util.Keep;

public class ASin extends Function {
    public ASin(IValue[] values, String name) throws Exception {
        super(values, name);
    }

    @Override
    @Keep
    public int getRequiredArguments() {
        return 1;
    }

    @Override
    @Keep
    public double get() {
        return Math.asin(getArg(0));
    }
}
