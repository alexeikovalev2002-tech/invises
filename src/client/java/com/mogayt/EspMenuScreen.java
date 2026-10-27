package com.mogayt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class EspMenuScreen extends Screen {

    public EspMenuScreen() {
        super(Text.literal("Invisible Player ESP"));
    }

    @Override
    protected void init() {
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal(BlockEspMod.espEnabled ? "ESP: ВКЛ" : "ESP: ВЫКЛ"),
                (button) -> {
                    BlockEspMod.espEnabled = !BlockEspMod.espEnabled;
                    button.setMessage(Text.literal(BlockEspMod.espEnabled ? "ESP: ВКЛ" : "ESP: ВЫКЛ"));
                }
        ).dimensions(this.width / 2 - 100, cy - 10, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Закрыть"),
                (button) -> this.close()
        ).dimensions(this.width / 2 - 100, cy + 30, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
