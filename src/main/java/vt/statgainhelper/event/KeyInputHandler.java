package vt.statgainhelper.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import vt.statgainhelper.screen.ItemSelectionScreen;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = "key.categories.statgainhelper";
    public static final String KEY_OPEN_ITEM_SELECTION = "key.statgainhelper.openitemselection";

    public static KeyBinding openItemSelection;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openItemSelection.wasPressed()) {
                client.setScreen(new ItemSelectionScreen());
            }
        });
    }

    public static void registerKeyBindings() {
        openItemSelection = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_ITEM_SELECTION,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0,
                KEY_CATEGORY
        ));

        registerKeyInputs();
    }
}
