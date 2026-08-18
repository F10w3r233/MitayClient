package com.flower.mitayclient.GUI.buttons.Menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;







@Environment(EnvType.CLIENT)
public class MenuButton extends MenuPressable
{
    protected final PressAction onPress;
    public static Builder builder(Component message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    protected MenuButton(int x, int y, int width, int height, Component message, PressAction onPress)
    {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);

    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Component message;
        private final PressAction onPress;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;

        public Builder(Component message, PressAction onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width)
        {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height)
        {
            return this.position(x, y).size(width, height);
        }

        public MenuButton build() {
            MenuButton buttonWidget = new MenuButton(this.x, this.y, this.width, this.height, this.message, this.onPress);
            return buttonWidget;
        }

    }


    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(MenuButton button);
    }
}
