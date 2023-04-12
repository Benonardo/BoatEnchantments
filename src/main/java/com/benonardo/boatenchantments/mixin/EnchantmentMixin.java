package com.benonardo.boatenchantments.mixin;

import com.benonardo.boatenchantments.duck.MultiTargetEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements MultiTargetEnchantment {

    @Unique
    private EnchantmentTarget[] secondaryTargets;

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void considerSecondaryTargets(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (secondaryTargets != null) {
            for (var target : secondaryTargets) {
                if (target.isAcceptableItem(stack.getItem()))  {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }

    @Unique
    @Override
    public void setSecondaryTargets(EnchantmentTarget[] value) {
        this.secondaryTargets = value;
    }

    @Unique
    @Override
    public EnchantmentTarget[] getSecondaryTargets() {
        return this.secondaryTargets;
    }
}
