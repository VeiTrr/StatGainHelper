package vt.statgainhelper;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import vt.statgainhelper.callback.GenericContainerRenderCallback;
import vt.statgainhelper.callback.InventoryRenderCallback;

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
        InventoryRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (client.world != null) {
                if (client.player != null) {
                    MarkItemsInventory(client, drawContext, items);
                }
            }
        });
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            if (client.world != null) {
                if (client.player != null) {
                    MarkItemsHotbar(client, matrices, items);
                }
            }
        });

        GenericContainerRenderCallback.EVENT.register((drawContext, mouseX, mouseY, delta) -> {
            if (client.world != null) {
                if (client.player != null) {
                    MarkItemsContainer(client, drawContext, items);
                }
            }
        });
    }

    private static void MarkItemsHotbar(MinecraftClient Client, DrawContext drawContext, List<String> items) {
        double scaleFactor = Client.getWindow().getScaleFactor();
        int screenWidth = (int) (Client.getWindow().getWidth() / scaleFactor);
        int screenHeight = (int) (Client.getWindow().getHeight() / scaleFactor);
        ClientPlayerEntity Player = Client.player;
        int i = 0;
        for (ItemStack itemStack : Player.getInventory().main) {
            i++;
            if (itemStack != null && items.contains(GetItemId(itemStack))) {
                if (i < 10) {
                    if (Client.currentScreen == null) {
                        int x = (screenWidth / 2) - 91 + ((i - 1) * 20);
                        int y = screenHeight - 21;
                        drawContext.drawTexture(SELECTED_TEXTURE, x, y, 0, 0, 22, 22, 22, 22);
                    }
                }
            }
        }
    }

    private void MarkItemsInventory(MinecraftClient client, DrawContext drawContext, List<String> items) {
        double scaleFactor = client.getWindow().getScaleFactor();
        int screenWidth = (int) (client.getWindow().getWidth() / scaleFactor);
        int screenHeight = (int) (client.getWindow().getHeight() / scaleFactor);
        ClientPlayerEntity Player = client.player;
        int i = 0;
        for (ItemStack itemStack : Player.getInventory().main) {
            i++;
            if (itemStack != null && items.contains(GetItemId(itemStack))) {
                if (i < 10) {
                    int x = (screenWidth / 2) - 101 + (i * 18);
                    int y = screenHeight / 2 + 56;
                    drawContext.drawTexture(SELECTED_TEXTURE, x, y, 0, 0, 22, 22, 22, 22);
                }
                for (int j = 0; j < 3; j++) {
                    if (i > 9 + j * 9 && i < 19 + j * 9) {
                        int x = (screenWidth / 2) - 101 + ((i - (9 + j * 9)) * 18);
                        int y = screenHeight / 2 - 2 + j * 18;
                        drawContext.drawTexture(SELECTED_TEXTURE, x, y, 0, 0, 22, 22, 22, 22);
                    }
                }
            }
        }
    }

    private void MarkItemsContainer(MinecraftClient client, DrawContext drawContext, List<String> items) {
        double scaleFactor = client.getWindow().getScaleFactor();
        int screenWidth = (int) (client.getWindow().getWidth() / scaleFactor);
        int screenHeight = (int) (client.getWindow().getHeight() / scaleFactor);
        ClientPlayerEntity Player = client.player;
        if (Player.currentScreenHandler instanceof GenericContainerScreenHandler) {
            GenericContainerScreenHandler container = (GenericContainerScreenHandler) Player.currentScreenHandler;
            int rows = container.getRows();
            if (rows == 3 || rows == 6) {
                int i = 0;
                for (Slot slot : container.slots) {
                    i++;
                    ItemStack itemStack = slot.getStack();
                    if (itemStack != null && items.contains(GetItemId(itemStack))) {
                        for (int j = 0; j < rows; j++) {
                            if (i > j * 9 && i < 10 + j * 9) {
                                int x = (screenWidth / 2) - 101 + ((i - (j * 9)) * 18);
                                int y = screenHeight / 2 + (rows == 3 ? -69 : -96) + j * 18;
                                drawContext.drawTexture(SELECTED_TEXTURE, x, y, 0, 0, 22, 22, 22, 22);
                            }
                        }
                        for (int j = 0; j < 3; j++) {
                            if (i > 9 * rows + j * 9 && i < 9 * rows + 10 + j * 9) {
                                int x = (screenWidth / 2) - 101 + ((i - (9 * rows + j * 9)) * 18);
                                int y = screenHeight / 2 + (rows == 3 ? -2 : 25) + j * 18;
                                drawContext.drawTexture(SELECTED_TEXTURE, x, y, 0, 0, 22, 22, 22, 22);
                            }
                        }
                        if (i > 9 * 3 + rows * 9 && i < 9 * 3 + rows * 9 + 10) {
                            int x = (screenWidth / 2) - 101 + ((i - (9 * 3 + rows * 9)) * 18);
                            int y = screenHeight / 2 + (rows == 3 ? 2 : 29) + 3 * 18;
                            drawContext.drawTexture(SELECTED_TEXTURE, x, y, 0, 0, 22, 22, 22, 22);
                        }
                    }
                }
            }
        }
    }


    public static String GetItemId(ItemStack itemStack) {
        return itemStack.getItem().getRegistryEntry().registryKey().getValue().toString();
    }
}