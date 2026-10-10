package com.mr712.mrmousenavigation.handler;

import com.mr712.mrmousenavigation.config.MouseNavigationConfig;
import com.mr712.mrmousenavigation.mixin.AdvancementsScreenAccessor;
import com.mr712.mrmousenavigation.mixin.CreativeInventoryScreenAccessor;
import com.mr712.mrmousenavigation.mixin.RecipeBookResultsAccessor;
import com.mr712.mrmousenavigation.mixin.RecipeBookScreenAccessor;
import com.mr712.mrmousenavigation.mixin.RecipeBookWidgetAccessor;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class MouseNavigationHandler {
    private static final Deque<Screen> FORWARD_STACK = new ArrayDeque<>();
    private static Screen lastClosedScreen = null;

    private static boolean pressKey(Screen screen, int key) {
        return screen.keyPressed(new KeyInput(key, 0, 0));
    }

    public static boolean handleMouseButton(long window, int button, int action, int mods) {
        if (action != GLFW.GLFW_PRESS) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Screen currentScreen = client.currentScreen;
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
        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && currentScreen instanceof ChatScreen && config.enableChatMiddleClickSend) {
            if (pressKey(currentScreen, GLFW.GLFW_KEY_ENTER)) {
                playClickSound(client, config);
                return true;
            }
        }

        boolean isBack = (!config.invertButtons && button == GLFW.GLFW_MOUSE_BUTTON_4)
                || (config.invertButtons && button == GLFW.GLFW_MOUSE_BUTTON_5);
        boolean isForward = (!config.invertButtons && button == GLFW.GLFW_MOUSE_BUTTON_5)
                || (config.invertButtons && button == GLFW.GLFW_MOUSE_BUTTON_4);

        if (!isBack && !isForward) {
            return false;
        }

        // 1. Chat Screen Navigation
        if (currentScreen instanceof ChatScreen) {
            if (config.enableChatHistory) {
                int key = isBack ? GLFW.GLFW_KEY_UP : GLFW.GLFW_KEY_DOWN;
                if (pressKey(currentScreen, key)) {
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
        if (currentScreen instanceof BookScreen || currentScreen instanceof LecternScreen || currentScreen instanceof BookEditScreen) {
            if (config.enableBooks) {
                int key = isBack ? GLFW.GLFW_KEY_PAGE_UP : GLFW.GLFW_KEY_PAGE_DOWN;
                if (pressKey(currentScreen, key)) {
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
        if (currentScreen instanceof CreativeInventoryScreen creativeScreen) {
            if (config.enableCreativeTabs) {
                List<ItemGroup> tabs = ItemGroups.getGroups();
                if (tabs.size() > 1) {
                    ItemGroup currentTab = CreativeInventoryScreenAccessor.mrmousenavigation$getSelectedTab();
                    int currentIndex = tabs.indexOf(currentTab);
                    if (currentIndex == -1) {
                        currentIndex = 0;
                    }
                    int newIndex = isBack ? (currentIndex - 1 + tabs.size()) % tabs.size() : (currentIndex + 1) % tabs.size();
                    if (newIndex != currentIndex) {
                        ((CreativeInventoryScreenAccessor) creativeScreen).mrmousenavigation$setSelectedTab(tabs.get(newIndex));
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
        if (config.enableAdvancements) {
            if (currentScreen instanceof AdvancementsScreen advancementsScreen) {
                AdvancementsScreenAccessor accessor = (AdvancementsScreenAccessor) advancementsScreen;
                Map<AdvancementEntry, AdvancementTab> tabsMap = accessor.mrmousenavigation$getTabs();
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
                        advancementsScreen.selectTab(newTab.getRoot().getAdvancementEntry());
                        accessor.mrmousenavigation$getAdvancementHandler().selectTab(newTab.getRoot().getAdvancementEntry(), true);
                        playClickSound(client, config);
                        return true;
                    }
                }
                return true;
            }

            if (currentScreen.getClass().getName().contains("BetterAdvancementsScreen")
                    || currentScreen.getClass().getSimpleName().contains("Advancement")) {
                if (handleModdedAdvancements(currentScreen, isBack, client, config)) {
                    return true;
                }
                return true;
            }
        }

        // 5. Recipe Book Screens (Crafting, Inventory, etc.)
        if (config.enableRecipeBook && currentScreen instanceof RecipeBookScreen<?> recipeBookScreen) {
            RecipeBookWidget<?> recipeBookWidget = ((RecipeBookScreenAccessor) recipeBookScreen).mrmousenavigation$getRecipeBook();
            if (recipeBookWidget != null && recipeBookWidget.isOpen()) {
                RecipeBookResults results = ((RecipeBookWidgetAccessor) recipeBookWidget).mrmousenavigation$getRecipesArea();
                if (results != null) {
                    RecipeBookResultsAccessor resultsAccessor = (RecipeBookResultsAccessor) results;
                    int totalPages = resultsAccessor.mrmousenavigation$getPageCount();
                    if (totalPages > 1) {
                        int currentPage = resultsAccessor.mrmousenavigation$getCurrentPage();
                        int newPage = isBack ? (currentPage - 1 + totalPages) % totalPages : (currentPage + 1) % totalPages;
                        if (newPage != currentPage) {
                            resultsAccessor.mrmousenavigation$setCurrentPage(newPage);
                            resultsAccessor.mrmousenavigation$refreshResultButtons();
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
                    client.setScreen(forwardScreen);
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

    private static boolean handleModdedAdvancements(Screen screen, boolean isBack, MinecraftClient client, MouseNavigationConfig config) {
        try {
            Field tabsField = null;
            Field selectedTabField = null;
            Class<?> clazz = screen.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Field f : clazz.getDeclaredFields()) {
                    if (f.getName().equals("tabs") && Map.class.isAssignableFrom(f.getType())) {
                        tabsField = f;
                    }
                    if (f.getName().equals("selectedTab")) {
                        selectedTabField = f;
                    }
                }
                clazz = clazz.getSuperclass();
            }

            if (tabsField != null) {
                tabsField.setAccessible(true);
                Map<?, ?> tabsMap = (Map<?, ?>) tabsField.get(screen);
                if (tabsMap != null && tabsMap.size() > 1) {
                    List<?> tabs = new ArrayList<>(tabsMap.values());
                    Object currentTab = null;
                    if (selectedTabField != null) {
                        selectedTabField.setAccessible(true);
                        currentTab = selectedTabField.get(screen);
                    }
                    int currentIndex = tabs.indexOf(currentTab);
                    if (currentIndex == -1) {
                        currentIndex = 0;
                    }
                    int newIndex = isBack ? (currentIndex - 1 + tabs.size()) % tabs.size() : (currentIndex + 1) % tabs.size();
                    if (newIndex != currentIndex) {
                        Object newTab = tabs.get(newIndex);
                        Method getRootNode = null;
                        for (Method m : newTab.getClass().getMethods()) {
                            if ((m.getName().equals("getRootNode") || m.getName().equals("getRootAdvancement") || m.getName().equals("getRoot")) && m.getParameterCount() == 0) {
                                getRootNode = m;
                                break;
                            }
                        }
                        Object root = getRootNode != null ? getRootNode.invoke(newTab) : null;
                        Object entry = root;
                        if (root != null) {
                            for (Method m : root.getClass().getMethods()) {
                                if (m.getParameterCount() == 0 && (m.getName().equals("holder") || m.getName().equals("getAdvancementEntry") || m.getName().equals("method_53649") || m.getName().equals("getAdvancement"))) {
                                    Object nested = m.invoke(root);
                                    if (nested != null) {
                                        entry = nested;
                                        break;
                                    }
                                }
                            }
                        }
                        if (entry != null) {
                            for (Method m : screen.getClass().getMethods()) {
                                if (m.getParameterCount() == 1 && (m.getName().equals("selectTab") || m.getName().equals("onSelectedTabChanged") || m.getName().equals("method_2866"))) {
                                    m.invoke(screen, entry);
                                    break;
                                }
                            }
                            Field clientAdvField = null;
                            clazz = screen.getClass();
                            while (clazz != null && clazz != Object.class) {
                                for (Field f : clazz.getDeclaredFields()) {
                                    if (f.getName().equals("clientAdvancements") || f.getName().equals("advancements") || f.getName().equals("advancementHandler")) {
                                        clientAdvField = f;
                                        break;
                                    }
                                }
                                if (clientAdvField != null) break;
                                clazz = clazz.getSuperclass();
                            }
                            if (clientAdvField != null) {
                                clientAdvField.setAccessible(true);
                                Object clientAdv = clientAdvField.get(screen);
                                if (clientAdv != null) {
                                    for (Method m : clientAdv.getClass().getMethods()) {
                                        if (m.getParameterCount() == 2 && m.getParameterTypes()[1] == boolean.class
                                                && (m.getName().equals("selectTab") || m.getName().equals("setSelectedTab") || m.getName().equals("method_2864"))) {
                                            m.invoke(clientAdv, entry, true);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        playClickSound(client, config);
                        return true;
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    private static void triggerBack(Screen currentScreen) {
        recordClosedScreen(currentScreen);
        if (!pressKey(currentScreen, GLFW.GLFW_KEY_ESCAPE)) {
            currentScreen.close();
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

    private static void playClickSound(MinecraftClient client, MouseNavigationConfig config) {
        if (config.enableSound && client.getSoundManager() != null) {
            client.getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
        }
    }

    public static void clearHistory() {
        FORWARD_STACK.clear();
        lastClosedScreen = null;
    }
}
