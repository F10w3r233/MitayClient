package com.flower.mitayclient.GUI.Widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultilineTextFieldWidget extends AbstractWidget {

    private final Font font;
    private final List<String> lines = new ArrayList<>();
    private final List<WrappedLine> wrappedLines = new ArrayList<>();

    private int cursorLine;
    private int cursorColumn;
    private int selectionStartLine = -1;
    private int selectionStartColumn = -1;
    private int scrollOffset;
    private boolean dragging;
    private long lastClickTime;

    private Runnable onTextChanged;
    private Consumer<String> onEnterPressed;

    // 颜色
    private static final int BACKGROUND_COLOR = 0xFF000000;
    private static final int BORDER_COLOR = 0xFFAAAAAA;
    private static final int CURSOR_COLOR = 0xFFFFFFFF;
    private static final int SELECTION_COLOR = 0x804444FF;
    private static final int TEXT_COLOR = 0xFFE0E0E0;

    public MultilineTextFieldWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.font = Minecraft.getInstance().font;
        lines.add("");
        cursorLine = 0;
        cursorColumn = 0;
        scrollOffset = 0;
        recalculateWrappedLines();
    }


    // ==================== 公共方法 ====================

    public String getText() {
        return String.join("\n", lines);
    }

    public void setText(String text) {
        lines.clear();
        if (text.isEmpty()) {
            lines.add("");
        } else {
            for (String part : text.split("\n", -1)) {
                lines.add(part);
            }
        }
        cursorLine = Math.min(cursorLine, lines.size() - 1);
        cursorColumn = Math.min(cursorColumn, lines.get(cursorLine).length());
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
    }

    public void setOnTextChanged(Runnable onTextChanged)
    {
        this.onTextChanged = onTextChanged;
    }

    private void onTextChanged()
    {
        if (onTextChanged != null)
        {
            onTextChanged.run();
        }
    }

    public void setOnEnterPressed(Consumer<String> onEnterPressed) {
        this.onEnterPressed = onEnterPressed;
    }

    // ==================== 渲染 ====================

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float a)
    {
        // 背景与边框
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, BACKGROUND_COLOR);
        renderBorder(guiGraphics);

        int innerX = getX() + 3;
        int innerY = getY() + 2;
        int lineHeight = font.lineHeight + 1;
        int visibleLines = (height - 4) / lineHeight;

        guiGraphics.enableScissor(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1);

        // 选择高亮
        renderSelection(guiGraphics, innerX, innerY, lineHeight);

        // 绘制文本
        for (int i = 0; i < visibleLines; i++) {
            int wrappedIndex = scrollOffset + i;
            if (wrappedIndex >= wrappedLines.size()) break;
            WrappedLine wl = wrappedLines.get(wrappedIndex);
            int y = innerY + i * lineHeight;
            guiGraphics.text(font, wl.text, innerX, y, TEXT_COLOR, false);
        }

        // 光标闪烁（基于系统时间，每 500ms 切换）
        if (isFocused() && (System.currentTimeMillis() / 500) % 2 == 0) {
            int visualLine = getVisualLineFromRaw(cursorLine, cursorColumn);
            int visibleVisualIndex = visualLine - scrollOffset;
            if (visibleVisualIndex >= 0 && visibleVisualIndex < visibleLines) {
                int cursorX = innerX + font.width(getCurrentLine().substring(0, cursorColumn));
                int cursorY = innerY + visibleVisualIndex * lineHeight;
                guiGraphics.fill(cursorX, cursorY - 1, cursorX + 1, cursorY + lineHeight - 1, CURSOR_COLOR);
            }
        }

        guiGraphics.disableScissor();
        renderScrollBar(guiGraphics, visibleLines);
    }

    private void renderBorder(GuiGraphicsExtractor guiGraphics) {
        int x1 = getX(), y1 = getY(), x2 = getX() + width, y2 = getY() + height;
        guiGraphics.fill(x1, y1, x2, y1 + 1, BORDER_COLOR);
        guiGraphics.fill(x1, y2 - 1, x2, y2, BORDER_COLOR);
        guiGraphics.fill(x1, y1, x1 + 1, y2, BORDER_COLOR);
        guiGraphics.fill(x2 - 1, y1, x2, y2, BORDER_COLOR);
    }

    private void renderSelection(GuiGraphicsExtractor guiGraphics, int innerX, int innerY, int lineHeight) {
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
                guiGraphics.fill(drawX, lineY, drawX + drawWidth, lineY + lineHeight, SELECTION_COLOR);
            }
        }
    }

    private void renderScrollBar(GuiGraphicsExtractor guiGraphics, int visibleLines) {
        if (wrappedLines.size() <= visibleLines) return;
        int scrollBarHeight = height - 2;
        int thumbHeight = Math.max(10, visibleLines * scrollBarHeight / wrappedLines.size());
        int maxScroll = wrappedLines.size() - visibleLines;
        int thumbY = getY() + 1 + (scrollOffset * (scrollBarHeight - thumbHeight) / maxScroll);
        guiGraphics.fill(getX() + width - 3, getY() + 1, getX() + width - 1, getY() + height - 1, 0x80FFFFFF);
        guiGraphics.fill(getX() + width - 3, thumbY, getX() + width - 1, thumbY + thumbHeight, 0xFFCCCCCC);
    }

    // ==================== 新版鼠标/键盘事件 ====================

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.isMouseOver(event.x(), event.y())) {
            setFocused(false);
            return false;
        }
        setFocused(true);
        if (event.button() == 0) {
            int innerX = getX() + 3;
            int innerY = getY() + 2;
            int lineHeight = font.lineHeight + 1;
            int visibleLines = (height - 4) / lineHeight;
            int relativeY = (int) event.y() - innerY;
            int visualLineIndex = relativeY / lineHeight + scrollOffset;
            if (!wrappedLines.isEmpty()) {
                visualLineIndex = Mth.clamp(visualLineIndex, 0, wrappedLines.size() - 1);
            }
            WrappedLine wl = wrappedLines.get(visualLineIndex);
            int charX = (int) event.x() - innerX;
            int col = font.plainSubstrByWidth(wl.text, charX).length();
            int rawLine = wl.rawLineIndex;
            int rawColumn = wl.startColumn + col;
            if (rawColumn > lines.get(rawLine).length()) rawColumn = lines.get(rawLine).length();

            long now = System.currentTimeMillis();
            // 双击选中整行
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
            int lineHeight = font.lineHeight + 1;
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


    @Override
    public boolean keyPressed(KeyEvent event)
    {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        if (!isFocused()) return false;
        boolean ctrl = event.hasControlDown();
        boolean shift = event.hasShiftDown();

        if (ctrl) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_A -> selectAll();
                case GLFW.GLFW_KEY_C -> copyToClipboard();
                case GLFW.GLFW_KEY_X -> { copyToClipboard(); deleteSelection(); }
                case GLFW.GLFW_KEY_V -> pasteFromClipboard();
                default -> { return false; }
            }
            return true;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT -> moveCursorLeft(shift);
            case GLFW.GLFW_KEY_RIGHT -> moveCursorRight(shift);
            case GLFW.GLFW_KEY_UP -> moveCursorUp(shift);
            case GLFW.GLFW_KEY_DOWN -> moveCursorDown(shift);
            case GLFW.GLFW_KEY_HOME -> moveCursorHome(shift);
            case GLFW.GLFW_KEY_END -> moveCursorEnd(shift);
            case GLFW.GLFW_KEY_PAGE_UP -> scrollOffset = Math.max(0, scrollOffset - ((height - 4) / (font.lineHeight + 1)));
            case GLFW.GLFW_KEY_PAGE_DOWN -> {
                int vis = (height - 4) / (font.lineHeight + 1);
                scrollOffset = Math.min(wrappedLines.size() - vis, scrollOffset + vis);
            }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> insertNewLine();
            case GLFW.GLFW_KEY_BACKSPACE -> deleteChar(false);
            case GLFW.GLFW_KEY_DELETE -> deleteChar(true);
            case GLFW.GLFW_KEY_ESCAPE -> { clearSelection(); return true; }
            default -> { return false; }
        }
        return true;
    }


    @Override
    public boolean charTyped(CharacterEvent event)
    {
        if (!isFocused()) return false;

        int codePoint = event.codepoint();

        // 过滤换行符（已由 keyPressed 处理）和其他控制字符
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
        return true;
    }

    // ==================== 编辑逻辑 ====================

    private void insertNewLine() {
        deleteSelection();
        String current = lines.get(cursorLine);
        String before = current.substring(0, cursorColumn);
        String after = current.substring(cursorColumn);
        lines.set(cursorLine, before);
        lines.add(cursorLine + 1, after);
        cursorLine++;
        cursorColumn = 0;
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
        if (onEnterPressed != null) onEnterPressed.accept(before);
    }

    private void deleteChar(boolean forward) {
        if (hasSelection()) {
            deleteSelection();
            return;
        }
        if (forward) {
            String current = lines.get(cursorLine);
            if (cursorColumn < current.length()) {
                lines.set(cursorLine, current.substring(0, cursorColumn) + current.substring(cursorColumn + 1));
            } else if (cursorLine < lines.size() - 1) {
                lines.set(cursorLine, current + lines.get(cursorLine + 1));
                lines.remove(cursorLine + 1);
            } else return;
        } else {
            if (cursorColumn > 0) {
                String current = lines.get(cursorLine);
                lines.set(cursorLine, current.substring(0, cursorColumn - 1) + current.substring(cursorColumn));
                cursorColumn--;
            } else if (cursorLine > 0) {
                String prev = lines.get(cursorLine - 1);
                int oldLength = prev.length();
                lines.set(cursorLine - 1, prev + lines.get(cursorLine));
                lines.remove(cursorLine);
                cursorLine--;
                cursorColumn = oldLength;
            } else return;
        }
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
    }

    private void deleteSelection()
    {
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
        for (int i = endL; i > startL; i--) lines.remove(i);
        cursorLine = startL;
        cursorColumn = startC;
        clearSelection();
        recalculateWrappedLines();
        onTextChanged();
    }

    private void moveCursorLeft(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();

        if (cursorColumn > 0) cursorColumn--;
        else if (cursorLine > 0) {
            cursorLine--;
            cursorColumn = lines.get(cursorLine).length();
        }
    }

    private void moveCursorRight(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();

        String current = lines.get(cursorLine);
        if (cursorColumn < current.length()) cursorColumn++;
        else if (cursorLine < lines.size() - 1) {
            cursorLine++;
            cursorColumn = 0;
        }
    }

    private void moveCursorUp(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();

        int currentVisual = getVisualLineFromRaw(cursorLine, cursorColumn);
        if (currentVisual > 0) {
            setCursorFromVisualLine(currentVisual - 1, getVisualColumn(cursorLine, cursorColumn));
        }
    }

    private void moveCursorDown(boolean extendSelection) {
        if (!extendSelection) clearSelection();
        else if (!hasSelection()) startSelection();

        int currentVisual = getVisualLineFromRaw(cursorLine, cursorColumn);
        if (currentVisual < wrappedLines.size() - 1) {
            setCursorFromVisualLine(currentVisual + 1, getVisualColumn(cursorLine, cursorColumn));
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
            if (line.isEmpty()) {
                wrappedLines.add(new WrappedLine(i, 0, ""));
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

    // ==================== 内部类 ====================

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

    // ==================== 叙述（空实现） ====================

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        // 无需实现
    }
}