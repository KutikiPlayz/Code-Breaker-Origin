package coolguy.kutikiplayz.mixin;

import coolguy.kutikiplayz.extension.CodeLivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CodeLivingEntity {
    @Unique private static final TrackedData<Boolean> IN_CODE_VISION = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void registerCodeVision(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(IN_CODE_VISION, false);
    }

    @Override public boolean code_breaker_origin$inCodeVision() { return ((LivingEntity) (Object) this).getDataTracker().get(IN_CODE_VISION); }
    @Override public void code_breaker_origin$setInCodeVision(boolean inCodeVision) { ((LivingEntity) (Object) this).getDataTracker().set(IN_CODE_VISION, inCodeVision); }
}
