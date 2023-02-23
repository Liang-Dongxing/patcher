package com.bit.patcher;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

/**
 * @author Liang
 * @version 1.0
 * @date 2020/12/1 16:00
 * Created by IntelliJ IDEA
 * <p>
 * 获取国际化信息
 */
public class PatcherBundle extends DynamicBundle {
    public static final PatcherBundle INSTANCE = new PatcherBundle();
    private static final @NonNls String BUNDLE = "messages.PatcherBundle";

    public PatcherBundle() {
        super(BUNDLE);
    }

    /**
     * 获取国际化信息
     *
     * @param key    国际化信息的key
     * @param params 国际化信息的参数
     * @return 国际化信息
     */
    public static @NotNull @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
