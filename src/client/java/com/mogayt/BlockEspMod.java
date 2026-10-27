package com.mogayt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BlockEspMod implements ClientModInitializer {

    // Включатель ESP
    public static boolean espEnabled = true;
    // Дистанция поиска
    public static int playerDistance = 64;

    // Список найденных невидимых игроков
    public static final List<PlayerEntity> foundInvisiblePlayers = new ArrayList<>();

    private static KeyBinding openMenuKey;

    // Слой для отрисовки сквозь стены
    public static final RenderLayer THROUGH_WALLS = RenderLayer.of(
            "mog-mod-through-walls",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINES,
            1536,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.LINES_PROGRAM)
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                    .writeMaskState(RenderPhase.COLOR_MASK)
                    .cull(RenderPhase.DISABLE_CULLING)
                    .build(false));

    @Override
    public void onInitializeClient() {
        // Регистрируем клавишу для открытия меню (H)
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mog-mod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.mog-mod.keys"
        ));

        // Регистрируем рендерер
        EspRenderer.register();

        // Каждый тик ищем невидимых игроков
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
                // Пропускаем себя
                if (p == client.player) continue;

                // Главное условие: игрок должен быть невидимым
                if (!p.isInvisible()) continue;

                // Проверка дистанции
                if (p.squaredDistanceTo(client.player) <= maxSq) {
                    foundInvisiblePlayers.add(p);
                }
            }
        });
    }
}
