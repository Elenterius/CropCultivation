package com.creativechasm.cropcultivation.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class TextUtil
{
    public static final ITextComponent EMPTY_STRING = new StringTextComponent("");
    public static final List<ITextComponent> EMPTY_LINES = ImmutableList.of();

    public static final String[] GRADES = new String[]{"F", "E", "C", "B", "A"};

    public static ITextComponent insetTextComponent(ITextComponent component) {
        return new StringTextComponent(" ").appendSibling(component);
    }

    public static ITextComponent wrapInRoundBrackets(ITextComponent component) {
        return new StringTextComponent("(").appendSibling(component).appendText(")");
    }
}
