package com.flower.mitayclient.GUI.buttons.PlaceList.Large;

import com.flower.mitayclient.GUI.screen.ProfileUtil.PlayerProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.List;
import java.util.Map;


@Environment(EnvType.CLIENT)
public class PlaceListButton extends PlaceListPressable
{
    protected final PressAction onPress;

    public static Builder builder(Component message, PressAction onPress) {
        return new Builder(message, onPress);
    }

    protected PlaceListButton(int x, int y, int width, int height, Component message, PressAction onPress, String iconName, Identifier iconIdentifier, PlayerSkin skin, String type, PlayerProfile profile, String desc, List<String> output)
    {
        super(x, y, width, height, message, iconName, iconIdentifier, skin, type, profile, desc, output);
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

        public String thisIconName;
        public Identifier thisIconIdentifier;
        public PlayerSkin thisSkin;
        private String thisType;
        private PlayerProfile thisProfile;
        private String thisDesc;
        private List<String> thisOutput;

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

        public Builder icon(Identifier iconIdentifier)
        {
            thisIconIdentifier = iconIdentifier;
            return this;
        }

        public Builder icon(PlayerSkin skin)
        {
            thisSkin = skin;
            return this;
        }

        public Builder type(String type) {
            thisType = type;
            return this;
        }

        public Builder profile(PlayerProfile profile) {
            thisProfile = profile;
            return this;
        }

        public Builder desc(String desc) {
            thisDesc = desc;
            return this;
        }

        public Builder output(List<String> output) {
            thisOutput = output;
            return this;
        }

        public PlaceListButton build() {
            PlaceListButton buttonWidget = new PlaceListButton(this.x, this.y, this.width, this.height, this.message, this.onPress, thisIconName, thisIconIdentifier, thisSkin, thisType, thisProfile, thisDesc, thisOutput);
            return buttonWidget;
        }

    }


    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(PlaceListButton button);
    }
}