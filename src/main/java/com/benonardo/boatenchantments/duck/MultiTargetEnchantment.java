package com.benonardo.boatenchantments.duck;

import net.minecraft.enchantment.EnchantmentTarget;

public interface MultiTargetEnchantment {

    void setSecondaryTargets(EnchantmentTarget... value);
    EnchantmentTarget[] getSecondaryTargets();

}
