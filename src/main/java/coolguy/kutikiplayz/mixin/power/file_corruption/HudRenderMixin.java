package coolguy.kutikiplayz.mixin.power.file_corruption;

import com.llamalad7.mixinextras.sugar.Local;
import coolguy.kutikiplayz.extension.power.CodeHudRender;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(HudRender.class)
public abstract class HudRenderMixin implements CodeHudRender {
    @Unique private int fadeInTime;
    @Override public int code_breaker_origin$getFadeInTime() { return fadeInTime; }
    @Override public void code_breaker_origin$setFadeInTime(int fadeInTime) { this.fadeInTime = fadeInTime; }

    @Unique private int fadeOutTime;
    @Override public int code_breaker_origin$getFadeOutTime() { return fadeOutTime; }
    @Override public void code_breaker_origin$setFadeOutTime(int fadeOutTime) { this.fadeOutTime = fadeOutTime; }

    @Unique private float shakeThreshold;
    @Override public float code_breaker_origin$getShakeThreshold() { return shakeThreshold; }
    @Override public void code_breaker_origin$setShakeThreshold(float shakeThreshold) { this.shakeThreshold = shakeThreshold; }

    @Unique private float maxShakeIntensity;
    @Override public float code_breaker_origin$getMaxShakeIntensity() { return maxShakeIntensity; }
    @Override public void code_breaker_origin$setMaxShakeIntensity(float maxShakeIntensity) { this.maxShakeIntensity = maxShakeIntensity; }

    @Override public boolean code_breaker_origin$shouldFade() { return this.fadeInTime > 0 || this.fadeOutTime > 0; }

    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "()Lio/github/apace100/calio/data/SerializableData;"))
    private static SerializableData addCustomData() {
        return new SerializableData()
                .add("fade_in_time", SerializableDataTypes.NON_NEGATIVE_INT, 0)
                .add("fade_out_time", SerializableDataTypes.NON_NEGATIVE_INT, 0)
                .add("shake_threshold", SerializableDataType.boundNumber(SerializableDataTypes.FLOAT, 0f, 1f), 0f)
                .add("max_shake_intensity", SerializableDataTypes.NON_NEGATIVE_FLOAT, 0f);
    }

    @Redirect(method = "lambda$static$0", at = @At(value = "NEW", target = "(Ljava/util/Optional;Lnet/minecraft/util/Identifier;ZZIII)Lio/github/apace100/apoli/util/HudRender;"))
    private static HudRender addCustomData2(Optional<EntityCondition> condition, Identifier spriteLocation, boolean shouldRender, boolean inverted, int barIndex, int iconIndex, int order, @Local(argsOnly = true) SerializableData.Instance data) {
        HudRender hudRender = new HudRender(condition, spriteLocation, shouldRender, inverted, barIndex, iconIndex, order);
        ((CodeHudRender) hudRender).code_breaker_origin$setFadeInTime(data.getInt("fade_in_time"));
        ((CodeHudRender) hudRender).code_breaker_origin$setFadeOutTime(data.getInt("fade_out_time"));
        ((CodeHudRender) hudRender).code_breaker_origin$setShakeThreshold(data.getFloat("shake_threshold"));
        ((CodeHudRender) hudRender).code_breaker_origin$setMaxShakeIntensity(data.getFloat("max_shake_intensity"));
        return hudRender;
    }

    @Redirect(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lio/github/apace100/calio/data/SerializableData;instance()Lio/github/apace100/calio/data/SerializableData$Instance;"))
    private static SerializableData.Instance addCustomData3(SerializableData data, @Local(argsOnly = true) HudRender hudRender) {
        return data.instance()
                .set("fade_in_time", ((CodeHudRender) hudRender).code_breaker_origin$getFadeInTime())
                .set("fade_out_time", ((CodeHudRender) hudRender).code_breaker_origin$getFadeOutTime())
                .set("shake_threshold", ((CodeHudRender) hudRender).code_breaker_origin$getShakeThreshold())
                .set("max_shake_intensity", ((CodeHudRender) hudRender).code_breaker_origin$getMaxShakeIntensity());
    }

    @Redirect(method = "getActive", at = @At(value = "INVOKE", target = "Lio/github/apace100/apoli/util/HudRender;shouldRender(Lnet/minecraft/entity/Entity;)Z"))
    private boolean allowFade(HudRender hudRender, Entity viewer) {
        if (hudRender.shouldRender() && ((CodeHudRender) hudRender).code_breaker_origin$shouldFade())
            return true;
        return hudRender.shouldRender(viewer);
    }
}
