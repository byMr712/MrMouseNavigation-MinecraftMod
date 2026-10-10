package com.mr712.mrmousenavigation.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeBookResults.class)
public interface RecipeBookResultsAccessor {
    @Accessor("pageCount")
    int mrmousenavigation$getPageCount();

    @Accessor("currentPage")
    int mrmousenavigation$getCurrentPage();

    @Accessor("currentPage")
    void mrmousenavigation$setCurrentPage(int page);

    @Invoker("refreshResultButtons")
    void mrmousenavigation$refreshResultButtons();
}
