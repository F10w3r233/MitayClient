package com.flower.mitayclient.GUI.Widget;

import com.flower.Mitayclient;
import com.flower.mitayclient.util.MitayUtils;
import com.flower.mitayclient.util.ModIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.IMEPreeditOverlay;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultiColumnTextFieldWidget extends AbstractWidget 
{

    Identifier rect = ModIdentifier.get("textures/gui/widget/combo_box/rect.png");
    private final Font font;
    private final List<String> lines = new ArrayList<>();
    private final List<String> placeholders = new ArrayList<>();
    private final List<WrappedLine> wrappedLines = new ArrayList<>();

    private int cursorLine;
    private int cursorColumn;
    private int selectionStartLine = -1;
    private int selectionStartColumn = -1;
    private int scrollOffset;
    private boolean dragging;
    private long lastClickTime;

    private Runnable onTextChanged;
    private Consumer<String> onRowChanged;

    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final int BORDER_COLOR = 0xFFAAAAAA;
    private static final int CURSOR_COLOR = 0xFFFFFFFF;
    private static final int SELECTION_COLOR = 0x804444FF;
    private static final int TEXT_COLOR = MitayUtils.getFontColor();
    private static final int PLACEHOLDER_COLOR = 0xFF808080;
    private static final int SEPARATOR_COLOR = Mitayclient.getConfig().isDarkShown() ? 0xFF555555 : CommonColors.GRAY;

    private static final int LINE_SPACING_EXTRA = 5; //5

    Identifier rect_top = ModIdentifier.get("textures/gui/widget/combo_box/rect_top.png");
    Identifier rect_mid = ModIdentifier.get("textures/gui/widget/combo_box/rect_mid.png");
    Identifier rect_bottom = ModIdentifier.get("textures/gui/widget/combo_box/rect_bottom.png");

    public MultiColumnTextFieldWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.font = Minecraft.getInstance().font;
        lines.add("");
        placeholders.add("");
        cursorLine = 0;
        cursorColumn = 0;
        recalculateWrappedLines();
    }

    // ==================== 公共方法 ====================

    public void setLineCount(int count) {
        while (lines.size() < count) {
            lines.add("");
            placeholders.add("");
        }
        while (lines.size() > count) {
            lines.remove(lines.size() - 1);
            placeholders.remove(placeholders.size() - 1);
        }
        if (cursorLine >= lines.size()) cursorLine = lines.size() - 1;
        recalculateWrappedLines();
    }

    public int getLineCount() {
        return lines.size();
    }



    public void setLineText(int index, String text) {
        if (index < 0 || index >= lines.size()) return;
        lines.set(index, text != null ? text : "");
        recalculateWrappedLines();
        onTextChanged();
    }

    public void setPlaceholder(int index, String placeholder) {
        if (index < 0 || index >= placeholders.size()) return;
        placeholders.set(index, placeholder != null ? placeholder : "");
    }


    public String getLineText(int index) {
        if (index < 0 || index >= lines.size()) return "";
        return lines.get(index);
    }

    public String getLineText(int index, String defaultIfEmpty) {
        String text = getLineText(index).trim();
        if (text.isEmpty()) return defaultIfEmpty;
//        if (index < 0 || index >= lines.size()) return "";
        return lines.get(index);
    }

    public int getLineInt(int index, int defaultIfEmpty) {
        String text = getLineText(index).trim();
        if (text.isEmpty()) return defaultIfEmpty;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultIfEmpty;
        }
    }

    public String[] getAllLines() {
        return lines.toArray(new String[0]);
    }

    public void setOnTextChanged(Runnable onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    public void setOnRowChanged(Consumer<String> onRowChanged) {
        this.onRowChanged = onRowChanged;
    }

    // ==================== 渲染 ====================

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // 背景
//        graphics.fill(getX(), getY(), getX() + width, getY() + height, BACKGROUND_COLOR);

        int innerX = getX() + 3;
        int innerY = getY() + 2;
        int lineHeight = font.lineHeight + 1 + LINE_SPACING_EXTRA;
        int visibleLines = (height - 4) / lineHeight;

        int listY = getY() - 1;
        int rectX = getX() - 2;
        int rectY = listY;
        int rectWidth = getWidth() + 2;

        int textureColor = MitayUtils.getTextureColor();
        float textureAlpha = Mitayclient.getConfig().isDarkShown() ?  0.1f : 0.3f;

        for(int index = 0; index < visibleLines;)
        {
            index++;

            if(visibleLines == 1)
            {
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_top, rectX, rectY, 0,0,rectWidth,5,rectWidth,5, ARGB.color(textureAlpha, textureColor));
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_mid, rectX, rectY+5, 0,0,rectWidth,13,rectWidth,13, ARGB.color(textureAlpha, textureColor));
                graphics.blit(RenderPipelines.GUI_TEXTURED, rect_bottom, rectX, rectY + 12+6, 0, 0, rectWidth, 5, rectWidth, 5, ARGB.color(textureAlpha, textureColor));
            }else {
                if(index == 1)
                {
                    //draw rect top
                    graphics.blit(RenderPipelines.GUI_TEXTURED, rect_top, rectX, rectY, 0,0,rectWidth,5,rectWidth,5, ARGB.color(textureAlpha, textureColor));
                    graphics.blit(RenderPipelines.GUI_TEXTURED, rect_mid, rectX, rectY+5, 0,0,rectWidth,14,rectWidth,14, ARGB.color(textureAlpha, textureColor));
                    rectY += (5 + 11);
                }else if(index == visibleLines)
                {
                    //draw rect buttom
                    graphics.blit(RenderPipelines.GUI_TEXTURED, rect_mid, rectX, rectY+4, 0, 0, rectWidth, 16, rectWidth, 16, ARGB.color(textureAlpha, textureColor));
                    graphics.blit(RenderPipelines.GUI_TEXTURED, rect_bottom, rectX, rectY + 12+8, 0, 0, rectWidth, 5, rectWidth, 5, ARGB.color(textureAlpha, textureColor));
                }else
                {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, rect_mid, rectX, rectY+3, 0, 0, rectWidth, 12, rectWidth, 12, ARGB.color(textureAlpha, textureColor));
                    rectY += 11;
                    //draw middle part
                }
            }
        }

        // 边框
//        renderBorder(graphics);





        graphics.enableScissor(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1);

        // 选择高亮
        renderSelection(graphics, innerX, innerY, lineHeight);

        int lastRawLine = -1;
        for (int i = 0; i < visibleLines; i++)
        {
            int wrappedIndex = scrollOffset + i;
            if (wrappedIndex >= wrappedLines.size()) break;
            WrappedLine wl = wrappedLines.get(wrappedIndex);

            // 逻辑行切换时绘制分割线
            if (wl.rawLineIndex != lastRawLine && lastRawLine != -1) {
                int sepY = innerY + i * lineHeight - 1;
                graphics.fill(innerX, sepY, getX() + width - 3, sepY + 1, SEPARATOR_COLOR);
            }
            lastRawLine = wl.rawLineIndex;

            int y = innerY + i * lineHeight;

            // 决定显示文本：实际行非空，显示其文本（可能只是换行片段）；实际行为空时，显示占位符（灰色）
            String actualLine = lines.get(wl.rawLineIndex);
            if (!actualLine.isEmpty()) {
                // 显示实际文本片段
                graphics.text(font, wl.text, innerX, y+1+2, TEXT_COLOR, false);
            } else {
                // 实际行为空，且这是该逻辑行的第一个可视化行（startColumn==0）时显示占位符
                if (wl.startColumn == 0) {
                    String placeholder = placeholders.get(wl.rawLineIndex);
                    if (!placeholder.isEmpty()) {
                        graphics.text(font, placeholder, innerX+2, y+2+1, PLACEHOLDER_COLOR, false);
                    }
                }
                // 若后续还有换行片段（空文本不应该换行），忽略
            }
        }

        // 光标（只有在实际文本非空时，或者虽然为空但光标应在行首）
        if (isFocused() && (System.currentTimeMillis() / 500) % 2 == 0) {
            int visualLine = getVisualLineFromRaw(cursorLine, cursorColumn);
            int visibleVisualIndex = visualLine - scrollOffset;
            if (visibleVisualIndex >= 0 && visibleVisualIndex < visibleLines) {
                // 光标的X位置：基于当前实际行文本
                String currentLineText = lines.get(cursorLine);
                int cursorX = innerX + font.width(currentLineText.substring(0, cursorColumn));
                int cursorY = innerY + visibleVisualIndex * lineHeight;
                graphics.fill(cursorX, cursorY - 1, cursorX + 1, cursorY + lineHeight - 1, CURSOR_COLOR);
            }
        }

        graphics.disableScissor();
        renderScrollBar(graphics, visibleLines);

        if (this.preeditOverlay != null) {
            int visualLine = getVisualLineFromRaw(cursorLine, cursorColumn);
            int visibleVisualIndex = visualLine - scrollOffset;
            int cursorX = innerX + font.width(lines.get(cursorLine).substring(0, cursorColumn));
            int cursorY = innerY + visibleVisualIndex * lineHeight;
            this.preeditOverlay.updateInputPosition(cursorX, cursorY);
            graphics.setPreeditOverlay(this.preeditOverlay);
        }
    }

    private void renderSelection(GuiGraphicsExtractor graphics, int innerX, int innerY, int lineHeight) {
        if (!hasSelection()) return;
        int startLine, startCol, endLine, endCol;
        if (comparePositions(cursorLine, cursorColumn, selectionStartLine, selectionStartColumn) < 0) {
            startLine = cursorLine; startCol = cursorColumn;
            endLine = selectionStartLine; endCol = selectionStartColumn;
        } else {
            startLine = selectionStartLine; startCol = selectionStartColumn;
            endLine = cursorLine; endCol = cursorColumn;
        }

        int startVisual = getVisualLineFromRaw(startLine, startCol);
        int endVisual = getVisualLineFromRaw(endLine, endCol);

        for (int vLine = startVisual; vLine <= endVisual; vLine++) {
            if (vLine < scrollOffset) continue;
            int visibleIndex = vLine - scrollOffset;
            int maxVisible = (height - 4) / lineHeight;
            if (visibleIndex >= maxVisible) break;

            WrappedLine wl = wrappedLines.get(vLine);
            int lineY = innerY + visibleIndex * lineHeight;

            // 高亮区域基于该可视化行的文本
            int selStartInLine = (vLine == startVisual)
                    ? font.width(wl.text.substring(0, getColumnInWrapped(startLine, startCol, wl)))
                    : 0;
            int selEndInLine;
            if (vLine == endVisual) {
                int col = getColumnInWrapped(endLine, endCol, wl);
                selEndInLine = font.width(wl.text.substring(0, col));
                if (endCol == lines.get(endLine).length() && col == wl.text.length()) {
                    selEndInLine = font.width(wl.text);
                }
            } else {
                selEndInLine = font.width(wl.text);
            }

            int drawX = innerX + selStartInLine;
            int drawWidth = selEndInLine - selStartInLine;
            if (drawWidth > 0) {
                graphics.fill(drawX, lineY, drawX + drawWidth, lineY + lineHeight, SELECTION_COLOR);
            }
        }
    }

    private void renderScrollBar(GuiGraphicsExtractor graphics, int visibleLines) {
        if (wrappedLines.size() <= visibleLines) return;
        int scrollBarHeight = height - 2;
        int thumbHeight = Math.max(10, visibleLines * scrollBarHeight / wrappedLines.size());
        int maxScroll = wrappedLines.size() - visibleLines;
        int thumbY = getY() + 1 + (scrollOffset * (scrollBarHeight - thumbHeight) / maxScroll);
        graphics.fill(getX() + width - 3, getY() + 1, getX() + width - 1, getY() + height - 1, 0x80FFFFFF);
        graphics.fill(getX() + width - 3, thumbY, getX() + width - 1, thumbY + thumbHeight, 0xFFCCCCCC);
    }

    // ==================== 事件处理 ====================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(event.x(), event.y())) {
            setFocused(false);
            return false;
        }
        setFocused(true);
        if (event.button() == 0) {
            int innerX = getX() + 3;
            int innerY = getY() + 2;
            int lineHeight = font.lineHeight + 1 + LINE_SPACING_EXTRA;
            int relativeY = (int) event.y() - innerY;
            int visualLineIndex = relativeY / lineHeight + scrollOffset;
            if (wrappedLines.isEmpty()) return true;
            visualLineIndex = Mth.clamp(visualLineIndex, 0, wrappedLines.size() - 1);
            WrappedLine wl = wrappedLines.get(visualLineIndex);
            int charX = (int) event.x() - innerX;
            // 对于空行，plainSubstrByWidth 对空字符串返回空字符串，col 将为 0
            int col = font.plainSubstrByWidth(wl.text, charX).length();
            int rawLine = wl.rawLineIndex;
            int rawColumn = wl.startColumn + col;
            if (rawColumn > lines.get(rawLine).length()) rawColumn = lines.get(rawLine).length();

            long now = System.currentTimeMillis();
            if (now - lastClickTime < 250 && doubleClick) {
                selectAllCurrentLine();
            } else {
                cursorLine = rawLine;
                cursorColumn = rawColumn;
                clearSelection();
                dragging = true;
            }
            lastClickTime = now;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging && isFocused()) {
            int innerX = getX() + 3;
            int innerY = getY() + 2;
            int lineHeight = font.lineHeight + 1 + LINE_SPACING_EXTRA;
            int relativeY = (int) event.y() - innerY;
            int visualLineIndex = relativeY / lineHeight + scrollOffset;
            if (wrappedLines.isEmpty()) return true;
            visualLineIndex = Mth.clamp(visualLineIndex, 0, wrappedLines.size() - 1);
            WrappedLine wl = wrappedLines.get(visualLineIndex);
            int charX = (int) event.x() - innerX;
            int col = font.plainSubstrByWidth(wl.text, charX).length();
            int rawLine = wl.rawLineIndex;
            int rawColumn = wl.startColumn + col;
            if (rawColumn > lines.get(rawLine).length()) rawColumn = lines.get(rawLine).length();

            if (!hasSelection()) {
                selectionStartLine = cursorLine;
                selectionStartColumn = cursorColumn;
            }
            cursorLine = rawLine;
            cursorColumn = rawColumn;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = false;
        return super.mouseReleased(event);
    }
    @Nullable
    private IMEPreeditOverlay preeditOverlay;

    @Override
    public boolean preeditUpdated(@Nullable PreeditEvent event) {
        this.preeditOverlay = event != null ? new IMEPreeditOverlay(event, this.font, 9 + 1) : null;
        return true;
    }


    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        int modifiers = event.modifiers();
        boolean ctrl = event.hasControlDown();
        boolean shift = event.hasShiftDown();
        // 如果输入法组合激活，只处理Ctrl组合键，其他全部放行
        if (this.preeditOverlay != null) {
            if (event.hasControlDown()) {
                switch (event.key()) {
                    case 65 -> { selectAll(); return true; }
                    case 67 -> { copyToClipboard(); return true; }
                    case 88 -> { copyToClipboard(); deleteSelection(); return true; }
                    case 86 -> { pasteFromClipboard(); return true; }
                    default -> {}
                }
            }
            return false; // 所有其他按键交给输入法
        }

        if (!isFocused()) return false;

        // 对于方向键、Home、End 等，只在 Shift 按下（扩展选区）或
        // 已有选区时才消费事件。否则交还给系统，避免干扰输入法。
        switch (keyCode) {
            case 263, 262, 265, 264, 268, 269:
                if (shift) {
                    // 只处理 Shift+方向键 的选择扩展
                    if (!hasSelection()) startSelection();
                    switch (keyCode) {
                        case 263 -> moveCursorLeft(true);
                        case 262 -> moveCursorRight(true);
                        case 265 -> moveCursorUp(true);
                        case 264 -> moveCursorDown(true);
                        case 268 -> moveCursorHome(true);
                        case 269 -> moveCursorEnd(true);
                    }
                    return true;
                }
                return false;   // 永远不拦截普通方向键

            case 266: // PAGE_UP
                scrollOffset = Math.max(0, scrollOffset - ((height - 4) / (font.lineHeight + 1)));
                return true;
            case 267: // PAGE_DOWN
                int vis = (height - 4) / (font.lineHeight + 1);
                scrollOffset = Math.min(wrappedLines.size() - vis, scrollOffset + vis);
                return true;
            case 257, 335: // ENTER, KP_ENTER
                if (cursorLine + 1 < lines.size()) {
                    cursorLine++;
                    cursorColumn = 0;
                    clearSelection();
                }
                return true;
            case 259: // BACKSPACE
                deleteChar(false);
                return true;
            case 261: // DELETE
                deleteChar(true);
                return true;
            case 256: // ESCAPE
                clearSelection();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!isFocused()) return false;
        // 移除对 preeditOverlay 的检查，允许 IME 提交字符
        int codePoint = event.codepoint();
        if (codePoint == '\n' || codePoint == '\r') return false;
        if (!Character.isValidCodePoint(codePoint) || Character.isISOControl(codePoint)) return false;

        deleteSelection();
        String current = lines.get(cursorLine);
        String newLine = current.substring(0, cursorColumn) + (char) codePoint + current.substring(cursorColumn);
        lines.set(cursorLine, newLine);
        cursorColumn++;
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
        if (onRowChanged != null) onRowChanged.accept(newLine);
        return true;
    }
    // ==================== 内部编辑逻辑 ====================

    private void deleteChar(boolean forward) {
        if (hasSelection()) {
            deleteSelection();
            return;
        }
        if (forward) {
            String current = lines.get(cursorLine);
            if (cursorColumn < current.length()) {
                lines.set(cursorLine, current.substring(0, cursorColumn) + current.substring(cursorColumn + 1));
            } else {
                return; // 已在行尾，什么都不做
            }
        } else {
            if (cursorColumn > 0) {
                String current = lines.get(cursorLine);
                lines.set(cursorLine, current.substring(0, cursorColumn - 1) + current.substring(cursorColumn));
                cursorColumn--;
            } else {
                // 光标在行首，不合并行，也不删除
                return;
            }
        }
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
    }

    private void deleteSelection() {
        if (!hasSelection()) return;
        int startL, startC, endL, endC;
        if (comparePositions(cursorLine, cursorColumn, selectionStartLine, selectionStartColumn) < 0) {
            startL = cursorLine; startC = cursorColumn;
            endL = selectionStartLine; endC = selectionStartColumn;
        } else {
            startL = selectionStartLine; startC = selectionStartColumn;
            endL = cursorLine; endC = cursorColumn;
        }
        String startLine = lines.get(startL);
        String endLine = lines.get(endL);
        lines.set(startL, startLine.substring(0, startC) + endLine.substring(endC));
        for (int i = endL; i > startL; i--) {
            lines.remove(i);
            placeholders.remove(i);
        }
        cursorLine = startL;
        cursorColumn = startC;
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
    }

    private void moveCursorLeft(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();
        if (cursorColumn > 0) {
            cursorColumn--;
        } else if (cursorLine > 0) {
            cursorLine--;
            cursorColumn = lines.get(cursorLine).length();
        }
    }

    private void moveCursorRight(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();
        String current = lines.get(cursorLine);
        if (cursorColumn < current.length()) {
            cursorColumn++;
        } else if (cursorLine < lines.size() - 1) {
            cursorLine++;
            cursorColumn = 0;
        }
    }

    private void moveCursorUp(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();
        int curVis = getVisualLineFromRaw(cursorLine, cursorColumn);
        if (curVis > 0) {
            setCursorFromVisualLine(curVis - 1, getVisualColumn(cursorLine, cursorColumn));
        }
    }

    private void moveCursorDown(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();
        int curVis = getVisualLineFromRaw(cursorLine, cursorColumn);
        if (curVis < wrappedLines.size() - 1) {
            setCursorFromVisualLine(curVis + 1, getVisualColumn(cursorLine, cursorColumn));
        }
    }

    private void moveCursorHome(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();
        cursorColumn = 0;
    }

    private void moveCursorEnd(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();
        cursorColumn = lines.get(cursorLine).length();
    }

    private void selectAll() {
        selectionStartLine = 0;
        selectionStartColumn = 0;
        cursorLine = lines.size() - 1;
        cursorColumn = lines.get(cursorLine).length();
    }

    private void selectAllCurrentLine() {
        selectionStartLine = cursorLine;
        selectionStartColumn = 0;
        cursorColumn = lines.get(cursorLine).length();
    }

    private void clearSelection() {
        selectionStartLine = -1;
        selectionStartColumn = -1;
    }

    private boolean hasSelection() {
        return selectionStartLine != -1 && (selectionStartLine != cursorLine || selectionStartColumn != cursorColumn);
    }

    private void startSelection() {
        selectionStartLine = cursorLine;
        selectionStartColumn = cursorColumn;
    }

    private void copyToClipboard() {
        if (!hasSelection()) return;
        Minecraft.getInstance().keyboardHandler.setClipboard(getSelectedText());
    }

    private void pasteFromClipboard() {
        String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
        if (clipboard == null || clipboard.isEmpty()) return;
        deleteSelection();
        String[] pastedLines = clipboard.split("\n", -1);
        if (pastedLines.length == 0) return;

        String current = lines.get(cursorLine);
        String before = current.substring(0, cursorColumn);
        String after = current.substring(cursorColumn);
        lines.set(cursorLine, before + pastedLines[0]);

        for (int i = 1; i < pastedLines.length; i++) {
            lines.add(cursorLine + i, pastedLines[i]);
            placeholders.add(cursorLine + i, "");
        }
        int lastIndex = cursorLine + pastedLines.length - 1;
        lines.set(lastIndex, lines.get(lastIndex) + after);
        cursorLine = lastIndex;
        cursorColumn = pastedLines[pastedLines.length - 1].length();
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
    }

    private String getSelectedText() {
        if (!hasSelection()) return "";
        int startL, startC, endL, endC;
        if (comparePositions(cursorLine, cursorColumn, selectionStartLine, selectionStartColumn) < 0) {
            startL = cursorLine; startC = cursorColumn;
            endL = selectionStartLine; endC = selectionStartColumn;
        } else {
            startL = selectionStartLine; startC = selectionStartColumn;
            endL = cursorLine; endC = cursorColumn;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = startL; i <= endL; i++) {
            String line = lines.get(i);
            int from = (i == startL) ? startC : 0;
            int to = (i == endL) ? endC : line.length();
            sb.append(line, from, to);
            if (i < endL) sb.append('\n');
        }
        return sb.toString();
    }

    // ==================== 换行计算 ====================

    private void recalculateWrappedLines() {
        wrappedLines.clear();
        int contentWidth = width - 6;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // 只基于实际文本换行
            if (line.isEmpty()) {
                // 空行占一个空可视化行
                wrappedLines.add(new WrappedLine(i, 0, ""));
                continue;
            }
            int col = 0;
            while (col < line.length()) {
                String remaining = line.substring(col);
                String visible = font.plainSubstrByWidth(remaining, contentWidth);
                if (visible.isEmpty() && !remaining.isEmpty()) {
                    visible = remaining.substring(0, 1);
                }
                wrappedLines.add(new WrappedLine(i, col, visible));
                col += visible.length();
                if (col >= line.length()) break;
            }
        }
        clampScroll();
    }

    private int getVisualLineFromRaw(int rawLine, int rawCol) {
        for (int i = 0; i < wrappedLines.size(); i++) {
            WrappedLine wl = wrappedLines.get(i);
            if (wl.rawLineIndex == rawLine) {
                if (rawCol >= wl.startColumn && rawCol <= wl.startColumn + wl.text.length()) {
                    if (rawCol == wl.startColumn + wl.text.length() && i + 1 < wrappedLines.size()
                            && wrappedLines.get(i + 1).rawLineIndex == rawLine) {
                        continue;
                    }
                    return i;
                }
            }
        }
        return Math.max(0, wrappedLines.size() - 1);
    }

    @Override
    public void setFocused(boolean focused)
    {
        super.setFocused(focused);
        // 通知系统当前控件支持文本输入（用于 IME）
        Minecraft.getInstance().onTextInputFocusChange(this, focused && this.active);
    }

    private void setCursorFromVisualLine(int visualLine, int targetVisualColumn) {
        if (visualLine < 0 || visualLine >= wrappedLines.size()) return;
        WrappedLine wl = wrappedLines.get(visualLine);
        int col = Math.min(targetVisualColumn, wl.text.length());
        cursorLine = wl.rawLineIndex;
        cursorColumn = wl.startColumn + col;
        cursorColumn = Math.min(cursorColumn, lines.get(cursorLine).length());
    }

    private int getVisualColumn(int rawLine, int rawCol) {
        for (WrappedLine wl : wrappedLines) {
            if (wl.rawLineIndex == rawLine && rawCol >= wl.startColumn && rawCol <= wl.startColumn + wl.text.length()) {
                return font.width(wl.text.substring(0, rawCol - wl.startColumn));
            }
        }
        return 0;
    }

    private int getColumnInWrapped(int rawLine, int rawCol, WrappedLine wl) {
        if (wl.rawLineIndex != rawLine) return 0;
        return Mth.clamp(rawCol - wl.startColumn, 0, wl.text.length());
    }

    private void clampScroll() {
        int visibleLines = (height - 4) / (font.lineHeight + 1);
        int maxScroll = Math.max(0, wrappedLines.size() - visibleLines);
        int cursorVisual = getVisualLineFromRaw(cursorLine, cursorColumn);
        scrollOffset = Mth.clamp(scrollOffset, cursorVisual - visibleLines + 1, cursorVisual);
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
    }

    private int comparePositions(int line1, int col1, int line2, int col2) {
        if (line1 != line2) return Integer.compare(line1, line2);
        return Integer.compare(col1, col2);
    }

    private String getCurrentLine() {
        return lines.get(cursorLine);
    }

    private static class WrappedLine {
        final int rawLineIndex;
        final int startColumn;
        final String text;

        WrappedLine(int rawLineIndex, int startColumn, String text) {
            this.rawLineIndex = rawLineIndex;
            this.startColumn = startColumn;
            this.text = text;
        }
    }

    private void onTextChanged() {
        if (onTextChanged != null) onTextChanged.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        // 可留空
    }


}