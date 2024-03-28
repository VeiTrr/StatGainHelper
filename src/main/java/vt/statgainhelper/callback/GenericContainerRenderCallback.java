package vt.statgainhelper.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;

public interface GenericContainerRenderCallback {
    Event<GenericContainerRenderCallback> EVENT = EventFactory.createArrayBacked(GenericContainerRenderCallback.class, (listeners) -> (drawContext, mouseX, mouseY, delta) -> {
        for (GenericContainerRenderCallback event : listeners) {
            event.onRender(drawContext, mouseX, mouseY, delta);
        }
    });

    void onRender(DrawContext drawContext, int mouseX, int mouseY, float delta);
}