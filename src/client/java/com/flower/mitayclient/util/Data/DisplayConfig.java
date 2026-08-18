package com.flower.mitayclient.util.Data;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DisplayConfig {
    private final Properties properties;
    private final Path configPath;

    // 配置键常量
    public static final String SHOW_TOP = "show_top";
    public static final String SHOW_HOTBAR = "show_hotbar";
    public static final String SHOW_ANIME = "show_anime";
    public static final String SHOW_DARK = "show_dark";
    public static final String SHOW_MUSIC = "show_music";
    public static final String SHOW_CONTAINER = "show_container";
    public static final String SHOW_TOOLBAR = "show_toolbar";
    public static final String SHOW_EFFECT = "show_effect";
    public static final String BG_INDEX = "bg_index";

    // 缓存字段（所有配置值在加载时初始化，修改时同步更新）
    private boolean cachedTopShown;
    private boolean cachedHotbarShown;
    private boolean cachedAnimeShown;
    private boolean cachedDarkShown;
    private boolean cachedMusicShown;
    private boolean cachedContainerShown;
    private boolean cachedToolbarShown;
    private boolean cachedEffectShown;
    private int cachedBgIndex;

    public DisplayConfig() {
        this.properties = new Properties();
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve("preference.properties");
        loadConfig();
    }

    private void loadConfig() {
        try {
            if (Files.exists(configPath)) {
                try (InputStream input = Files.newInputStream(configPath)) {
                    properties.load(input);
                }
                System.out.println("配置加载成功: " + configPath);
            } else {
                setDefaultValues();
                saveConfig();
                System.out.println("创建默认配置文件: " + configPath);
            }
        } catch (IOException e) {
            System.err.println("加载配置文件失败: " + e.getMessage());
            setDefaultValues();
        }
        // 无论何种情况，加载完成后同步缓存
        refreshCache();
    }

    private void setDefaultValues() {
        properties.setProperty(SHOW_TOP, "true");
        properties.setProperty(SHOW_HOTBAR, "true");
        properties.setProperty(SHOW_ANIME, "false");
        properties.setProperty(SHOW_DARK, "false");
        properties.setProperty(SHOW_MUSIC, "true");
        properties.setProperty(SHOW_CONTAINER, "false");
        properties.setProperty(SHOW_TOOLBAR, "true");
        properties.setProperty(SHOW_EFFECT, "true");
        properties.setProperty(BG_INDEX, "0");
    }

    private void refreshCache() {
        cachedTopShown = Boolean.parseBoolean(properties.getProperty(SHOW_TOP, "true"));
        cachedHotbarShown = Boolean.parseBoolean(properties.getProperty(SHOW_HOTBAR, "true"));
        cachedAnimeShown = Boolean.parseBoolean(properties.getProperty(SHOW_ANIME, "false"));
        cachedDarkShown = Boolean.parseBoolean(properties.getProperty(SHOW_DARK, "false"));
        cachedMusicShown = Boolean.parseBoolean(properties.getProperty(SHOW_MUSIC, "true"));
        cachedContainerShown = Boolean.parseBoolean(properties.getProperty(SHOW_CONTAINER, "false"));
        cachedToolbarShown = Boolean.parseBoolean(properties.getProperty(SHOW_TOOLBAR, "true"));
        cachedEffectShown = Boolean.parseBoolean(properties.getProperty(SHOW_EFFECT, "true"));
        cachedBgIndex = Integer.parseInt(properties.getProperty(BG_INDEX, "0"));
    }

    public void saveConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            try (OutputStream output = Files.newOutputStream(configPath)) {
                properties.store(output, "YourMod 显示设置\n这是你的模组的配置文件");
            }
            System.out.println("配置保存成功: " + configPath);
        } catch (IOException e) {
            System.err.println("保存配置文件失败: " + e.getMessage());
        }
    }

    // ========== 获取配置值（直接返回缓存，无解析开销） ==========
    public boolean isTopShown() {
        return cachedTopShown;
    }
    public boolean isHotbarShown() {
        return cachedHotbarShown;
    }
    public boolean isAnimeShown() {
        return cachedAnimeShown;
    }
    public boolean isDarkShown() {
        return cachedDarkShown;
    }
    public boolean isMusicShown() {
        return cachedMusicShown;
    }
    public boolean isContainerShown() {
        return cachedContainerShown;
    }
    public boolean isToolbarShown() {
        return cachedToolbarShown;
    }
    public boolean isEffectShown() {
        return cachedEffectShown;
    }
    public int getBG_Index() {
        return cachedBgIndex;
    }

    // ========== 设置配置值（更新缓存 + Properties + 保存） ==========
    public void setTopShown(boolean show) {
        cachedTopShown = show;
        properties.setProperty(SHOW_TOP, String.valueOf(show));
        saveConfig();
    }
    public void setHotbarShown(boolean show) {
        cachedHotbarShown = show;
        properties.setProperty(SHOW_HOTBAR, String.valueOf(show));
        saveConfig();
    }
    public void setAnimeShown(boolean show) {
        cachedAnimeShown = show;
        properties.setProperty(SHOW_ANIME, String.valueOf(show));
        saveConfig();
    }
    public void setDarkShown(boolean show) {
        cachedDarkShown = show;
        properties.setProperty(SHOW_DARK, String.valueOf(show));
        saveConfig();
    }
    public void setMusicShown(boolean show) {
        cachedMusicShown = show;
        properties.setProperty(SHOW_MUSIC, String.valueOf(show));
        saveConfig();
    }
    public void setContainerShown(boolean show) {
        cachedContainerShown = show;
        properties.setProperty(SHOW_CONTAINER, String.valueOf(show));
        saveConfig();
    }
    public void setToolbarShown(boolean show) {
        cachedToolbarShown = show;
        properties.setProperty(SHOW_TOOLBAR, String.valueOf(show));
        saveConfig();
    }
    public void setEffectShown(boolean show) {
        cachedEffectShown = show;
        properties.setProperty(SHOW_EFFECT, String.valueOf(show));
        saveConfig();
    }
    public void setBgIndex(int index) {
        cachedBgIndex = index;
        properties.setProperty(BG_INDEX, String.valueOf(index));
        saveConfig();
    }

    // ========== Toggle 方法（直接操作缓存） ==========
    public void toggleTopShown() {
        setTopShown(!cachedTopShown);
    }
    public void toggleHotbarShown() {
        setHotbarShown(!cachedHotbarShown);
    }
    public void toggleAnimeShown() {
        setAnimeShown(!cachedAnimeShown);
    }
    public void toggleDarkShown() {
        setDarkShown(!cachedDarkShown);
    }
    public void toggleMusicShown() {
        setMusicShown(!cachedMusicShown);
    }
    public void toggleContainerShown() {
        setContainerShown(!cachedContainerShown);
    }
    public void toggleToolbarShown() {
        setToolbarShown(!cachedToolbarShown);
    }
    public void toggleEffectShown() {
        setEffectShown(!cachedEffectShown);
    }

    // ========== 调试方法 ==========
    public Path getConfigPath() {
        return configPath;
    }
}