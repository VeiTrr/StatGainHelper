package vt.statgainhelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import vt.statgainhelper.callback.DrawSlotCallaback;
import vt.statgainhelper.callback.RenderHotbarItemCallback;

import java.util.List;

public class CallbackManager {

    private final MinecraftClient client;
    private final List<String> items;
    public static final Identifier SELECTED_TEXTURE = new Identifier("statgainhelper", "textures/gui/selection.png");

    public CallbackManager(MinecraftClient client, List<String> items) {
        this.client = client;
        this.items = items;
    }

    public void registerCallbacks() {

        DrawSlotCallaback.EVENT.register((drawContext, slot) -> {
            if (client.world != null) {
                if (client.player != null) {
                    MarkItemsInSlot(drawContext, slot, items);
                }
            }
        });

        RenderHotbarItemCallback.EVENT.register((context, x, y, f, player, stack, seed) -> {
            if (client.world != null) {
                if (client.player != null) {
                    MarkItemsHotbar(context, x, y, player, stack, items);
                }
            }
        });
    }

    private static void MarkItemsInSlot(DrawContext drawContext, Slot slot, List<String> items) {
        if (slot != null) {
            ItemStack itemStack = slot.getStack();
            if (itemStack != null && items.contains(GetItemId(itemStack))) {
                int x = slot.x - 2;
                int y = slot.y - 2;
                drawContext.drawTexture(SELECTED_TEXTURE, x, y, 0, 0, 20, 20, 20, 20);
            }
        }
    }

    private static void MarkItemsHotbar(DrawContext context, int x, int y, PlayerEntity player, ItemStack stack, List<String> items) {
        if (player != null) {
            if (player instanceof ClientPlayerEntity) {
                if (stack != null && items.contains(GetItemId(stack))) {
                    if (player.getInventory().main.indexOf(stack) < 9) {
                        context.drawTexture(SELECTED_TEXTURE, x - 2, y - 2, 0, 0, 20, 20, 20, 20);
                    }
                }
            }
        }
    }


    public static String GetItemId(ItemStack itemStack) {
        return itemStack.getItem().getRegistryEntry().registryKey().getValue().toString();
    }
}