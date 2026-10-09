# Developer & Agent Guidelines — MrMouseNavigation (26.3)

## Версия и стек
- **Minecraft:** 26.3
- **Маппинги:** Mojang Official Mappings
- **Fabric Loader:** 0.19.5
- **Fabric Loom:** 1.18-SNAPSHOT
- **Fabric API:** 0.162.0+26.3
- **Java:** 25 (сборка на JDK 25 с release = 25)

## Особенности реализации в данной версии
Начиная с Minecraft 26.2/26.3 произошла дальнейшая оптимизация GUI подсистемы Mojang и Java 25:
- **Специфика Fabric Loom:** `loom { noIntermediateMappings() }` с локальным `empty-mappings.jar`.
- **Совместимость Mixin:** В `mousenavigation.mixins.json` задан уровень `"compatibilityLevel": "JAVA_25"`.
- **Целевой класс инжекции мыши:** `net.minecraft.client.MouseHandler` -> метод `onButton(long window, MouseButtonInfo info, int action)`.
- **Экраны и контекст:** Текущий активный экран получается через подсистему GUI `client.gui.screen()`, переход выполняется через `client.setScreenAndShow(Screen)`, закрытие через `currentScreen.onClose()`.
- **Ввод клавиш:** `net.minecraft.client.input.KeyEvent` и `com.mojang.blaze3d.platform.InputConstants`.
- **Книга рецептов:** `AbstractRecipeBookScreen<?>` и `RecipeBookComponent<?>` с аксессором `mousenavigation$getRecipeBook()`.
- **Графический рендеринг экрана настроек:** `extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta)`.
- **Звук клика:** `SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)`.

## Логика навигации
1. **Кнопка «Назад» (Mouse Button 4 / XBUTTON1 / нижняя боковая):**
   - Закрытие экранов и меню (эквивалент клавиши `Esc`).
   - Книги и кафедры: перелистывание назад.
   - Книга рецептов: переключение страниц рецептов назад.
   - Творческий инвентарь: переключение вкладок категорий назад.
   - Экран достижений: переключение категорий назад.
   - Чат (`ChatScreen`): история отправленных сообщений назад (стрелка `↑`).
2. **Кнопка «Вперёд» (Mouse Button 5 / XBUTTON2 / верхняя боковая):**
   - Перелистывание страниц книг и кафедр вперёд.
   - Переключение страниц книги рецептов вперёд.
   - Переключение вкладок творческого инвентаря вперёд.
   - Переключение категорий достижений вперёд.
   - Чат: история сообщений вперёд (стрелка `↓`).
   - История экранов: возвращение во вложенный экран, если вы вышли из него назад.
3. **Средняя кнопка мыши (СКМ / Mouse Button 3 / колёсико):**
   - В чате (`ChatScreen`): мгновенная отправка сообщения (эквивалент клавиши `Enter`).
4. **Конфигурация:**
   - Интеграция с ModMenu и экран настроек (`MouseNavigationConfigScreen`).
   - Конфигурационный файл: `config/mousenavigation.json`.

## Команды сборки
- Полная сборка JAR: `./gradlew build`
- Выходной файл: `build/libs/MrMouseNavigation-Fabric-26.3-byMr712-v1.0.jar`