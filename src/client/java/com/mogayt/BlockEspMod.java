package com.mogayt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BlockEspMod implements ClientModInitializer {

    public static boolean espEnabled = true;
    public static int playerDistance = 64;
    public static final List<PlayerEntity> foundInvisiblePlayers = new ArrayList<>();

    private static KeyBinding openMenuKey;
    // В 1.21.11 категория создаётся через create, а не register
    public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(
            Identifier.of("mog-mod", "keys")
    );

    @Override
    public void onInitializeClient() {
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mog-mod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                CATEGORY
        ));

        EspRenderer.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new EspMenuScreen());
                }
            }

            if (client.world == null || client.player == null) return;

            foundInvisiblePlayers.clear();
            if (!espEnabled) return;

            double maxSq = (double) playerDistance * playerDistance;

            for (PlayerEntity p : client.world.getPlayers()) {
                if (p == client.player) continue;
                if (!p.isInvisible()) continue;
                if (p.squaredDistanceTo(client.player) <= maxSq) {
                    foundInvisiblePlayers.add(p);
                }
            }
        });
    }
}
