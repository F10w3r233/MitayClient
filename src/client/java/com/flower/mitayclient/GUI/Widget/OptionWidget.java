package com.flower.mitayclient.GUI.Widget;

import com.flower.mitayclient.GUI.buttons.PlaceList.Large.PlaceListButton;
import com.flower.mitayclient.GUI.buttons.Switch.SwitchButton;
import com.flower.mitayclient.util.Resource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;

public class OptionWidget extends OptionPressable
{
    protected final PressAction onPress;
    public static SwitchButton thisSwitchButton;
    public OptionWidget(int x, int y, int width, int height, String description, PressAction onPress)
    {
        super(x, y, width, height, description);
        this.onPress = onPress;
    }

    public static Builder builder(String message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction
    {
        void onPress(OptionWidget button);
    }


    @Environment(EnvType.CLIENT)
    public static class Builder
    {
        private String description;
        private final PressAction onPress;
        private SwitchButton switchButton;
        private int x;
        private int y;
        private int width = 100 + 10;
        private int height = 22;

        public Builder(String description, PressAction onPress)
        {
            this.description = description;
            this.onPress = onPress;
        }

        public Builder position(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }



        public OptionWidget build()
        {
            return new OptionWidget(this.x, this.y, this.width, this.height, this.description, this.onPress);
        }
    }
}
