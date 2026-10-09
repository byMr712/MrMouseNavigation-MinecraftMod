package com.mr712.mrmousenavigation.handler;

import com.mr712.mrmousenavigation.config.MouseNavigationConfig;
import com.mr712.mrmousenavigation.mixin.RecipeBookScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public class MouseNavigationHandler {
    private static final Deque<Screen> FORWARD_STACK = new ArrayDeque<>();
    private static Screen lastClosedScreen = null;

    private static boolean pressKey(Screen screen, int key) {
        return screen.keyPressed(new KeyInput(key, 0, 0));
    }

    private static boolean pressKey(RecipeBookWidget<?> widget, int key) {
        return widget.keyPressed(new KeyInput(key, 0, 0));
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
                int key = isBack ? GLFW.GLFW_KEY_LEFT : GLFW.GLFW_KEY_RIGHT;
                if (pressKey(currentScreen, key) || pressKey(currentScreen, isBack ? GLFW.GLFW_KEY_PAGE_UP : GLFW.GLFW_KEY_PAGE_DOWN)) {
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
        if (currentScreen instanceof CreativeInventoryScreen) {
            if (config.enableCreativeTabs) {
                int key = isBack ? GLFW.GLFW_KEY_PAGE_UP : GLFW.GLFW_KEY_DOWN;
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
                int key = isBack ? GLFW.GLFW_KEY_PAGE_UP : GLFW.GLFW_KEY_DOWN;
                if (pressKey(currentScreen, key) || pressKey(currentScreen, isBack ? GLFW.GLFW_KEY_LEFT : GLFW.GLFW_KEY_RIGHT)) {
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
        if (config.enableRecipeBook && currentScreen instanceof RecipeBookScreen<?> recipeBookScreen) {
            RecipeBookWidget<?> recipeBookWidget = ((RecipeBookScreenAccessor) recipeBookScreen).mrmousenavigation$getRecipeBook();
            if (recipeBookWidget != null && recipeBookWidget.isOpen()) {
                int key = isBack ? GLFW.GLFW_KEY_PAGE_UP : GLFW.GLFW_KEY_DOWN;
                if (pressKey(recipeBookWidget, key)) {
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
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F));
        }
    }

    public static void clearHistory() {
        FORWARD_STACK.clear();
        lastClosedScreen = null;
    }
}
