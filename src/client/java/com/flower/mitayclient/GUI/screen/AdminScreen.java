//package com.flower.mitayclient.GUI.screen;
//
//import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
//import com.flower.mitayclient.GUI.buttons.PlaceList.Small.SmallButton;
//import com.flower.mitayclient.util.ModIdentifier;
//import com.google.common.collect.Lists;
//import org.lwjgl.glfw.GLFW;
//
//import java.util.Iterator;
//import java.util.List;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphicsExtractor;
//import net.minecraft.client.gui.components.Renderable;
//import net.minecraft.client.gui.components.events.GuiEventListener;
//import net.minecraft.client.gui.narration.NarratableEntry;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.input.KeyEvent;
//import net.minecraft.client.renderer.RenderPipelines;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.Identifier;
//
//public class AdminScreen extends Screen
//{
//    private final List<Renderable> drawables = Lists.newArrayList();
//    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addElement(T drawableElement)
//    {
//        this.drawables.add((Renderable)drawableElement);
//        return this.addWidget(drawableElement);
//    }
//
//    protected void remove(Renderable element)
//    {
//        if(renderables.contains(element))
//            this.renderables.remove(element);
//    }
//
//    private static final Identifier MATRIX_LARGE = ModIdentifier.get("textures/gui/sprites/screen/matrix_large.png");
//    private static final Identifier MATRIX_SIDE = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/sprites/screen/matrix_side.png");
//    private static final Identifier SELECTED = Identifier.fromNamespaceAndPath("mitayclient","textures/gui/sprites/screen/selected.png");
//    public AdminScreen()
//    {
//        super(Component.literal("Admin"));
//    }
//
//    PlaceListButton summon_iron_farm_npc_button = PlaceListButton.builder(Component.literal("召唤刷铁机假人"), button ->
//    {
//        PlaceListScreen.sendChatCommand("npc spawn 5");
//    }).icon("iron")
//            .dimensions((Minecraft.getInstance().getWindow().getGuiScaledWidth()-382)/2+7+115+5+2,(Minecraft.getInstance().getWindow().getGuiScaledHeight()-292)/2+10, 105, 30)
//            .build();
//
//
//
//    @Override
//    public void render(GuiGraphics context, int mouseX, int mouseY, float delta)
//    {
//        int scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
//        int scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
//        context.blit(RenderPipelines.GUI_TEXTURED, MATRIX_LARGE, (scaledWidth-382)/2-14,(scaledHeight-292)/2-14,0,0,382,292,382,292);
//        context.blit(RenderPipelines.GUI_TEXTURED,MATRIX_SIDE, (scaledWidth-382)/2,(scaledHeight-292)/2,0,0,115,263,115,263);
//
//        Iterator elements = renderables.iterator();
//        while(elements.hasNext()) {
//            Renderable drawable = (Renderable)elements.next();
//            drawable.render(context, mouseX, mouseY, delta);
//        }
//    }
//
//    @Override
//    protected void init()
//    {
//        int scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
//        int scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
//        addElement(SmallButton.builder(Component.literal("假人"), button ->
//        {
//            if(!renderables.contains(summon_iron_farm_npc_button))
//            {
//                addElement(summon_iron_farm_npc_button);
//            }
//        }).dimensions((scaledWidth-382)/2+7, (scaledHeight-292)/2+25, 100, 22).icon("npc").build());
//
//        addElement(SmallButton.builder(Component.literal("管理员面板"), button ->
//        {
//            remove((Renderable) summon_iron_farm_npc_button);
//        }).dimensions((scaledWidth-382)/2+7, (scaledHeight-292)/2+25+22, 100, 22).icon("").build());
//        super.init();
//    }
//
//    @Override
//    public boolean keyPressed(KeyEvent keyCode)
//    {
//        if(keyCode.input() == GLFW.GLFW_KEY_E)
//        {
//            Minecraft.getInstance().player.closeContainer();
//            return true;
//        }
//        return super.keyPressed(keyCode);
//    }
//}
