package vt.statgainhelper.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vt.statgainhelper.callback.GenericContainerRenderCallback;

@Mixin(GenericContainerScreen.class)
public class GenericContainerScreenMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {
        GenericContainerRenderCallback.EVENT.invoker().onRender(drawContext, mouseX, mouseY, delta);
    }
}