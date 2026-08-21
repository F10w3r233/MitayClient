package com.flower.mitayclient.GUI.buttons.PlaceList.Large;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import com.flower.mitayclient.util.Skin.SkinCacheHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.PlayerSkin;

import static com.flower.mitayclient.util.MitayUtils.getWorldIcon;
import static com.flower.mitayclient.util.Resource.*;


@Environment(EnvType.CLIENT)
public abstract class PlaceListPressable extends AbstractWidget
{


    //nether
    public static final Identifier PIG_MAN = ModIdentifier.get("textures/gui/hud/places/gold_ingot.png");
    public static final Identifier WITHER_SKULL = ModIdentifier.get("textures/gui/hud/places/wither_skull.png");
    public static final Identifier GHAST_FARM = ModIdentifier.get("textures/gui/hud/places/ghast_farm.png");

    //end
    public static final Identifier ENDER_MAN_FARM = ModIdentifier.get("textures/gui/hud/places/ender_pearl.png");


    //overworld
    public static final Identifier EXCHANGE = ModIdentifier.get("textures/gui/hud/places/emerald.png");
    public static final Identifier HOME = ModIdentifier.get("textures/gui/hud/places/birch_planks.png");


    public static final Identifier MOB_TOWER_MAIN = ModIdentifier.get("textures/gui/hud/places/slime_ball.png");
    //    private static final Identifier MOB_TOWER_RESOURCE = Identifier.of("mitayclient","textures/gui/sprites/hud/places/christmas_chest.png");
    public static final Identifier MOB_TOWER_RESOURCE = ModIdentifier.get("textures/gui/hud/places/chest.png");
    public static final Identifier MOB_TOWER_AFK = ModIdentifier.get("textures/gui/hud/places/afk.png");

    public static final Identifier IRON_FARM = ModIdentifier.get("textures/gui/hud/places/iron_ingot.png");
    public static final Identifier SUGAR_CANE = ModIdentifier.get("textures/gui/hud/places/sugar_cane.png");
    public static final Identifier GUARDIAN = ModIdentifier.get("textures/gui/hud/places/guardian.png");
    public static final Identifier STONE = ModIdentifier.get("textures/gui/hud/places/stone.png");
    public static final Identifier FURNACE = ModIdentifier.get("textures/gui/hud/places/furnace.png");
    public static final Identifier SPAWNPOINT = ModIdentifier.get("textures/gui/hud/places/spawnpoint.png");



    public static final Identifier END_PORTAL = ModIdentifier.get("textures/gui/hud/places/end_portal_frame.png");
    public static final Identifier END_MAINLAND = ModIdentifier.get("textures/gui/hud/places/end_stone.png");


    //creative world
    public static final Identifier CREATIVE_WORLD = ModIdentifier.get("textures/gui/hud/places/redstone.png");




    public static final Identifier BUTTON = ModIdentifier.get("textures/gui/widget/side_bar_large/button_large.png");
    public static final Identifier BUTTON_DARK = ModIdentifier.get("textures/gui/widget/side_bar_large/button_large_dark.png");
    public static final Identifier BUTTON_FOCUS = ModIdentifier.get("textures/gui/widget/side_bar_large/button_large_hovered.png");
    public static final Identifier BUTTON_FOCUS_DARK = ModIdentifier.get("textures/gui/widget/side_bar_large/button_large_hovered_dark.png");

    public static final Identifier CLOCK = ModIdentifier.get("textures/gui/screen/profile/clock.png");

    public static final Identifier MULTI_DIMENSION = ModIdentifier.get("textures/gui/widget/place_list/multi_dimension.png");
    public static final Identifier OVERWORLD_SIDE = ModIdentifier.get("textures/gui/hud/places/grass.png");
    public static final Identifier NETHER_SIDE = ModIdentifier.get("textures/gui/widget/place_list/nether.png");

    String iconName;
    Identifier iconIdentifier;
    PlayerSkin skin;
    String type;
    PlayerProfile profile;
    String desc;

    public PlaceListPressable(int i, int j, int k, int l, Component text, String iconName, Identifier iconIdentifier, PlayerSkin skin, String type, PlayerProfile profile, String desc)
    {
        super(i, j, k, l, text);
        this.iconName = iconName;
        this.iconIdentifier = iconIdentifier;
        this.skin = skin;
        this.type = type;
        this.profile = profile;
        this.desc = desc;
    }
    public abstract void onPress();

    public static Identifier getIdentifier(String iconName)
    {
        if(iconName == null)
        {
            return null;
        }
        return switch (iconName) {
            case "ender_man_farm" -> ENDER_MAN_FARM;
            case "exchange" -> EXCHANGE;
            case "home" -> HOME;
            case "creative" -> CREATIVE_WORLD;
            case "mob_main" -> MOB_TOWER_MAIN;
            case "mob_resource" -> MOB_TOWER_RESOURCE;
            case "mob_afk" -> MOB_TOWER_AFK;
            case "end_mainland" -> END_MAINLAND;
            case "end_portal" -> END_PORTAL;
            case "iron" -> IRON_FARM;
            case "sugar_cane" -> SUGAR_CANE;
            case "pig_man" -> PIG_MAN;
            case "wither_skull" -> WITHER_SKULL;
            case "guardian" -> GUARDIAN;
            case "stone" -> STONE;
            case "furnace" -> FURNACE;
            case "ghast_farm" -> GHAST_FARM;
            case "spawnpoint" -> SPAWNPOINT;


            default -> null;
        };
    }


    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        Identifier button;
        if(Mitayclient.getConfig().isDarkShown())
        {
            if(this.isHovered)
            {
                button = BUTTON_FOCUS_DARK;
            }else button = BUTTON_DARK;
        }else {
            if(this.isHovered)
            {
                button = BUTTON_FOCUS;
            }else button = BUTTON;
        }
        if(this.isHovered)
        {
            context.blit(RenderPipelines.GUI_TEXTURED, button, this.getX(), this.getY(), 0, 0,210, 30,210,30);
        }

        int color;
        if(Mitayclient.getConfig().isDarkShown())
        {
            color = CommonColors.WHITE;
        }else color = 0xFF0B242E;


        if(type != null)
        {
            if(type.startsWith("ranking"))
            {
                if(profile != null)
                {
                    String rank = type.split("_")[1];
                    //排名
                    context.text(Minecraft.getInstance().font, rank, getX() + 12, getY() + 11, color, false);
                    //方案一
                    // 名字
                    context.text(Minecraft.getInstance().font, this.getMessage(), (width - getStringWidth(getMessage().getString()) + 6) / 2 + getX(), getY() + 5, color, false);
//                    //时间
                    context.text(Minecraft.getInstance().font, profile.time + " 小时", (width - getStringWidth(profile.time + " 小时") + 6) / 2 + getX(), getY() + 16, CommonColors.GRAY, false);
//                    context.blit(RenderPipelines.GUI_TEXTURED, CLOCK, (width - getStringWidth(profile.time + " 小时") + 6) / 2 + getX()-14, getY()+10, 0,0,20,20,20,20);

                    //方案二
                    //名字
//                    context.text(Minecraft.getInstance().font, this.getMessage(), (width- getStringWidth(getMessage().getString())+6)/2+getX()-4, getY()+10, color, false);
//                    //时间
//                    context.text(Minecraft.getInstance().font, profile.time + " 小时", getX()+160 , getY() + 10, color, false);
//                    context.blit(RenderPipelines.GUI_TEXTURED, CLOCK, getX()+140, getY()+3, 0,0,22,22,22,22);
//
//                    SkinCacheHelper.renderHead(context, profile.name, getX()+28, getY()+6,16);
                    SkinCacheHelper.renderHeadWith3D(context, profile.name, getX()+28, getY()+6,16, 0.5f);
                }
            } else if (type.startsWith("shared_place"))
            {
                String desc = profile.name;
                String[] worldInfo = profile.time.split("/");
                String worldName = worldInfo[0];
                String coordinate = worldInfo[1].replaceAll("_", " ");
                String uploader = worldInfo[2];

                Identifier worldIcon = getWorldIcon(worldName.trim());
                if(worldIcon != null)
                    context.blit(RenderPipelines.GUI_TEXTURED, worldIcon, this.getX()+10, this.getY()+6, 0,0,16,16,16,16);
                context.text(Minecraft.getInstance().font, desc, (width - getStringWidth(desc)) / 2 + getX(), getY() + 5, color, false);
                //显示坐标
//                context.text(Minecraft.getInstance().font, uploader + " · " + coordinate, (width - getStringWidth(uploader + " · " + coordinate)) / 2 + getX(), getY() + 16, CommonColors.GRAY, false);
                //不显示坐标
                context.text(Minecraft.getInstance().font, uploader, (width - getStringWidth(uploader)) / 2 + getX(), getY() + 16, CommonColors.GRAY, false);
            }
        }else {
            if(skin != null)
            {
                PlayerFaceExtractor.extractRenderState(context, skin, this.getX()+10, this.getY()+6, 16);
            } else if(getIdentifier(iconName) != null)
            {
                context.blit(RenderPipelines.GUI_TEXTURED,getIdentifier(iconName), this.getX()+10, this.getY()+5, 0, 0,20, 20,20,20);
            }else if (iconIdentifier != null) {
                context.blit(RenderPipelines.GUI_TEXTURED,iconIdentifier, this.getX()+10, this.getY()+5, 0, 0,20, 20,20,20);
            }
        }


        if(profile == null)
            context.text(Minecraft.getInstance().font, this.getMessage(), (width - getStringWidth(getMessage().getString()) + 6) / 2 + getX(), getY() + 10, color, false);


        //desc 规范：
        /*
        * 1.     multiDimension -> 在右下角渲染一个 传送门和草方块图标
        * 2.     (overworld/nether)_afk_bot201 -> 在右下角渲染一个 草方块图标/传送门图标，并告知
        * 3.     (overworld/nether)_resource -> 在右下角渲染一个 传送门图标，在tooltip渲染所有物品
        * */
        if (desc != null)
        {
            String description = "";
            Identifier icon = null;

            if (desc.contains("multiDimension"))
            {
                description += "§6双维度装置§f";
                icon = MULTI_DIMENSION;
            } else {
                if (desc.contains("overworld"))
                {
                    description += "§6主世界侧§f\n";
                    icon = OVERWORLD_icon;
                }else if(desc.contains("nether"))
                {
                    description += "§6地狱侧§f\n";
                    icon = NETHER_SIDE;
                }

                if (desc.contains("afk"))
                {
                    description += "若资源点无物品，需要有一个玩家在此挂机\n" +
                            "在游戏启动器的名字框输入 " + "§6" + desc.split("_")[2] +"\n§f启动游戏并加入服务器，挂机一会就有物品\n" +
                            "挂机完可将此 §6" + desc.split("_")[2] + "§f 帐号退出游戏";
                }else if(desc.contains("resource"))
                {
                    description += "§7拿取资源处§f\n" +
                            "若资源不足，请看§3挂机点§f的说明操作\n" +
                            "产出：\n";
                }
            }

            if(icon != null)
            {
                context.blit(RenderPipelines.GUI_TEXTURED, icon, getX() + getWidth() - 16, getY()+15, 0,0, 12,12,12,12);
            }
            setTooltip(Tooltip.create(Component.literal(description)));
        }
    }



    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        this.onPress();
    }

//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
//    {
//        if (!this.active || !this.visible) {
//            return false;
//        } else if (KeyCodes.isToggle(keyCode)) {
//            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
//            this.onPress();
//            return true;
//        } else {
//            return false;
//        }
//    }
}