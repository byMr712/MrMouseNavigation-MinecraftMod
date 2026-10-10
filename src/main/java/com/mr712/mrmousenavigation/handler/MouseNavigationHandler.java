package com.mr712.mrmousenavigation.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mr712.mrmousenavigation.config.MouseNavigationConfig;
import com.mr712.mrmousenavigation.mixin.AdvancementsScreenAccessor;
import com.mr712.mrmousenavigation.mixin.CreativeModeInventoryScreenAccessor;
import com.mr712.mrmousenavigation.mixin.RecipeBookComponentAccessor;
import com.mr712.mrmousenavigation.mixin.RecipeBookPageAccessor;
import com.mr712.mrmousenavigation.mixin.RecipeBookScreenAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class MouseNavigationHandler {
    private static final Deque<Screen> FORWARD_STACK = new ArrayDeque<>();
    private static Screen lastClosedScreen = null;

    public static boolean handleMouseButton(long window, int button, int action, int mods) {
        if (action != 1) { // 1 = GLFW_PRESS
            return false;
        }

        Minecraft client = Minecraft.getInstance();
        Screen currentScreen = client.gui.screen();
        if (currentScreen == null) {
            FORWARD_STACK.clear();
            lastClosedScreen = null;
            return false;
        }

        MouseNavigationConfig config = MouseNavigationConfig.getInstance();
        if (!config.enabled) {
            return false;
        }

        // Middle mouse button in ChatScreen sends the message
        if (button == InputConstants.MOUSE_BUTTON_MIDDLE && currentScreen instanceof ChatScreen && config.enableChatMiddleClickSend) {
            KeyEvent enterEvent = new KeyEvent(InputConstants.KEY_RETURN, 0, 0);
            if (currentScreen.keyPressed(enterEvent)) {
                playClickSound(client, config);
                return true;
            }
        }

        boolean isBack = (!config.invertButtons && button == InputConstants.MOUSE_BUTTON_4)
                || (config.invertButtons && button == InputConstants.MOUSE_BUTTON_5);
        boolean isForward = (!config.invertButtons && button == InputConstants.MOUSE_BUTTON_5)
                || (config.invertButtons && button == InputConstants.MOUSE_BUTTON_4);

        if (!isBack && !isForward) {
            return false;
        }

        // 1. Chat Screen Navigation
        if (currentScreen instanceof ChatScreen) {
            if (config.enableChatHistory) {
                KeyEvent historyEvent = isBack
                        ? new KeyEvent(InputConstants.KEY_UP, 0, 0)
                        : new KeyEvent(InputConstants.KEY_DOWN, 0, 0);
                if (currentScreen.keyPressed(historyEvent)) {
                    playClickSound(client, config);
                    return true;
                }
            }
            if (isBack && config.enableScreenBack) {
                triggerBack(currentScreen);
                playClickSound(client, config);
                return true;
            }
            return false;
        }

        // 2. Books & Lecterns
        if (currentScreen instanceof BookViewScreen || currentScreen instanceof LecternScreen || currentScreen instanceof BookEditScreen) {
            if (config.enableBooks) {
                KeyEvent pageEvent = isBack
                        ? new KeyEvent(InputConstants.KEY_PAGEUP, 0, 0)
                        : new KeyEvent(InputConstants.KEY_PAGEDOWN, 0, 0);
                if (currentScreen.keyPressed(pageEvent)) {
                    playClickSound(client, config);
                    return true;
                }
            }
            if (isBack && config.enableScreenBack) {
                triggerBack(currentScreen);
                playClickSound(client, config);
                return true;
            }
            return false;
        }

        // 3. Creative Inventory Tabs
        if (currentScreen instanceof CreativeModeInventoryScreen creativeScreen) {
            if (config.enableCreativeTabs) {
                List<CreativeModeTab> tabs = CreativeModeTabs.tabs();
                if (tabs.size() > 1) {
                    CreativeModeTab currentTab = CreativeModeInventoryScreenAccessor.mrmousenavigation$getSelectedTab();
                    int currentIndex = tabs.indexOf(currentTab);
                    if (currentIndex == -1) {
                        currentIndex = 0;
                    }
                    int newIndex = isBack ? (currentIndex - 1 + tabs.size()) % tabs.size() : (currentIndex + 1) % tabs.size();
                    if (newIndex != currentIndex) {
                        ((CreativeModeInventoryScreenAccessor) creativeScreen).mrmousenavigation$selectTab(tabs.get(newIndex));
                        playClickSound(client, config);
                        return true;
                    }
                }
            }
            if (isBack && config.enableScreenBack) {
                triggerBack(currentScreen);
                playClickSound(client, config);
                return true;
            }
            return false;
        }

        // 4. Advancements Screen
        if (currentScreen instanceof AdvancementsScreen advancementsScreen) {
            if (config.enableAdvancements) {
                AdvancementsScreenAccessor accessor = (AdvancementsScreenAccessor) advancementsScreen;
                Map<AdvancementHolder, AdvancementTab> tabsMap = accessor.mrmousenavigation$getTabs();
                if (tabsMap != null && tabsMap.size() > 1) {
                    List<AdvancementTab> tabs = new ArrayList<>(tabsMap.values());
                    AdvancementTab currentTab = accessor.mrmousenavigation$getSelectedTab();
                    int currentIndex = tabs.indexOf(currentTab);
                    if (currentIndex == -1) {
                        currentIndex = 0;
                    }
                    int newIndex = isBack ? (currentIndex - 1 + tabs.size()) % tabs.size() : (currentIndex + 1) % tabs.size();
                    if (newIndex != currentIndex) {
                        AdvancementTab newTab = tabs.get(newIndex);
                        accessor.mrmousenavigation$getAdvancements().setSelectedTab(newTab.getRootNode().holder(), true);
                        playClickSound(client, config);
                        return true;
                    }
                }
            }
            if (isBack && config.enableScreenBack) {
                triggerBack(currentScreen);
                playClickSound(client, config);
                return true;
            }
            return false;
        }

        // 5. Recipe Book Screens (Crafting, Inventory, etc.)
        if (config.enableRecipeBook && currentScreen instanceof AbstractRecipeBookScreen<?> recipeBookScreen) {
            RecipeBookComponent<?> recipeBook = ((RecipeBookScreenAccessor) recipeBookScreen).mrmousenavigation$getRecipeBook();
            if (recipeBook != null && recipeBook.isVisible()) {
                RecipeBookPage page = ((RecipeBookComponentAccessor) recipeBook).mrmousenavigation$getRecipeBookPage();
                if (page != null) {
                    RecipeBookPageAccessor pageAccessor = (RecipeBookPageAccessor) page;
                    int totalPages = pageAccessor.mrmousenavigation$getTotalPages();
                    if (totalPages > 1) {
                        int currentPage = pageAccessor.mrmousenavigation$getCurrentPage();
                        int newPage = isBack ? (currentPage - 1 + totalPages) % totalPages : (currentPage + 1) % totalPages;
                        if (newPage != currentPage) {
                            pageAccessor.mrmousenavigation$setCurrentPage(newPage);
                            pageAccessor.mrmousenavigation$updateButtonsForPage();
                            playClickSound(client, config);
                            return true;
                        }
                    }
                }
            }
        }

        // 6. Forward Screen History Navigation
        if (isForward && config.enableScreenForward) {
            if (!FORWARD_STACK.isEmpty()) {
                Screen forwardScreen = FORWARD_STACK.pop();
                if (forwardScreen != null) {
                    client.setScreenAndShow(forwardScreen);
                    playClickSound(client, config);
                    return true;
                }
            }
        }

        // 7. General Screen Back / Close (Esc)
        if (isBack && config.enableScreenBack) {
            triggerBack(currentScreen);
            playClickSound(client, config);
            return true;
        }

        return false;
    }

    private static void triggerBack(Screen currentScreen) {
        recordClosedScreen(currentScreen);
        KeyEvent escEvent = new KeyEvent(InputConstants.KEY_ESCAPE, 0, 0);
        if (!currentScreen.keyPressed(escEvent)) {
            currentScreen.onClose();
        }
    }

    private static void recordClosedScreen(Screen screen) {
        if (screen != null && !(screen instanceof ChatScreen)) {
            lastClosedScreen = screen;
            if (FORWARD_STACK.size() > 10) {
                FORWARD_STACK.removeLast();
            }
            FORWARD_STACK.push(screen);
        }
    }

    private static void playClickSound(Minecraft client, MouseNavigationConfig config) {
        if (config.enableSound && client.getSoundManager() != null) {
            client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    public static void clearHistory() {
        FORWARD_STACK.clear();
        lastClosedScreen = null;
    }
}
