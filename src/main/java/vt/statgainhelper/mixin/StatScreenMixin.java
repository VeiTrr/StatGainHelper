package vt.statgainhelper.mixin;

import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vt.statgainhelper.screen.ItemSelectionScreen;

@Mixin(StatsScreen.class)
public class StatScreenMixin {
    @Inject(method = "createButtons", at = @At("TAIL"))
    public void createButtons(CallbackInfo ci) {
        ((ScreenAccessor) this).invokeAddDrawableChild(ButtonWidget.builder(Text.translatable("stat.statgainhelperButton"),
                        button -> ((ScreenAccessor) this).getClient().setScreen(new ItemSelectionScreen()))
                .dimensions(((ScreenAccessor) this).getWidth() / 2 + 120,
                        ((ScreenAccessor) this).getHeight() - 52,
                        80,
                        20)
                .build());
    }

}
