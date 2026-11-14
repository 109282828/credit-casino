package com.sam.creditcasino;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;

public class BankerRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("creditcasino:textures/entity/roman.png.WEBP");
    public BankerRenderer(EntityRendererManager renderManager) { super(renderManager); }
    @Override public ResourceLocation getTextureLocation(ZombieEntity entity) { return TEXTURE; }
}
