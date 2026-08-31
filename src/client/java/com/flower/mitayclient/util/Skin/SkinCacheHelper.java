package com.flower.mitayclient.util.Skin;


import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Matrix3x2fStack;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static net.minecraft.client.resources.DefaultPlayerSkin.get;
import static net.minecraft.client.resources.DefaultPlayerSkin.getDefaultSkin;

/**
 * 玩家皮肤缓存工具 —— 利用官方 PlayerSkinRenderCache 实现离线头像渲染。
 * 所有皮肤文件会被自动保存到 .minecraft/assets/skins/，无需额外操作。
 */
public final class SkinCacheHelper
{
    private static final Minecraft MC = Minecraft.getInstance();

    public static Identifier getSkinTexture(String name)
    {
        return getSkinTexture(ResolvableProfile.createUnresolved(name));
    }

    public static Identifier getSkinTexture(UUID uuid)
    {
        return getSkinTexture(ResolvableProfile.createUnresolved(uuid));
    }


    public static Identifier getSkinTexture(ResolvableProfile profile)
    {
        Optional<PlayerSkinRenderCache.RenderInfo> optional =
                MC.playerSkinRenderCache().lookup(profile).getNow(Optional.empty());

        PlayerSkin skin;
        if (optional.isPresent()) {
            skin = optional.get().playerSkin();
        } else {
            // 首次加载，触发下载（异步）
            MC.playerSkinRenderCache().createLookup(profile);
            // 使用默认皮肤
            skin = getDefaultSkin();
        }

        return skin.body().texturePath();
    }
//    /*
//     * 在 GUI 上渲染指定玩家的头部皮肤。
//     * 如果皮肤已经下载并缓存，直接使用；否则先显示默认皮肤，并触发异步下载。
//     */
    public static void renderHead(GuiGraphicsExtractor graphics, String name, int x, int y, int size)
    {
        renderHead(graphics, name, x, y, size, 0);
    }

    public static void renderHead(GuiGraphicsExtractor graphics, String name, int x, int y, int size, int color)
    {
//        ResolvableProfile profile = ResolvableProfile.createResolved(new GameProfile(uuid, name));
        ResolvableProfile profile = ResolvableProfile.createUnresolved(name);
        // 获取已完成的查找结果，不触发新的异步任务，不阻塞
        Optional<PlayerSkinRenderCache.RenderInfo> opt =
                MC.playerSkinRenderCache().lookup(profile).getNow(Optional.empty());

        if (opt.isPresent())
        {
            PlayerSkin skin = opt.get().playerSkin();
            if (MC.getConnection() != null)
            {
                if (MC.getConnection().getPlayerInfo(name) != null)
                {
                    skin = MC.getConnection().getPlayerInfo(name).getSkin();
                }
            }
            // 检查纹理是否已经实际注册（已上传）
            Identifier texture = skin.body().texturePath();
            if (MC.getTextureManager().getTexture(texture) != null)
            {
                PlayerFaceExtractor.extractRenderState(graphics, skin, x, y, size, color);
            }
            // 纹理未就绪时不做任何事，下一帧再试
        }
        // 如果连基本查找结果都没有（第一次遇到该玩家），也不绘制，交给预加载后台慢慢处理
    }

    public static void renderHeadWith3D(GuiGraphicsExtractor graphics, String name, int x, int y, int size, float threeDeeNess)
    {
        Identifier skin = getSkinTexture(name);
        renderHeadWith3D(graphics, skin, x, y, size, threeDeeNess);
    }
    public static void renderHeadWith3D(GuiGraphicsExtractor graphics, Identifier skin, int x, int y, int size, float threeDeeNess)
    {

        // 绘制脸部（不变形）
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 8, 8, size, size, 8, 8, 64, 64, -1);

        // 帽子缩放系数（0 ~ 0.25 增量，对应 0% ~ 100%）
        float scale = 1.0f + threeDeeNess * 0.25f;
        if (scale != 1.0f)
        {
            Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            // 平移到帽子中心
            pose.translate(x + size / 2f, y + size / 2f);
            pose.scale(scale, scale);
            pose.translate(-size / 2f, -size / 2f);
            // 绘制帽子层（坐标相对于当前矩阵，即 0,0 为帽子左上角）
            graphics.blit(RenderPipelines.GUI_TEXTURED, skin, 0, 0, 40, 8, size, size, 8, 8, 64, 64, -1);
            pose.popMatrix();
        } else
        {
            // 不缩放时直接绘制
            graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 40, 8, size, size, 8, 8, 64, 64, -1);
        }
    }


    public static CompletableFuture<Void> preloadSkinAsync(ResolvableProfile profile) {
        // 注意：不要把 join 放到主线程；这里用异步线程等待
        return CompletableFuture.runAsync(() -> MC.playerSkinRenderCache().lookup(profile).join());
    }

    // ===================== 缓存失效 =====================
    public static void invalidateSkin(ResolvableProfile profile) {
        try {
            PlayerSkinRenderCache cache = MC.playerSkinRenderCache();
            // 优先尝试按 profile 精确清除
            cache.getClass().getMethod("clear", ResolvableProfile.class).invoke(cache, profile);
        } catch (NoSuchMethodException e) {
            // 旧映射没有按 profile 清除的方法，退化为清空全部
            try {
                PlayerSkinRenderCache cache = MC.playerSkinRenderCache();
                cache.getClass().getMethod("clear").invoke(cache);
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
    }
}
