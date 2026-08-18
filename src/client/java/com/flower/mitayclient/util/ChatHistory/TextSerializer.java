package com.flower.mitayclient.util.ChatHistory;

import com.flower.mitayclient.util.Resource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TextSerializer
{
//    static RegistryWrapper.WrapperLookup registries = null;
    public static String serialize(FormattedCharSequence orderedText)
    {
        if(Minecraft.getInstance().getCameraEntity() != null)
        {
//            registries = MinecraftClient.getInstance().getCameraEntity().getRegistryManager();
            MutableComponent resultText = Component.empty();

            orderedText.accept((index, style, codePoint) -> {
                // 逐个字符构建文本
                String character = new String(Character.toChars(codePoint));
                MutableComponent charText = Component.literal(character).setStyle(style);
                resultText.append(charText);
                return true;
            });

            return Resource.textToJson(resultText);

        }
        return null;
    }

    public static String serialize(Component text)
    {
        return serialize(text.getVisualOrderText());
    }


    static Path path = FabricLoader.getInstance().getConfigDir().resolve("chatHistory.txt");
    List<Component> chatLines = new ArrayList<>();
    public static void saveToFile(String str)
    {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonth().getValue();
        int day = today.getDayOfMonth();

        LocalDate currentDate = LocalDate.now();
        LocalTime time = LocalTime.now().withNano(0);
        //2026-02-03-*-09:29-*Content*-{}
        if(Files.exists(path))
        {
            try
            {
                Files.writeString(path, Files.readString(path, StandardCharsets.UTF_8) == null ? "" : "\n" + currentDate + "-*-" + time + "-content-" + str,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.APPEND,
                        StandardOpenOption.WRITE);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }else
        {
            try
            {
                Files.createFile(path);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    public static Component deserialize(String str)
    {
        if(Minecraft.getInstance().getCameraEntity() != null)
        {
             return Resource.jsonToText(str);
        }
        return null;
    }

    public static List<String> readLongLines(String filePath) {
        try {
            return Files.readAllLines(Path.of(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static Component[] splitComponent(Component text, String delimiter) {
        if (delimiter.isEmpty()) {
            throw new IllegalArgumentException("分隔符不能为空");
        }

        // 步骤1：把 Component 平铺为字符序列，并为每个字符保留其样式
        List<Style> charStyles = new ArrayList<>();
        StringBuilder fullTextBuilder = new StringBuilder();

        text.visit((style, str) -> {
            for (int i = 0; i < str.length(); i++) {
                fullTextBuilder.append(str.charAt(i));
                charStyles.add(style);
            }
            return Optional.empty();
        }, Style.EMPTY);

        String fullText = fullTextBuilder.toString();

        // 步骤2：找出所有分隔符的起始位置
        List<Integer> splitPositions = new ArrayList<>();
        int searchFrom = 0;
        while (searchFrom < fullText.length()) {
            int idx = fullText.indexOf(delimiter, searchFrom);
            if (idx == -1) break;
            splitPositions.add(idx);
            searchFrom = idx + delimiter.length(); // 跳到分隔符之后继续搜索
        }

        // 如果没有分隔符，直接返回原文本
        if (splitPositions.isEmpty()) {
            return new Component[]{text};
        }

        // 步骤3：根据分隔符位置切割字符样式序列，并重建 Component
        List<Component> parts = new ArrayList<>();
        int segmentStart = 0;

        for (int splitIdx : splitPositions) {
            // 从 segmentStart 到 splitIdx(不含) 是一个分段
            MutableComponent part = buildComponentFromChars(fullText, charStyles, segmentStart, splitIdx);
            parts.add(part);
            segmentStart = splitIdx + delimiter.length(); // 下一段从分隔符之后开始
        }

        // 最后一段（最后一个分隔符之后的部分）
        if (segmentStart <= fullText.length()) {
            MutableComponent part = buildComponentFromChars(fullText, charStyles, segmentStart, fullText.length());
            parts.add(part);
        } else {
            // 如果原文本以分隔符结尾，则添加一个空组件（模拟 String.split 行为）
            parts.add(Component.empty());
        }

        return parts.toArray(new Component[0]);
    }

    /**
     * 根据字符索引范围，将相同样式的连续字符合并为一个 Component.literal 片段
     */
    private static MutableComponent buildComponentFromChars(String fullText, List<Style> charStyles, int start, int end) {
        MutableComponent result = Component.empty();
        if (start >= end) return result;

        int i = start;
        while (i < end) {
            Style currentStyle = charStyles.get(i);
            int j = i + 1;
            // 合并相同样式的连续字符
            while (j < end && Objects.equals(charStyles.get(j), currentStyle)) {
                j++;
            }
            String segment = fullText.substring(i, j);
            result.append(Component.literal(segment).setStyle(currentStyle));
            i = j;
        }
        return result;
    }

    public static Component stripLeadingSpaces(Component text, int count)
    {
        final int[] remaining = {count};
        MutableComponent result = Component.empty();

        text.visit((style, str) -> {
            if (remaining[0] <= 0) {
                if (!str.isEmpty()) {
                    result.append(Component.literal(str).setStyle(style));
                }
                return Optional.empty();
            }

            int index = 0;
            // 跳过开头的空格
            while (index < str.length() && str.charAt(index) == ' ' && remaining[0] > 0) {
                index++;
                remaining[0]--;
            }

            // 添加剩余部分
            if (index < str.length()) {
                result.append(Component.literal(str.substring(index)).setStyle(style));
            }

            return Optional.empty();
        }, Style.EMPTY);

        return result;
    }

    //去除开头指定字符串
    public static Component removeLeadingText(Component text, String toRemove)
    {
        MutableComponent result = Component.empty();
        StringBuilder allText = new StringBuilder();

        // 先收集所有文本内容
        text.visit((style, str) -> {
            allText.append(str);
            return Optional.empty();
        }, Style.EMPTY);

        // 检查是否以指定文本开头
        String fullText = allText.toString();
        if (!fullText.startsWith(toRemove)) {
            return text;
        }

        // 计算要从哪里开始保留
        int startIndex = toRemove.length();

        // 重新构建文本，从 startIndex 开始保留
        final int[] processed = {0};
        final boolean[] started = {false};

        text.visit((style, str) -> {
            if (str.isEmpty()) {
                return Optional.empty();
            }

            // 计算当前片段在整体文本中的位置
            int segmentStart = processed[0];
            int segmentEnd = segmentStart + str.length();

            // 如果当前片段完全在要删除的部分之前
            if (segmentEnd <= startIndex) {
                processed[0] = segmentEnd;
                return Optional.empty();
            }

            // 如果当前片段完全在要删除的部分之后
            if (segmentStart >= startIndex) {
                result.append(Component.literal(str).setStyle(style));
                processed[0] = segmentEnd;
                return Optional.empty();
            }

            // 当前片段跨越了删除边界，需要截取
            int cutIndex = startIndex - segmentStart;
            String remaining = str.substring(cutIndex);
            if (!remaining.isEmpty()) {
                result.append(Component.literal(remaining).setStyle(style));
            }
            processed[0] = segmentEnd;

            return Optional.empty();
        }, Style.EMPTY);

        return result;
    }

    //去除结尾指定字符串
    public static Component removeTrailingText(Component text, String toRemove)
    {
        MutableComponent result = Component.empty();
        StringBuilder allText = new StringBuilder();

        // 先收集所有文本内容
        text.visit((style, str) -> {
            allText.append(str);
            return Optional.empty();
        }, Style.EMPTY);

        // 检查是否以指定文本结尾
        String fullText = allText.toString();
        if (!fullText.endsWith(toRemove)) {
            return text;
        }

        // 计算要保留的字符数
        int keepLength = fullText.length() - toRemove.length();

        // 重新构建文本，保留前 keepLength 个字符
        final int[] processed = {0};
        final boolean[] done = {false};

        text.visit((style, str) -> {
            if (done[0]) {
                return Optional.empty();
            }

            for (int i = 0; i < str.length(); i++) {
                if (processed[0] >= keepLength) {
                    done[0] = true;
                    return Optional.empty();
                }

                // 添加当前字符
                String charStr = String.valueOf(str.charAt(i));
                result.append(Component.literal(charStr).setStyle(style));
                processed[0]++;
            }

            return Optional.empty();
        }, Style.EMPTY);

        return result;
    }

    /**
     * 将两个 Component 拼接在一起，保持各自的样式不变。
     *
     * @param first  第一个组件
     * @param second 第二个组件
     * @return 拼接后的新组件
     */
    public static Component concat(Component first, Component second) {
        // 创建一个空的可变组件，然后依次追加，避免修改原组件
        MutableComponent result = Component.empty();
        result.append(first);
        result.append(second);
        return result;
    }



    /**
     * 将整个 Component 的全部文字颜色设置为指定颜色（会覆盖原有的颜色设置）。
     *
     * @param text  原始组件
     * @param color 目标颜色（TextColor 对象）
     * @return 新组件，所有文字均为指定颜色
     */
    public static Component setColor(Component text, TextColor color) {
        MutableComponent result = Component.empty();
        text.visit((style, str) -> {
            // 用 withColor 替换掉原有的颜色，保留其他所有样式属性
            Style newStyle = style.withColor(color);
            result.append(Component.literal(str).setStyle(newStyle));
            return Optional.empty();
        }, Style.EMPTY);
        return result;
    }

    /**
     * 便捷方法：使用 RGB 整数值设置整串文本颜色。
     *
     * @param text 原始组件
     * @param rgb  颜色（例如 0xFF5555）
     * @return 新组件
     */
    public static Component setColor(Component text, int rgb) {
        return setColor(text, TextColor.fromRgb(rgb));
    }
}
