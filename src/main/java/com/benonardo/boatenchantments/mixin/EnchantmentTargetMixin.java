package com.benonardo.boatenchantments.mixin;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@SuppressWarnings("UnusedMixin")
@Mixin(EnchantmentTarget.class)
public abstract class EnchantmentTargetMixin {

    @Shadow
    public abstract boolean isAcceptableItem(Item item);

}