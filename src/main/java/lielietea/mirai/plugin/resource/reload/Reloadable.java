package lielietea.mirai.plugin.resource.reload;

import org.jetbrains.annotations.Nullable;

public interface Reloadable {

    default ReloadResult reload(@Nullable String[] args) {
        return ReloadResult.SKIPPED;
    }

    String getReloadableName();

    enum ReloadResult {
        SUCCESS,
        FAILED,
        SKIPPED,
    }
}
