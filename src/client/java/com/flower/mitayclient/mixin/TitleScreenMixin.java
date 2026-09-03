package com.flower.mitayclient.mixin;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.buttons.Accessibility.AccessibilityButton;
import com.flower.mitayclient.GUI.buttons.Menu.MenuButton;
import com.flower.mitayclient.GUI.screen.AboutScreen;
import com.flower.mitayclient.GUI.screen.WallpaperScreen;
import com.flower.mitayclient.util.Data.PlayerDataHandler;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.CommonColors;

import static com.flower.mitayclient.GUI.HUD.ToolBarHudRenderer.drawScaledText;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen
{
    @Unique
    boolean isReleaseVersion = true;
    @Unique
    String version = "4.6.3.1";
//    static String ip = "g.a1.ocent.net:10130";
    @Unique
    final String IP = isReleaseVersion ? "g.a1.ocent.net:10130" : "127.0.0.1";
    @Unique
    private RenderTarget offscreenTarget;
//    final String IP = "127.0.0.1";
    Identifier statusIconTexture = ModIdentifier.get("textures/gui/hud/player_list/network/pinging.png");
    List<Component> playerList = null;
    Component serverStatusText = Component.literal("");

    Identifier PING_5 = ModIdentifier.get("textures/gui/hud/player_list/network/ping_5.png");
    Identifier PING_4 = ModIdentifier.get("textures/gui/hud/player_list/network/ping_4.png");
    Identifier PING_3 = ModIdentifier.get( "textures/gui/hud/player_list/network/ping_3.png");
    Identifier PING_2 = ModIdentifier.get( "textures/gui/hud/player_list/network/ping_2.png");
    Identifier PING_1= ModIdentifier.get("textures/gui/hud/player_list/network/ping_1.png");
    Identifier PING_UNKNOWN= ModIdentifier.get("textures/gui/hud/player_list/network/ping_unknown.png");

    ServerData server = new ServerData("Mitay", IP, ServerData.Type.LAN);

    public void connect()
    {
        this.connect(server);
    }

    public void connect(ServerData entry)
    {
        ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(entry.ip), entry, false, null);
    }
    Minecraft minecraft = Minecraft.getInstance();

    private static final Identifier MITAY_LOGO = Identifier.fromNamespaceAndPath("mitayclient", "textures/gui/logo_4.png");

    protected TitleScreenMixin(Component title)
    {
        super(title);
    }


    MenuButton connectButton = MenuButton.builder(Component.literal(""), button ->                                                        //直连服务器按钮
    {
        this.connect();
    }).dimensions(50, 180, 162, 27).build();







    @Inject(at = @At("HEAD"), method = "createNormalMenuOptions", cancellable = true)
    public int addNormalWidgets(int topPos, final int spacing, CallbackInfoReturnable<Integer> cir)
    {
        cir.cancel();
        return topPos;
    }

    @Inject(at = @At("HEAD"), method = "init", cancellable = true)
    public void addNormalWidgets(CallbackInfo ci)
    {
        ci.cancel();
        this.addRenderableWidget(MenuButton.builder(Resource.SINGLE, button ->                                                        //直连服务器按钮
        {
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }).dimensions(50, 140, 162, 27).build());



        this.addRenderableWidget(connectButton);

        this.addRenderableWidget(                                                                                    //自定义按钮
                MenuButton.builder(Resource.SETTINGS, button ->
                                Minecraft.getInstance().setScreen(new OptionsScreen(this, Minecraft.getInstance().options, false))


                        ).dimensions(50, 220, 162, 27)
                        .build());

        this.addRenderableWidget(                                                                                    //自定义按钮
                MenuButton.builder(Resource.QUIT, button ->
                                this.minecraft.stop()


                        ).dimensions(50, 260, 162, 27)
                        .build());




        this.addRenderableWidget(                                                                                    //自定义按钮
                AccessibilityButton.builder(Component.translatable(""), button ->
                                minecraft.setScreen(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()))
                        ).dimensions(minecraft.getWindow().getGuiScaledWidth()-19, minecraft.getWindow().getGuiScaledHeight()-19,15,15)
                        .type("language")
                        .build());




        //accessibility 按钮
        this.addRenderableWidget(                                                                                    //自定义按钮
                AccessibilityButton.builder(Component.translatable(""), button ->
                                    minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options))
                        ).dimensions(minecraft.getWindow().getGuiScaledWidth()-41, minecraft.getWindow().getGuiScaledHeight()-18,15,15)
                        .type("accessibility")
                        .build());

        this.addRenderableWidget(                                                                                    //自定义按钮
                AccessibilityButton.builder(Component.translatable(""), button ->
                                minecraft.setScreen(new WallpaperScreen())
                        ).dimensions(minecraft.getWindow().getGuiScaledWidth()-62, minecraft.getWindow().getGuiScaledHeight()-19,15,15)
                        .type("wallpaper")
                        .build());

        this.addRenderableWidget(                                                                                    //自定义按钮
                AccessibilityButton.builder(Component.translatable(""), button ->
                                minecraft.setScreen(new AboutScreen(Component.literal("")))
                        ).dimensions(minecraft.getWindow().getGuiScaledWidth()-83, minecraft.getWindow().getGuiScaledHeight()-18,15,15)
                        .type("about")
                        .build());

        //刷新
        this.addRenderableWidget(                                                                                    //自定义按钮
                AccessibilityButton.builder(Component.translatable(""), button ->
                                minecraft.setScreen(new TitleScreen())
                        ).dimensions(220, 184,15,15)
                        .type("refresh")
                        .build());
    }


    ServerStatusPinger serverListPinger = new ServerStatusPinger();
    private ThreadPoolExecutor serverPingerThreadPool;
    @Unique
    private boolean isPingStarted = false;

    @Inject(at = @At("RETURN"), method = "init")
    public void onInit(CallbackInfo ci)
    {
        if (serverPingerThreadPool == null) {
            serverPingerThreadPool = new ScheduledThreadPoolExecutor(5,
                    (new ThreadFactoryBuilder())
                            .setNameFormat("Server Pinger #%d")
                            .setDaemon(true)
                            .setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LogUtils.getLogger()))
                            .build()
            );
        }

    }

    @Inject(at = @At("HEAD"), method = "extractRenderState", cancellable = true)
    public void render(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float a, CallbackInfo ci)
    {
        ci.cancel();
        updatePlayerList();
        int width = context.guiWidth();
        int height = context.guiHeight();


        context.blit(RenderPipelines.GUI_TEXTURED, WallpaperScreen.getCurrentWallpaper(Mitayclient.getConfig().getBG_Index()), 0,0,0,0, width, height, width, height);
//        context.blit(RenderPipelines.GUI_TEXTURED,MITAY_LOGO, 30,80,0,0,215,46,215,46);
        context.blit(RenderPipelines.GUI_TEXTURED,MITAY_LOGO, 24,40,0,0,1047/5,534/5,1047/5,534/5);
        context.text(font, "Minecraft 26.1 | §6Mitay Client " + version,2, this.height - 10,CommonColors.WHITE);

        // 此时 serverPingerThreadPool 应非空，因为 init 已执行
        // 但仍建议添加防御性检查
        if (serverPingerThreadPool == null)
        {
            // 如果因为某些原因仍未初始化，则在此处创建（保险）
            serverPingerThreadPool = new ScheduledThreadPoolExecutor(5,
                    (new ThreadFactoryBuilder())
                            .setNameFormat("Server Pinger #%d")
                            .setDaemon(true)
                            .setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LogUtils.getLogger()))
                            .build()
            );
        }
        //初始化serverInfo
        if(server.state() == ServerData.State.INITIAL && !isPingStarted)
        {
            isPingStarted = true;
            server.setState(ServerData.State.PINGING);
            server.motd = CommonComponents.EMPTY;
            server.status = CommonComponents.EMPTY;

            serverPingerThreadPool.submit(() -> {
                try {
                    serverListPinger.pingServer(
                            server,
                            () -> {
                                // 这里是 saver 回调，用于保存服务器 Favicon 等数据
                                // 如果不需要保存，可以留空或实现保存逻辑
                            },
                            () -> {
                                server.setState(ServerData.State.SUCCESSFUL);
                                this.minecraft.execute(this::update);
                                PlayerDataHandler.ping = (int) server.ping;
                            },
                            EventLoopGroupHolder.remote(true)  // 指定网络后端为 TCP
                    );
                } catch (UnknownHostException var2) {
                    server.setState(ServerData.State.UNREACHABLE);
                    this.minecraft.execute(this::update);
                    PlayerDataHandler.ping = (int) server.ping;
                } catch (Exception var3) {
                    server.setState(ServerData.State.UNREACHABLE);
                    this.minecraft.execute(this::update);
                    PlayerDataHandler.ping = (int) server.ping;
                }
            });
        }

        super.extractRenderState(context, mouseX, mouseY, a);

        context.blit(RenderPipelines.GUI_TEXTURED, statusIconTexture, 64,187,0,0,12,10,12,10);

        String playerAmount = "";

        int color = CommonColors.WHITE;
//        if(Mitayclient.getConfig().isDarkShown())
//        {
//            color = CommonColors.WHITE;
//        }else color = CommonColors.BLACK;


        String online_text_pt1 = Resource.CONNECT.getString() + " | ";
        context.text(font, online_text_pt1, 86, 189, color,true);




        int pt1_length = font.width(online_text_pt1);
        String online_text_pt2 = "";
        if(server.state() == ServerData.State.UNREACHABLE || server.state() == ServerData.State.INCOMPATIBLE)
        {
            context.text(font, serverStatusText, 86+pt1_length, 189,CommonColors.RED,true);
        }else
        {
            if(server.state() == ServerData.State.SUCCESSFUL)
            {
                playerAmount = server.status.getString().replace("/20", "");
            }else if(server.state() == ServerData.State.PINGING || server.state() == ServerData.State.INITIAL)
            {
                playerAmount = Resource.PINGING.getString();
            }
            context.text(font, Resource.ONLINE.getString() + playerAmount, 86+pt1_length, 189, CommonColors.GREEN,true);
        }

        if(!isReleaseVersion)
        {
            drawScaledText(context, font, Component.literal("测试版本"), 0,0,4.0f, CommonColors.WHITE, true);
            drawScaledText(context, font, Component.literal("如果你看到这行字请联系服主"), 0,40,2.0f, CommonColors.WHITE, true);
        }
    }


    private void update()
    {
        switch (server.state())
        {
            case INITIAL:
            case PINGING:
                this.statusIconTexture = PING_1;
                break;
            case INCOMPATIBLE, UNREACHABLE:
                this.statusIconTexture = PING_UNKNOWN;
                serverStatusText = Resource.CLOSED;
                break;
            case SUCCESSFUL:
                if (this.server.ping < 150L)
                {
                    statusIconTexture = PING_5;
                } else if (this.server.ping < 300L)
                {
                    this.statusIconTexture = PING_4;
                } else if (this.server.ping < 600L)
                {
                    this.statusIconTexture = PING_3;
                } else if (this.server.ping < 1000L)
                {
                    this.statusIconTexture = PING_2;
                } else
                {
                    this.statusIconTexture = PING_1;
                }
                playerList = server.playerList;
        }
    }

    public void updatePlayerList()
    {
        this.playerList = server.playerList;
        StringBuilder nameList = new StringBuilder();
        int index = 0;
        for (Component name : playerList)
        {
            index++;
            String nameStr = name.getString().replace("literal{", "").replace("}", "");
            String nextLine = index == playerList.size() ? "" : "\n";
            nameList.append(nameStr).append(nextLine);
        }
        connectButton.setTooltip(Tooltip.create(Component.literal(nameList.toString())));
    }

    @Override
    public boolean keyPressed(KeyEvent input)
    {
        if(input.input() == GLFW.GLFW_KEY_F5)
            Minecraft.getInstance().setScreen(new TitleScreen());
        return super.keyPressed(input);
    }

    //清理线程池
    @Override
    public void removed() {
        super.removed();
        if (serverPingerThreadPool != null) {
            serverPingerThreadPool.shutdownNow(); // 立即中断，释放资源
        }
    }
}
