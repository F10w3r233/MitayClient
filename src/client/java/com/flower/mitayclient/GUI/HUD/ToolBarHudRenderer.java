package com.flower.mitayclient.GUI.HUD;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.Data.PlayerDataHandler;
import com.flower.mitayclient.util.Resource;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.joml.Matrix3x2fStack;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public class ToolBarHudRenderer
{
    public final static Map<String, Identifier> Enchantment = new HashMap<>();
    static
    {
        Enchantment.put("smite", Resource.smite);
        Enchantment.put("mending", Resource.mending);
        Enchantment.put("unbreaking", Resource.unbreaking);
        Enchantment.put("protection", Resource.protection);
        Enchantment.put("feather_falling", Resource.feather_falling);
        Enchantment.put("looting", Resource.looting);
        Enchantment.put("efficiency", Resource.efficiency);
        Enchantment.put("fortune", Resource.fortune);
        Enchantment.put("sharpness", Resource.sharpness);
        Enchantment.put("fire_aspect", Resource.fire_aspect);
        Enchantment.put("sweeping_edge", Resource.sweeping_edge);
        Enchantment.put("knockback", Resource.knockback);
        Enchantment.put("silk_touch", Resource.silk_touch);

        //trident
        Enchantment.put("loyalty", Resource.loyalty);
        Enchantment.put("channeling", Resource.channeling);
        Enchantment.put("riptide", Resource.riptide);
        Enchantment.put("impaling", Resource.impaling);

        //mace
        Enchantment.put("wind_burst", Resource.wind_burst);
        Enchantment.put("density", Resource.density);
        Enchantment.put("breach", Resource.breach);

        //bow
        Enchantment.put("flame", Resource.flame);
        Enchantment.put("punch", Resource.punch);
        Enchantment.put("infinity", Resource.infinity);
        Enchantment.put("power", Resource.power);

        //crossbow

    }

    private static Player getCameraPlayer()
    {
        return Minecraft.getInstance().getCameraEntity() instanceof Player playerEntity ? playerEntity : null;
    }
    public static int k1 = Minecraft.getInstance().getWindow().getGuiScaledWidth()+ 120;
    public static int k2 = Minecraft.getInstance().getWindow().getGuiScaledWidth()+ 120;
    static int playerLeftToolDamageColor = 0xFFDCDCDC;
    static int playerRightToolDamageColor = 0xFFDCDCDC;
    static int playerLeftToolNameColor = 0xFFDCDCDC;
    static int playerRightToolNameColor = 0xFFDCDCDC;

    static int leftBiaHeight = 0;
    static int rightBiaHeight = 0;

    private static int lastScreenWidth = -1;

    public static void render(GuiGraphicsExtractor context)
    {

        if(Mitayclient.getConfig().isToolbarShown())
        {
            if(!Minecraft.getInstance().options.hideGui)
            {
                int currentWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                if (lastScreenWidth != currentWidth)
                {
                    k1 = currentWidth + 120;
                    k2 = currentWidth + 120;
                    lastScreenWidth = currentWidth;
                }
                Map<String, Integer> leftEnchantments = new HashMap<>();
                Map<String, Integer> rightEnchantments = new HashMap<>();
                Minecraft client = Minecraft.getInstance();
                List<ItemStack> itemStacks = new ArrayList<>();
                itemStacks.add(getCameraPlayer().getItemInHand(InteractionHand.MAIN_HAND));
                itemStacks.add(getCameraPlayer().getItemInHand(InteractionHand.OFF_HAND));

                //主副手物品
                ItemStack leftHandItem = itemStacks.get(1);
                ItemStack rightHandItem = itemStacks.get(0);


                //主副手物品颜色处理
                if (leftHandItem.isEnchanted())
                {
                    playerLeftToolNameColor = 0xFF54FBFB;
                    if(leftBiaHeight < 15)
                        leftBiaHeight++;
                }else
                {
                    if (leftBiaHeight > 0)
                        leftBiaHeight--;
                    playerLeftToolNameColor = 0xFFDCDCDC;
                }

                if(rightHandItem.isEnchanted())
                {
                    if(rightBiaHeight < 15)
                        rightBiaHeight++;
                    playerRightToolNameColor = 0xFF54FBFB;
                }else
                {
                    if (rightBiaHeight > 0)
                        rightBiaHeight--;
                    playerRightToolNameColor = 0xFFDCDCDC;
                }




                //耐久颜色 （是否有经验修补）
                for(Object2IntMap.Entry<Holder<net.minecraft.world.item.enchantment.Enchantment>> enchantment: leftHandItem.getEnchantments().entrySet())
                {
                    String enchantmentName = enchantment.getKey().getRegisteredName().replace("minecraft:", "");
                    int level = enchantment.getIntValue();
                    if(enchantmentName.equals("mending") || enchantmentName.equals("silk_touch") || enchantmentName.equals("channeling")  || enchantmentName.equals("flame") || enchantmentName.equals("infinity"))
                    {
                        level = 0;
                    }
                    if(enchantmentName.contains("mending"))
                    {
                        playerLeftToolDamageColor = 0xFF54FBFB;
                    }else playerLeftToolDamageColor = 0xFFDCDCDC;



                    for(Map.Entry<String, Identifier> entry : Enchantment.entrySet())
                    {
//                System.out.println("enchatment name: " + enchantmentName);
//                System.out.println("entry name: " + entry.getKey());
                        if(entry.getKey().equals(enchantmentName))
                            leftEnchantments.put(enchantmentName, level);
                    }
                }

                for(Object2IntMap.Entry<Holder<net.minecraft.world.item.enchantment.Enchantment>> enchantment: rightHandItem.getEnchantments().entrySet())
                {
                    String enchantmentName = enchantment.getKey().getRegisteredName().replace("minecraft:", "");
                    int level = enchantment.getIntValue();
                    if(enchantmentName.equals("mending") || enchantmentName.equals("silk_touch") || enchantmentName.equals("channeling") || enchantmentName.equals("flame") || enchantmentName.equals("infinity"))
                    {
                        level = 0;
                    }
                    if(enchantmentName.contains("mending"))
                    {
                        playerRightToolDamageColor = 0xFF10F447;
                    }else playerRightToolDamageColor = 0xFFDCDCDC;

                    for(Map.Entry<String, Identifier> entry : Enchantment.entrySet())
                    {
//                System.out.println("enchatment name: " + enchantmentName);
//                System.out.println("entry name: " + entry.getKey());
                        if(entry.getKey().equals(enchantmentName))
                            rightEnchantments.put(enchantmentName, level);
                    }
                }


                Identifier tool_bar;
                Identifier tool_bar_mending;
                if(Mitayclient.getConfig().isDarkShown())
                {
                    tool_bar = Resource.TOOL_BAR_DARK;
                }else
                {
                    tool_bar = Resource.TOOL_BAR;
                }

                if(leftHandItem.isDamageableItem())//左手----------------------------------------------------------------------------
                {

                    if(k2 > client.getWindow().getGuiScaledWidth()-130)
                        k2-=2;

                    Component itemName = leftHandItem.getHoverName();
                    Component damage = Component.nullToEmpty(leftHandItem.getMaxDamage() - leftHandItem.getDamageValue() + "/" + leftHandItem.getMaxDamage());
                    int nameLength = 0;
                    if(client.font.width(itemName) > client.font.width(damage))
                    {
                        nameLength = client.font.width(itemName);
                    }
                    nameLength -= client.font.width("netherite ");

                    int enchantmentLength = 0;
                    if(leftEnchantments.size() > 4)
                    {
                        int length = leftEnchantments.size() - 4;
                        int times = length == 1 ? 14 : 18;
                        enchantmentLength += length * times;
//                System.out.println(length);
//                System.out.println(enchantmentLength);
                    }

                    int finalLength = Math.max(nameLength, enchantmentLength);

                    context.blit(RenderPipelines.GUI_TEXTURED, tool_bar, k2 - 5 - finalLength, client.getWindow().getGuiScaledHeight()-120  , 0,0,105+finalLength,37+leftBiaHeight,105+finalLength,37+leftBiaHeight);

//            context.drawItem(leftHandItem, k2-length + 10, client.getWindow().getScaledHeight()-115+9);
                    drawScaledItem(context,leftHandItem, k2 + 2 - finalLength, client.getWindow().getGuiScaledHeight()-113,1.5f);

                    //物品名
                    context.text(client.font, itemName, k2+34-finalLength, client.getWindow().getGuiScaledHeight()-120+8, playerLeftToolNameColor, true);
                    //耐久
                    context.text(client.font, damage , k2+34-finalLength, client.getWindow().getGuiScaledHeight()-102, playerLeftToolDamageColor, true);


                    int index = 0;
                    for (Map.Entry<String, Integer> entry : leftEnchantments.entrySet())
                    {
                        Identifier icon = Enchantment.get(entry.getKey());
                        String level = "";
                        switch (entry.getValue())
                        {
                            case 1 -> level = "I";
                            case 2 -> level = "II";
                            case 3 -> level = "III";
                            case 4 -> level = "IV";
                            case 5 -> level = "V";
                            default -> level = "";
                        }
                        context.blit(RenderPipelines.GUI_TEXTURED, icon, k2 + 8 + index *10-finalLength, client.getWindow().getGuiScaledHeight()-91, 0,0,13,13,13,13);
                        drawScaledText(context,Minecraft.getInstance().font, Component.nullToEmpty(level), k2 + 8 + index * 10+9-finalLength, client.getWindow().getGuiScaledHeight()-83,0.9f,Mitayclient.getConfig().isDarkShown() ? Resource.WHITE : 0,false);
                        index += 2;
                    }


                }else k2 = client.getWindow().getGuiScaledWidth();









                if(rightHandItem.isDamageableItem())//右手
                {
                    if(k1 > client.getWindow().getGuiScaledWidth()-130)
                        k1-=2;

                    Component itemName = rightHandItem.getHoverName();
                    Component damage = Component.nullToEmpty(rightHandItem.getMaxDamage() - rightHandItem.getDamageValue() + "/" + rightHandItem.getMaxDamage());
                    int nameLength = 0;
                    if(client.font.width(itemName) > client.font.width(damage))
                    {
                        nameLength = client.font.width(itemName);
                    }
                    nameLength -= client.font.width("netherite ");

                    int enchantmentLength = 0;
                    if(rightEnchantments.size() > 4)
                    {
                        int length = rightEnchantments.size() - 4;
                        int times = length == 1 ? 14 : 18;
                        enchantmentLength += length * times;
//                System.out.println(length);
//                System.out.println(enchantmentLength);
                    }

                    int finalLength = Math.max(nameLength, enchantmentLength);

                    //工具框
                    context.blit(RenderPipelines.GUI_TEXTURED,tool_bar, k1-5-finalLength, client.getWindow().getGuiScaledHeight()-65  , 0,0,105+finalLength,37+rightBiaHeight,105+finalLength,37+rightBiaHeight);
                    //物品
                    drawScaledItem(context,rightHandItem, k1 + 2-finalLength, client.getWindow().getGuiScaledHeight()-59,1.5f);
                    //物品名
                    context.text(Minecraft.getInstance().font, rightHandItem.getHoverName().getString(), k1+34-finalLength, client.getWindow().getGuiScaledHeight()-57, playerRightToolNameColor, true);
                    //耐久
                    context.text(client.font, rightHandItem.getMaxDamage() - rightHandItem.getDamageValue() + "/" + rightHandItem.getMaxDamage() , k1+34-finalLength, client.getWindow().getGuiScaledHeight()-47, playerRightToolDamageColor, true);

                    int index = 0;
                    for (Map.Entry<String, Integer> entry : rightEnchantments.entrySet())
                    {
                        Identifier icon = Enchantment.get(entry.getKey());
                        String level = "";
                        switch (entry.getValue())
                        {
                            case 1 -> level = "I";
                            case 2 -> level = "II";
                            case 3 -> level = "III";
                            case 4 -> level = "IV";
                            case 5 -> level = "V";
                            default -> level = "";
                        }
                        context.blit(RenderPipelines.GUI_TEXTURED, icon, k1 + 8 + index *10-finalLength, client.getWindow().getGuiScaledHeight()-36, 0,0,13,13,13,13);
                        drawScaledText(context,Minecraft.getInstance().font, Component.nullToEmpty(level), k1 + 8 + index * 10+9-finalLength, client.getWindow().getGuiScaledHeight()-28,0.9f, Resource.WHITE,true);
                        index += 2;
                    }
                }else k1 = client.getWindow().getGuiScaledWidth();
            }
        }
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

    public static void drawScaledItem(GuiGraphicsExtractor context, ItemStack item, int x, int y, float scale)
    {
        Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        context.item(item,0,0);
        matrices.popMatrix();
    }
}
