package com.flower.mitayclient.GUI.screen;

import com.flower.mitayclient.GUI.HUD.ToolBarHudRenderer;
import com.flower.mitayclient.GUI.buttons.Accessibility.AccessibilityPressableWidget;
import com.flower.mitayclient.GUI.buttons.Badge.BadgeButton;
import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
import com.flower.mitayclient.GUI.screen.BadgeUtil.BadgeCache;
import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.BadgesPayload;
import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.RequestBadgesPayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.LeaderBoard.RequestLeaderboardPayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.GUI.screen.ProfileUtil.RequestGameTimePayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RankTitle;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RequestTitlesPayload;
import com.flower.mitayclient.GUI.screen.SideBarUtil.SideType;
import com.flower.mitayclient.util.ModIdentifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Items;

import java.util.*;

import static com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile.allPlayerTitles;
import static com.flower.mitayclient.util.ChatHistory.TextSerializer.setColor;
import static com.flower.mitayclient.util.ChatHistory.TextSerializer.splitComponent;
import static com.flower.mitayclient.util.MitayUtils.getFontColor;
import static com.flower.mitayclient.util.MitayUtils.getTitleColor;
import static com.flower.mitayclient.util.Resource.getCameraPlayer;
import static com.flower.mitayclient.util.Resource.getStringWidth;
import static com.flower.mitayclient.util.Skin.SkinCacheHelper.*;

public class ProfileScreen extends SideBarScreen
{
    private static final Identifier PROFILE = ModIdentifier.get("textures/gui/screen/side_bar/icons/profile.png");
    private static final Identifier TITLE = ModIdentifier.get("textures/gui/screen/profile/title.png");
    private int panelX, panelY;
    public ProfileScreen()
    {
        super(Component.empty(), Type.CUSTOM);
    }

    Map<String, SideType> sideTypeMap = new LinkedHashMap<>();



    public static SideType profileSide;
    SideType rankingList;

    public void initializeMaps()
    {
        //SideType

        profileSide = SideType.withCustomStyle(PROFILE, "个人档案");
        rankingList = SideType.withCustomStyle(AccessibilityPressableWidget.PLAYER_LIST, "排行榜");

        sideTypeMap.put("个人档案", profileSide);
        sideTypeMap.put("排行榜", rankingList);
    }

    @Override
    protected void init()
    {
        initializeMaps();

        //重置状态，打开屏幕时查看自己的资料
        this.viewingProfile = null;

        //向服务端发送请求，初始化玩家时间
        ClientPlayNetworking.send(new RequestGameTimePayload());
        //请求排行榜
        ClientPlayNetworking.send(new RequestLeaderboardPayload());
        //请求称号
        ClientPlayNetworking.send(new RequestTitlesPayload());
        //请求勋章
//        ClientPlayNetworking.send(new RequestBadgesPayload());
        //初始化排行榜Entry
//        initializeRankingButtons(PlayerProfile.leaderboardMap);

        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;
        super.sideTypeMap = this.sideTypeMap;
        super.currentScreen = ScreenType.PROFILE;
        super.init();
    }

    private boolean leaderboardLoaded = false;
    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        super.extractRenderState(context, mouseX, mouseY, a);

        if(currentSideType.typeName.equals("个人档案"))
        {
            renderProfile(context, new PlayerProfile(getCameraPlayer().getScoreboardName(), PlayerProfile.selfGameTime));
        }else {
//            renderRankingList(context);
            // 每帧检查排行榜数据是否已经到达
            if (!leaderboardLoaded && PlayerProfile.leaderboardMap != null && !PlayerProfile.leaderboardMap.isEmpty()) {
                leaderboardLoaded = true;
                initializeRankingButtons(PlayerProfile.leaderboardMap);
                // 如果当前正显示“排行榜”侧栏，重新填充滚动区域
                if (currentSideType != null && "排行榜".equals(currentSideType.typeName)) {
                    showContent(currentSideType, () -> {});
                }
            }
        }

//        renderEnchantedTexture(context, PROFILE, panelX + 0 + -40, panelY + 13 + 24 + 50, 32,32);
    }

    @Override
    public void showContent(SideType type, Runnable runnable)
    {
        clearAllWidgets();
        //用于往scrollArea里添加组件，具体的文字、图标另写方法（如：renderProfile()）。
        switch (type.typeName)
        {
            case "个人档案" -> {
//                this.viewingProfile = null;


                String targetPlayer = (viewingProfile != null) ? viewingProfile.name :
                        (getCameraPlayer() != null ? getCameraPlayer().getScoreboardName() : "");

                updateBadgeButtons(targetPlayer);

                int badgeX = panelX + 130;
                int badgeY = panelY + 13 + 24 + 32 + 16 + 6;
                for (BadgeButton badge : badgeButtons) {
                    badge.setX(badgeX);
                    badge.setY(badgeY);
                    badgeX += 40;
                    scrollArea.children.add(badge);
                }
            }
            case "排行榜" -> {
//                super.scrollArea.children.add(field);
                super.showContent(rankingButtonList, PlaceListButton.class);

            }
        }
    }
    //待改为通用方法
    /*
    * @param
    * 'PlayerProfile' : [name, skin]
    * */
    public void renderProfile(GuiGraphicsExtractor context, PlayerProfile profile)
    {
        String name = profile.name;
        String time = profile.time;
        if (viewingProfile != null) {
            name = viewingProfile.name;
            time = viewingProfile.time;
        } else {
            name = getCameraPlayer().getScoreboardName();
            time = PlayerProfile.selfGameTime;
        }
        if (time != null)
        {
            if(time.startsWith("."))
                time = "0" + time;
        }
        Component displayName = Component.literal("null");

        boolean containsDisplayName = false;
        for (PlayerInfo player : players)
        {
            if (player.getTabListDisplayName() != null)
            {
                if (player.getTabListDisplayName().getString().contains(name))
                {
                    containsDisplayName = true;
                    displayName = player.getTabListDisplayName();
                }
            }
        }

        renderHeadWith3D(context, name, panelX + 130, panelY + 13, 24, 0.5f);
//        graphics.blit(RenderPipelines.GUI_TEXTURED, Resource.PLACE_icon, panelX+130, panelY + 10, 0,0,24,24,24,24);
        ToolBarHudRenderer.drawScaledText(context, font, containsDisplayName ? splitComponent(displayName, "] ")[1] : Component.literal(name), panelX+135 + 28, panelY+20, 1.5f, getFontColor(), false);
        context.item(Items.CLOCK.getDefaultInstance(), panelX+130, panelY + 13 + 24 + 10);
        context.text(font, "游戏时长：" + time + " " + "小时", panelX + 130 + 20, panelY + 13 + 24 + 14, getFontColor(), false);

//        context.item(Items.TRIDENT.getDefaultInstance(), panelX+130, panelY + 13 + 24 + 32);
        context.blit(RenderPipelines.GUI_TEXTURED, TITLE, panelX+130, panelY + 13 + 24 + 32,0,0,16,16,16,16);
        context.text(font, "头衔：", panelX + 130 + 20, panelY + 13 + 24 + 36, getFontColor(), false);
        if(allPlayerTitles.containsKey(name))
        {
            List<String> titles = PlayerProfile.allPlayerTitles.get(name);
            // 绘制称号，例如用逗号分隔
            for (String title : titles)
            {
                context.text(font, title, panelX + 130 + 20 + getStringWidth("头衔：") + 4, panelY + 13 + 24 + 36, getTitleColor(title), false);
            }
        }
    }

    Collection<PlayerInfo> players = Minecraft.getInstance().getConnection().getOnlinePlayers();

    //为null时则是个人资料
    private PlayerProfile viewingProfile = null;
    private List<BadgeButton> badgeButtons = new ArrayList<>();

    public  PlaceListButton createRankingButton(PlayerProfile profile, String rank)
    {
        String name = profile.name;
        Component displayName = Component.literal("null");

        boolean containsDisplayName = false;
//        for (PlayerInfo player : players)
//        {
//            if (player.getTabListDisplayName().getString().contains(name))
//            {
//                containsDisplayName = true;
//                displayName = player.getTabListDisplayName();
//            }
//        }

        List<String> playerTitles = new ArrayList<>();
        playerTitles = PlayerProfile.allPlayerTitles.get(name);
        if(playerTitles != null) //有displayName
        {
            if(!playerTitles.isEmpty())
            {
                containsDisplayName = true;
                displayName = setColor(Component.literal("[" + playerTitles.get(0) + "] " + name), RankTitle.getTitleColor(playerTitles.get(0)));
            }
        }

        return PlaceListButton.builder(containsDisplayName ? splitComponent(displayName, "] ")[1] : Component.literal(name), button -> {
            this.viewingProfile = profile;
            currentSideType = profileSide;
            showContent(profileSide, () -> {});  // 立即刷新勋章
        }).type("ranking_" + rank).profile(profile).dimensions(0, 0, 210, 30).build();
    }

    /**
     * 根据玩家名更新勋章按钮列表
     */
    private void updateBadgeButtons(String playerName)
    {
        badgeButtons.clear();
        if (playerName == null || BadgeCache.isEmpty()) return;

        for (BadgesPayload.BadgeInfo info : BadgeCache.get().values()) {
            // 解析所有者列表
            String[] owners = info.owner().split(",");
            for (String owner : owners) {
                if (owner.trim().equalsIgnoreCase(playerName)) {
                    // 创建勋章按钮（这里只做展示，点击可以留空）
                    BadgeButton btn = BadgeButton.builder(
                                    Component.literal(info.name()),
                                    b -> { /* 点击勋章的回调，可以暂时为空 */ }
                            )
                            .badge(info)
                            .dimensions(0, 0, 36, 36)// 初始尺寸，后续会动态调整
                            .build();

                    btn.setTooltip(Tooltip.create(Component.literal("§6" + info.name() + "\n" +
                            "§7" + info.desc() + "\n"
                            + "§b获取时间：" + info.time())));
                    badgeButtons.add(btn);
                    break; // 一个玩家在一个勋章中只出现一次
                }
            }
        }
    }


    List<PlaceListButton> rankingButtonList = new ArrayList<>();
    public void initializeRankingButtons(Map<String, Double> rankingMap)
    {
        rankingButtonList.clear();
        int index = 0;
        for (Map.Entry<String, Double> rankingMapEntry : rankingMap.entrySet())
        {
            index++;
            String name = rankingMapEntry.getKey();
            double time = rankingMapEntry.getValue();
            rankingButtonList.add(createRankingButton(new PlayerProfile(name, time), String.valueOf(index)));
        }

    }

    // 当排行榜数据到达时，更新按钮并刷新显示
    public void onLeaderboardReceived() {
        // 重新生成按钮列表
        initializeRankingButtons(PlayerProfile.leaderboardMap);
        // 如果当前正在显示“排行榜”侧栏，需要重新将其内容放入滚动区域
        if (currentSideType != null && "排行榜".equals(currentSideType.typeName)) {
            showContent(currentSideType, () -> {}); // 这会清空并重新填充按钮
        }
    }

    boolean hasDisplayName(String name)
    {
        boolean flag = false;
        for (PlayerInfo player : players)
        {
            if (player.getTabListDisplayName().getString().contains(name))
            {
                flag = true;
            }
        }
        return flag;
    }


}
