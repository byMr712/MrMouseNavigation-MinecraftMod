package com.mr712.mrmousenavigation.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    @Accessor("selectedTab")
    static CreativeModeTab mrmousenavigation$getSelectedTab() {
        throw new AssertionError();
    }

    @Invoker("selectTab")
    void mrmousenavigation$selectTab(CreativeModeTab tab);
}
