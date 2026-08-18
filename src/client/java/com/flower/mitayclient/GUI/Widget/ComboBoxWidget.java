package com.flower.mitayclient.GUI.Widget;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.ModIdentifier;
import com.flower.mitayclient.util.Resource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 下拉选择框（ComboBox），项数无限制，向下展开。
 *
 * <h3>集成到 Screen 中：</h3>
 * <pre>{@code
 * // 在 Screen 的渲染末尾调用
 * @Override
 * public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
 *     super.render(graphics, mouseX, mouseY, delta);
 *     myComboBox.renderExpanded(graphics, mouseX, mouseY);
 * }
 *
 * // 处理展开列表的鼠标点击
 * @Override
 * public boolean mouseClicked(double mouseX, double mouseY, int button) {
 *     if (myComboBox.isExpanded() && myComboBox.mouseClickedExpanded(mouseX, mouseY, button)) {
 *         return true;
 *     }
 *     return super.mouseClicked(mouseX, mouseY, button);
 * }
 * }</pre>
 */
public class ComboBoxWidget extends AbstractWidget
{
    Identifier rect = ModIdentifier.get("textures/gui/widget/combo_box/rect.png");
    Identifier rect_top = ModIdentifier.get("textures/gui/widget/combo_box/rect_top.png");
    Identifier rect_mid = ModIdentifier.get("textures/gui/widget/combo_box/rect_mid.png");
    Identifier rect_bottom = ModIdentifier.get("textures/gui/widget/combo_box/rect_bottom.png");

    private static final Identifier SELECTED = Resource.SELECTED_icon;

    private final Font font;
    private final List<String> items = new ArrayList<>();

//    private int selectedIndex = -1;
    private int selectedIndex = 0;
    private boolean expanded;
    private int hoveredIndex = -1;       // 展开时鼠标悬停的项（或键盘高亮）
    private int listScrollOffset;        // 列表垂直滚动偏移（以项为单位）

    private Consumer<Integer> onSelectionChanged;   // 参数：新选中的索引，-1 表示无选中

    // 外观常量
    private static final int ARROW_WIDTH = 10;
    private static final int ITEM_HEIGHT = 12;          // 每项高度（含间距）
    private static final int MAX_VISIBLE_ITEMS = 5;     // 下拉列表最多显示几项（无滚动时）
    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final int BORDER_COLOR = 0xFFAAAAAA;
//    private static final int TEXT_COLOR = 0xFFE0E0E0;
    private static final int TEXT_COLOR = CommonColors.BLACK;
    private static final int LIST_BG_COLOR = 0xFF222222;
    private static final int LIST_HIGHLIGHT_COLOR = 0xFF4444FF;
    private static int ARROW_COLOR = 0xFFCCCCCC;

    static
    {
        ARROW_COLOR = Mitayclient.getConfig().isDarkShown() ? 0xFFCCCCCC : CommonColors.BLACK;
    }

    public ComboBoxWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.font = Minecraft.getInstance().font;
    }

    // ==================== 公共 API ====================

    public void addItem(String item) {
        items.add(item);
    }

    public void setItems(List<String> items) {
        this.items.clear();
        this.items.addAll(items);
        if (selectedIndex >= items.size()) selectedIndex = -1;
    }

    public String getSelectedItem() {
        return (selectedIndex >= 0 && selectedIndex < items.size()) ? items.get(selectedIndex) : "";
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= -1 && index < items.size()) {
            selectedIndex = index;
            if (onSelectionChanged != null) onSelectionChanged.accept(index);
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (!expanded) {
            hoveredIndex = -1;
            listScrollOffset = 0;
        }
    }

    public void setOnSelectionChanged(Consumer<Integer> onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
    }

    // ==================== 渲染主体（折叠状态下的选中项） ====================

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        Identifier worldIcon = MitayUtils.getWorldIdentifier(this.getSelectedItem().trim());
        // 背景
//        graphics.fill(getX(), getY(), getX() + width, getY() + height, BACKGROUND_COLOR);

        graphics.blit(RenderPipelines.GUI_TEXTURED, rect, getX()-3, getY(), 0,0,104,24,104,24, ARGB.color(0.3f, MitayUtils.getTextureColor()));
        graphics.blit(RenderPipelines.GUI_TEXTURED, worldIcon, getX()+5, getY()+4, 0,0,12,12,12,12);

        // 边框
//        graphics.fill(getX(), getY(), getX() + width, getY() + 1, BORDER_COLOR);
//        graphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, BORDER_COLOR);d
//        graphics.fill(getX(), getY(), getX() + 1, getY() + height, BORDER_COLOR);
//        graphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, BORDER_COLOR);

        // 选中文本（或占位符）
        String displayText = getSelectedItem();
        if (displayText.isEmpty() && !items.isEmpty()) {
            displayText = items.get(0); // 默认显示第一项（但 selectedIndex 可能为-1）
        }
        int textX = getX() + 3;
        int textY = getY() + (height - font.lineHeight) / 2;
        if (!displayText.isEmpty()) {
            graphics.text(font, font.plainSubstrByWidth(displayText, width - ARROW_WIDTH - 6), textX, textY+1, MitayUtils.getFontColor(), false);
        }

        // 右侧小箭头
        int arrowX = getX() + width - ARROW_WIDTH - 2;
        int arrowY = getY() + (height - font.lineHeight) / 2;
        graphics.text(font, "v", arrowX, arrowY, ARROW_COLOR, false);

        // 注意：展开列表由外部调用 renderExpanded() 绘制
    }

    /**
     * 由 Screen 在 render 末尾调用，绘制向下展开的列表。
     */
    public void renderExpanded(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
    {
        Identifier worldIcon = MitayUtils.getWorldIdentifier(this.getSelectedItem().trim());
        if (!expanded || items.isEmpty()) return;

        int listX = getX();
        int listY = getY() + height;
        int listWidth = width;
        int maxVisible = Math.min(items.size(), MAX_VISIBLE_ITEMS);
        int visibleHeight = maxVisible * ITEM_HEIGHT;

        // 裁剪区域：确保列表不会无限延伸出屏幕底部（简单起见，我们不裁剪，但可限制绘制数量）
        // 实际只绘制 maxVisible 项，所以无需裁剪
        int textureColor = MitayUtils.getTextureColor();
        float textureAlpha = 0.1f;
        // 列表背景
        int rectY = listY;
        for(int index = 0; index < maxVisible;)
        {
            index++;
            if(index == 1)
            {
                //draw rect top
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_top, getX(), rectY, 0,0,98,5,98,5, ARGB.color(textureAlpha, textureColor));
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_mid, getX(), rectY+5, 0,0,98,11,98,11, ARGB.color(textureAlpha, textureColor));
                rectY += (5 + 11);
            }else if(index == maxVisible)
            {
                //draw rect buttom
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_mid, getX(), rectY, 0, 0, 98, 12, 98, 12, ARGB.color(textureAlpha, textureColor));
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_bottom, getX(), rectY + 12, 0, 0, 98, 5, 98, 5, ARGB.color(textureAlpha, textureColor));
            }else
            {
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_mid, getX(), rectY, 0, 0, 98, 11, 98, 11, ARGB.color(textureAlpha, textureColor));
                rectY += 11;
                //draw middle part
            }
        }
//        graphics.fill(listX, listY, listX + listWidth, listY + visibleHeight, LIST_BG_COLOR);


        // 绘制可见项
        int startIndex = listScrollOffset;
        int endIndex = Math.min(startIndex + maxVisible, items.size());
        for (int i = startIndex; i < endIndex; i++)
        {
            int itemY = listY + (i - startIndex) * ITEM_HEIGHT;
            boolean isHovered = (i == hoveredIndex);
            boolean isSelected = (i == selectedIndex);

            // 高亮背景
            if (isHovered || isSelected)
            {
                int bgColor = isHovered ? LIST_HIGHLIGHT_COLOR : 0xFF666666;
//                graphics.fill(listX + 1, itemY, listX + listWidth - 1, itemY + ITEM_HEIGHT+2, bgColor);
                graphics.blit(RenderPipelines.GUI_TEXTURED, SELECTED,
                        listX + 1, itemY+5, 0, 0, 3, 7,
                        3, 7, ARGB.color(1f, bgColor));
            }



            // 文本
            String itemText = items.get(i);
            int textX = listX + 3;
            int textY = itemY + (ITEM_HEIGHT - font.lineHeight) / 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, MitayUtils.getWorldIdentifier(itemText.trim()), textX+8, textY+2,0,0,10,10,10,10);
            graphics.text(font, font.plainSubstrByWidth(itemText, listWidth - 6), textX + 2, textY+3, MitayUtils.getFontColor(), false);
        }

        // 如果项数超过可见数，绘制滚动条指示（简易示意：右侧小条）
        if (items.size() > maxVisible) {
            int scrollBarHeight = visibleHeight;
            int thumbHeight = Math.max(6, scrollBarHeight * maxVisible / items.size());
            int maxScrollOffset = items.size() - maxVisible;
            int thumbY = listY + (listScrollOffset * (scrollBarHeight - thumbHeight) / maxScrollOffset);
            graphics.fill(listX + listWidth - 3, listY + 1, listX + listWidth - 1, listY + visibleHeight - 1, 0x80FFFFFF);
            graphics.fill(listX + listWidth - 3, thumbY, listX + listWidth - 1, thumbY + thumbHeight, 0xFFCCCCCC);
        }
    }

    /**
     * 处理展开列表区域的鼠标点击，由 Screen 在 mouseClicked 中调用。
     *
     * @return true 表示事件已消费
     */
    public boolean mouseClickedExpanded(double mouseX, double mouseY, int button) {
        if (!expanded || button != 0) return false;

        int listX = getX();
        int listY = getY() + height;
        int maxVisible = Math.min(items.size(), MAX_VISIBLE_ITEMS);
        int visibleHeight = maxVisible * ITEM_HEIGHT;

        if (mouseX >= listX && mouseX < listX + width && mouseY >= listY && mouseY < listY + visibleHeight) {
            int relativeY = (int) mouseY - listY;
            int clickedIndex = relativeY / ITEM_HEIGHT + listScrollOffset;
            if (clickedIndex >= 0 && clickedIndex < items.size()) {
                selectedIndex = clickedIndex;
                setExpanded(false);
                if (onSelectionChanged != null) onSelectionChanged.accept(clickedIndex);
                return true;
            }
        } else {
            // 点击列表外区域，关闭
            setExpanded(false);
            return true; // 消费事件，防止穿透
        }
        return false;
    }

    // ==================== 鼠标点击（组件主体） ====================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
//        System.out.printf("Mouse: %.1f, %.1f  Widget: %d, %d, %d, %d%n",
//                event.x(), event.y(), getX(), getY(), getX() + width, getY() + height);
//        double mouseX = event.x();
//        double mouseY = event.y();
//        if(!(mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth()+100
//            && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight()))
//        {
//            setExpanded(false);
//            setFocused(false);
//            return false;
//        }
        if (!isMouseOver(event.x(), event.y()))
        {
            setExpanded(false);
            setFocused(false);
            return false;
        }
        setFocused(true);
        if (event.button() == 0) {
            // 切换展开状态
            setExpanded(!expanded);
            if (expanded) {
                hoveredIndex = selectedIndex;
                listScrollOffset = Mth.clamp(hoveredIndex, 0, Math.max(0, items.size() - MAX_VISIBLE_ITEMS));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        // 不处理拖动
        return false;
    }

    // ==================== 键盘交互 ====================

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isFocused()) return false;

        int key = event.key();
        if (key == 256) { // Escape
            if (expanded) {
                setExpanded(false);
                return true;
            }
            return false;
        }

        if (!expanded) {
            // 折叠状态：上下键循环选择项，回车展开
            if (key == 264 || key == 265) { // Down / Up
                if (items.isEmpty()) return true;
                if (selectedIndex == -1) {
                    selectedIndex = (key == 264) ? 0 : items.size() - 1;
                } else {
                    int step = (key == 264) ? 1 : -1;
                    selectedIndex = Math.floorMod(selectedIndex + step, items.size());
                }
                if (onSelectionChanged != null) onSelectionChanged.accept(selectedIndex);
                return true;
            } else if (key == 257 || key == 335) { // Enter
                if (!items.isEmpty()) {
                    setExpanded(true);
                    hoveredIndex = selectedIndex;
                    listScrollOffset = Mth.clamp(hoveredIndex, 0, Math.max(0, items.size() - MAX_VISIBLE_ITEMS));
                }
                return true;
            }
        } else {
            // 展开状态：上下键移动高亮，回车确认，Escape 关闭
            if (key == 264 || key == 265) { // Down / Up
                if (items.isEmpty()) return true;
                int step = (key == 264) ? 1 : -1;
                hoveredIndex = Math.floorMod(hoveredIndex == -1 ? selectedIndex : hoveredIndex + step, items.size());
                // 自动滚动保证高亮项可见
                int maxVisible = Math.min(items.size(), MAX_VISIBLE_ITEMS);
                listScrollOffset = Mth.clamp(listScrollOffset, hoveredIndex - maxVisible + 1, hoveredIndex);
                listScrollOffset = Mth.clamp(listScrollOffset, 0, items.size() - maxVisible);
                return true;
            } else if (key == 257 || key == 335) { // Enter
                if (hoveredIndex >= 0) {
                    selectedIndex = hoveredIndex;
                    setExpanded(false);
                    if (onSelectionChanged != null) onSelectionChanged.accept(selectedIndex);
                }
                return true;
            } else if (key == 258) { // Tab 可能也关闭
                setExpanded(false);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent event) {
        // 不处理字符输入
        return false;
    }

    // ==================== 杂项 ====================

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            setExpanded(false);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // 可留空
    }
}