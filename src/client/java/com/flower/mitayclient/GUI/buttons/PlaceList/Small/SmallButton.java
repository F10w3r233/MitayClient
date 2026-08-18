package com.flower.mitayclient.GUI.buttons.PlaceList.Small;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


@Environment(EnvType.CLIENT)
public class SmallButton extends SmallPressable
{
    protected final PressAction onPress;
    public static String thisIconName;
    public static Identifier thisIconIdentifier;
    public static Builder builder(Component message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    protected SmallButton(int x, int y, int width, int height, Component message, PressAction onPress, String iconName, Identifier iconIdentifier)
    {
        super(x, y, width, height, message, iconName, iconIdentifier);
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
        private int width = 100;
        private int height = 22;

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

        public Builder icon(String iconName)
        {
            thisIconName = iconName;
            return this;
        }

        public Builder iconIdentifier(Identifier iconIdentifier)
        {
            thisIconIdentifier = iconIdentifier;
            return this;
        }

        public SmallButton build() {
            SmallButton buttonWidget = new SmallButton(this.x, this.y, this.width, this.height, this.message, this.onPress, thisIconName, thisIconIdentifier);
            return buttonWidget;
        }

    }


    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(SmallButton button);
    }
}
