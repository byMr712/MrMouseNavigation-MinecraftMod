package com.mr712.mrmousenavigation.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookPage.class)
public interface RecipeBookPageAccessor {
    @Accessor("totalPages")
    int mrmousenavigation$getTotalPages();

    @Accessor("currentPage")
    int mrmousenavigation$getCurrentPage();

    @Accessor("currentPage")
    void mrmousenavigation$setCurrentPage(int page);

    @Invoker("updateButtonsForPage")
    void mrmousenavigation$updateButtonsForPage();
}
