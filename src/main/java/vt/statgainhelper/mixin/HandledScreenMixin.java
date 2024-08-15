package vt.statgainhelper.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vt.statgainhelper.callback.DrawSlotCallaback;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Inject(method = "drawSlot", at = @At(value = "TAIL"))
    public void drawSlot(DrawContext drawContext, Slot slot, CallbackInfo callbackInfo) {
        DrawSlotCallaback.EVENT.invoker().onDrawSlot(drawContext, slot);
    }
}
