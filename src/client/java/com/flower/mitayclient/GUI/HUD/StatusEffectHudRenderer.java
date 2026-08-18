package com.flower.mitayclient.GUI.HUD;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.Resource;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.*;
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

import static com.flower.mitayclient.util.Resource.getStringWidth;

public class StatusEffectHudRenderer
{
    // 动画偏移变量（与原来一致）
    static int x1 = 0;
    static int x2 = 0;
    static int x3 = 0;
    static int x4 = 0;
    static int x5 = 0;

    // 缓存字段
    private static Set<String> cachedEffectSignature = null;
    private static List<Runnable> cachedRunnables = null;

    public static void render(GuiGraphicsExtractor context)
    {
        if (!Mitayclient.getConfig().isEffectShown()) return;

        Minecraft client = Minecraft.getInstance();
        Collection<MobEffectInstance> collection = client.player.getActiveEffects();

        if (!collection.isEmpty() && (client.screen == null || !Minecraft.getInstance().screen.showsActiveEffects()) && !Minecraft.getInstance().options.hideGui)
        {
            // 生成当前效果签名
            Set<String> currentSignature = getEffectSignature(collection);
            boolean hasChanged = !currentSignature.equals(cachedEffectSignature);

            if (hasChanged)
            {
                cachedEffectSignature = currentSignature;
                cachedRunnables = buildRunnables(collection, context);
                // 重置动画偏移（效果变化时重新播放入场动画）
                x1 = -83;
                x2 = -79;
                x3 = -76;
                x4 = -50;
                x5 = -90;
            }

            // 每帧更新动画（即使列表未变化，也要推进动画）
            // 注意：动画变量需要在执行每个任务前更新，但这里我们在循环里统一处理
            // 因此下面执行任务时，每个任务中直接使用当前的 x1~x5
            // 但动画在每帧都应该推进，所以我们在执行前更新一次
            updateAnimation();

            // 执行缓存的渲染任务
            if (cachedRunnables != null)
            {
                for (Runnable task : cachedRunnables)
                {
                    task.run();
                }
            }
        }
        else
        {
            // 无效果时清空缓存
            cachedEffectSignature = null;
            cachedRunnables = null;
            // 动画偏移重置到初始（但可以保留，进入效果时重新开始）
        }
    }

    /**
     * 生成药水效果的签名（效果ID + 等级），忽略持续时间
     */
    private static Set<String> getEffectSignature(Collection<MobEffectInstance> effects)
    {
        Set<String> signature = new HashSet<>();
        for (MobEffectInstance effect : effects)
        {
            String name = effect.getEffect().getRegisteredName();
            String level = String.valueOf(effect.getAmplifier() + 1);
            signature.add(name + ":" + level);
        }
        return signature;
    }

    /**
     * 构建渲染任务列表（已排序）
     */
    private static List<Runnable> buildRunnables(Collection<MobEffectInstance> collection, GuiGraphicsExtractor context)
    {
        List<Runnable> runnables = new ArrayList<>();
        int gap = 40;
        int i = 0;

        // 排序（仅当效果变化时执行）
        List<MobEffectInstance> sortedEffects = Ordering.natural().reverse().sortedCopy(collection);

        for (MobEffectInstance statusEffectInstance : sortedEffects)
        {
            i++;
            Holder<MobEffect> registryEntry = statusEffectInstance.getEffect();
            MobEffect statusEffect1 = registryEntry.value();

            // 透明度计算（持续时间小于10秒时闪烁）
            float f = 1.0F;
            if (statusEffectInstance.endsWithin(200))
            {
                int m = statusEffectInstance.getDuration();
                int n = 10 - m / 20;
                f = Mth.clamp((float)m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)m * (float)Math.PI / 5.0F) * Mth.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
                f = Mth.clamp(f, 0.0F, 1.0F);
            }
            float finalF = f;
            int alpha = ARGB.white(finalF);

            // 时间字符串
            int totalDuration = statusEffectInstance.getDuration() / 20;
            int min;
            String minStr;
            if (totalDuration <= 0)
            {
                min = 0;
                minStr = "0" + min;
            }
            else
            {
                min = totalDuration / 60;
                if (min < 10)
                    minStr = "0" + min;
                else
                    minStr = String.valueOf(min);
            }
            int second;
            String secondStr;
            if (totalDuration < 60)
            {
                second = totalDuration;
                if (second < 10)
                    secondStr = "0" + second;
                else
                    secondStr = String.valueOf(second);
            }
            else
            {
                second = totalDuration % 60;
                if (second < 10)
                    secondStr = "0" + second;
                else
                    secondStr = String.valueOf(second);
            }

            // 效果等级
            String effectLevel;
            switch (statusEffectInstance.getAmplifier())
            {
                case 1: effectLevel = " II"; break;
                case 2: effectLevel = " III"; break;
                case 3: effectLevel = " IV"; break;
                case 4: effectLevel = " V"; break;
                case 5: effectLevel = " VI"; break;
                default: effectLevel = "";
            }
            String time = minStr + ":" + secondStr;
            Component effectName = Component.literal(statusEffect1.getDisplayName().getString() + effectLevel).setStyle(Style.EMPTY.withBold(true));

            int length;
            if (getStringWidth(time) > (getStringWidth(effectName)) + getStringWidth(effectLevel))
                length = getStringWidth(time);
            else
                length = getStringWidth(effectName);
            length += 10;

            int finalLength = length;
            int finalI = i;

            // 构建渲染任务
            runnables.add(() ->
            {
                Identifier bar;
                if (Mitayclient.getConfig().isDarkShown())
                {
                    if (statusEffectInstance.isAmbient())
                        bar = Resource.EFFECT_BAR_AMBIENT_DARK;
                    else
                        bar = Resource.EFFECT_BAR_DARK;
                }
                else
                {
                    if (statusEffectInstance.isAmbient())
                        bar = Resource.EFFECT_BAR_AMBIENT;
                    else
                        bar = Resource.EFFECT_BAR;
                }
                int n = gap * finalI;

                // 背景条
                context.blit(RenderPipelines.GUI_TEXTURED, bar, x1, n - 8, 0, 0, 35 + finalLength, 34, 35 + finalLength, 34, alpha);

                // 负面效果背景标记
                if (!(statusEffect1.isBeneficial()))
                {
                    context.blit(RenderPipelines.GUI_TEXTURED, Resource.EFFECT_BAR_BAD, x2, n - 3, 0, 0, 24, 24, 24, 24, alpha);
                }

                // 效果图标
                Identifier sprite = getEffectTexture(registryEntry);
                context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x3, n, 18, 18, alpha);

                // 效果名称
                context.text(Minecraft.getInstance().font, Component.literal(statusEffect1.getDisplayName().getString() + effectLevel).setStyle(Style.EMPTY.withBold(true)), x4, n - 1, ARGB.color(alpha, statusEffect1.getColor()));

                // 时间（剩余10秒内红色闪烁）
                int timeColor;
                if (min == 0 && second <= 10)
                {
                    timeColor = 12400439; // 红色
                }
                else
                {
                    timeColor = 111111;
                }
                context.text(Minecraft.getInstance().font, minStr + ":" + secondStr, x4, n + 11, ARGB.color(alpha, timeColor));
            });
        }

        return runnables;
    }

    /**
     * 每帧更新动画偏移量（与原来逻辑一致）
     */
    private static void updateAnimation()
    {
        if (x1 < 16) x1++;
        if (x2 < 21) x2++;
        if (x3 < 24) x3++;
        if (x4 < 50) x4++;
        if (x5 < 95) x5++;
    }

    public static Identifier getEffectTexture(Holder<MobEffect> effect)
    {
        return (Identifier) effect.unwrapKey().map(ResourceKey::identifier).map((id) -> {
            return id.withPrefix("mob_effect/");
        }).orElseGet(MissingTextureAtlasSprite::getLocation);
    }
}