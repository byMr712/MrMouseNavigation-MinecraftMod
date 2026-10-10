# Developer & Agent Guidelines — MrMouseNavigation (1.21)

## Версия и стек
- **Minecraft:** 1.21
- **Маппинги:** Yarn (1.21+build.9)
- **Fabric Loader:** 0.19.5
- **Fabric Loom:** 1.18-SNAPSHOT
- **Fabric API:** Не требуется (мод полностью автономен)
- **Java:** 21 (сборка на JDK 25 с release = 21)

## Особенности реализации в данной версии
В Minecraft 1.21 используется Yarn-маппинг и классическая архитектура интерфейсов:
- **Перехват кликов мыши:** `@Mixin(net.minecraft.client.Mouse.class)` -> инжекция в метод `onMouseButton(long window, int button, int action, int mods)`.
- **Книга рецептов:** В версиях 1.21/1.21.1 интерфейс `RecipeBookScreen` отсутствует, экраны с книгой рецептов (`InventoryScreen`, `CraftingScreen` и др.) реализуют интерфейс `net.minecraft.client.gui.screen.recipebook.RecipeBookProvider`. Виджет получается через публичный метод `recipeBookProvider.getRecipeBookWidget()`.
- **Клавиши:** Нажатие клавиш вызывается через `currentScreen.keyPressed(int keyCode, int scanCode, int modifiers)`.
- **Звук клика:** `PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F)`.

## Логика навигации
1. **Кнопка «Назад» (Mouse Button 4 / XBUTTON1 / нижняя боковая):**
   - Закрытие экранов и меню (эквивалент клавиши `Esc`).
   - Книги и кафедры: перелистывание назад.
   - Книга рецептов: переключение страниц рецептов назад.
   - Творческий инвентарь: переключение вкладок категорий назад.
   - Экран достижений: переключение между вкладками достижений (нужен мод Better Advancements).
   - Чат (`ChatScreen`): история отправленных сообщений назад (стрелка `↑`).
2. **Кнопка «Вперёд» (Mouse Button 5 / XBUTTON2 / верхняя боковая):**
   - Перелистывание страниц книг и кафедр вперёд.
   - Переключение страниц книги рецептов вперёд.
   - Переключение вкладок творческого инвентаря вперёд.
   - Экран достижений: переключение между вкладками достижений (нужен мод Better Advancements).
   - Чат: история сообщений вперёд (стрелка `↓`).
   - История экранов: возвращение во вложенный экран, если вы вышли из него назад.
3. **Средняя кнопка мыши (СКМ / Mouse Button 3 / колёсико):**
   - В чате (`ChatScreen`): мгновенная отправка сообщения (эквивалент клавиши `Enter`).
4. **Конфигурация:**
   - Интеграция с ModMenu и экран настроек (`MouseNavigationConfigScreen`).
   - Конфигурационный файл: `config/mrmousenavigation.json`.

## Команды сборки
- Полная сборка JAR: `./gradlew build`
- Выходной файл: `build/libs/MrMouseNavigation-Fabric-1.21-byMr712-v1.0.jar`