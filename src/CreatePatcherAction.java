import com.dialog.PatcherDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class CreatePatcherAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        PatcherDialog dialog = new PatcherDialog(event);
        // PatcherDialogOld dialog = new PatcherDialogOld(event);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.requestFocus();

    }
}
