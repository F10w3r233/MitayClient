package com.flower.mitayclient.util;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RankTitle;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Player;

import static com.flower.mitayclient.util.MitayUtils.getFontColor;

public class Resource
{

    @Unique
    @Nullable
    public static Player getCameraPlayer()
    {
        return Minecraft.getInstance().getCameraEntity() instanceof Player playerEntity ? playerEntity : null;
    }

    public static String textToJson(Component text)
    {
        Gson gson = new Gson();
        JsonElement jsonElement = ComponentSerialization.CODEC
                .encodeStart(JsonOps.INSTANCE, text)
                .getOrThrow(); // 处理编码错误
        return gson.toJson(jsonElement);
    }

    public static Component jsonToText(String json)
    {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
        return ComponentSerialization.CODEC
                .decode(JsonOps.INSTANCE, jsonElement)
                .getOrThrow()
                .getFirst();
    }

    public static int getStringWidth(Component text)
    {
        return Minecraft.getInstance().font.width(text);
    }
    public static int getStringWidth(String text)
    {
        return getStringWidth(Component.literal(text));
    }

    public static Component OVERWORLD = Component.translatable("world.mitayclient.overworld");
    public static Component NETHER = Component.translatable("world.mitayclient.nether");
    public static Component END = Component.translatable("world.mitayclient.end");
    public static Component CREATIVE = Component.translatable("world.mitayclient.creative");


    public static Component PLACE = Component.translatable("screen.mitayclient.place");
    public static Component PLAYER_LIST = Component.translatable("screen.mitayclient.player");
    public static Component CHAT_HISTORY = Component.translatable("screen.mitayclient.history");
    public static Component PROFILE = Component.translatable("screen.mitayclient.profile");

    //-------------------------------------------------icon----------------------------------------------------
    public static final Identifier END_icon = ModIdentifier.get("textures/gui/hud/places/ender_eye.png");
    public static final Identifier NETHER_icon = ModIdentifier.get("textures/gui/hud/places/blaze_powder.png");
    public static final Identifier OVERWORLD_icon = ModIdentifier.get("textures/gui/hud/places/grass.png");
    public static final Identifier CREATIVE_WORLD_icon = ModIdentifier.get("textures/gui/hud/places/redstone.png");

    public static final Identifier SELECTED_icon = ModIdentifier.get("textures/gui/screen/selected.png");



    //地点 Text
    public static Component EXCHANGE_text = Component.translatable("place.mitayclient.exchange");
    public static Component TOWN_text = Component.translatable("place.mitayclient.town");
    public static Component MOB_TOWER_text = Component.translatable("place.mitayclient.mob_tower");
    public static Component RESOURCE_text = Component.translatable("place.mitayclient.resource");
    public static Component AFK_text = Component.translatable("place.mitayclient.afk");
    public static Component BACK_text = Component.translatable("place.mitayclient.back");
    public static Component IRON_text = Component.translatable("place.mitayclient.iron");
    public static Component SUGAR_CANE_text = Component.translatable("place.mitayclient.sugar_cane");
    public static Component FURNACE_text = Component.translatable("place.mitayclient.furnace");
    public static Component STONE_text = Component.translatable("place.mitayclient.stone");
    public static Component GUARDIAN_text = Component.translatable("place.mitayclient.guardian");
    public static Component PIG_MAN_text = Component.translatable("place.mitayclient.pigman");
    public static Component WITHER_SKULL_text = Component.translatable("place.mitayclient.wither_skull");
    public static Component GHAST_FARM_text = Component.translatable("place.mitayclient.ghast_farm");
    public static Component PORTAL_text = Component.translatable("place.mitayclient.portal");
    public static Component MAINLAND_text = Component.translatable("place.mitayclient.mainland");
    public static Component ENDER_MAN_text = Component.translatable("place.mitayclient.enderman");


    public static Component ZOO_text = Component.translatable("place.mitayclient.zoo");

    public static Component SINGLE = Component.translatable("menu.mitayclient.single");
    public static Component CONNECT = Component.translatable("menu.mitayclient.connect");
    public static Component PINGING = Component.translatable("menu.mitayclient.pinging");
    public static Component ONLINE = Component.translatable("menu.mitayclient.online");
    public static Component CLOSED = Component.translatable("menu.mitayclient.closed");
    public static Component SETTINGS = Component.translatable("menu.mitayclient.settings");
    public static Component QUIT = Component.translatable("menu.mitayclient.quit");
    public static Component STATISTIC = Component.translatable("menu.mitayclient.statistic");
    public static Component OPTIONS = Component.translatable("menu.mitayclient.options");
    public static Component OPEN_TO_LAN = Component.translatable("menu.mitayclient.open_to_lan");
    public static Component SAVING = Component.translatable("menu.mitayclient.saving");


    public static final Identifier MITAY = ModIdentifier.get("textures/gui/mitay.png");
    public static final Identifier Music = ModIdentifier.get("textures/gui/hud/music.png");

    //目标玩家
    public static final Identifier STATUS_BAR =  ModIdentifier.get("textures/gui/hud/status_bar/status_bar.png");
    public static final Identifier STATUS_BAR_DARK =  ModIdentifier.get("textures/gui/hud/status_bar/status_bar_dark.png");

    //状态栏
    public static final Identifier INFO_BAR = ModIdentifier.get("textures/gui/hud/top_info_bar/info_bar.png");
    public static final Identifier INFO_BAR_DARK = ModIdentifier.get("textures/gui/hud/top_info_bar/info_bar_dark.png");

    //药水效果
        public static final Identifier EFFECT_BAR = ModIdentifier.get("textures/gui/hud/effect_bar/effect_bar.png");
    public static final Identifier EFFECT_BAR_DARK = ModIdentifier.get( "textures/gui/hud/effect_bar/effect_bar_dark.png");
    public static final Identifier EFFECT_BAR_AMBIENT = ModIdentifier.get("textures/gui/hud/effect_bar/effect_bar_ambient.png");
    public static final Identifier EFFECT_BAR_AMBIENT_DARK = ModIdentifier.get( "textures/gui/hud/effect_bar/effect_bar_ambient_dark.png");
    public static final Identifier EFFECT_BAR_BAD = ModIdentifier.get("textures/gui/hud/effect_bar/effect_bar_bad.png");

    //工具
    public static final Identifier TOOL_BAR = ModIdentifier.get("textures/gui/hud/tool_bar/tool_bar.png");
    public static final Identifier TOOL_BAR_DARK = ModIdentifier.get("textures/gui/hud/tool_bar/tool_bar_dark.png");
    public static final Identifier TOOL_BAR_MENDING = ModIdentifier.get("textures/gui/hud/tool_bar/tool_bar_mending.png");
    public static final Identifier TOOL_BAR_MENDING_DARK = ModIdentifier.get("textures/gui/hud/tool_bar/tool_bar_mending_dark.png");

    public static final Identifier HOTBAR_TEXTURE = ModIdentifier.get("textures/gui/hud/hotbar/hotbar.png");
    public static final Identifier HOTBAR_SELECTION_TEXTURE = ModIdentifier.get("textures/gui/hud/hotbar/hotbar_selection.png");

    public static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/armor_empty");
    public static final Identifier ARMOR_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/armor_half");
    public static final Identifier ARMOR_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/armor_full");
    public static final Identifier FOOD_EMPTY_HUNGER_TEXTURE = Identifier.withDefaultNamespace("hud/food_empty_hunger");
    public static final Identifier FOOD_HALF_HUNGER_TEXTURE = Identifier.withDefaultNamespace("hud/food_half_hunger");
    public static final Identifier FOOD_FULL_HUNGER_TEXTURE = Identifier.withDefaultNamespace("hud/food_full_hunger");
    public static final Identifier FOOD_EMPTY_TEXTURE = Identifier.withDefaultNamespace("hud/food_empty");
    public static final Identifier FOOD_HALF_TEXTURE = Identifier.withDefaultNamespace("hud/food_half");
    public static final Identifier FOOD_FULL_TEXTURE = Identifier.withDefaultNamespace("hud/food_full");
    public static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("hud/experience_bar_background");
    public static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = Identifier.withDefaultNamespace("hud/experience_bar_progress");



    public static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = ModIdentifier.get("textures/gui/hud/hotbar/offhand.png");
    public static final Identifier HOTBAR_OFFHAND_RIGHT_TEXTURE = ModIdentifier.get("textures/gui/hud/hotbar/offhand.png");
    public static final Identifier OFFHAND_GOLDEN = ModIdentifier.get("textures/gui/hud/hotbar/offhand_golden.png");


    public static final Identifier HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.parse("hud/hotbar_attack_indicator_background");
    public static final Identifier HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.parse("hud/hotbar_attack_indicator_progress");


    public static final Identifier LOCKED = ModIdentifier.get("textures/gui/hud/container/locked.png");
    public static final Identifier UNLOCKED = ModIdentifier.get("textures/gui/hud/container/unlocked.png");
    public static final Identifier HOPPER = ModIdentifier.get("textures/gui/hud/container/hopper.png");
    public static Identifier chest = null;

    public static int WHITE = -1;
    public static int BLACK = 0xFF000000;
    public static int GREY = 0xFF808080;

    static
    {

        LocalDate today = LocalDate.now();

        // 获取当前月份和日期
        Month currentMonth = today.getMonth();
        int currentDay = today.getDayOfMonth();
        boolean isChristmas = false;

        if(currentMonth == Month.DECEMBER)
        {
            isChristmas = currentDay > 20 && currentDay < 31;
        }

        if(!isChristmas)
        {
            chest = ModIdentifier.get("textures/gui/hud/places/chest.png");
        }else chest = ModIdentifier.get("textures/gui/hud/places/christmas_chest.png");
    }


    public static boolean containsOneOfBoth(String string, String str1, String str2)
    {
        return string.contains(str1) || string.contains(str2);
    }

    public static boolean containsBoth(String string, String str1, String str2)
    {
        return string.contains(str1) && string.contains(str2);
    }





    public static final Identifier smite = ModIdentifier.get("textures/gui/hud/enchantment/smite.png");
    public static final Identifier mending = ModIdentifier.get("textures/gui/hud/enchantment/mending.png");
    public static final Identifier unbreaking = ModIdentifier.get("textures/gui/hud/enchantment/unbreaking.png");
    public static final Identifier protection = ModIdentifier.get("textures/gui/hud/enchantment/protection.png");
    public static final Identifier feather_falling = ModIdentifier.get("textures/gui/hud/enchantment/feather_falling.png");
    public static final Identifier looting = ModIdentifier.get("textures/gui/hud/enchantment/looting.png");
    public static final Identifier efficiency = ModIdentifier.get("textures/gui/hud/enchantment/efficiency.png");
    public static final Identifier fortune = ModIdentifier.get("textures/gui/hud/enchantment/fortune.png");
    public static final Identifier sharpness = ModIdentifier.get("textures/gui/hud/enchantment/sharpness.png");
    public static final Identifier fire_aspect = ModIdentifier.get("textures/gui/hud/enchantment/fire_aspect.png");
    public static final Identifier sweeping_edge = ModIdentifier.get("textures/gui/hud/enchantment/sweeping_edge.png");
    public static final Identifier knockback = ModIdentifier.get("textures/gui/hud/enchantment/knockback.png");
    public static final Identifier silk_touch = ModIdentifier.get("textures/gui/hud/enchantment/silk_touch.png");

    //trident
    public static final Identifier loyalty = ModIdentifier.get("textures/gui/hud/enchantment/trident/loyalty.png");
    public static final Identifier channeling = ModIdentifier.get("textures/gui/hud/enchantment/trident/channeling.png");
    public static final Identifier riptide = ModIdentifier.get("textures/gui/hud/enchantment/trident/riptide.png");
    public static final Identifier impaling = ModIdentifier.get("textures/gui/hud/enchantment/trident/impaling.png");

    //mace
    public static final Identifier wind_burst = ModIdentifier.get("textures/gui/hud/enchantment/mace/wind_burst.png");
    public static final Identifier density = ModIdentifier.get("textures/gui/hud/enchantment/mace/density.png");
    public static final Identifier breach = ModIdentifier.get("textures/gui/hud/enchantment/mace/breach.png");

    //bow
    public static final Identifier flame = ModIdentifier.get("textures/gui/hud/enchantment/bow/flame.png");
    public static final Identifier punch = ModIdentifier.get("textures/gui/hud/enchantment/bow/punch.png");
    public static final Identifier infinity = ModIdentifier.get("textures/gui/hud/enchantment/bow/infinity.png");
    public static final Identifier power = ModIdentifier.get("textures/gui/hud/enchantment/bow/power.png");


    //Accessibility
    public static final Identifier LANGUAGE_icon = ModIdentifier.get("textures/gui/hud/accessibility/language.png");
    public static final Identifier PLACE_icon = ModIdentifier.get("textures/gui/screen/side_bar/icons/place.png");

}
