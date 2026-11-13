package com.sam.creditcasino;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class UpgraderScreen extends ContainerScreen<UpgraderContainer> {

    private static final ResourceLocation BG = new ResourceLocation("textures/gui/container/generic_54.png");

    public UpgraderScreen(UpgraderContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.addButton(new Button(x + 40, y + 90, 40, 20, new StringTextComponent("30%"), b -> { this.menu.tryUpgrade(30); }));
        this.addButton(new Button(x + 85, y + 90, 40, 20, new StringTextComponent("50%"), b -> { this.menu.tryUpgrade(50); }));
        this.addButton(new Button(x + 130, y + 90, 40, 20, new StringTextComponent("70%"), b -> { this.menu.tryUpgrade(70); }));
        
        this.addButton(new Button(x + 60, y + 115, 60, 20, new StringTextComponent("UPGRADE"), b -> {
            this.menu.tryUpgrade(this.menu.currentChance);
        }));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(BG);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        this.font.drawShadow(matrixStack, "ITEM UPGRADER", x + 60, y + 10, 0xFFFFFF);
        this.font.drawShadow(matrixStack, "Шанс: " + this.menu.currentChance + "%", x + 65, y + 50, 0xFFAA00);
    }
}
