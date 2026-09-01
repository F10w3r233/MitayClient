package com.flower.mitayclient.GUI.screen;

import com.flower.mitayclient.GUI.HUD.ToolBarHudRenderer;
import com.flower.mitayclient.GUI.Widget.ComboBoxWidget;
import com.flower.mitayclient.GUI.Widget.MultiColumnTextFieldWidget;
import com.flower.mitayclient.GUI.buttons.Accessibility.AccessibilityButton;
import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
import com.flower.mitayclient.GUI.buttons.PlaceList.Small.SmallButton;
import com.flower.mitayclient.GUI.screen.PlaceListUtil.PlaceCache;
import com.flower.mitayclient.GUI.screen.PlaceListUtil.PlacesPayload;
import com.flower.mitayclient.GUI.screen.PlaceListUtil.RequestPlacesPayload;
import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import com.flower.mitayclient.GUI.screen.SideBarUtil.SideType;
import com.flower.mitayclient.util.Data.PlayerDataHandler;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static com.flower.mitayclient.GUI.buttons.Accessibility.AccessibilityPressableWidget.TICK;
import static com.flower.mitayclient.util.MitayUtils.*;


//做好空值处理

public class PlaceListScreen extends SideBarScreen
{
    @Override
    public boolean preeditUpdated(@Nullable PreeditEvent event) {
        // 先让 scrollArea 处理（它可能转发给子控件）
        if (scrollArea.preeditUpdated(event)) {
            return true;
        }
        // 再调用 super，以处理其他子控件（如 comboBox 等）
        return super.preeditUpdated(event);
    }
    Identifier SHARED_PLACE = ModIdentifier.get("textures/gui/screen/place_list/shared_place.png");
    Coordinate coordinate = new Coordinate(999239, 239999, 999239);

    public PlaceListScreen()
    {
        super(Component.literal("place list"), Type.NORMAL);
    }

    SideType overworldSide;
    SideType netherSide;
    SideType endSide;
    SideType creativeSide;
    SideType sharedPlaceSide;


    Map<String, SideType> sideTypeMap = new LinkedHashMap<>();
    Map<String, List<PlaceListButton>> subMenuButtonsMap = new LinkedHashMap<>();

    //SubMenuButtons
    List<String> mob_farm_outputs = List.of("rotten_flesh", "bone", "arrow", "gunpowder", "string", "slime_ball", "redstone", "spider_eye", "sugar", "stick", "glass_bottle", "glowstone_dust");
    List<PlaceListButton> mob_tower_subButtons = Arrays.asList(
            createContentButton(Resource.RESOURCE_text, "mob_resource", "overworld_resource", mob_farm_outputs,"tpplace mob_resource"),
            createContentButton(Resource.AFK_text, "mob_afk", "overworld_afk_bot01",null,"tpplace mob_afk"),
            createContentButton(Resource.BACK_text, "", () -> super.switchContent(overworldSide))
    );
    List<PlaceListButton> pig_man_subButtons = Arrays.asList(
            createContentButton(Resource.RESOURCE_text, "mob_resource", "", List.of("gold_nugget", "gold_ingot", "gold_block"), "tpplace pigman_resource"),
            createContentButton(Resource.AFK_text, "mob_afk", "tpplace pigman_afk"),
            createContentButton(Resource.BACK_text, "back", () -> super.switchContent(netherSide))
    );

    List<PlaceListButton> ghast_farm_subButtons = Arrays.asList(
            createContentButton(Resource.RESOURCE_text, "mob_resource", "overworld_resource", List.of("ghast_tear", "gunpowder"), "tpplace ghast_farm_resource"),
            createContentButton(Resource.AFK_text, "mob_afk", "nether_afk_bot03",null,"tpplace ghast_farm_afk"),
            createContentButton(Resource.BACK_text, "back", () -> super.switchContent(netherSide))
    );

    List<PlaceListButton> guardian_subButtons = Arrays.asList(
            createContentButton(Resource.RESOURCE_text,  "mob_resource", "nether_resource", List.of("prismarine_shard", "prismarine_crystals", "cod", "ink_sac"), "tpplace guardian_resource"),
            createContentButton(Resource.AFK_text, "mob_afk", "overworld_afk_bot02",null,"tpplace guardian_afk"),
            createContentButton(Resource.BACK_text, "back", () -> super.switchContent(overworldSide))
    );
    //SubMenuType


    //ContentButtons
    List<PlaceListButton> overworldButtons = Arrays.asList(
//            createContentButton(Resource.EXCHANGE_text, "exchange", "tpplace trade"),
            createContentButton(Resource.TOWN_text, "home", "tpplace base"),
            createContentButton(Resource.MOB_TOWER_text, "mob_main", () -> openSubMenu(mob_tower_subButtons)),
            createContentButton(Resource.IRON_text, "iron", "", List.of("iron_ingot", "poppy"), "tpplace iron"),
            createContentButton(Resource.FURNACE_text, "furnace", "tpplace furnace"),
            createContentButton(Resource.STONE_text, "stone", "", List.of("cobblestone"),"tpplace stone"),
            createContentButton(Resource.GUARDIAN_text, "guardian", "multiDimension",() -> openSubMenu(guardian_subButtons))
    );

    List<PlaceListButton> netherButtons = Arrays.asList(
            createContentButton(Resource.PIG_MAN_text, "pig_man", () -> openSubMenu(pig_man_subButtons)),
            createContentButton(Resource.WITHER_SKULL_text, "wither_skull", "",List.of("wither_skeleton_skull", "coal"),"tpplace wither_skull_farm"),
            createContentButton(Resource.GHAST_FARM_text, "ghast_farm", "multiDimension", () -> openSubMenu(ghast_farm_subButtons))
    );
    List<PlaceListButton> endButtons = Arrays.asList(
            createContentButton(Resource.PORTAL_text, "end_portal", () -> Minecraft.getInstance().setScreen(null)),
            createContentButton(Resource.MAINLAND_text, "end_mainland", "tpplace end_mainland"),
            createContentButton(Resource.ENDER_MAN_text, "ender_man_farm", "", List.of("ender_pearl"),"tpplace enderman_farm")
    );
    List<PlaceListButton> creativeButtons = Arrays.asList(
            createContentButton(Resource.CREATIVE, "creative", "tpplace creativeWorld")
    );

    List<PlaceListButton> sharedPlaceButtons = new ArrayList<>();


    //初始化this.sideTypeMap
    public void initializeMaps()
    {
        //SideType
        overworldSide = SideType.withPlaceButtons(Resource.OVERWORLD_icon, "主世界", overworldButtons);
        netherSide = SideType.withPlaceButtons(Resource.NETHER_icon, "地狱", netherButtons);
        endSide = SideType.withPlaceButtons(Resource.END_icon, "末地", endButtons);
        creativeSide = SideType.withPlaceButtons(Resource.CREATIVE_WORLD_icon, "创造世界", creativeButtons);
        sharedPlaceSide = SideType.withPlaceButtons(SHARED_PLACE, "玩家分享地点", sharedPlaceButtons);

        sideTypeMap.put("主世界", overworldSide);
        sideTypeMap.put("地狱", netherSide);
        sideTypeMap.put("末地", endSide);
        sideTypeMap.put("创造世界", creativeSide);
        sideTypeMap.put("玩家分享地点", sharedPlaceSide);

        subMenuButtonsMap.put("沼泽刷怪塔-二级菜单", mob_tower_subButtons);
        subMenuButtonsMap.put("猪人塔-二级菜单", pig_man_subButtons);

    }

    public int getWorldInComboBox(String currentWorld)
    {
        return switch (currentWorld)
        {
            case "overworld" -> 0;
            case "nether" -> 1;
            case "end" -> 2;
            case "creativeWorld" -> 3;
            default -> 0;
        };
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
//        String[] playerWorld = PlayerDataHandler.playerAndWorldNames;
        String currentWorld = getWorldName(Minecraft.getInstance().level.dimensionTypeRegistration().getRegisteredName().replace("minecraft:", ""));
//        String currentWorld = getCurrentWorld(playerWorld, getCameraPlayer().getName().getString());
        comboBox.setSelectedIndex(getWorldInComboBox(currentWorld));
        updateCoordinate();
        super.extractRenderState(context, mouseX, mouseY, a);
        if(super.scrollArea.children.contains(coordField))
        {
            if(super.children().contains(addButton))
            {
                super.removeWidget(addButton);
            }
            renderAddLocation(context);
        }

        if(shouldDrawWarning)
            drawWarning(context, warningText);

        comboBox.renderExpanded(context, mouseX, mouseY);
    }

    public void renderAddLocation(GuiGraphicsExtractor context)
    {
        context.blit(RenderPipelines.GUI_TEXTURED, Resource.PLACE_icon, panelX+130, panelY + 10, 0,0,24,24,24,24);
        ToolBarHudRenderer.drawScaledText(context, font, Component.literal("添加一个地点..."), panelX+135 + 28, panelY+14, 2.0f, MitayUtils.getFontColor(), false);
    }


    boolean shouldDrawAddLocation = false;
    MultiColumnTextFieldWidget descField = new MultiColumnTextFieldWidget(10, 40, 150, 33, Component.literal("描述"));
    MultiColumnTextFieldWidget coordField = new MultiColumnTextFieldWidget(10, 40, 150, 50, Component.literal("坐标"));
    private ComboBoxWidget comboBox;
    SmallButton confirmButton;
    AccessibilityButton addButton = AccessibilityButton.builder(Component.literal("添加一个地点"),
            button -> {
        for (PlaceListButton sharedPlaceButton : sharedPlaceButtons)
        {
            super.scrollArea.children.remove(sharedPlaceButton);
        }
        shouldDrawAddLocation = true;

        //添加comboBox等组件
        super.scrollArea.children.add(comboBox);
        super.scrollArea.children.add(descField);
        super.scrollArea.children.add(coordField);
        super.scrollArea.children.add(confirmButton);
    }).type("add").dimensions(this.panelX + 318, this.panelY + 230, 30, 30).build();

    @Override
    public <T extends AbstractWidget> void showContent(List<T> buttons, Class<T> type)
    {
        if (currentSideType.typeName.equals("玩家分享地点"))
        {
            clearAllWidgets();
            super.addRenderableWidget(addButton);
            sharedPlaceButtons.clear();
            for (Map.Entry<Integer, PlacesPayload.PlaceInfo> entry : PlaceCache.getPlaces().entrySet())
            {
                int id = entry.getKey();
                PlacesPayload.PlaceInfo info = entry.getValue();
                String world = info.world();
                String uploader = info.uploader();
                String desc = info.desc();
                String coordinate = format(info.x()) + "_" +format(info.y()) + "_" + format(info.z());
                sharedPlaceButtons.add(PlaceListButton.builder(Component.empty(), button -> {
                    sendChatCommand("tpplus " + world + " " + info.x() + " " + info.y() + " " + info.z());
                    Minecraft.getInstance().setScreen(null);
                }).type("shared_place").profile(new PlayerProfile(desc, world + "/" + coordinate + "/" + uploader)).dimensions(0,0,210,30).build());

//                System.out.println("地点 " + id + ": " + info.desc() + " (" + info.world() + ")");
            }



            super.showContent(buttons, type);

        }else
        {
            super.removeWidget(addButton);
            super.showContent(buttons, type);
        }
    }

    int x,y,z;
    String world;
    String desc;

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
        desc = descField.getLineText(0, "DESC");
        x = coordField.getLineInt(0, 999239);
        y = coordField.getLineInt(1, 239999);
        z = coordField.getLineInt(2, 999239);
        this.coordinate.setCoordinate(x,y,z);
    }


    @Override
    public void init()
    {

        //请求共享列表
        ClientPlayNetworking.send(new RequestPlacesPayload());
        initializeMaps();
        //初始化父类sideTypeMap
        super.sideTypeMap = this.sideTypeMap;
        //初始化父类二级菜单Map
        super.subMenuButtonsMap = this.subMenuButtonsMap;
//        super.currentScreen = NavigationScreen.ScreenType.; //这里要在NavigationScreen中添加枚举类型
        super.currentScreen = ScreenType.PLACE;



        //MultiColumnTextField
        coordField.setLineCount(3);   // 固定3行

        descField.setPlaceholder(0, "地点名");

        coordField.setPlaceholder(0, "X");
        coordField.setPlaceholder(1, "Y");
        coordField.setPlaceholder(2, "Z");

        x = (int) Double.parseDouble(format(Minecraft.getInstance().player.getX()));
        y = (int) Double.parseDouble(format(Minecraft.getInstance().player.getY()));
        z = (int) Double.parseDouble(format(Minecraft.getInstance().player.getZ()));

        coordField.setLineText(0, String.valueOf(x));
        coordField.setLineText(1, String.valueOf(y));
        coordField.setLineText(2, String.valueOf(z));



        descField.setPosition(panelX+155, panelY + 120);
        coordField.setPosition(panelX+155, panelY + 145);

        //ComboBox
        comboBox = new ComboBoxWidget(panelX+130, panelY + 40, 100, 20, Component.literal("选择"));
        comboBox.addItem("     主世界");
        comboBox.addItem("     地狱");
        comboBox.addItem("     末地");
        comboBox.addItem("     创造世界");
        comboBox.setOnSelectionChanged(index -> {
//            System.out.println("选中: " + comboBox.getSelectedItem());
        });
        super.addRenderableWidget(comboBox);

        confirmButton = SmallButton.builder(Component.literal("添加"), button ->
        {
            addPlace(coordinate, world, desc);
        }).iconIdentifier(TICK).dimensions(panelX + 170, panelY + 120 + 75, 100, 22).build();





        super.init();
    }

    boolean shouldDrawWarning = false;
    String warningText = "";
    public void addPlace(Coordinate coordinate, String world, String desc)
    {
        int x = coordinate.x;
        int y = coordinate.y;
        int z = coordinate.z;

        if (x == 999239 || y == 239999 || z == 999239)
        {
            shouldDrawWarning = true;
//            System.out.println("坐标值不能为空");
            warningText = "坐标值不能为空";
        }
        if (desc.equals("DESC"))
        {
            shouldDrawWarning = true;
//            System.out.println("地点名不能为空");
            warningText = "地点名不能为空";
        }

        if (!(x == 999239 || y == 239999 || z == 999239) && !desc.equals("DESC"))
        {
            shouldDrawWarning = false;
            warningText = "";
        }

        if(!shouldDrawWarning)
        {
            MitayUtils.sendChatCommand("placelist add " + world + " " + x + " " + y + " " + z + " " + desc);
            Minecraft.getInstance().setScreen(null);
        }
    }

    public void drawWarning(GuiGraphicsExtractor context, String warning)
    {
        context.text(font, warning, panelX+155, panelY + 140, CommonColors.RED);
    }

    class Coordinate
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
            addPlace(coordinate, world, desc);
        }

        if(keyCode == GLFW.GLFW_KEY_E)
        {
            if (coordField.isFocused() || descField.isFocused())
                return false;
        }
        if (scrollArea.keyPressed(event)) {
            return true;
        }
        return super.keyPressed(event);
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
}
