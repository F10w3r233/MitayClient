package com.flower.mitayclient.GUI.buttons.Badge;

import com.flower.mitayclient.GUI.buttons.Badge.BadgeButton;
import com.flower.mitayclient.GUI.screen.BadgeUtil.BadgeCache;
import com.flower.mitayclient.GUI.screen.BadgeUtil.networking.BadgesPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BadgeButton extends BadgePressable
{
    String type;
    BadgesPayload.BadgeInfo badgeInfo;
    protected final PressAction onPress;
    public static Builder builder(Component message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    protected BadgeButton(int x, int y, int width, int height, Component message, PressAction onPress, String type, BadgesPayload.BadgeInfo badgeInfo)
    {
        super(x, y, width, height, message, type, badgeInfo);
        this.onPress = onPress;
        this.type = type;
        this.badgeInfo = badgeInfo;
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
        private BadgesPayload.BadgeInfo badgeInfo;

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

            }
            return this;
        }
        public Builder badge(BadgesPayload.BadgeInfo badgeInfo)
        {
            this.badgeInfo = badgeInfo;
            return this;
        }

        public BadgeButton build() {
            BadgeButton buttonWidget = new BadgeButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.type, this.badgeInfo);
            return buttonWidget;
        }

    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(BadgeButton button);
    }
}

