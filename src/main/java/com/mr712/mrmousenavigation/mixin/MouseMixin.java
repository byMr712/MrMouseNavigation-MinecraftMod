package com.mr712.mrmousenavigation.mixin;

import com.mr712.mrmousenavigation.handler.MouseNavigationHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseMixin {

    @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
    private void mrmousenavigation$onButton(long window, MouseButtonInfo info, int action, CallbackInfo ci) {
        if (MouseNavigationHandler.handleMouseButton(window, info.button(), action, info.modifiers())) {
            ci.cancel();
        }
    }
}
