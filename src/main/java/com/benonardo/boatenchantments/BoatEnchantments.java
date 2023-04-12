package com.benonardo.boatenchantments;

import com.benonardo.boatenchantments.duck.MultiTargetEnchantment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;

public class BoatEnchantments implements ModInitializer {

    @Override
    public void onInitialize() {
        EnchantmentTarget boatEnchantmentTarget;
        try {
            boatEnchantmentTarget = (EnchantmentTarget)EnchantmentTarget.class.getDeclaredField("BOAT").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        ((MultiTargetEnchantment)Enchantments.FROST_WALKER).setSecondaryTargets(boatEnchantmentTarget);
        ((MultiTargetEnchantment)Enchantments.THORNS).setSecondaryTargets(boatEnchantmentTarget);
        ((MultiTargetEnchantment)Enchantments.EFFICIENCY).setSecondaryTargets(boatEnchantmentTarget);
        ((MultiTargetEnchantment)Enchantments.UNBREAKING).setSecondaryTargets(boatEnchantmentTarget);
        ((MultiTargetEnchantment)Enchantments.BINDING_CURSE).setSecondaryTargets(boatEnchantmentTarget);
        ((MultiTargetEnchantment)Enchantments.VANISHING_CURSE).setSecondaryTargets(boatEnchantmentTarget);
    }

}
