package com.mr712.mrmousenavigation.mixin;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {
    @Accessor("tabs")
    Map<AdvancementHolder, AdvancementTab> mrmousenavigation$getTabs();

    @Accessor("selectedTab")
    AdvancementTab mrmousenavigation$getSelectedTab();

    @Accessor("advancements")
    ClientAdvancements mrmousenavigation$getAdvancements();
}
