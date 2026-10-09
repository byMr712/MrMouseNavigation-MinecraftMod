package com.mr712.mrmousenavigation;

import com.mr712.mrmousenavigation.config.MouseNavigationConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MouseNavigationClient implements ClientModInitializer {
    public static final String MOD_ID = "mrmousenavigation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        MouseNavigationConfig.getInstance();
        LOGGER.info("MrMouseNavigation initialized successfully!");
    }
}
