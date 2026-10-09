package com.mr712.mrmousenavigation.gui;

import com.mr712.mrmousenavigation.config.MouseNavigationConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MouseNavigationConfigScreen extends Screen {
    private final Screen parent;
    private final MouseNavigationConfig tempConfig;

    public MouseNavigationConfigScreen(Screen parent) {
        super(Component.translatable("mrmousenavigation.config.title"));
        this.parent = parent;
        this.tempConfig = MouseNavigationConfig.getInstance().copy();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 34;
        int rowHeight = 24;
        int totalWidth = 320;
        int halfWidth = 156;
        int btnHeight = 20;

        int col1X = centerX - (totalWidth / 2);
        int col2X = col1X + halfWidth + 8;

        // Row 0: Master Enable (Full Width)
        addRenderableWidget(createToggleOption(
                col1X, startY, totalWidth, btnHeight,
                "mrmousenavigation.config.enabled",
                tempConfig.enabled,
                () -> tempConfig.enabled = !tempConfig.enabled,
                null
        ));

        // Row 1: Screen Back (Col 1) & Screen Forward (Col 2)
        addRenderableWidget(createToggleOption(
                col1X, startY + rowHeight, halfWidth, btnHeight,
                "mrmousenavigation.config.screen_back",
                tempConfig.enableScreenBack,
                () -> tempConfig.enableScreenBack = !tempConfig.enableScreenBack,
                "mrmousenavigation.config.screen_back.tooltip"
        ));
        addRenderableWidget(createToggleOption(
                col2X, startY + rowHeight, halfWidth, btnHeight,
                "mrmousenavigation.config.screen_forward",
                tempConfig.enableScreenForward,
                () -> tempConfig.enableScreenForward = !tempConfig.enableScreenForward,
                "mrmousenavigation.config.screen_forward.tooltip"
        ));

        // Row 2: Books (Col 1) & Recipe Book (Col 2)
        addRenderableWidget(createToggleOption(
                col1X, startY + rowHeight * 2, halfWidth, btnHeight,
                "mrmousenavigation.config.books",
                tempConfig.enableBooks,
                () -> tempConfig.enableBooks = !tempConfig.enableBooks,
                "mrmousenavigation.config.books.tooltip"
        ));
        addRenderableWidget(createToggleOption(
                col2X, startY + rowHeight * 2, halfWidth, btnHeight,
                "mrmousenavigation.config.recipe_book",
                tempConfig.enableRecipeBook,
                () -> tempConfig.enableRecipeBook = !tempConfig.enableRecipeBook,
                "mrmousenavigation.config.recipe_book.tooltip"
        ));

        // Row 3: Creative Tabs (Col 1) & Advancements (Col 2)
        addRenderableWidget(createToggleOption(
                col1X, startY + rowHeight * 3, halfWidth, btnHeight,
                "mrmousenavigation.config.creative_tabs",
                tempConfig.enableCreativeTabs,
                () -> tempConfig.enableCreativeTabs = !tempConfig.enableCreativeTabs,
                "mrmousenavigation.config.creative_tabs.tooltip"
        ));
        addRenderableWidget(createToggleOption(
                col2X, startY + rowHeight * 3, halfWidth, btnHeight,
                "mrmousenavigation.config.advancements",
                tempConfig.enableAdvancements,
                () -> tempConfig.enableAdvancements = !tempConfig.enableAdvancements,
                "mrmousenavigation.config.advancements.tooltip"
        ));

        // Row 4: Chat History (Col 1) & Chat Send on Middle Click (Col 2)
        addRenderableWidget(createToggleOption(
                col1X, startY + rowHeight * 4, halfWidth, btnHeight,
                "mrmousenavigation.config.chat_history",
                tempConfig.enableChatHistory,
                () -> tempConfig.enableChatHistory = !tempConfig.enableChatHistory,
                "mrmousenavigation.config.chat_history.tooltip"
        ));
        addRenderableWidget(createToggleOption(
                col2X, startY + rowHeight * 4, halfWidth, btnHeight,
                "mrmousenavigation.config.chat_middle_click",
                tempConfig.enableChatMiddleClickSend,
                () -> tempConfig.enableChatMiddleClickSend = !tempConfig.enableChatMiddleClickSend,
                "mrmousenavigation.config.chat_middle_click.tooltip"
        ));

        // Row 5: Click Sound (Col 1) & Invert Buttons (Col 2)
        addRenderableWidget(createToggleOption(
                col1X, startY + rowHeight * 5, halfWidth, btnHeight,
                "mrmousenavigation.config.sound",
                tempConfig.enableSound,
                () -> tempConfig.enableSound = !tempConfig.enableSound,
                "mrmousenavigation.config.sound.tooltip"
        ));
        addRenderableWidget(createToggleOption(
                col2X, startY + rowHeight * 5, halfWidth, btnHeight,
                "mrmousenavigation.config.invert",
                tempConfig.invertButtons,
                () -> tempConfig.invertButtons = !tempConfig.invertButtons,
                "mrmousenavigation.config.invert.tooltip"
        ));

        // Bottom Controls
        int bottomY = this.height - 28;
        addRenderableWidget(Button.builder(Component.translatable("mrmousenavigation.config.reset"), btn -> {
            tempConfig.resetToDefaults();
            rebuildWidgets();
        }).bounds(centerX - 155, bottomY, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("mrmousenavigation.config.save"), btn -> {
            MouseNavigationConfig current = MouseNavigationConfig.getInstance();
            current.enabled = tempConfig.enabled;
            current.enableScreenBack = tempConfig.enableScreenBack;
            current.enableScreenForward = tempConfig.enableScreenForward;
            current.enableBooks = tempConfig.enableBooks;
            current.enableRecipeBook = tempConfig.enableRecipeBook;
            current.enableCreativeTabs = tempConfig.enableCreativeTabs;
            current.enableAdvancements = tempConfig.enableAdvancements;
            current.enableChatHistory = tempConfig.enableChatHistory;
            current.enableChatMiddleClickSend = tempConfig.enableChatMiddleClickSend;
            current.enableSound = tempConfig.enableSound;
            current.invertButtons = tempConfig.invertButtons;
            current.save();
            this.onClose();
        }).bounds(centerX - 50, bottomY, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("mrmousenavigation.config.cancel"), btn -> this.onClose())
                .bounds(centerX + 55, bottomY, 100, 20).build());
    }

    private Button createToggleOption(int x, int y, int width, int height, String key, boolean state, Runnable onToggle, String tooltipKey) {
        MutableComponent label = Component.translatable(key).append(": ");
        if (state) {
            label.append(Component.translatable("mrmousenavigation.config.state.on").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        } else {
            label.append(Component.translatable("mrmousenavigation.config.state.off").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }

        Button.Builder builder = Button.builder(label, btn -> {
            onToggle.run();
            rebuildWidgets();
        }).bounds(x, y, width, height);

        if (tooltipKey != null) {
            builder.tooltip(Tooltip.create(Component.translatable(tooltipKey)));
        }

        return builder.build();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 14, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreenAndShow(this.parent);
        }
    }
}
