package com.sam.creditcasino;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;
public class CollectorEntity extends ZombieEntity {
    public CollectorEntity(EntityType<? extends ZombieEntity> type, World world) { super(type, world); }
}
package com.sam.creditcasino;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
public class CollectorRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("creditcasino:textures/entity/collector.png");
    public CollectorRenderer(EntityRendererManager renderManager) { super(renderManager); }
    @Override public ResourceLocation getTextureLocation(ZombieEntity entity) { return TEXTURE; }
}
