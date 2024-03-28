package vt.statgainhelper;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

import java.util.List;

import static vt.statgainhelper.event.KeyInputHandler.registerKeyBindings;

public class StatGainHelperClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        registerKeyBindings();
        MinecraftClient Client = MinecraftClient.getInstance();
        List<String> items = List.of("minecraft:iron_sword", "item2", "item3");
        CallbackManager callbackManager = new CallbackManager(Client, items);
        callbackManager.registerCallbacks();
    }


}