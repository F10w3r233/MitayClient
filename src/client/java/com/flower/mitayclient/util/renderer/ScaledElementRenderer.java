package com.flower.mitayclient.util.renderer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;

public class ScaledElementRenderer
{
    public static void drawScaledItem(GuiGraphicsExtractor context, ItemStack item, int x, int y, float scale)
    {
        Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        context.item(item,0,0);
        matrices.popMatrix();
    }

    public static void drawScaledText(GuiGraphicsExtractor context, Font font,
                                      Component text, int x, int y, float scale,
                                      int color, boolean shadow)
    {
        Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        context.text(font, text, 0, 0, color, shadow);
        matrices.popMatrix();
    }
}
