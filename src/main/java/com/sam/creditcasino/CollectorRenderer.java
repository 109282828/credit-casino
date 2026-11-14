package com.sam.creditcasino;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;

public class CollectorRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("creditcasino:textures/entity/collector.png.WEBP");
    public CollectorRenderer(EntityRendererManager renderManager) { super(renderManager); }
    @Override public ResourceLocation getTextureLocation(ZombieEntity entity) { return TEXTURE; }
}
