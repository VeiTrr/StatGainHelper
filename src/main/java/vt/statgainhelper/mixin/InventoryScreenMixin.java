package vt.statgainhelper.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vt.statgainhelper.callback.InventoryRenderCallback;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "render", at = @At(value = "TAIL"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;renderBackground(Lnet/minecraft/client/gui/DrawContext;)V")))
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {
        InventoryRenderCallback.EVENT.invoker().onInventoryRender(drawContext, delta);
    }
}