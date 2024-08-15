package vt.statgainhelper.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vt.statgainhelper.callback.RenderHotbarItemCallback;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderHotbarItem", at = @At("TAIL"))
    public void renderHotbarItemMixin(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        RenderHotbarItemCallback.EVENT.invoker().onRenderHotbarItem(context, x, y, f, player, stack, seed);
    }
}
