package com.mr712.mrmousenavigation.integration;

import com.mr712.mrmousenavigation.gui.MouseNavigationConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return MouseNavigationConfigScreen::new;
    }
}
