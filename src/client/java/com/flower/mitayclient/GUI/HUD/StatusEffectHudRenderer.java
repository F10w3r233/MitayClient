package com.flower.mitayclient.GUI.HUD;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.Resource;
import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.*;
import java.util.stream.Collectors;

public class StatusEffectHudRenderer {

    // 目标常量（不再动画，动画值由每个条目管理）
    private static final int BAR_X = 16;
    private static final int BADGE_X = 21;
    private static final int ICON_X = 24;
    private static final int TEXT_X = 50;
    private static final int GAP = 40;

    // 渲染状态列表
    private static final List<EffectEntry> entries = new ArrayList<>();

    // 缓存排序后的活跃效果
    private static List<MobEffectInstance> sortedActiveEffects = new ArrayList<>();
    private static Set<Holder<MobEffect>> lastActiveHolders = null;

    public static void render(GuiGraphicsExtractor context) {
        if (!Mitayclient.getConfig().isEffectShown()) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        Collection<MobEffectInstance> activeEffects = client.player.getActiveEffects();
        if (activeEffects.isEmpty()
                || (client.screen != null && !client.screen.showsActiveEffects())
                || client.options.hideGui) {
            // 无效果时清空状态
            entries.clear();
            lastActiveHolders = null;
            return;
        }

        // 检测效果变化，更新排序列表
        Set<Holder<MobEffect>> currentHolders = activeEffects.stream()
                .map(MobEffectInstance::getEffect)
                .collect(Collectors.toSet());
        if (lastActiveHolders == null || !currentHolders.equals(lastActiveHolders)) {
            lastActiveHolders = currentHolders;
            sortedActiveEffects = Ordering.natural().reverse().sortedCopy(activeEffects);
            syncEntriesWithActiveEffects();
        }

        // 每帧更新所有条目动画
        for (Iterator<EffectEntry> it = entries.iterator(); it.hasNext(); ) {
            EffectEntry entry = it.next();
            entry.tick();
            if (entry.isDead()) {
                it.remove();
            }
        }

        // 绘制
        for (int i = 0; i < entries.size(); i++) {
            EffectEntry entry = entries.get(i);
            // 当前条目在排序中的索引（对于存活、非移除状态的条目重新计算，移除中的使用原位置）
            int renderIndex = entry.isRemoving ? entry.removingIndex : i;
            int yTarget = GAP * (renderIndex + 1); // i 从 0 开始，但原代码 i 从 1 开始
            entry.updateYTarget(yTarget);

            renderEffect(client, context, entry);
        }
    }

    // 同步活跃效果与条目列表：标记移除、添加新效果
    private static void syncEntriesWithActiveEffects() {
        Set<Holder<MobEffect>> activeHolders = sortedActiveEffects.stream()
                .map(MobEffectInstance::getEffect)
                .collect(Collectors.toSet());

        // 标记不再活跃的条目为移除
        for (EffectEntry entry : entries) {
            if (!activeHolders.contains(entry.holder) && !entry.isRemoving) {
                entry.markForRemoval();
            }
        }

        // 添加新效果（在活跃中但不在条目列表中，且不是正在移除的）
        Set<Holder<MobEffect>> existingHolders = entries.stream()
                .filter(e -> !e.isRemoving)
                .map(e -> e.holder)
                .collect(Collectors.toSet());

        for (MobEffectInstance instance : sortedActiveEffects) {
            Holder<MobEffect> holder = instance.getEffect();
            if (!existingHolders.contains(holder)) {
                // 新效果，初始从右侧滑入
                entries.add(new EffectEntry(instance));
            }
        }
    }

    // 绘制单个效果条目
    private static void renderEffect(Minecraft client, GuiGraphicsExtractor context, EffectEntry entry) {
        MobEffectInstance instance = entry.instance;
        Holder<MobEffect> holder = instance.getEffect();
        MobEffect effect = holder.value();
        int offsetX = Math.round(entry.currentX);

        // 闪烁透明度
        float f = 1.0F;
        if (instance.endsWithin(200)) {
            int m = instance.getDuration();
            int n = 10 - m / 20;
            f = Mth.clamp((float) m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                    + Mth.cos((float) m * 3.1415927F / 5.0F)
                    * Mth.clamp((float) n / 10.0F * 0.25F, 0.0F, 0.25F);
            f = Mth.clamp(f, 0.0F, 1.0F);
        }
        int alpha = ARGB.white(f);

        // 时间格式化
        int totalDuration = Math.max(0, instance.getDuration() / 20);
        int min = totalDuration / 60;
        int second = totalDuration % 60;
        String time = String.format("%02d:%02d", min, second);

        // 等级文本
        String effectLevel = switch (instance.getAmplifier()) {
            case 1 -> " II";
            case 2 -> " III";
            case 3 -> " IV";
            case 4 -> " V";
            case 5 -> " VI";
            default -> "";
        };

        // 复用组件
        Component effectName = entry.cachedName;
        if (effectName == null) {
            effectName = Component.literal(effect.getDisplayName().getString() + effectLevel)
                    .setStyle(Style.EMPTY.withBold(true));
            entry.cachedName = effectName;
        }

        // 条长度
        int nameWidth = client.font.width(effectName);
        int timeWidth = client.font.width(time);
        int length = Math.max(nameWidth, timeWidth) + 10;

        int y = Math.round(entry.currentY);

        // 背景栏
        Identifier bar;
        if (Mitayclient.getConfig().isDarkShown()) {
            bar = instance.isAmbient() ? Resource.EFFECT_BAR_AMBIENT_DARK : Resource.EFFECT_BAR_DARK;
        } else {
            bar = instance.isAmbient() ? Resource.EFFECT_BAR_AMBIENT : Resource.EFFECT_BAR;
        }
        context.blit(RenderPipelines.GUI_TEXTURED, bar,
                BAR_X + offsetX, y - 8, 0, 0,
                35 + length, 34, 35 + length, 34, alpha);

        // 负面标记
        if (!effect.isBeneficial()) {
            context.blit(RenderPipelines.GUI_TEXTURED, Resource.EFFECT_BAR_BAD,
                    BADGE_X + offsetX, y - 3, 0, 0,
                    24, 24, 24, 24, alpha);
        }

        // 图标
        Identifier sprite = getEffectTexture(holder);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite,
                ICON_X + offsetX, y, 18, 18, alpha);

        // 效果名称
        context.text(client.font, effectName,
                TEXT_X + offsetX, y - 1,
                ARGB.color(alpha, effect.getColor()));

        // 倒计时
        int timeColor = (min == 0 && second <= 10) ? 12400439 : 111111;
        context.text(client.font, time,
                TEXT_X + offsetX, y + 11,
                ARGB.color(alpha, timeColor));
    }

    private static Identifier getEffectTexture(Holder<MobEffect> effect) {
        return effect.unwrapKey()
                .map(ResourceKey::identifier)
                .map(id -> id.withPrefix("mob_effect/"))
                .orElseGet(MissingTextureAtlasSprite::getLocation);
    }

    // 内部条目类
    private static class EffectEntry {
        final MobEffectInstance instance;
        final Holder<MobEffect> holder;
        Component cachedName;

        float currentX, targetX;
        float currentY, targetY;

        boolean isRemoving;
        int removingIndex; // 移除时冻结的索引，用于保持位置
        int removeTimer;   // 移除动画计时器（帧）

        EffectEntry(MobEffectInstance instance) {
            this.instance = instance;
            this.holder = instance.getEffect();
            // 新效果从右侧进入
            this.currentX = 200;
            this.targetX = 0;
            this.currentY = 0;
            this.targetY = 0;
            this.isRemoving = false;
        }

        void markForRemoval() {
            this.isRemoving = true;
            this.targetX = -200; // 向左滑出
            this.removeTimer = 20; // 20 ticks 内完成滑出
            // 冻结当前的 Y 位置，防止跟随排序移动
            this.targetY = this.currentY;
            this.removingIndex = -1; // 将由外部设置
        }

        void updateYTarget(int newTargetY) {
            if (!isRemoving) {
                this.targetY = newTargetY;
            }
        }

        void tick() {
            // 水平动画
            float xDiff = targetX - currentX;
            if (Math.abs(xDiff) < 0.5f) {
                currentX = targetX;
            } else {
                currentX += xDiff * 0.3f; // 缓动系数
            }

            // 垂直动画
            float yDiff = targetY - currentY;
            if (Math.abs(yDiff) < 0.5f) {
                currentY = targetY;
            } else {
                currentY += yDiff * 0.3f;
            }

            // 移除计时
            if (isRemoving) {
                removeTimer--;
                if (removeTimer <= 0 && currentX <= -199) {
                    // 已充分滑出，标记为死亡
                }
            }
        }

        boolean isDead() {
            return isRemoving && removeTimer <= 0 && Math.abs(currentX - targetX) < 1;
        }
    }
}