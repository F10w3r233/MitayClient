package com.flower.mitayclient;


import com.flower.mitayclient.GUI.screen.BadgeUtil.BadgeCache;
import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.BadgesPayload;
import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.RequestBadgesPayload;
import com.flower.mitayclient.GUI.screen.PlaceListUtil.PlaceCache;
import com.flower.mitayclient.GUI.screen.PlaceListUtil.PlacesPayload;
import com.flower.mitayclient.GUI.screen.PlaceListUtil.RequestPlacesPayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.GameTimePayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.LeaderBoard.LeaderboardPayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.LeaderBoard.RequestLeaderboardPayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.GUI.screen.ProfileUtil.RequestGameTimePayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.PlayerTitlesPayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RequestTitlesPayload;
import com.flower.mitayclient.GUI.HUD.Tab.network.RequestTabInfoPayload;
import com.flower.mitayclient.GUI.HUD.Tab.network.TabInfoCache;
import com.flower.mitayclient.GUI.HUD.Tab.network.TabInfoPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;

import java.text.DecimalFormat;
import java.util.Map;

import static com.flower.mitayclient.util.ChatHistory.ChatKeywordDetector.onlinePlayers;

@Environment(EnvType.CLIENT)
public class MitayModClient implements ClientModInitializer
{
    DecimalFormat df = new DecimalFormat(".0");
    @Override
    public void onInitializeClient()
    {

//=======================================================排行榜==================================================
        // 注册 C2S 请求包
        PayloadTypeRegistry.serverboundPlay().register(RequestLeaderboardPayload.TYPE, RequestLeaderboardPayload.STREAM_CODEC);
        // 注册 S2C 排行榜包（客户端接收）
        PayloadTypeRegistry.clientboundPlay().register(LeaderboardPayload.TYPE, LeaderboardPayload.STREAM_CODEC);

        ClientPlayNetworking.registerGlobalReceiver(LeaderboardPayload.TYPE, (payload, context) -> {
            Map<String, Double> leaderboard = payload.leaderboard();
            // 存入静态容器，比如 PlayerProfile.leaderboardMap = leaderboard;
            PlayerProfile.leaderboardMap = leaderboard;
            System.out.println("收到排行榜，共 " + leaderboard.size() + " 名玩家");
        });



//=======================================================称号==================================================
        // C2S 请求称号
        PayloadTypeRegistry.serverboundPlay().register(RequestTitlesPayload.TYPE, RequestTitlesPayload.STREAM_CODEC);
        // S2C 称号响应
        PayloadTypeRegistry.clientboundPlay().register(PlayerTitlesPayload.TYPE, PlayerTitlesPayload.STREAM_CODEC);

        ClientPlayNetworking.registerGlobalReceiver(PlayerTitlesPayload.TYPE, (payload, context) -> {
            PlayerProfile.allPlayerTitles = payload.titleMap();   // 存入 Map
            // 同步更新名称集合，用于缓存玩家名字，减少chat处渲染遍历的复杂度O(n) -> O(1)
            PlayerProfile.playerNameSet.clear();
            PlayerProfile.playerNameSet.addAll(payload.titleMap().keySet());
            System.out.println("收到玩家称号数据，人数：" + payload.titleMap().size());
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ClientPlayNetworking.send(new RequestTitlesPayload());
            onlinePlayers = Minecraft.getInstance().getConnection().getOnlinePlayers().size();
        });



//=======================================================个人游戏时间==================================================
        // 1. 注册 S2C 包的编解码器（play 阶段，客户端接收）
        PayloadTypeRegistry.clientboundPlay().register(GameTimePayload.TYPE, GameTimePayload.STREAM_CODEC);
        //发送请求
        PayloadTypeRegistry.serverboundPlay().register(
                RequestGameTimePayload.TYPE,
                RequestGameTimePayload.STREAM_CODEC
        );
        // 2. 注册业务处理器
        ClientPlayNetworking.registerGlobalReceiver(
                GameTimePayload.TYPE,
                (payload, context) -> {
                    try {
                        int ticks = payload.gameTime();
                        double hours = ticks / 72000.0;
                        PlayerProfile.selfGameTime = String.valueOf(df.format(hours));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

        //=================================共享地点=====================================
        // 注册
        PayloadTypeRegistry.serverboundPlay().register(RequestPlacesPayload.TYPE, RequestPlacesPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PlacesPayload.TYPE, PlacesPayload.STREAM_CODEC);

// 接收地点数据
        ClientPlayNetworking.registerGlobalReceiver(PlacesPayload.TYPE, (payload, context) -> {
            // 存到静态缓存，例如 PlacesCache.places = payload.places();
            PlaceCache.setPlaces(payload.places());
            System.out.println("收到地点数据: " + payload.places().size());
        });

//        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
//            ClientPlayNetworking.send(new RequestPlacesPayload());
//        });

        //================================== Tab数据 ========================================
        // 注册 Payload
        PayloadTypeRegistry.serverboundPlay().register(RequestTabInfoPayload.TYPE, RequestTabInfoPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TabInfoPayload.TYPE, TabInfoPayload.STREAM_CODEC);

        // 接收数据
        ClientPlayNetworking.registerGlobalReceiver(TabInfoPayload.TYPE, (payload, context) -> {
            TabInfoCache.set(payload.playerInfoMap());
//            System.out.println("收到Tab信息，共 " + payload.playerInfoMap().size() + " 名玩家");
        });

        // ====================================== 勋章 =================================
        PayloadTypeRegistry.serverboundPlay().register(RequestBadgesPayload.TYPE, RequestBadgesPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BadgesPayload.TYPE, BadgesPayload.STREAM_CODEC);

        ClientPlayNetworking.registerGlobalReceiver(BadgesPayload.TYPE, (payload, context) -> {
            BadgeCache.set(payload.badges());
            System.out.println("收到勋章数据，共 " + payload.badges().size() + " 个");
        });
    }
}
