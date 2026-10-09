package com.mr712.mrmousenavigation.mixin;

import com.mr712.mrmousenavigation.handler.MouseNavigationHandler;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void mrmousenavigation$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (MouseNavigationHandler.handleMouseButton(window, button, action, mods)) {
            ci.cancel();
        }
    }
}
