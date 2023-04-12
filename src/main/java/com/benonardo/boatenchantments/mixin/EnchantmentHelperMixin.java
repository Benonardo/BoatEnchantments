package com.benonardo.boatenchantments.mixin;

import com.benonardo.boatenchantments.duck.MultiTargetEnchantment;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Unique
    private static Enchantment getPossibleEntriesCapturedEnchantment;

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "getPossibleEntries", at = @At("STORE"))
    private static Enchantment captureEnchantment(Enchantment original) {
        getPossibleEntriesCapturedEnchantment = original;
        return original;
    }

    @ModifyExpressionValue(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
    private static boolean considerSecondaryTargets(boolean original, int power, ItemStack stack, boolean treasureAllowed) {
        for (var target : ((MultiTargetEnchantment)getPossibleEntriesCapturedEnchantment).getSecondaryTargets()) {
            if (target.isAcceptableItem(stack.getItem())) return true;
        }
        return original;
    }

}
