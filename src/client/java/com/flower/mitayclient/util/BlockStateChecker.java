package com.flower.mitayclient.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockStateChecker
{
    public static BlockHitResult rayTrace(Level world, Player player, double maxDistance)
    {
        // 1. 计算射线的起点和方向
        Vec3 start = player.getEyePosition(); // 射线起点：眼睛位置
        // 计算射线终点：从起点加上视线方向向量乘以最大距离
        Vec3 rotation = player.getViewVector(1.0F); // 获取视线方向向量
        Vec3 end = start.add(rotation.x * maxDistance, rotation.y * maxDistance, rotation.z * maxDistance);

        // 2. 创建 RaycastContext
        // 参数分别是：起点、终点、碰撞形状类型、流体处理方式、检查实体（这里不检查，所以用null）
        ClipContext context = new ClipContext(
                start,
                end,
                ClipContext.Block.OUTLINE, // 使用方块的轮廓进行碰撞检测
                ClipContext.Fluid.NONE, // 不处理流体，如需检测流体可改为 APPROXIMATE 或 EXACT
                player // 排除玩家自身作为碰撞实体
        );

        // 3. 执行射线检测
        BlockHitResult hitResult = world.clip(context);
        return hitResult;
    }
    public static void displayHopperInfo(Player player, List<ItemStack> items, HopperBlockEntity hopper)
    {
        // 按物品类型分组计数
        Map<Item, Integer> itemCounts = new HashMap<>();
        Map<Item, ItemStack> itemSamples = new HashMap<>();

        for (ItemStack stack : items) {
            Item item = stack.getItem();
            itemCounts.put(item, itemCounts.getOrDefault(item, 0) + stack.getCount());
            itemSamples.putIfAbsent(item, stack);
        }

        // 显示每种物品
        for (Map.Entry<Item, Integer> entry : itemCounts.entrySet()) {
            Item item = entry.getKey();
            int count = entry.getValue();
            ItemStack sample = itemSamples.get(item);

            Component itemText = Component.literal("• ")
                    .append(sample.getHoverName().copy().withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" x" + count).withStyle(ChatFormatting.GREEN));

//            player.displayClientMessage(itemText, true);
        }

        // 显示总物品数
        int totalItems = items.stream().mapToInt(ItemStack::getCount).sum();
//        player.displayClientMessage(Component.literal("总物品数: " + totalItems).withStyle(ChatFormatting.YELLOW), true);
    }
}
