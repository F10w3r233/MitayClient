package com.flower.mitayclient.mixin;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.HUD.HeartType;
import com.flower.mitayclient.util.*;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.flower.mitayclient.util.Resource.HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE;
import static com.flower.mitayclient.util.Resource.HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE;


@Mixin(Gui.class)
public abstract class InGameHudMixin
{
    @Unique
    @Nullable
    private Player getCameraPlayer()
    {
        return this.client.getCameraEntity() instanceof Player playerEntity ? playerEntity : null;
    }

    @Shadow public abstract Font getFont();
    @Shadow private int tickCount;


    private final RandomSource random = RandomSource.create();

    @Inject(at = @At("HEAD"), method = "extractFood", cancellable = true)
    private void renderFood(final GuiGraphicsExtractor context, final Player player, final int yLineBase, final int xRight, CallbackInfo ci)
    {
        if(Mitayclient.getConfig().isHotbarShown())
        {
            ci.cancel();
            FoodData hungerManager = player.getFoodData();
            int i = hungerManager.getFoodLevel();

            for (int j = 0; j < 10; j++) {
                int k = yLineBase;
                Identifier identifier;
                Identifier identifier2;
                Identifier identifier3;
                if (player.hasEffect(MobEffects.HUNGER)) {
                    identifier = Resource.FOOD_EMPTY_HUNGER_TEXTURE;
                    identifier2 = Resource.FOOD_HALF_HUNGER_TEXTURE;
                    identifier3 = Resource.FOOD_FULL_HUNGER_TEXTURE;
                } else {
                    identifier = Resource.FOOD_EMPTY_TEXTURE;
                    identifier2 = Resource.FOOD_HALF_TEXTURE;
                    identifier3 = Resource.FOOD_FULL_TEXTURE;
                }

                if (player.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (i * 3 + 1) == 0) {
                    k = yLineBase + (this.random.nextInt(3) - 1);
                }

                int l = xRight - j * 8 - 9;
                context.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, l, k - 3 , 9, 9);
                if (j * 2 + 1 < i ) {
                    context.blitSprite(RenderPipelines.GUI_TEXTURED, identifier3, l, k - 3, 9, 9);
                }

                if (j * 2 + 1 == i) {
                    context.blitSprite(RenderPipelines.GUI_TEXTURED, identifier2, l, k - 3, 9, 9);
                }
            }





        }
    }

    @Inject(at = @At("HEAD"), method = "extractArmor", cancellable = true)
    private static void renderArmor(final GuiGraphicsExtractor context, final Player player, final int yLineBase, final int numHealthRows, final int healthRowHeight, final int xLeft, CallbackInfo ci)
    {
        if(Mitayclient.getConfig().isHotbarShown())
        {
            ci.cancel();
            int l = player.getArmorValue();
            if (l > 0) {
                int m = yLineBase - (numHealthRows - 1) * healthRowHeight - 10;

                for (int n = 0; n < 10; n++) {
                    int o = xLeft + n * 8;
                    if (n * 2 + 1 < l) {
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, Resource.ARMOR_FULL_TEXTURE, o, m - 3, 9, 9);
                    }

                    if (n * 2 + 1 == l) {
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, Resource.ARMOR_HALF_TEXTURE, o, m - 3, 9, 9);
                    }

                    if (n * 2 + 1 > l) {
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, Resource.ARMOR_EMPTY_TEXTURE, o, m - 3, 9, 9);
                    }
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "extractHearts", cancellable = true)
    private void renderHealthBar(
            final GuiGraphicsExtractor context,
            final Player player,
            final int x,
            final int y,
            final int lines,
            final int regeneratingHeartIndex,
            final float maxHealth,
            final int lastHealth,
            final int health,
            final int absorption,
            final boolean blinking,
            CallbackInfo ci
    ) {
        if(Mitayclient.getConfig().isHotbarShown())
        {
            ci.cancel();
            HeartType heartType = HeartType.fromPlayerState(player);
            boolean bl = player.level().getLevelData().isHardcore();
            int i = Mth.ceil((double)maxHealth / 2.0);
            int j = Mth.ceil((double)absorption / 2.0);
            int k = i * 2;

            for (int l = i + j - 1; l >= 0; l--) {
                int m = l / 10;
                int n = l % 10;
                int o = x + n * 8;
                int p = y - m * lines;
                if (lastHealth + absorption <= 4) {
                    p += this.random.nextInt(2);
                }

                if (l < i && l == regeneratingHeartIndex) {
                    p -= 2;
                }

                this.drawHeart(context, HeartType.CONTAINER, o, p - 3, bl, blinking, false);
                int q = l * 2;
                boolean bl2 = l >= i;
                if (bl2) {
                    int r = q - k;
                    if (r < absorption) {
                        boolean bl3 = r + 1 == absorption;
                        drawHeart(context, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, o, p - 3, bl, false, bl3);
                    }
                }

                if (blinking && q < health) {
                    boolean bl4 = q + 1 == health;
                    drawHeart(context, heartType, o, p - 3, bl, true, bl4);
                }

                if (q < lastHealth) {
                    boolean bl4 = q + 1 == lastHealth;
                    drawHeart(context, heartType, o, p - 3, bl, false, bl4);
                }
            }
        }
    }

    private void drawHeart(GuiGraphicsExtractor context, HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half) {
        context.blitSprite(RenderPipelines.GUI_TEXTURED, type.getTexture(hardcore, half, blinking), x, y, 9, 9);
    }


    private void renderHotbarItem(GuiGraphicsExtractor context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int seed)
    {
        if(Mitayclient.getConfig().isHotbarShown())
        {
            if (!stack.isEmpty()) {
                float f = (float)stack.getPopTime() - tickCounter.getGameTimeDeltaPartialTick(false);
                if (f > 0.0F) {
                    float g = 1.0F + f / 5.0F;
                    context.pose().pushMatrix();
                    context.pose().translate((float)(x + 8), (float)(y + 12));
                    context.pose().scale(1.0F / g, (g + 1.0F) / 2.0F);
                    context.pose().translate((float)(-(x + 8)), (float)(-(y + 12)));
                }

                context.item(player, stack, x, y - 2, seed);
                if (f > 0.0F)
                {
                    context.pose().popMatrix();
                }

                context.itemDecorations(this.client.font, stack, x, y - 2);
            }
        }
    }

    Minecraft client = Minecraft.getInstance();




//    DrawContext context2 = new DrawContext(MinecraftClient.getInstance(), MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());


    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void renderEffect(final GuiGraphicsExtractor graphics, final DeltaTracker deltaTracker ,CallbackInfo ci)
    {
        if (Mitayclient.getConfig().isEffectShown())
            ci.cancel();
    }



    int y = 0;
    int l = 0;



    @Inject(method = "extractItemHotbar", at = @At("HEAD"), cancellable = true)
    public void renderHotbar(final GuiGraphicsExtractor context, final DeltaTracker deltaTracker, CallbackInfo ci)
    {
        if(Mitayclient.getConfig().isHotbarShown())
        {
            ci.cancel();

            Player playerEntity = this.getCameraPlayer();

            if (playerEntity != null)
            {
                ItemStack itemStack = playerEntity.getOffhandItem();
                HumanoidArm arm = playerEntity.getMainArm().getOpposite();
                int i = client.getWindow().getGuiScaledWidth() / 2;
                context.pose().pushMatrix();
                context.pose().translate(0.0F, 0.0F);
                context.blit(RenderPipelines.GUI_TEXTURED,Resource.HOTBAR_TEXTURE, i - 92, client.getWindow().getGuiScaledHeight() - y - 2 + 1, 0,0,184, 23,184,23, ARGB.white(0.8F));
                if(!(playerEntity.getInventory().getSelectedSlot() == 0 || playerEntity.getInventory().getSelectedSlot() == 8))
                {
                    context.blit(RenderPipelines.GUI_TEXTURED,Resource.HOTBAR_SELECTION_TEXTURE, i - 92 + playerEntity.getInventory().getSelectedSlot() * 20 + 2, client.getWindow().getGuiScaledHeight() - y - 2 + 1 , 0, 0, 21, 23, 21, 23 , ARGB.white(0.77F));
                }else if(playerEntity.getInventory().getSelectedSlot() == 8)
                {
                    context.blit(RenderPipelines.GUI_TEXTURED,Resource.HOTBAR_SELECTION_TEXTURE, i - 92 + playerEntity.getInventory().getSelectedSlot() * 20 + 2, client.getWindow().getGuiScaledHeight() - y - 2 + 1, 0, 0, 22, 23, 22, 23 , ARGB.white(0.77F));
                } else context.blit(RenderPipelines.GUI_TEXTURED,Resource.HOTBAR_SELECTION_TEXTURE, i - 92 + playerEntity.getInventory().getSelectedSlot() * 20, client.getWindow().getGuiScaledHeight() - y - 2 + 1, 0, 0, 23, 23, 23, 23 , ARGB.white(0.77F));


                //动画
                if(y<23)
                {
                    y += 4;
                }
                if(l<300)
                {
                    l ++;
                }

                if (!itemStack.isEmpty()) {
                    if (arm == HumanoidArm.LEFT)
                    {
                        Player player = null;
                        if(Minecraft.getInstance().getCameraEntity() instanceof Player)
                        {
                            player = (Player) Minecraft.getInstance().getCameraEntity();
                        }
                        if(player.getOffhandItem().is(Items.TOTEM_OF_UNDYING))
                        {
                            context.blit(RenderPipelines.GUI_TEXTURED,Resource.OFFHAND_GOLDEN, i - 91 - 29 - 1, client.getWindow().getGuiScaledHeight() - 23 - 3 + 1,0,0, 23, 23,23,23);
                        }else {
                            context.blit(RenderPipelines.GUI_TEXTURED,Resource.HOTBAR_OFFHAND_LEFT_TEXTURE, i - 91 - 29 - 1, client.getWindow().getGuiScaledHeight() - 23 - 3 + 1,0,0, 23, 23,23,23);
                        }

                    } else {
                        context.blit(RenderPipelines.GUI_TEXTURED,Resource.HOTBAR_OFFHAND_RIGHT_TEXTURE, i + 91, client.getWindow().getGuiScaledHeight() - 23,0,0, 23, 24,23,24);
                    }
                }

                context.pose().popMatrix();
                int l = 1;

                int m;
                int n;
                int o;
                for(m = 0; m < 9; ++m) {
                    n = i - 90 + m * 20 + 2;
                    o = context.guiHeight() - 16 - 3;
                    this.renderHotbarItem(context, n, o, deltaTracker, playerEntity, playerEntity.getInventory().getItem(m), l++);
                }

                if (!itemStack.isEmpty()) {
                    m = context.guiHeight() - 16 - 3;
                    if (arm == HumanoidArm.LEFT) {
                        this.renderHotbarItem(context, i - 91 - 26, m, deltaTracker, playerEntity, itemStack, l++);
                    } else {
                        this.renderHotbarItem(context, i + 91 + 10, m, deltaTracker, playerEntity, itemStack, l++);
                    }
                }

                if (this.client.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                    float f = this.client.player.getAttackStrengthScale(0.0F);
                    if (f < 1.0F) {
                        n = context.guiHeight() - 20;
                        o = i + 91 + 6;
                        if (arm == HumanoidArm.RIGHT) {
                            o = i - 91 - 22;
                        }

                        int p = (int)(f * 19.0F);
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, o, n, 18, 18);
                        context.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 18, 18, 0, 18 - p, o, n + 18 - p, 18, p);
                    }
                }
            }
        }
    }
}
