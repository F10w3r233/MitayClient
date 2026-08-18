package com.flower.mitayclient.GUI.screen;


import com.flower.mitayclient.GUI.screen.ProfileUtil.LeaderBoard.RequestLeaderboardPayload;
import com.flower.mitayclient.util.ChatHistory.*;
import com.flower.mitayclient.util.Resource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;


import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static com.flower.mitayclient.util.MitayUtils.*;

public class ChatHistoryScreen extends NavigationScreen
{
    private static final Identifier MATRIX_LARGE_DARK = Identifier.fromNamespaceAndPath("mitayclient", "textures/gui/sprites/screen/matrix_large_dark.png");
    public ChatHistoryScreen()
    {
        super(Component.literal("ChatHistory"));
    }
    Map<Integer, Component> chatLinesMap = new LinkedHashMap<>();
    public static List<ClickCoordinate> clickEventList = new ArrayList<>();
    public static Map<Integer, ClickCoordinate> clickCoordinateMap = new HashMap<>();
    int currentIndex = 1;

    @Override
    protected void init()
    {
        ClientPlayNetworking.send(new RequestLeaderboardPayload());
        List<String> lines = TextSerializer.readLongLines(FabricLoader.getInstance().getConfigDir().resolve("chatHistory.txt").toString());

        int mapIndex = 0;
        String lastMesTime = null;

        for (String str : lines) {
            if (str.isEmpty()) continue;

            String[] parts = str.split("-content-");
            if (parts.length < 2) continue;

            String curMesTime = parts[0].trim();
            String messageContent = parts[1];

            if (!curMesTime.isEmpty()) {
                if (lastMesTime == null) {
                    // 第一条消息：显示它自己的时间
                    String label = TimeFormatter.getTime(curMesTime, null);
                    if (label != null) {
                        chatLinesMap.put(mapIndex++, Component.literal(label));
                    }
                } else {
                    // 后续消息：判断是否显示上一条消息的时间
                    String label = TimeFormatter.getTime(lastMesTime, curMesTime);
                    if (label != null) {
                        chatLinesMap.put(mapIndex++, Component.literal(label + "T!I!M!E!"));
                    }
                }
                lastMesTime = curMesTime; // 更新上一条时间
            }

            // 添加消息正文
            Component content = TextSerializer.deserialize(messageContent);
            chatLinesMap.put(mapIndex++, content);
        }

        if (currentIndex != chatLinesMap.size())
        {
            currentIndex = chatLinesMap.size();
        }

        super.currentScreen = ScreenType.CHAT_HISTORY;
        super.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
    {
//        context.drawTexture(RenderLayer::getGuiTextured, MATRIX_LARGE_DARK, (context.getScaledWindowWidth()-382)/2-14,(context.getScaledWindowHeight()-292)/2-14,0,0,382,292,382,292);
        int index = 0;
        for(int i = currentIndex; i > currentIndex - 30;i--)
        {
            index ++;
            if(i < 0)
            {
                break;
            }
            if(chatLinesMap.get(i-1) != null)
            {
                ChatRenderer.render(context, chatLinesMap.get(i-1), index, mouseX, mouseY);
            }
        }
        super.extractRenderState(context, mouseX, mouseY, a);
    }



    @Override
    public boolean keyPressed(KeyEvent keyInput)
    {
        if(keyInput.input() == GLFW.GLFW_KEY_E)
            Minecraft.getInstance().setScreen(null);
        return super.keyPressed(keyInput);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
    {
        clickCoordinateMap.clear();
//        System.out.println("列表已清除");

        boolean scrolled = verticalAmount != 0 || horizontalAmount != 0;

        if (scrolled)
        {
            if((currentIndex == chatLinesMap.size() &&  verticalAmount < 0) || (currentIndex == 30 && verticalAmount > 0))
                return false;
            int i = 1;
            if(hasShiftDown())
                i = 5;
            if(verticalAmount < 0)
                i = -i;
            currentIndex -= i;
            return true;
        }


//        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled)
    {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        Set<Map.Entry<Integer, ClickCoordinate>> entries = clickCoordinateMap.entrySet();
        for (Map.Entry<Integer, ClickCoordinate> entry : entries)
        {
            int y = entry.getKey();
            ClickCoordinate coordinateObj = entry.getValue();
            int x = coordinateObj.x;
            String worldName = coordinateObj.worldName;
            String coordinate = coordinateObj.coordinate;
            if(mouseY >= y && mouseY <= y + 34
                    && mouseX >= x && mouseX <= x + Resource.getStringWidth(coordinate + worldName + "   "))
            {
                String[] coordinates = coordinate.split("/");
                double x1 = Double.parseDouble(coordinates[0].trim().equals(".0") ? "0" : coordinates[0].trim());
                double y1 = Double.parseDouble(coordinates[1].trim().equals(".0") ? "0" : coordinates[1].trim());
                double z1 = Double.parseDouble(coordinates[2].trim().equals(".0") ? "0" : coordinates[2].trim());
                System.out.println("coordinate: " + "tpplus " + getEnWorldName(worldName) + " " + x1 + " " + y1 + " " + z1);
                sendChatCommand("tpplus " + getEnWorldName(worldName) + " " + x1 + " " + y1 + " " + z1);
                Minecraft.getInstance().setScreen(null);
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private boolean hasShiftDown()
    {
        long window = Minecraft.getInstance().getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
}
