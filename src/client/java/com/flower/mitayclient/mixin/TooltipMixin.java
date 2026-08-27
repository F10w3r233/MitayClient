package com.flower.mitayclient.mixin;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.flower.mitayclient.GUI.HUD.ToolBarHudRenderer.drawScaledItem;

@Mixin(ClientTextTooltip.class)
public class TooltipMixin
{
    @Shadow
    private final FormattedCharSequence text;

    public TooltipMixin(FormattedCharSequence text)
    {
        this.text = text;
    }

    @Inject(method = "extractText", at = @At("HEAD"), cancellable = true)
    public void renderText(final GuiGraphicsExtractor graphics, final Font font, final int x, final int y, CallbackInfo ci)
    {
        ci.cancel();
        String content = getStringWithStyle(text);
        int index = 0;
        for (String line : content.lines().toList())
        {
            int itemColor = CommonColors.WHITE;

            index++;
            if (line.contains("$"))
            {

                //minecraft:poppy
                String itemRegisterID = "minecraft:" + line.replaceFirst("^§[0-9a-zA-Z]\\s*", "").replace("$", "").trim();
                Identifier itemId =
                        Identifier.tryParse(itemRegisterID);
                String pureItemName = itemRegisterID.replace("minecraft:", "");//poppy
                if(itemId != null)
                {
                    Optional<Holder.Reference<Item>> optHolder = BuiltInRegistries.ITEM.get(itemId);
                    if (optHolder.isPresent())
                    {
                        Item item = optHolder.get().value();
                        itemColor = item.getDefaultInstance().getRarity().color().getColor();
                        Component itemName = Component.translatable(item.getDefaultInstance().getItemName().getString());//物品中文名
                        line = "   " + itemName.getString();
                        drawScaledItem(graphics, item.getDefaultInstance(), x + 1, y + index - 2, 0.6f);
                    }

                }
            }
            System.out.println(itemColor);
            graphics.text(font, line, x, y, ARGB.color(1f, itemColor), true);
        }
    }

    private static String getStringWithStyle(FormattedCharSequence sequence)
    {
        StringBuilder sb = new StringBuilder();
        AtomicReference<Style> lastStyle = new AtomicReference<>(null);

        sequence.accept((index, style, codePoint) -> {
            Style previous = lastStyle.getAndSet(style);

            // 样式变化时插入格式代码
            if (!style.equals(previous)) {
                // 如果 previous 不为 null，先插入重置符（可选）
                if (previous != null) {
                    sb.append('§').append('r');
                }

                // 处理颜色
                TextColor textColor = style.getColor();
                if (textColor != null) {
                    ChatFormatting color = getChatFormattingByColor(textColor.getValue());
                    if (color != null && color.isColor()) {
                        sb.append('§').append(color.getChar());
                    }
                }

                // 处理其他格式（加粗、斜体等）
                if (style.isBold()) sb.append('§').append('l');
                if (style.isItalic()) sb.append('§').append('o');
                if (style.isUnderlined()) sb.append('§').append('n');
                if (style.isStrikethrough()) sb.append('§').append('m');
                if (style.isObfuscated()) sb.append('§').append('k');
            }

            // 追加字符（包括换行符 \n）
            sb.appendCodePoint(codePoint);
            return true;
        });

        return sb.toString();
    }

    /**
     * 根据 RGB 颜色值查找对应的 ChatFormatting 枚举
     */
    private static ChatFormatting getChatFormattingByColor(int rgb) {
        for (ChatFormatting format : ChatFormatting.values()) {
            Integer color = format.getColor();
            if (color != null && color == rgb) {
                return format;
            }
        }
        return null;
    }
}
