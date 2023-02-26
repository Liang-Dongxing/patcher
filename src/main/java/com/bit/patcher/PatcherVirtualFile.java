package com.bit.patcher;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Liang
 * @version 1.0
 * @date 2023/2/26 16:07
 * Created by IntelliJ IDEA
 */
@Getter
@Setter
@Builder
public class PatcherVirtualFile {

    private VirtualFile virtualFile;
    private Module module;
    private String path;
    private String name;
    private String moduleName;

    @Override
    public String toString() {
        return name + " (" + path + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatcherVirtualFile that = (PatcherVirtualFile) o;

        return virtualFile.equals(that.virtualFile);
    }

    @Override
    public int hashCode() {
        return virtualFile != null ? virtualFile.hashCode() : 0;
    }
}
