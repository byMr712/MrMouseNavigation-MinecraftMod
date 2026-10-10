# Developer & Agent Guidelines — MrMouseNavigation (26.1.2)

## Версия и стек
- **Minecraft:** 26.1.2
- **Маппинги:** Mojang Official Mappings
- **Fabric Loader:** 0.19.5
- **Fabric Loom:** 1.18-SNAPSHOT
- **Fabric API:** Не требуется (мод полностью автономен)
- **Java:** 25 (сборка на JDK 25 с release = 25)

## Особенности реализации в данной версии
Начиная с Minecraft 26.x произошёл переход на официальные маппинги Mojang и Java 25:
- **Специфика Fabric Loom:** Включено `loom { noIntermediateMappings() }` и локальный `empty-mappings.jar`.
- **Совместимость Mixin:** В `mrmousenavigation.mixins.json` задан уровень `"compatibilityLevel": "JAVA_25"`.
- **Целевой класс инжекции мыши:** `net.minecraft.client.MouseHandler` -> метод `onButton(long window, MouseButtonInfo info, int action)`. Кнопка и модификаторы берутся из `info.button()` и `info.modifiers()`.
- **Экраны и контекст:** `net.minecraft.client.Minecraft.getInstance()`, текущий экран `client.screen`, закрытие экрана `currentScreen.onClose()`, переход `client.setScreenAndShow(Screen)`.
- **Ввод клавиш:** `net.minecraft.client.input.KeyEvent` и `com.mojang.blaze3d.platform.InputConstants`.
- **Книга рецептов:** `AbstractRecipeBookScreen<?>` и `RecipeBookComponent<?>` с аксессором `mrmousenavigation$getRecipeBook()`, проверка видимости через `recipeBook.isVisible()`.
- **Графический рендеринг экрана настроек:** `extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta)` и `graphics.centeredText(...)`.
- **Звук клика:** `SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)`.

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
- Выходной файл: `build/libs/MrMouseNavigation-Fabric-26.1.2-byMr712-v1.0.jar`