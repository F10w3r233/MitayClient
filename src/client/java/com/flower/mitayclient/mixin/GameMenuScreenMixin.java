package com.flower.mitayclient.mixin;


import com.flower.mitayclient.GUI.buttons.Menu.MenuButton;
import com.flower.mitayclient.util.Resource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class GameMenuScreenMixin extends Screen
{
    private static final Identifier MITAY_UNIVERSE = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/icon4x.png");

    protected GameMenuScreenMixin(Component title)
    {
        super(title);
    }

    Minecraft minecraft;
    @Inject(at = @At("HEAD"), method = "createPauseMenu", cancellable = true)
    public void initWidgets(CallbackInfo ci)
    {
        ci.cancel();
        minecraft = Minecraft.getInstance();
        int openToLanHeight = 0;
        int quitHeight = 0;
        if(this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished())
        {
            openToLanHeight = this.minecraft.getWindow().getGuiScaledHeight()/2-40;
            quitHeight = this.minecraft.getWindow().getGuiScaledHeight()/2;
        }else
        {
            openToLanHeight = -10000;
            quitHeight = this.minecraft.getWindow().getGuiScaledHeight()/2-40;
        }
        this.addRenderableWidget(
                MenuButton.builder(Resource.STATISTIC, button ->
                                this.minecraft.setScreen(new StatsScreen(this, this.minecraft.player.getStats())))
                        .dimensions(80, this.minecraft.getWindow().getGuiScaledHeight()/2-160,150,20)
                        .build()
        );

        this.addRenderableWidget(                                                                                    //自定义按钮
                MenuButton.builder(Resource.BACK_text, button ->
                {
                    this.minecraft.setScreen(null);
                    this.minecraft.mouseHandler.grabMouse();
                })
                        .dimensions(80, this.minecraft.getWindow().getGuiScaledHeight()/2-120, 150, 20)
                        .build()
        );

        this.addRenderableWidget(                                                                                    //自定义按钮
                MenuButton.builder(Resource.OPTIONS, button ->
                                this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options, true)))
                        .dimensions(80, this.minecraft.getWindow().getGuiScaledHeight()/2-80, 150, 20)
                        .build()
        );


        this.addRenderableWidget(
                MenuButton.builder(Resource.OPEN_TO_LAN,button ->
                {
                    if (this.minecraft.hasSingleplayerServer() && !this.minecraft.getSingleplayerServer().isPublished())
                    {
                        this.minecraft.setScreen(new ShareToLanScreen(this));
                    }
                })
                        .dimensions(80, openToLanHeight,150,20)
                        .build());

        this.addRenderableWidget(                                                                                    //自定义按钮
                MenuButton.builder(Resource.QUIT, button ->
                        {
                            boolean bl = this.minecraft.isLocalServer();
                            if(bl)
                            {
                                this.minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
                                minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE);
                                minecraft.setScreen(new TitleScreen());
                            }else
                            {
                                minecraft.disconnectFromWorld(ClientLevel.DEFAULT_QUIT_MESSAGE);
                                minecraft.setScreen(new TitleScreen());

                            }
                        })
                        .dimensions(80, quitHeight, 150, 20)
                        .build());
    }

    @Inject(at = @At("RETURN"), method = "extractBackground",cancellable = true)
    public void renderMitayLogo(final GuiGraphicsExtractor context, final int mouseX, final int mouseY, final float a, CallbackInfo ci)
    {
        ci.cancel();
        //
        context.blit(RenderPipelines.GUI_TEXTURED, MITAY_UNIVERSE, this.minecraft.getWindow().getGuiScaledWidth()-420, 20, 0,0,420,202,420,202);
    }
}
