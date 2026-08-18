package com.flower.mitayclient.util.Skin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.joml.Matrix3x2fStack;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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

//    /**
//     * 在 GUI 上渲染指定玩家的头部皮肤。
//     * 如果皮肤已经下载并缓存，直接使用；否则先显示默认皮肤，并触发异步下载。
//     *
//     * @param graphics GuiGraphicsExtractor（渲染上下文）
//     * @param uuid     玩家 UUID
//     * @param x        左上角 X 坐标
//     * @param y        左上角 Y 坐标
//     * @param size     渲染尺寸（像素，建议 8 的倍数）
//     */
//    public static void renderHead(GuiGraphicsExtractor graphics, UUID uuid, int x, int y, int size)
//    {
//        ResolvableProfile profile = ResolvableProfile.createUnresolved(uuid);
//        // createLookup 返回一个 Supplier，每次调用都会返回当前可用皮肤（默认或已加载）
//        Supplier<PlayerSkinRenderCache.RenderInfo> skinSupplier = MC.playerSkinRenderCache().createLookup(profile);
//        PlayerSkin skin = skinSupplier.get().playerSkin();
//        // 提取并绘制头部区域（包含帽子层）
//        PlayerFaceExtractor.extractRenderState(graphics, skin, x, y, size);
//    }

    public static void renderHead(GuiGraphicsExtractor graphics, String name, int x, int y, int size)
    {
        renderHead(graphics, name, x, y, size, 0);
    }

    public static void renderHead(GuiGraphicsExtractor graphics, String name, int x, int y, int size, int color)
    {
        ResolvableProfile profile = ResolvableProfile.createUnresolved(name);
        // 获取已完成的查找结果，不触发新的异步任务，不阻塞
        Optional<PlayerSkinRenderCache.RenderInfo> opt =
                MC.playerSkinRenderCache().lookup(profile).getNow(Optional.empty());

        if (opt.isPresent())
        {
            PlayerSkin skin = opt.get().playerSkin();
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

    /**
     * 强制预加载皮肤（同步等待完成），保证后续渲染直接使用真实皮肤。
     * 建议在玩家加入游戏、或打开包含头像的界面时调用一次。
     */
    public static CompletableFuture<Void> preloadSkinAsync(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            ResolvableProfile profile = ResolvableProfile.createUnresolved(uuid);
            // 使用 join 但运行在异步线程池
            MC.playerSkinRenderCache().lookup(profile).join();
        });
    }

    /**
     * 检查皮肤是否已成功加载到内存缓存（仅表示当前会话中已加载）。
     * 注意：即使返回 false，磁盘上也可能已有文件（但未加载到缓存）。
     */
    public static boolean isSkinCachedInMemory(UUID uuid) {
        ResolvableProfile profile = ResolvableProfile.createUnresolved(uuid);
        Optional<PlayerSkinRenderCache.RenderInfo> current = MC.playerSkinRenderCache().lookup(profile).getNow(Optional.empty());
        return current.isPresent();
    }

    public static void renderHeadWithHat(GuiGraphicsExtractor graphics, UUID uuid, int x, int y, int size) {
        Identifier skin = getSkinTexture(uuid);

        // 绘制脸部
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 8, 8, size, size, 8, 8, 64, 64, -1);
        // 绘制帽子层 (固定从 40,8 取)
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 40, 8, size, size, 8, 8, 64, 64, -1);
    }

    public static void renderHeadWithHat(GuiGraphicsExtractor graphics, String name, int x, int y, int size) {
        Identifier skin = getSkinTexture(name);

        // 绘制脸部
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 8, 8, size, size, 8, 8, 64, 64, -1);
        // 绘制帽子层 (固定从 40,8 取)
        graphics.blit(RenderPipelines.GUI_TEXTURED, skin, x, y, 40, 8, size, size, 8, 8, 64, 64, -1);
    }

    public static void renderHeadWith3D(GuiGraphicsExtractor graphics, String name, int x, int y, int size, float threeDeeNess)
    {
        Identifier skin = getSkinTexture(name);
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


}
