package com.flower.mitayclient.GUI.buttons.Switch;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import static com.flower.mitayclient.util.Resource.getStringWidth;


@Environment(EnvType.CLIENT)
public class SwitchButton extends SwitchPressable
{
    protected final PressAction onPress;
    public SwitchButton(int i, int j, int k, int l, Component text, PressAction onPress, boolean flag)
    {
        super(i, j, k, l, text, flag);
        this.onPress = onPress;
        this.flag = flag;
    }

    public static Builder builder(Component message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    @Override
    public void onPress() {
        this.toggle();
        this.onPress.onPress(this);
    }

    // 改为实例方法
    public void toggle()
    {
        this.flag = !this.flag;
    }

    // 添加获取状态的方法
    public boolean isOn() {
        return this.flag;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private Component message = Component.empty();
        private final SwitchButton.PressAction onPress;
        private int x;
        private int y;
        private int width = getStringWidth(message) + 190;
        private int height = 20;
        private boolean flag;



        public Builder(Component message, SwitchButton.PressAction onPress) {
            this.message = message;
            this.onPress = onPress;
        }


        public Builder position(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder flag(boolean flag)
        {
            this.flag = flag;
            return this;
        }

        public SwitchButton build() {
            return new SwitchButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.flag);
        }


    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(SwitchButton button);
    }
}