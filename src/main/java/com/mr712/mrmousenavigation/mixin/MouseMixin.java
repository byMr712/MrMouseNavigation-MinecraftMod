package com.mr712.mrmousenavigation.mixin;

import com.mr712.mrmousenavigation.handler.MouseNavigationHandler;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void mrmousenavigation$onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
        if (MouseNavigationHandler.handleMouseButton(window, input.button(), action, input.modifiers())) {
            ci.cancel();
        }
    }
}
