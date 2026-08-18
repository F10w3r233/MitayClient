package com.flower.mitayclient.GUI.buttons.Accessibility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;







@Environment(EnvType.CLIENT)
public class AccessibilityButton extends AccessibilityPressableWidget
{
    String type;
    protected final PressAction onPress;
    public static Builder builder(Component message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    protected AccessibilityButton(int x, int y, int width, int height, Component message, PressAction onPress, String type)
    {
        super(x, y, width, height, message, type);
        this.onPress = onPress;
        this.type = type;
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
        String type;
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

        public Builder type(String type)
        {
            switch (type)
            {
                case "language" -> this.type = "language";
                case "accessibility" -> this.type = "accessibility";
                case "wallpaper" -> this.type = "wallpaper";
                case "about" -> this.type = "about";
                case "place" -> this.type = "place";
                case "player_list" -> this.type = "player_list";
                case "settings" -> this.type = "settings";
                case "chat_history" -> this.type = "chat_history";
                case "profile" -> this.type = "profile";
                case "tick" -> this.type = "tick";
                case "refresh" -> this.type = "refresh";
                case "add" -> this.type = "add";
            }
            return this;
        }

        public AccessibilityButton build() {
            AccessibilityButton buttonWidget = new AccessibilityButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.type);
            return buttonWidget;
        }

    }


    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(AccessibilityButton button);
    }
}
