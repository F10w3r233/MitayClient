package com.flower.mitayclient.GUI.HUD;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.Resource;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
    static int x1 = 0;
    static int x2 = 0;
    static int x3 = 0;
    static int x4 = 0;
    static int x5 = 0;

    static int originalSize;

    public static void render(GuiGraphicsExtractor context)
    {
        if(Mitayclient.getConfig().isEffectShown())
        {
            Minecraft client = Minecraft.getInstance();

            Collection<MobEffectInstance> collection = client.player.getActiveEffects();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            if (!collection.isEmpty() && (client.screen == null || !Minecraft.getInstance().screen.showsActiveEffects()) && !Minecraft.getInstance().options.hideGui)
            {
                int i = 0;
                int gap = 40;
                Iterator var6 = Ordering.natural().reverse().sortedCopy(collection).iterator();
//            StatusEffect statusEffectSpriteManager = client.getStatusEffectSpriteManager();
                while (var6.hasNext())
                {
                    MobEffectInstance statusEffectInstance = (MobEffectInstance)var6.next();
                    Holder<MobEffect> registryEntry = statusEffectInstance.getEffect();
                    if(collection.size() != originalSize)
                    {
                        x1 = -83;
                        x2 = -79;
                        x3 = -76;
                        x4 = -50;
                        x5 = -90;
                    }

                    if(x1 < 16)
                    {
                        x1++;
                    }
                    if(x2 < 21)
                    {
                        x2++;
                    }
                    if(x3 < 24)
                    {
                        x3++;
                    }
                    if(x4 < 50)
                    {
                        x4++;
                    }
                    if(x5 < 95)
                    {
                        x5++;
                    }


                    Holder<MobEffect> statusEffect = statusEffectInstance.getEffect();
                    i++;
                    MobEffect statusEffect1 = statusEffectInstance.getEffect().value();
                    int finalI = i;


                    float f = 1.0F;
                    if (statusEffectInstance.endsWithin(200))
                    {
                        int m = statusEffectInstance.getDuration();
                        int n = 10 - m / 20;
                        f = Mth.clamp((float)m / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)m * 3.1415927F / 5.0F) * Mth.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
                        f = Mth.clamp(f, 0.0F, 1.0F);
                    }
                    float finalF = f;
                    int alpha = ARGB.white(finalF);

                    //计时
                    int totalDuration = statusEffectInstance.getDuration()/20;
                    int min;
                    String minStr;
                    if(totalDuration <= 0)
                    {
                        min = 0;
                        minStr = "0" + min;
                    }else {
                        min = totalDuration / 60;
                        if(min < 10)
                        {
                            minStr = "0" + min;
                        }else minStr = String.valueOf(min);
                    }
                    int second;
                    String secondStr;
                    if(totalDuration < 60)
                    {
                        second = totalDuration;
                        if(second < 10)
                        {
                            secondStr = "0" + second;
                        }else secondStr = String.valueOf(second);
                    }else {
                        second = totalDuration % 60;
                        if(second < 10)
                        {
                            secondStr = "0" + second;
                        }else secondStr = String.valueOf(second);
                    }

                    //effectName
                    String effectLevel;
                    switch (statusEffectInstance.getAmplifier())
                    {
                        case 1 : effectLevel = " II";break;
                        case 2 : effectLevel = " III";break;
                        case 3 : effectLevel = " IV";break;
                        case 4 : effectLevel = " V";break;
                        case 5 : effectLevel = " VI";break;
                        default: effectLevel = "";
                    }
                    String time = minStr + ":" + secondStr;
//                String effectName = statusEffect1.getName().copy().setStyle(Style.EMPTY.withBold(true)).getString()+effectLevel;
                    Component effectName = Component.literal(statusEffect1.getDisplayName().getString() + effectLevel).setStyle(Style.EMPTY.withBold(true));
                    int length;
                    if(getStringWidth(time) > (getStringWidth(effectName)) + getStringWidth(effectLevel))
                    {
                        length = getStringWidth(time);
                    }else length = getStringWidth(effectName);
                    length += 10;

                    int finalLength = length;
                    list.add(()->
                    {
                        Identifier bar;
                        if(Mitayclient.getConfig().isDarkShown())
                        {
                            if(statusEffectInstance.isAmbient())
                            {
                                bar = Resource.EFFECT_BAR_AMBIENT_DARK;
                            }else bar = Resource.EFFECT_BAR_DARK;

                        }else
                        {
                            if(statusEffectInstance.isAmbient())
                            {
                                bar = Resource.EFFECT_BAR_AMBIENT;
                            }else bar = Resource.EFFECT_BAR;
                        }
                        int n = gap* finalI;
                        //-----BAR------
                        context.blit(RenderPipelines.GUI_TEXTURED,bar, x1, n-8, 0,0, 35+finalLength,34,35+finalLength,34, alpha);

                        if(!(statusEffect.value()).isBeneficial())
                        {
                            context.blit(RenderPipelines.GUI_TEXTURED,Resource.EFFECT_BAR_BAD, x2, n - 3, 0, 0, 24, 24, 24, 24, alpha);
                        }

                        Identifier sprite = getEffectTexture(registryEntry);
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x3, n, 18, 18, alpha);

                        //"§l" +
                        context.text(client.font,  Component.literal(statusEffect1.getDisplayName().getString() + effectLevel).setStyle(Style.EMPTY.withBold(true)), x4, n-1, ARGB.color(alpha, statusEffect1.getColor()));

                        int timeColor;
                        if(min == 0 && second <= 10)
                        {
                            timeColor = 12400439;
                        }else timeColor = 111111;
                        context.text(client.font, minStr + ":" + secondStr, x4, n+11, ARGB.color(alpha, timeColor));
                    });
                }

                list.forEach(Runnable::run);
            }
            originalSize = collection.size();
        }
    }

    public static Identifier getEffectTexture(Holder<MobEffect> effect)
    {
        return (Identifier)effect.unwrapKey().map(ResourceKey::identifier).map((id) -> {
            return id.withPrefix("mob_effect/");
        }).orElseGet(MissingTextureAtlasSprite::getLocation);
    }
}
