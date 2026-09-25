package coolguy.kutikiplayz.extension.power;

public interface CodeHudRender {
    int code_breaker_origin$getFadeInTime();
    void code_breaker_origin$setFadeInTime(int fadeInTime);

    int code_breaker_origin$getFadeOutTime();
    void code_breaker_origin$setFadeOutTime(int fadeOutTime);

    float code_breaker_origin$getShakeThreshold();
    void code_breaker_origin$setShakeThreshold(float shakeThreshold);

    float code_breaker_origin$getMaxShakeIntensity();
    void code_breaker_origin$setMaxShakeIntensity(float maxShakeIntensity);

    boolean code_breaker_origin$shouldFade();
}
