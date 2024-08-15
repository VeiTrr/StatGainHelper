package vt.statgainhelper.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface RenderHotbarItemCallback {
    Event<RenderHotbarItemCallback> EVENT = EventFactory.createArrayBacked(RenderHotbarItemCallback.class, (listeners) -> (context, x, y, f, player, stack, seed)
            -> {
        for (RenderHotbarItemCallback listener : listeners) {
            listener.onRenderHotbarItem(context, x, y, f, player, stack, seed);
        }
    });

    void onRenderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed);
}
