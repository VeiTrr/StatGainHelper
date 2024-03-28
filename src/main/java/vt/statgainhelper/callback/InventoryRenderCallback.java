package vt.statgainhelper.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;

public interface InventoryRenderCallback {
    Event<InventoryRenderCallback> EVENT = EventFactory.createArrayBacked(InventoryRenderCallback.class, (listeners) -> (matrixStack, delta) -> {
        for (InventoryRenderCallback event : listeners) {
            event.onInventoryRender(matrixStack, delta);
        }
    });

    void onInventoryRender(DrawContext drawContext, float tickDelta);
}
