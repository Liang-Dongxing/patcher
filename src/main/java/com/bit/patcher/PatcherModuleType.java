package com.bit.patcher;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Liang
 * @version 1.0
 * @date 2023/2/26 20:57
 * Created by IntelliJ IDEA
 */
@Getter
@Setter
@Builder
public class PatcherModuleType {

    private String name;
    private String type;

    @Override
    public String toString() {
        return "(" + name + ") " + type;
    }
}
