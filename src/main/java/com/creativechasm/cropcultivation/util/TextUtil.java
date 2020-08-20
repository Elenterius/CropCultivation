package com.creativechasm.cropcultivation.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TextUtil
{
    public static final ITextComponent EMPTY_STRING = new StringTextComponent("");
    public static final String[] GRADES = new String[]{"F", "E", "C", "B", "A"};

    public static ITextComponent insetTextComponent(ITextComponent component) {
        return new StringTextComponent(" ").appendSibling(component);
    }

    public static ITextComponent wrapInRoundBrackets(ITextComponent component) {
        return new StringTextComponent("(").appendSibling(component).appendText(")");
    }
}
