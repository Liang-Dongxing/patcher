package com.ldx;

import com.intellij.openapi.actionSystem.AnActionEvent;

public class PatcherDialogFactory {

    public static PatcherDialog getInstance(AnActionEvent event){
        PatcherDialog patcherDialog = new PatcherDialog(event);
        return patcherDialog;
    }
}
