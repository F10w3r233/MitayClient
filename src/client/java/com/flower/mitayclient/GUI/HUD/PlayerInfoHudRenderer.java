package com.flower.mitayclient.GUI.HUD;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.Data.PlayerDataHandler;
import com.flower.mitayclient.util.Locations;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import java.util.Objects;

import com.flower.mitayclient.util.Skin.SkinCacheHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;

import static com.flower.mitayclient.util.Resource.getCameraPlayer;
import static com.flower.mitayclient.util.Resource.getStringWidth;

public class PlayerInfoHudRenderer
{
    static int color;
    static int playerNameColor = 0xFFDCDCDC;
    static int playerPingColor = 0xFFDCDCDC;


    static Identifier pingIcon;


    static int o = 0;
    static int o2 = 0;
    static int o3 = 0;

    static String orginalWorld;
    static Component orginalPlace;


    public static void render(GuiGraphicsExtractor context)
    {
        if(Mitayclient.getConfig().isTopShown() && !Minecraft.getInstance().options.hideGui)
        {
            playerNameColor = MitayUtils.getNameColor(getCameraPlayer().getScoreboardName());

            Minecraft client = Minecraft.getInstance();

            //获取玩家延迟颜色
            if(client != null)
            {
                if(client.getConnection().getPlayerInfo(getCameraPlayer().getScoreboardName()) != null)
                {
                    int ping = PlayerDataHandler.ping;
                    if(ping != 0)
                    {
                        if(ping <= 150)
                        {
                            playerPingColor = 0xFF01B207;
                            pingIcon =  ModIdentifier.get("textures/gui/hud/player_list/network/ping_5.png");
                        }else if(ping > 150 && ping <= 300)
                        {
                            playerPingColor = 0xFF01B207;
                            pingIcon = ModIdentifier.get( "textures/gui/hud/player_list/network/ping_4.png");
                        }else if(ping > 300 && ping <= 600)
                        {
                            playerPingColor = 0xFFFFFF55;
                            pingIcon = ModIdentifier.get("textures/gui/hud/player_list/network/ping_3.png");
                        }else if(ping > 600 && ping < 1000)
                        {
                            playerPingColor = 0xFFA52A2A;
                            pingIcon = ModIdentifier.get("textures/gui/hud/player_list/network/ping_2.png");
                        }else if(ping > 1000)
                        {
                            playerPingColor = 0xFFA52A2A;
                            pingIcon = ModIdentifier.get("textures/gui/hud/player_list/network/ping_1.png");
                        }
                    }
                }
            }

            String worldName = "";
            Component translatableWorldName = Component.literal("");
            //世界名
//            System.out.println(getCameraPlayer().getEntityWorld().getDimensionEntry().getIdAsString());
            switch (getCameraPlayer().level().dimensionTypeRegistration().getRegisteredName())
            {
                case "minecraft:overworld" : worldName = "主世界";translatableWorldName = Component.translatable("world.mitayclient.overworld");color = 0xFF01B207;break;
                case "minecraft:the_nether" : worldName = "地狱";translatableWorldName = Component.translatable("world.mitayclient.nether"); color = 0xFFBD3737;break;
                case "minecraft:the_end" : worldName = "末地";translatableWorldName = Component.translatable("world.mitayclient.end");color = 0xFF890D89;break;
            }
            if(worldName.equals("主世界") && getCameraPlayer().isCreative())
            {
                worldName = "创造世界";
                translatableWorldName = Component.translatable("world.mitayclient.creative");
            }


            //玩家坐标 获取地点名
            double placeX = getCameraPlayer().getX();
            double placeZ = getCameraPlayer().getZ();
            Component place = Locations.getPlace(placeX,placeZ, worldName);


            ClientPacketListener clientPlayNetworkHandler = null;
            if(client != null)
            {
                if(client.player != null)
                {
                    clientPlayNetworkHandler = client.player.connection;
                }
            }

            PlayerInfo playerListEntry = null;
            if(clientPlayNetworkHandler.getPlayerInfo(getCameraPlayer().getUUID()) != null)
            {
                playerListEntry = clientPlayNetworkHandler.getPlayerInfo(getCameraPlayer().getUUID());
            }

            if(orginalWorld != worldName || !Objects.equals(place, orginalPlace)) //地点变化 载入动画重播放
            {
                o = -34;
                o2 = -24;
                o3 = -14;
            }



            if(o < 10)
            {
                o++;
            }


            if(o2<18)
                o2++;
            if(o3<28)
                o3++;




            //玩家名长度处理
            String playerName = getCameraPlayer().getScoreboardName();

            int nameLength = getStringWidth(playerName);
            int pingLength = nameLength + 2;
            int biaNameLength = nameLength - 62;
            int placeLength;
            if(place != null)
            {
                placeLength = getStringWidth(translatableWorldName + "·" + place.getString());
            }else placeLength = getStringWidth(translatableWorldName);

            int finalBiaLength = 0;
            if(nameLength > getStringWidth("Mod_TestBot") || placeLength > getStringWidth("Mod_TestBot 刷铁机记"))
            {
                finalBiaLength = Math.max(biaNameLength, placeLength-290);
            }

            int x = (client.getWindow().getGuiScaledWidth()-(160+finalBiaLength))/2;

            //顶部信息栏渲染
            Identifier bar;
            int biaX = 0;
            int biaY = 0;
            float infoAlpha = 1f;
            if(Mitayclient.getConfig().isDarkShown())
            {
                bar = Resource.INFO_BAR_DARK;
                biaX = 2;
                biaY = 0;
            }else
            {
                biaX = 0;
                biaY = 0;
                infoAlpha = 0.88f;
                bar = Resource.INFO_BAR;
            }
            context.blit(RenderPipelines.GUI_TEXTURED, bar, x-biaX, o+biaY, 0, 0,160+finalBiaLength, 34, 160+finalBiaLength, 34, ARGB.white(infoAlpha));
//            context.drawTexture(RenderLayer::getGuiTextured, bar, x-biaX, o+biaY, 0, 0,160, 34, 160, 34, ColorHelper.getWhite(infoAlpha));
//            System.out.println(getStringWidth("  OverWorld 刷铁机"));




            if(playerListEntry != null)
            {
                //玩家头像渲染
//                PlayerFaceExtractor.extractRenderState(context, playerListEntry.getSkin(), x+24, o2, 18);
                SkinCacheHelper.renderHeadWith3D(context, playerListEntry.getProfile().name(), x+24, o2, 18, 0.5f);
            }


            //玩家名渲染
            context.text(client.font, playerName, x+55, o2, playerNameColor, true);

            //延迟图标渲染
            if(client != null)
            {
                if(client.getConnection() != null)
                {
                    if(client.getConnection().getPlayerInfo(getCameraPlayer().getScoreboardName()) != null)
                    {
                        if(pingIcon != null && client.getConnection().getPlayerInfo(getCameraPlayer().getScoreboardName()).getLatency() != 0)
                        {
                            context.blit(RenderPipelines.GUI_TEXTURED,pingIcon, x+57+pingLength, o2, 0,0,10,8,10,8);

                            //延迟渲染
                            try
                            {
                                context.text(Minecraft.getInstance().font, String.valueOf(client.getConnection().getPlayerInfo(getCameraPlayer().getScoreboardName()).getLatency()), x+55+pingLength+14, o2, playerPingColor);
                            }catch (NullPointerException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            Identifier icon = null;
            switch (worldName)
            {
                case "主世界" -> icon = Resource.OVERWORLD_icon;
                case "地狱" -> icon = Resource.NETHER_icon;
                case "末地" -> icon = Resource.END_icon;
                case "创造世界" -> icon = Resource.CREATIVE_WORLD_icon;
            }
            context.blit(RenderPipelines.GUI_TEXTURED, icon, x+55, o3-1, 0, 0,9, 9, 9, 9);



            if(place != null)
            {
                //地点渲染
                context.text(Minecraft.getInstance().font, "· " + place.getString(), x+68+client.font.width(translatableWorldName)+1, o3, color);
            }
            //世界名渲染
            context.text(Minecraft.getInstance().font, translatableWorldName.getString(), x+66, o3, color);

            orginalPlace = place;
            orginalWorld = worldName;
        }
    }
}
