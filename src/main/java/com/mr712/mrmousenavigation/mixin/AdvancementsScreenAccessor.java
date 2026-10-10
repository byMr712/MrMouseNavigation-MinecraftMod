package com.mr712.mrmousenavigation.mixin;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {
    @Accessor("tabs")
    Map<AdvancementEntry, AdvancementTab> mrmousenavigation$getTabs();

    @Accessor("selectedTab")
    AdvancementTab mrmousenavigation$getSelectedTab();

    @Accessor("advancementHandler")
    ClientAdvancementManager mrmousenavigation$getAdvancementHandler();
}
