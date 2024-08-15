package vt.statgainhelper.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;

public interface DrawSlotCallaback {
    Event<DrawSlotCallaback> EVENT = EventFactory.createArrayBacked(DrawSlotCallaback.class, (listeners) -> (drawContext, slot) -> {
        for (DrawSlotCallaback event : listeners) {
            event.onDrawSlot(drawContext, slot);
        }
    });

    void onDrawSlot(DrawContext drawContext, Slot slot);
}