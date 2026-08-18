package com.flower.mitayclient.GUI.screen;

import com.flower.Mitayclient;
import com.flower.mitayclient.GUI.HUD.ToolBarHudRenderer;
import com.flower.mitayclient.GUI.Widget.ComboBoxWidget;
import com.flower.mitayclient.GUI.Widget.MultiColumnTextFieldWidget;
import com.flower.mitayclient.GUI.buttons.Accessibility.AccessibilityPressableWidget;
import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
import com.flower.mitayclient.GUI.buttons.PlaceList.Small.SmallButton;
import com.flower.mitayclient.GUI.buttons.Switch.SwitchButton;
import com.flower.mitayclient.GUI.screen.SideBarUtil.SideType;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import net.minecraft.client.GraphicsPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

import java.awt.event.MouseEvent;
import java.util.*;

public class TeleportScreen extends SideBarScreen
{
    private static final Identifier TICK = ModIdentifier.get("textures/gui/widget/accessibility_button/tick.png");
    public TeleportScreen()
    {
        super(Component.literal("TeleportScreen"), Type.CUSTOM);
    }
    Collection<PlayerInfo> players;
    private int panelX, panelY;
    private static final int PANEL_WIDTH = 382;
    private static final int PANEL_HEIGHT = 292;
    Coordinate coordinate = new Coordinate(999239, 239999, 999239);
    int x,y,z;
    String world;

    List<PlaceListButton> playerButtons = new ArrayList<>();


    SideType toPlayerSide;
    SideType toLocationSide;

    public void initializeMaps()
    {
        //SideType
        toPlayerSide = SideType.withPlaceButtons(AccessibilityPressableWidget.PLAYER_LIST, "玩家", playerButtons);
        toLocationSide = SideType.withCustomStyle(AccessibilityPressableWidget.PLACE, "以坐标");

        sideTypeMap.put("传送至玩家", toPlayerSide);
        sideTypeMap.put("以坐标传送", toLocationSide);
    }

    public void initializePlayerButtons(Collection<PlayerInfo> players)
    {
        String currentPlayerName = this.minecraft.getCameraEntity().getScoreboardName();
        playerButtons.clear();
        for (PlayerInfo player : players)
        {
            Component displayName = player.getTabListDisplayName();
            boolean isDisplayNameNull = displayName == null;
            boolean isCurrentPlayer = currentPlayerName.equals(player.getProfile().name());
            playerButtons.add(createContentButton(
                    isDisplayNameNull ?  Component.literal(player.getProfile().name()) : displayName,
                    player.getSkin(),
                    isCurrentPlayer ? "home" : "tpw " + player.getProfile().name()));
        }
    }

    SmallButton confirmButton;

    MultiColumnTextFieldWidget field = new MultiColumnTextFieldWidget(10, 40, 150, 50, Component.literal("坐标"));
    private ComboBoxWidget comboBox;

    //------------------------init--------------------------
    @Override
    protected void init()
    {
        players = minecraft.getConnection().getOnlinePlayers();
        initializePlayerButtons(players);
        initializeMaps();
        super.currentScreen = ScreenType.PLAYER; //这里要在NavigationScreen中添加枚举类型
        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;

        //MultiColumnTextField
        field.setLineCount(3);   // 固定3行

        field.setPlaceholder(0, "X");
        field.setPlaceholder(1, "Y");
        field.setPlaceholder(2, "Z");

        field.setPosition(panelX+155, panelY + 120);

        //ComboBox
        comboBox = new ComboBoxWidget(panelX+130, panelY + 40, 100, 20, Component.literal("选择"));
        comboBox.addItem("     主世界");
        comboBox.addItem("     地狱");
        comboBox.addItem("     末地");
        comboBox.addItem("     创造世界");
        comboBox.setOnSelectionChanged(index -> {
//            System.out.println("选中: " + comboBox.getSelectedItem());
        });
        addRenderableWidget(comboBox);

         confirmButton = SmallButton.builder(Component.literal("传送"), button -> {
                teleport(coordinate, world);
            }).iconIdentifier(TICK).dimensions(panelX+170, panelY + 120 + 70, 100,22)
            .build();

        super.init();
    }

    //---------------------------render------------------------
    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
        updateCoordinate();
        super.extractRenderState(context, mouseX, mouseY, a);
        if(super.scrollArea.children.contains(comboBox))
            renderCoordinateDesc(context);
        comboBox.renderExpanded(context, mouseX, mouseY);
    }

    public void updateCoordinate()
    {
        String selectedItem = comboBox.getSelectedItem();
        String world = null;
        switch (selectedItem.trim())
        {
            case "主世界" -> world = "overworld";
            case "地狱" -> world = "nether";
            case "末地" -> world = "end";
            case "创造世界" -> world = "creativeWorld";
        }
        this.world = world;

        x = field.getLineInt(0, 999239);
        y = field.getLineInt(1, 239999);
        z = field.getLineInt(2, 999239);
        this.coordinate.setCoordinate(x,y,z);
    }


    public void teleport(Coordinate coordinate, String world)
    {
        if(x == 999239 || y == 239999 || z == 999239)
        {
            return;
        }
        int x = coordinate.x;
        int y = coordinate.y;
        int z = coordinate.z;
        MitayUtils.sendChatCommand("tpplus " + world + " " + x + " " + y + " " + z);
//        System.out.println("tpplus " + world + " " + x + " " + y + " " + z);
        Minecraft.getInstance().setScreen(null);
    }

    public void renderCoordinateDesc(GuiGraphicsExtractor graphics)
    {
        graphics.blit(RenderPipelines.GUI_TEXTURED, Resource.PLACE_icon, panelX+130, panelY + 10, 0,0,24,24,24,24);
        ToolBarHudRenderer.drawScaledText(graphics, font, Component.literal("传送到..."), panelX+135 + 28, panelY+14, 2.0f, MitayUtils.getFontColor(), false);
    }

    @Override
    public void showContent(SideType type, Runnable runnable)
    {
        clearAllWidgets();
        //用于往scrollArea里添加组件，具体的文字、图标另写方法（如：renderCoordinateDesc()）。
        switch (type.typeName)
        {
            case "玩家" -> super.showContent(playerButtons, PlaceListButton.class);
            case "以坐标" -> {
                clearAllWidgets();
                super.scrollArea.children.add(field);
                super.scrollArea.children.add(comboBox);
                super.scrollArea.children.add(confirmButton);
            }
        }
    }

    //用于修复ComboBox的点击事件
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        // 优先处理展开列表的点击
        if (comboBox.isExpanded() && comboBox.mouseClickedExpanded(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        // 将原版 keyPressed 转换为 KeyEvent 调用（如果你的 Screen 仍然用旧参数）
        // 也可以直接重定向到 comboBox.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));

        if(keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
        {
            teleport(coordinate, this.world);
        }
        return super.keyPressed(event);
    }

    static class Coordinate
    {
        int x,y,z;
        public Coordinate(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void setCoordinate(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
