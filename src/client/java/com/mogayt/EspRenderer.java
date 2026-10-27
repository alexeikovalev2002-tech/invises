package com.mogayt;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class EspRenderer {

    public static void register() {
        // Используем AFTER_ENTITIES — в 1.21.11 это основное событие для рендера в мире
        WorldRenderEvents.AFTER_ENTITIES.register(EspRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        if (!BlockEspMod.espEnabled || BlockEspMod.foundInvisiblePlayers.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        MatrixStack matrices = context.matrixStack();
        if (matrices == null) return;

        Vec3d cameraPos = context.camera().getPos();
        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;

        // Отключаем тест глубины, чтобы линии были видны сквозь стены
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        // Стандартный слой для линий — не требует RenderPhase
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        for (PlayerEntity player : BlockEspMod.foundInvisiblePlayers) {
            Box box = player.getBoundingBox();
            float x1 = (float) (box.minX - cameraPos.x);
            float y1 = (float) (box.minY - cameraPos.y);
            float z1 = (float) (box.minZ - cameraPos.z);
            float x2 = (float) (box.maxX - cameraPos.x);
            float y2 = (float) (box.maxY - cameraPos.y);
            float z2 = (float) (box.maxZ - cameraPos.z);

            drawBox(matrix, buffer, x1, y1, z1, x2, y2, z2, 1f, 0f, 0f);
        }

        // Возвращаем всё обратно
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void drawBox(Matrix4f matrix, VertexConsumer buffer,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float r, float g, float b) {
        line(matrix, buffer, x1, y1, z1, x2, y1, z1, r, g, b);
        line(matrix, buffer, x1, y1, z1, x1, y2, z1, r, g, b);
        line(matrix, buffer, x1, y1, z1, x1, y1, z2, r, g, b);
        line(matrix, buffer, x2, y1, z1, x2, y2, z1, r, g, b);
        line(matrix, buffer, x2, y1, z1, x2, y1, z2, r, g, b);
        line(matrix, buffer, x1, y2, z1, x2, y2, z1, r, g, b);
        line(matrix, buffer, x1, y2, z1, x1, y2, z2, r, g, b);
        line(matrix, buffer, x1, y1, z2, x2, y1, z2, r, g, b);
        line(matrix, buffer, x1, y1, z2, x1, y2, z2, r, g, b);
        line(matrix, buffer, x2, y2, z1, x2, y2, z2, r, g, b);
        line(matrix, buffer, x2, y1, z2, x2, y2, z2, r, g, b);
        line(matrix, buffer, x1, y2, z2, x2, y2, z2, r, g, b);
    }

    private static void line(Matrix4f matrix, VertexConsumer buffer,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float r, float g, float b) {
        // В 1.21.11 для линий нужно указывать нормаль
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, 1f).normal(0f, 1f, 0f);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, 1f).normal(0f, 1f, 0f);
    }
}
