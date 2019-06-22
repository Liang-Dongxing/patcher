import com.ldx.PatcherDialogFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class CreatePatcherAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        PatcherDialogFactory.getInstance(event);
    }
}
