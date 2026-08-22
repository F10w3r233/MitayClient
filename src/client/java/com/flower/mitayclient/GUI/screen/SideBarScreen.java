    package com.flower.mitayclient.GUI.screen;


    import com.flower.Mitayclient;
    import com.flower.mitayclient.GUI.Widget.MultiColumnTextFieldWidget;
    import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
    import com.flower.mitayclient.GUI.buttons.PlaceList.Small.SmallButton;
    import com.flower.mitayclient.GUI.buttons.Switch.SwitchButton;
    import com.flower.mitayclient.GUI.screen.SideBarUtil.SideType;
    import com.flower.mitayclient.util.ModIdentifier;
    import com.flower.mitayclient.util.Resource;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.gui.GuiGraphicsExtractor;
    import net.minecraft.client.gui.components.AbstractScrollArea;
    import net.minecraft.client.gui.components.AbstractWidget;
    import net.minecraft.client.gui.components.events.GuiEventListener;
    import net.minecraft.client.gui.narration.NarrationElementOutput;
    import net.minecraft.client.input.CharacterEvent;
    import net.minecraft.client.input.KeyEvent;
    import net.minecraft.client.input.MouseButtonEvent;
    import net.minecraft.client.input.PreeditEvent;
    import net.minecraft.client.renderer.RenderPipelines;
    import net.minecraft.network.chat.Component;
    import net.minecraft.resources.Identifier;
    import net.minecraft.util.ARGB;
    import net.minecraft.util.CommonColors;
    import net.minecraft.world.entity.player.PlayerSkin;
    import org.joml.Matrix3x2f;
    import org.jspecify.annotations.Nullable;

    import java.util.*;

    import static com.flower.mitayclient.util.MitayUtils.sendChatCommand;
    import static net.minecraft.util.CommonColors.*;


    public class SideBarScreen extends NavigationScreen
    {
        private static final Identifier MATRIX_LARGE = ModIdentifier.get("textures/gui/screen/matrix_large.png");
        private static final Identifier MATRIX_LARGE_DARK = ModIdentifier.get("textures/gui/screen/matrix_large_dark.png");
        private static final Identifier MATRIX_SIDE = ModIdentifier.get("textures/gui/screen/matrix_side.png");
        private static final Identifier MATRIX_SIDE_DARK = ModIdentifier.get("textures/gui/screen/matrix_side_dark.png");
        private static final Identifier SELECTED = Resource.SELECTED_icon;
        enum Type
        {
            OPTION,
            NORMAL,
            CUSTOM
        }
        private final Minecraft client = Minecraft.getInstance();
        Type type = Type.NORMAL;
        Map<String, SideType> sideTypeMap = new LinkedHashMap<>();
        public SideBarScreen(Component title, Type type)
        {
            super(title);
            this.type = type;
        }

        // 侧边按钮列表
        private final Map<SideType, SmallButton> sideTypeButtonsMap = new LinkedHashMap<>();
        // 内容按钮映射
        private final Map<SideType, List<PlaceListButton>> contentButtonsMap = new LinkedHashMap<>();
        private final Map<SideType, List<SwitchButton>> switchButtonsMap = new LinkedHashMap<SideType, List<SwitchButton>>();
        public Map<String, List<PlaceListButton>> subMenuButtonsMap = new LinkedHashMap<>();

        public SideType currentSideType;     //默认Map中第一个SideType




        // 面板位置和尺寸
        public static int panelX, panelY;
        public static final int PANEL_WIDTH = 382;
        public static final int PANEL_HEIGHT = 292;

        ContentScrollPanel scrollArea;



        //初始化ContentButtons
        public void initializeContentButtons()
        {
            for (Map.Entry<String, SideType> entry : sideTypeMap.entrySet())
            {
                SideType sideType = entry.getValue();
                this.contentButtonsMap.put(sideType, sideType.contentButtonList);
            }
        }

        public void initializeSwitchButtons()
        {
            for (Map.Entry<String, SideType> entry : sideTypeMap.entrySet())
            {
                SideType sideType = entry.getValue();
                this.switchButtonsMap.put(sideType, sideType.switchButtonList);
            }
        }

        /**
         * 创建Content按钮
         */
        public static PlaceListButton createContentButton(Component name, Identifier icon, String command)
        {
            return PlaceListButton.builder(name, button -> {
                sendChatCommand(command);
                Minecraft.getInstance().setScreen(null);
            }).icon(icon).dimensions(0, 0, 210, 30).build();
        }
        //带desc
        public static PlaceListButton createContentButton(Component name, Identifier icon, String desc, String command)
        {
            return PlaceListButton.builder(name, button -> {
                sendChatCommand(command);
                Minecraft.getInstance().setScreen(null);
            })
                    .icon(icon)
                    .desc(desc)
                    .dimensions(0, 0, 210, 30)
                    .build();
        }
        public static PlaceListButton createContentButton(Component name, String icon, String command)
        {
            return PlaceListButton.builder(name, button -> {
                sendChatCommand(command);
                Minecraft.getInstance().setScreen(null);
            }).icon(icon).dimensions(0, 0, 210, 30).build();
        }

        public static PlaceListButton createContentButton(Component name, String icon, String desc, String command)
        {
            return PlaceListButton.builder(name, button -> {
                sendChatCommand(command);
                Minecraft.getInstance().setScreen(null);
            })
                    .icon(icon)
                    .desc(desc)
                    .dimensions(0, 0, 210, 30)
                    .build();
        }
        public static PlaceListButton createContentButton(Component name, PlayerSkin skin, String command)
        {
            return PlaceListButton.builder(name, button -> {
                sendChatCommand(command);
                Minecraft.getInstance().setScreen(null);
            }).icon(skin).dimensions(0, 0, 210, 30).build();
        }

        /**
         * @Override 上方函数的重载
         * 创建Content按钮   (无命令，(二级菜单))
         */
        public static PlaceListButton createContentButton(Component name, Identifier icon, Runnable action)
        {
            return PlaceListButton.builder(name, button -> action.run())
                    .icon(icon).dimensions(0, 0, 210, 30).build();
        }
        public static PlaceListButton createContentButton(Component name, Identifier icon, String desc, Runnable action)
        {
            return PlaceListButton.builder(name, button -> action.run())
                    .icon(icon)
                    .desc(desc)
                    .dimensions(0, 0, 210, 30)
                    .build();
        }
        public static PlaceListButton createContentButton(Component name, String icon, Runnable action)
        {
            return PlaceListButton.builder(name, button -> action.run())
                    .icon(icon).dimensions(0, 0, 210, 30).build();
        }

        public static PlaceListButton createContentButton(Component name, String icon, String desc, Runnable action)
        {
            return PlaceListButton.builder(name, button -> action.run())
                    .icon(icon)
                    .desc(desc)
                    .dimensions(0, 0, 210, 30)
                    .build();
        }

        public static SwitchButton createSettingsButton(Component name, boolean flag, Runnable action)
        {
            return SwitchButton.builder(name, button -> action.run())
                    .position(0, 0).flag(flag).build();
        }



        public void switchContent(SideType type)
        {
            currentSideType = type;

            switch (this.type)
            {
                case NORMAL -> showContent(contentButtonsMap.get(type), PlaceListButton.class);
                case OPTION -> showContent(switchButtonsMap.get(type), SwitchButton.class);
                case CUSTOM -> showContent(type, () ->
                {

                });
            }
        }

    //    public <T extends AbstractWidget> void showContent(List<T> buttons, Class<T> type)
    //    {
    //        //清除旧Content按钮
    ////        for (GuiEventListener child : new ArrayList<>(children()))
    ////        {
    ////            if (child instanceof PlaceListButton || child instanceof SwitchButton)
    ////            {
    ////                removeWidget(child);
    ////            }
    ////        }
    ////
    ////        // 添加Content按钮
    ////        int startX = panelX + 129;
    ////        int startY = panelY + 10;
    ////
    ////        int i = 0;
    ////        int GAP = 0;
    ////
    ////        for (T button : buttons)
    ////        {
    ////            if(button instanceof PlaceListButton)
    ////            {
    ////                GAP = 30;
    ////            }else if(button instanceof SwitchButton)
    ////            {
    ////                GAP = 25;
    ////                startX = panelX + 129 + 10;
    ////                startY = panelY + 20;
    ////            }
    ////            button.setPosition(startX, startY + (i * GAP));
    ////            addRenderableWidget(button);
    ////            i++;
    ////        }
    //    }

        public <T extends AbstractWidget> void showContent(List<T> buttons, Class<T> type)
        {
            clearAllWidgets();
            // 确定起始坐标（相对于滚动面板的内部坐标）
            int startX = panelX + 129;
            int startY = panelY + 10;
            int gap = 0;

            if (!buttons.isEmpty())
            {
                T first = buttons.get(0);
                if (first instanceof PlaceListButton)
                {
                    gap = 30;
                } else if (first instanceof SwitchButton)
                {
                    gap = 25;
                    startX += 10; // SwitchButton 需要缩进
                    startY = panelY + 20;
                }
            }

            int i = 0;
            for (T button : buttons)
            {
                // 设置按钮位置为相对于滚动面板内部
                button.setPosition(startX, startY + (i * gap));
                // 将按钮添加到滚动面板（而不是直接 addRenderableWidget）
                scrollArea.children.add(button);
                i++;
            }

            // 刷新滚动区域的内容高度和滚动条状态
            scrollArea.refreshScrollAmount();
        }

        public void showContent(SideType type, Runnable runnable)
        {
            //由子类自定义Content内容
            clearAllWidgets();
            runnable.run();
        }

        public void clearAllWidgets()
        {
            for (GuiEventListener child : new ArrayList<>(children()))
            {
                if (child instanceof PlaceListButton || child instanceof SwitchButton || child instanceof MultiColumnTextFieldWidget)
                {
                    removeWidget(child);
                }
            }
            for (GuiEventListener child : new ArrayList<>(scrollArea.children))
            {
                if (child instanceof PlaceListButton || child instanceof SwitchButton || child instanceof MultiColumnTextFieldWidget)
                {
                    removeWidget(child);
                }
            }
            scrollArea.children.clear();
        }


        public void openSubMenu(List<PlaceListButton> buttons)
        {
            showContent(buttons, PlaceListButton.class);
        }

        private void renderSelectedIndicator(GuiGraphicsExtractor context)
        {
            SmallButton selectedButton = sideTypeButtonsMap.get(currentSideType);
            if (selectedButton != null)
            {
                int selectedX = selectedButton.getX();
                int selectedY = selectedButton.getY() + 7;

                context.blit(RenderPipelines.GUI_TEXTURED, SELECTED,
                        selectedX, selectedY, 0, 0, 3, 7,
                        3, 7);
            }
        }


        //========================================init=========================================
        @Override
        protected void init()
        {
            //==============初始化Map=================
            currentSideType = sideTypeMap.values().iterator().next();
            initializeContentButtons();
            initializeSwitchButtons();
    //        if(currentSideType == null)
    //        {
    //            System.out.println("null");
    //        }else System.out.println(currentSideType.typeName);

            clearWidgets();
            sideTypeButtonsMap.clear();

            //===============滚动区域===============
            panelX = (this.width - PANEL_WIDTH) / 2;
            panelY = (this.height - PANEL_HEIGHT) / 2;

            this.scrollArea = new ContentScrollPanel(
                    panelX + 129+30, panelY + 10,
                    200, 210);
            this.addRenderableWidget(this.scrollArea);


            //===========初始化侧边按钮==================
            int index = 0;
            for (Map.Entry<String, SideType> entry : sideTypeMap.entrySet())
            {
                String displayName = entry.getKey();
                SideType type = entry.getValue();

                SmallButton sideTypeButton = SmallButton.builder(Component.literal(displayName), button ->
                {
                    switchContent(type);
                }).iconIdentifier(type.icon).dimensions(panelX+1, panelY + 25 + (index * 22), 100, 22).build();
                sideTypeButtonsMap.put(type, sideTypeButton);
                addRenderableWidget(sideTypeButton);
                index++;
            }
            switchContent(currentSideType);
            super.init();
        }


        public void drawCyl(GuiGraphicsExtractor context) {
            Identifier cyl = ModIdentifier.get("textures/gui/screen/circle.png");
            final int DOT_SIZE = 5;
            final int SPACING = 20; // distance between centers
            int startY = 120;
            int endY = 370;
            int leftX = panelX + 10; // assume panelX is left edge
            int rightX = leftX + 340; // define width, or use a parameter

            int rowIndex = 0;
            for (int y = startY; y < endY; y += SPACING) {
                int offset = (rowIndex % 2 == 1) ? SPACING / 2 : 0; // offset half spacing for odd rows
                int x = leftX + offset;
                while (x < rightX) {
                    context.blit(RenderPipelines.GUI_TEXTURED, cyl, x, y, 0, 0, DOT_SIZE, DOT_SIZE, DOT_SIZE, DOT_SIZE,
                            ARGB.color(0.3f, BLACK));
//                    context.text(font, "⭐", x, y,
//                            ARGB.color(0.3f, CommonColors.WHITE), false);
                    x += SPACING;
                }
                rowIndex++;
            }
        }

        //==============================render======================================

        @Override
        public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float a)
        {

            Identifier large;
            Identifier side;
            if(Mitayclient.getConfig().isDarkShown())
            {
                large = MATRIX_LARGE_DARK;
                side = MATRIX_SIDE_DARK;
            }else
            {
                large = MATRIX_LARGE;
                side = MATRIX_SIDE;
            }
            context.blit(RenderPipelines.GUI_TEXTURED, large,
                    panelX - 14, panelY - 14, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, PANEL_WIDTH, PANEL_HEIGHT,
                    ARGB.color(0.83f, Mitayclient.getConfig().isDarkShown() ? GRAY : WHITE));
            context.blit(RenderPipelines.GUI_TEXTURED, side,
                    panelX, panelY, 0, 0, 115, 263, 115, 263,
                    ARGB.color(0.35f, Mitayclient.getConfig().isDarkShown() ? GRAY : WHITE));

//            drawCyl(context);

            renderSelectedIndicator(context);
            super.extractRenderState(context, mouseX, mouseY, a);
        }
    }


    //----------滚动区域-------------
    class ContentScrollPanel extends AbstractScrollArea
    {
        @Override
        public boolean preeditUpdated(@Nullable PreeditEvent event) {
            if (focusedChild != null) {
                return focusedChild.preeditUpdated(event);
            }
            return super.preeditUpdated(event);
        }
        private double dragStartY;
        private double dragStartScrollAmount;
        public final List<AbstractWidget> children = new ArrayList<>();
        private boolean scrolling = false; // 是否正在拖动滚动条
        private boolean scrollbarDragging = false; // 仅用于标记“当前拖动由滚动条触发”
        private AbstractWidget focusedChild;

        public ContentScrollPanel(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), AbstractScrollArea.defaultSettings(4));
        }

        @Override
        protected int contentHeight() {
            if (children.isEmpty()) return 0;
            int maxBottom = 0;
            for (AbstractWidget child : children) {
                maxBottom = Math.max(maxBottom, child.getBottom());
            }
            return maxBottom - this.getY();
        }

        // ---------- 焦点管理 ----------
        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (!focused) {
                // 当容器失去焦点时，通知子控件
                if (focusedChild != null) {
                    focusedChild.setFocused(false);
                    focusedChild = null;
                }
            }
        }

        private void setFocusedChild(AbstractWidget child) {
            if (focusedChild == child) return;
            if (focusedChild != null) {
                focusedChild.setFocused(false);
            }
            focusedChild = child;
            if (focusedChild != null) {
                focusedChild.setFocused(true);
                // 确保容器本身在 Screen 中处于焦点状态，这样事件才会被分派到这里
                if (!this.isFocused()) {
                    this.setFocused(true);
                }
            }
        }

        // ---------- 自定义滚动条可见逻辑 ----------
        private boolean isScrollbarVisible() {
            return contentHeight() > this.getHeight();
        }

        public boolean isOverScrollbar(double mouseX, double mouseY)
        {
            int scrollbarWidth = 6;
            int scrollbarX = this.getRight() - scrollbarWidth - 10; // 与绘制一致
            return mouseX >= scrollbarX &&
                    mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= this.getY() &&
                    mouseY <= this.getBottom();
        }

        private void renderCustomScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
        {
            if (!isScrollbarVisible()) return;

            int scrollbarWidth = 6;
            int scrollbarX = this.getRight() - scrollbarWidth - 10; // 左移10像素
            int scrollbarHeight = this.getHeight();

            // 绘制滚动条背景（可选）
            graphics.fill(scrollbarX, this.getY(), scrollbarX + scrollbarWidth +1, this.getBottom(),ARGB.color(0.8f, 0xFF646464));

            // 计算滑块位置和高度
            int contentH = contentHeight();
            int viewH = this.getHeight();
            float thumbHeight = Math.max(30, (float) viewH / contentH * viewH);
            float maxScroll = contentH - viewH;
            if (maxScroll <= 0) return;
            float thumbY = (float) (this.getY() + (scrollAmount() / maxScroll) * (viewH - thumbHeight));

            // 绘制滑块
            graphics.fill(scrollbarX, (int) thumbY, scrollbarX + scrollbarWidth +1, (int) (thumbY + thumbHeight), ARGB.color(0.5f, 0xFFAAAAAA));
        }

        private boolean isOverThumb(double mouseX, double mouseY)
        {
            int scrollbarWidth = 6;
            int scrollbarX = this.getRight() - scrollbarWidth - 10;
            int contentH = contentHeight();
            int viewH = this.getHeight();
            if (contentH <= viewH) return false;

            float thumbHeight = Math.max(30, (float) viewH / contentH * viewH);
            float maxScroll = contentH - viewH;
            float thumbY = (float) (this.getY() + (scrollAmount() / maxScroll) * (viewH - thumbHeight));

            return mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth
                    && mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
        }

        private void jumpScrollToMouse(double mouseY)
        {
            int viewH = this.getHeight();
            int contentH = contentHeight();
            float maxScroll = contentH - viewH;
            float thumbHeight = Math.max(30, (float) viewH / contentH * viewH);
            float availableTrack = viewH - thumbHeight;
            float relativeY = (float) (mouseY - this.getY() - thumbHeight / 2);
            float target = (relativeY / availableTrack) * maxScroll;
            setScrollAmount(Math.clamp(target, 0, maxScroll));
        }

        // ---------- 渲染 ----------
        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            graphics.enableScissor(this.getX() - 35, this.getY(), this.getRight(), this.getBottom() + 18);
            int scrollOffset = (int) this.scrollAmount();

            for (AbstractWidget child : this.children) {
                int savedY = child.getY();
                child.setY(savedY - scrollOffset);
                child.extractRenderState(graphics, mouseX, mouseY, a);
                child.setY(savedY);
            }

            graphics.disableScissor();
//            this.extractScrollbar(graphics, mouseX, mouseY);
            renderCustomScrollbar(graphics, mouseX, mouseY);
        }

        // ---------- 鼠标事件重写 ----------
        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!this.isMouseOver(event.x(), event.y()))
            {
                setFocusedChild(null);
                return false;
            }

            // 点击自定义滚动条区域
            if (isScrollbarVisible() && isOverScrollbar(event.x(), event.y())) {
                // 如果点在了滑块上 -> 开始拖动
                // 为了方便，也可以点轨道空白处直接跳转（像原版一样）
                if (isOverThumb(event.x(), event.y())) {
                    this.scrollbarDragging = true;
                    this.dragStartY = event.y();
                    this.dragStartScrollAmount = scrollAmount();
                } else {
                    // 点击轨道空白处：将滑块跳到对应位置
                    jumpScrollToMouse(event.y());
                }
                return true;
            }

            // 分发给内容子控件
            int scrollOffset = (int) this.scrollAmount();
            for (AbstractWidget child : children) {
                int oldY = child.getY();
                child.setY(oldY - scrollOffset);
                if (child.mouseClicked(event, doubleClick)) {
                    child.setY(oldY);
                    // 如果被点击的子控件是 AbstractWidget，将其设为焦点
                    if (child instanceof AbstractWidget widget) {
                        setFocusedChild(widget);
                    }
                    return true;
                }
                child.setY(oldY);
            }

            // 点击空白区域清除焦点
            setFocusedChild(null);
            return true;
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
            if (scrollbarDragging) {
                int contentH = contentHeight();
                int viewH = this.getHeight();
                float maxScroll = contentH - viewH;
                float thumbHeight = Math.max(30, (float) viewH / contentH * viewH);
                float availableTrack = viewH - thumbHeight;

                double deltaY = event.y() - dragStartY;
                double scrollDelta = (deltaY / availableTrack) * maxScroll;
                double newScroll = dragStartScrollAmount + scrollDelta;
                setScrollAmount(Math.clamp(newScroll, 0, maxScroll));
                return true;
            }

            // 内容子控件拖动
            int scrollOffset = (int) this.scrollAmount();
            for (AbstractWidget child : children) {
                int oldY = child.getY();
                child.setY(oldY - scrollOffset);
                if (child.mouseDragged(event, dx, dy)) {
                    child.setY(oldY);
                    return true;
                }
                child.setY(oldY);
            }
            return false;
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            if (scrollbarDragging) {
                scrollbarDragging = false;
                return true;
            }
            int scrollOffset = (int) this.scrollAmount();
            for (AbstractWidget child : children) {
                int oldY = child.getY();
                child.setY(oldY - scrollOffset);
                if (child.mouseReleased(event)) {
                    child.setY(oldY);
                    return true;
                }
                child.setY(oldY);
            }
            return false;
        }

//        @Override
//        public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
//            if (scrolling) {
//                return super.mouseDragged(event, dx, dy);
//            }
//            int scrollOffset = (int) this.scrollAmount();
//            for (AbstractWidget child : children) {
//                int oldY = child.getY();
//                child.setY(oldY - scrollOffset);
//                boolean consumed = child.mouseDragged(event, dx, dy);
//                child.setY(oldY);
//                if (consumed) return true;
//            }
//            return false;
//        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            if (this.isMouseOver(mouseX, mouseY)) {
                return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY * 4.0);
            }
            return false;
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            if (!this.isMouseOver(mouseX, mouseY)) return;

            int scrollOffset = (int) this.scrollAmount();
            for (AbstractWidget child : this.children) {
                int savedY = child.getY();
                child.setY(savedY - scrollOffset);
                child.mouseMoved(mouseX, mouseY);
                child.setY(savedY);
            }
        }

        // ---------- 键盘事件转发 ----------
        @Override
        public boolean keyPressed(KeyEvent event) {
            if (focusedChild != null && focusedChild.keyPressed(event)) {
                return true;
            }
            // 如果子控件不处理，再交给原版滚动逻辑（比如方向键滚动）
            return super.keyPressed(event);
        }

        @Override
        public boolean charTyped(CharacterEvent event) {
            if (focusedChild != null && focusedChild.charTyped(event)) {
                return true;
            }
            return super.charTyped(event);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {}
    }
