package com.flower.mitayclient.util;

import net.minecraft.resources.Identifier;

public class ModIdentifier
{
    public static Identifier get(String path)
    {
        return Identifier.fromNamespaceAndPath("mitayclient", path);
    }
}
