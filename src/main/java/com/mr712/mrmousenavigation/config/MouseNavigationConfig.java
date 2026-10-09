package com.mr712.mrmousenavigation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MouseNavigationConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("MrMouseNavigation");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "mrmousenavigation.json");

    private static MouseNavigationConfig INSTANCE;

    public boolean enabled = true;
    public boolean enableScreenBack = true;
    public boolean enableScreenForward = true;
    public boolean enableBooks = true;
    public boolean enableRecipeBook = true;
    public boolean enableCreativeTabs = true;
    public boolean enableAdvancements = true;
    public boolean enableChatHistory = true;
    public boolean enableChatMiddleClickSend = true;
    public boolean enableSound = false;
    public boolean invertButtons = false;

    public static MouseNavigationConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public static MouseNavigationConfig load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                MouseNavigationConfig config = GSON.fromJson(reader, MouseNavigationConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load MrMouseNavigation config, falling back to defaults", e);
            }
        }
        MouseNavigationConfig config = new MouseNavigationConfig();
        config.save();
        return config;
    }

    public void save() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save MrMouseNavigation config", e);
        }
    }

    public void resetToDefaults() {
        this.enabled = true;
        this.enableScreenBack = true;
        this.enableScreenForward = true;
        this.enableBooks = true;
        this.enableRecipeBook = true;
        this.enableCreativeTabs = true;
        this.enableAdvancements = true;
        this.enableChatHistory = true;
        this.enableChatMiddleClickSend = true;
        this.enableSound = false;
        this.invertButtons = false;
    }

    public MouseNavigationConfig copy() {
        MouseNavigationConfig copy = new MouseNavigationConfig();
        copy.enabled = this.enabled;
        copy.enableScreenBack = this.enableScreenBack;
        copy.enableScreenForward = this.enableScreenForward;
        copy.enableBooks = this.enableBooks;
        copy.enableRecipeBook = this.enableRecipeBook;
        copy.enableCreativeTabs = this.enableCreativeTabs;
        copy.enableAdvancements = this.enableAdvancements;
        copy.enableChatHistory = this.enableChatHistory;
        copy.enableChatMiddleClickSend = this.enableChatMiddleClickSend;
        copy.enableSound = this.enableSound;
        copy.invertButtons = this.invertButtons;
        return copy;
    }
}
