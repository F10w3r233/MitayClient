package com.flower.mitayclient.GUI.HUD;

import com.flower.Mitayclient;
//import com.flower.mitayclient.util.BlockStateChecker;
import com.flower.mitayclient.util.BlockStateChecker;
import com.flower.mitayclient.util.Data.PlayerDataHandler;
import com.flower.mitayclient.util.Resource;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ContainerItemHudRenderer
{
    private static Player getCameraPlayer()
    {
        return Minecraft.getInstance().getCameraEntity() instanceof Player playerEntity ? playerEntity : null;
    }
    static boolean isLocked = false;
    public static List<Map.Entry<Item, String>> getBlockList()
    {
        List<Map.Entry<Item, String>> blocks = new ArrayList<>();
        if(PlayerDataHandler.blocks != null && PlayerDataHandler.amount != null)
        {
            for(int i = 0;i <= PlayerDataHandler.blocks.length-1;i++)
            {
                blocks.add(new AbstractMap.SimpleEntry<>(BuiltInRegistries.ITEM.getValue(Identifier.parse("minecraft:" + PlayerDataHandler.blocks[i])).asItem(), PlayerDataHandler.amount[i]));
            }
        }
        return blocks;
    }


    static int length = 0;
    public static void render(GuiGraphicsExtractor context)
    {
//        if(Mitayclient.getConfig().isContainerShown() && !Minecraft.getInstance().options.hideGui)
//        {
//            int x = (Minecraft.getInstance().getWindow().getGuiScaledWidth())/2;
//            Identifier bar;
//            int biaX = 0;
//            int biaY = 0;
//            float alpha = 1f;
//            if(Mitayclient.getConfig().isDarkShown())
//            {
//                bar = Resource.EFFECT_BAR_DARK;
//                biaX = 4;
//                biaY = 0;
//            }else
//            {
//                biaX = 0;
//                biaY = 0;
//                bar = Resource.EFFECT_BAR;
//            }
//            if(PlayerDataHandler.containerType != null)
//            {
//                if(PlayerDataHandler.containerType.equals("none"))
//                {
//                    if(length > -64)
//                    {
//                        length -= 8;
//                    }
//
//                    if(alpha > 0)
//                        alpha -= 0.4f;
//                }
//
//                context.blit(RenderPipelines.GUI_TEXTURED, bar, x-biaX+90, 10+biaY, 0, 0,62+length, 34, 62+length, 34, ARGB.white(alpha));
//
//                if(PlayerDataHandler.containerType.equals("hopper")) //
//                {
//                    alpha = 1f;
//                    //判断是否上锁
//                    BlockHitResult blockHitResult = BlockStateChecker.rayTrace(getCameraPlayer().level(), getCameraPlayer(), getCameraPlayer().hasInfiniteMaterials() ? 5 : 4);
//                    BlockEntity blockEntity = getCameraPlayer().level().getBlockEntity(blockHitResult.getBlockPos());
//                    if(blockEntity instanceof HopperBlockEntity)
//                    {
//                        BlockState state = getCameraPlayer().level().getBlockState(blockHitResult.getBlockPos());
//                        isLocked = !state.getValue(HopperBlock.ENABLED);
//                    }
//
//                    if(PlayerDataHandler.isEmpty)
//                    {
//                        if(length > 0)
//                        {
//                            length -= 8;
//                        }else if(length < 0)
//                        {
//                            length += 8;
//                        }
//                    }else
//                    {
//                        if(length < 104)
//                        {
//                            length += 8;
//                        }
//                    }
//
//                    context.blit(RenderPipelines.GUI_TEXTURED, Resource.HOPPER, x+90+8,18,0,0,22,22,22,22, ARGB.white(alpha));
//                    Identifier lockTexture;
//                    if(isLocked)
//                    {
//                        lockTexture = Resource.LOCKED;
//                    }else {
//                        lockTexture = Resource.UNLOCKED;
//                    }
//                    context.blit(RenderPipelines.GUI_TEXTURED, lockTexture, x+90+12+20, 18, 0,0, 22,22,22,22,ARGB.white(alpha));
//
//
//                    int index = 0;
//                    for(Map.Entry<Item, String> entry : getBlockList())
//                    {
//                        index++;
//                        Item item = entry.getKey();
//                        String amount = entry.getValue();
//                        if(!item.getName(item.getDefaultInstance()).getString().equals("air"))
//                        {
//                            context.item(new ItemStack(item), x+90+12+20+10 + index * 18,18,ARGB.white(alpha));
//                            context.itemDecorations(Minecraft.getInstance().font, new ItemStack(item), x+90+12+20+10 + index * 18, 18, amount.equals("0") || amount.equals("1") ? "" : amount);
//                        }
//                    }
//                }else if(PlayerDataHandler.containerType.equals("chest"))
//                {
//                    context.blit(RenderPipelines.GUI_TEXTURED, Resource.chest, x+90+12,16,0,0,22,22,22,22, ARGB.white(1f));
//                    if(PlayerDataHandler.isEmpty)
//                    {
//                        if(length > 0)
//                        {
//                            length -= 8;
//                        }else if(length < 0)
//                        {
//                            length += 8;
//                        }
//                    }else
//                    {
//                        if(length < 64)
//                        {
//                            length += 8;
//                        }
//                        int totalAmount = 0;
//                        for(Map.Entry<Item, String> entry : getBlockList())
//                        {
//                            int amount = Integer.parseInt(entry.getValue());
//                            totalAmount += amount;
//                        }
////                        System.out.println(totalAmount);
//                        context.text(Minecraft.getInstance().font, totalAmount + " 个物品" ,x+90+12+20, 18, ARGB.color(255, Resource.WHITE));
//                    }
//                }
//            }
//        }
    }
}