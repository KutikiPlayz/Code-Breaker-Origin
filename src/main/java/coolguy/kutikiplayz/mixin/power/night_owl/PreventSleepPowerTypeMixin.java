package coolguy.kutikiplayz.mixin.power.night_owl;

import com.llamalad7.mixinextras.sugar.Local;
import coolguy.kutikiplayz.extension.power.CodePreventSleepPowerType;
import io.github.apace100.apoli.condition.BlockCondition;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.type.PreventSleepPowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(PreventSleepPowerType.class)
public abstract class PreventSleepPowerTypeMixin implements CodePreventSleepPowerType {
    @Unique private boolean ignoreForSleepCount;
    @Override public boolean code_breaker_origin$shouldIgnoreForSleepCount() { return ignoreForSleepCount; }
    @Override public void code_breaker_origin$setIgnoreForSleepCount(boolean ignoreForSleepCount) { this.ignoreForSleepCount = ignoreForSleepCount; }

    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "()Lio/github/apace100/calio/data/SerializableData;"))
    private static SerializableData addCustomData() {
        return new SerializableData()
                .add("ignore_for_sleep_count", SerializableDataTypes.BOOLEAN, false);
    }

    @Redirect(method = "lambda$static$0", at = @At(value = "NEW", target = "(Ljava/util/Optional;Lnet/minecraft/text/Text;ZILjava/util/Optional;)Lio/github/apace100/apoli/power/type/PreventSleepPowerType;"))
    private static PreventSleepPowerType addCustomData2(Optional<BlockCondition> blockCondition, Text message, boolean allowSpawnPoint, int priority, Optional<EntityCondition> condition, @Local(argsOnly = true) SerializableData.Instance data) {
        PreventSleepPowerType preventSleepPowerType = new PreventSleepPowerType(blockCondition, message, allowSpawnPoint, priority, condition);
        ((CodePreventSleepPowerType) preventSleepPowerType).code_breaker_origin$setIgnoreForSleepCount(data.getBoolean("ignore_for_sleep_count"));
        return preventSleepPowerType;
    }

    @Redirect(method = "lambda$static$1", at = @At(value = "INVOKE", target = "Lio/github/apace100/calio/data/SerializableData;instance()Lio/github/apace100/calio/data/SerializableData$Instance;"))
    private static SerializableData.Instance addCustomData3(SerializableData data, @Local(argsOnly = true) PreventSleepPowerType powerType) {
        return data.instance()
                .set("ignore_for_sleep_count", ((CodePreventSleepPowerType) powerType).code_breaker_origin$shouldIgnoreForSleepCount());
    }
}
