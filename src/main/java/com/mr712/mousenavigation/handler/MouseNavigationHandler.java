package com.mr712.mousenavigation.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.mr712.mousenavigation.config.MouseNavigationConfig;
import com.mr712.mousenavigation.mixin.RecipeBookScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayDeque;
import java.util.Deque;

public class MouseNavigationHandler {
    private static final Deque<Screen> FORWARD_STACK = new ArrayDeque<>();
    private static Screen lastClosedScreen = null;

    private static boolean pressKey(Screen screen, int key) {
        return screen.keyPressed(new KeyEvent(key, 0, 0));
    }

    private static boolean pressKey(RecipeBookComponent<?> component, int key) {
        return component.keyPressed(new KeyEvent(key, 0, 0));
    }

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
            if (pressKey(currentScreen, InputConstants.KEY_RETURN)) {
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
                int key = isBack ? InputConstants.KEY_UP : InputConstants.KEY_DOWN;
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
        if (currentScreen instanceof BookViewScreen || currentScreen instanceof LecternScreen || currentScreen instanceof BookEditScreen) {
            if (config.enableBooks) {
                int key = isBack ? InputConstants.KEY_LEFT : InputConstants.KEY_RIGHT;
                if (pressKey(currentScreen, key) || pressKey(currentScreen, isBack ? InputConstants.KEY_PAGEUP : InputConstants.KEY_PAGEDOWN)) {
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
        if (currentScreen instanceof CreativeModeInventoryScreen) {
            if (config.enableCreativeTabs) {
                int key = isBack ? InputConstants.KEY_PAGEUP : InputConstants.KEY_PAGEDOWN;
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

        // 4. Advancements Screen
        if (currentScreen instanceof AdvancementsScreen) {
            if (config.enableAdvancements) {
                int key = isBack ? InputConstants.KEY_PAGEUP : InputConstants.KEY_PAGEDOWN;
                if (pressKey(currentScreen, key) || pressKey(currentScreen, isBack ? InputConstants.KEY_LEFT : InputConstants.KEY_RIGHT)) {
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

        // 5. Recipe Book Screens (Crafting, Inventory, etc.)
        if (config.enableRecipeBook && currentScreen instanceof AbstractRecipeBookScreen<?> recipeBookScreen) {
            RecipeBookComponent<?> recipeBook = ((RecipeBookScreenAccessor) recipeBookScreen).mousenavigation$getRecipeBook();
            if (recipeBook != null && recipeBook.isVisible()) {
                int key = isBack ? InputConstants.KEY_PAGEUP : InputConstants.KEY_PAGEDOWN;
                if (pressKey(recipeBook, key)) {
                    playClickSound(client, config);
                    return true;
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
        if (!pressKey(currentScreen, InputConstants.KEY_ESCAPE)) {
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

