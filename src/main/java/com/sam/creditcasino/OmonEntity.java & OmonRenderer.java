package com.sam.creditcasino;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.world.World;
public class OmonEntity extends VindicatorEntity {
    public OmonEntity(EntityType<? extends VindicatorEntity> type, World world) { super(type, world); }
}
package com.sam.creditcasino;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.util.ResourceLocation;
public class OmonRenderer extends VindicatorRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("creditcasino:textures/entity/omon.png");
    public OmonRenderer(EntityRendererManager renderManager) { super(renderManager); }
    @Override public ResourceLocation getTextureLocation(VindicatorEntity entity) { return TEXTURE; }
}
