package com.flower.mitayclient.GUI.screen;

import com.flower.mitayclient.GUI.Widget.MultiColumnTextFieldWidget;
import com.flower.mitayclient.GUI.buttons.Badge.BadgeButton;
import com.flower.mitayclient.GUI.screen.BadgeUtil.BadgeCache;
import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.BadgesPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import org.jetbrains.annotations.Nullable;

public class TESTScreen extends Screen
{
    @Override
    public boolean preeditUpdated(@Nullable PreeditEvent event)
    {
        // 再调用 super，以处理其他子控件（如 comboBox 等）
        return super.preeditUpdated(event);
    }
    private int panelX, panelY;
    private static final int PANEL_WIDTH = 382;
    private static final int PANEL_HEIGHT = 292;
    int x, y, z;
    public TESTScreen()
    {
        super(Component.empty());
    }
    MultiColumnTextFieldWidget field = new MultiColumnTextFieldWidget(10, 40, 150, 60, Component.literal("坐标"));

    MultiColumnTextFieldWidget descField = new MultiColumnTextFieldWidget(100, 110, 150, 33, Component.literal("描述"));

    @Override
    protected void init()
    {
        BadgeButton button1 = BadgeButton.builder(Component.empty(), button -> {

        }).badge(BadgeCache.get().get("iron")).dimensions(100,100,16,16).build();
        addRenderableWidget(button1);
//        MultilineTextFieldWidget textField = new MultilineTextFieldWidget(10, 40, 200, 100, Component.literal(""));
        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;
//        textField.setOnTextChanged(() -> System.out.println("文本已更改"));
//        textField.setOnEnterPressed(line -> System.out.println("按下回车，当前行: " + line));
//        addRenderableWidget(textField);

        field.setLineCount(3);                      // 固定3行
        field.setPlaceholder(0, "X");          // 占位符提示
        field.setPlaceholder(1, "Y");
        field.setPlaceholder(2, "Z");

        addRenderableWidget(field);


        descField.setPlaceholder(0, "地点名");
        addRenderableWidget(descField);

//        addRenderableWidget(new MultipleTextFieldWidget(panelX+129, panelY+10, 210, 1));
        super.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        x = field.getLineInt(0, 0);
        y = field.getLineInt(1, 0);
        z = field.getLineInt(2, 0);
        graphics.text(font, String.valueOf(x), 1,1, CommonColors.WHITE);
        graphics.text(font, String.valueOf(y), 1,10, CommonColors.WHITE);
        graphics.text(font, String.valueOf(z), 1,20, CommonColors.WHITE);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}
