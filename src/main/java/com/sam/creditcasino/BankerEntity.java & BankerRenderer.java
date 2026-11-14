package com.sam.creditcasino;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;
public class BankerEntity extends ZombieEntity {
    public BankerEntity(EntityType<? extends ZombieEntity> type, World world) { super(type, world); }
}
package com.sam.creditcasino;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
public class BankerRenderer extends ZombieRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation("creditcasino:textures/entity/roman.png");
    public BankerRenderer(EntityRendererManager renderManager) { super(renderManager); }
    @Override public ResourceLocation getTextureLocation(ZombieEntity entity) { return TEXTURE; }
}
