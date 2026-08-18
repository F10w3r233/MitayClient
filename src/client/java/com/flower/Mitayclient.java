package com.flower;

import com.flower.mitayclient.GUI.HUD.*;
import com.flower.mitayclient.GUI.screen.ProfileUtil.title.RequestTitlesPayload;
import com.flower.mitayclient.event.KeyInputHandler;
import com.flower.mitayclient.util.ChatHistory.ChatRenderer;
import com.flower.mitayclient.util.Data.DisplayConfig;
import com.flower.mitayclient.util.ChatHistory.TextSerializer;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Skin.SkinCacheHelper;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.UUID;


/**
*
* Mitay Client for 1.21.11
* @version 3.0
* @author <13anx!aF10w3r / BanxiaFlower / 半夏秋花 / 秋花三葉>
*/
public class Mitayclient implements ModInitializer
{
	private static final Identifier MY_HUD_LAYER = ModIdentifier.get("mitayclient");
	private static DisplayConfig config;

	@Override
	public void onInitialize()
	{

		KeyInputHandler.register();
		config = new DisplayConfig();


		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,      // 锚点图层
				MY_HUD_LAYER,                // 你的图层 ID
				this::renderHud              // 渲染方法
		);

		ClientPlayConnectionEvents.JOIN.register(((listener, sender, client) -> {
			// 延迟 1 秒后预加载所有玩家，确保 player list 已就绪
			client.execute(() -> {
				client.getConnection().getOnlinePlayers().forEach(playerInfo -> {
					UUID uuid = playerInfo.getProfile().id();
					SkinCacheHelper.preloadSkinAsync(uuid);
				});

//				ClientPlayNetworking.send(new RequestTitlesPayload());
//				System.out.println("请求成功");
			});
		}));



		ClientReceiveMessageEvents.GAME.register((message, overlay) ->
		{
			Minecraft client = Minecraft.getInstance();
			if (client.player != null
					&& !overlay
					&& !message.getString().contains("{untitled}")
					&& !message.getString().contains("[Mitay Security]")
					&& !message.getString().contains("Mitay Watchdog")
					&& !message.getString().contains("欢迎进入Mitay")
					&& !message.getString().contains("已更改位于")
					&& !message.getString().contains("你的游戏模式")
					&& !message.getString().contains("已成功填充")
					&& !message.getString().contains("光影包已")
					&& !message.getString().contains("游戏模式改为")
					&& !message.getString().contains("召唤了新的")
					&& !message.getString().contains("无法在和平难度下召唤")
					&& !message.getString().contains("第一选取点")
					&& !message.getString().contains("第二选取点")
					&& !message.getString().contains("已移动")
					&& !message.getString().contains("已影响")
					&& !message.getString().contains("影响了")
					&& !message.getString().contains("影响了")
					&& !message.getString().contains("游戏模式设置为"))
			{
//				client.player.playSound(
////						SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
//						SoundEvents.DISPENSER_FAIL,
//						0.4f, // 音量
//						1.0f
//				);

				System.out.println(Minecraft.getInstance().getConnection().getConnection().getRemoteAddress().toString());
				if(Minecraft.getInstance().getConnection().getConnection().getRemoteAddress().toString().equals("g.a1.ocent.net/203.135.104.11:10130"))
				{
					if(ChatRenderer.getType(message) != null)
					{
						TextSerializer.serialize(message);
						TextSerializer.saveToFile(TextSerializer.serialize(message));
					}
				}
			}
		});
	}

	private void renderHud(GuiGraphicsExtractor context, DeltaTracker deltaTracker)
	{
		// 在这里写你的渲染逻辑
		// 参数与 HudRenderCallback 完全相同
		Minecraft client = Minecraft.getInstance();
		ContainerItemHudRenderer.render(context);
		ToolBarHudRenderer.render(context);
//			MusicInfoHudRenderer.render(drawContext);
		TargetPlayerInfoHudRenderer.render(context);
		PlayerInfoHudRenderer.render(context);
		StatusEffectHudRenderer.render(context);

	}
	public static DisplayConfig getConfig()
	{
		if (config == null)
		{
			config = new DisplayConfig();
		}
		return config;
	}
}