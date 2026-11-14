package com.sam.creditcasino;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.world.World;

public class OmonEntity extends VindicatorEntity {
    public OmonEntity(EntityType<? extends VindicatorEntity> type, World world) {
        super(type, world);
    }
}
