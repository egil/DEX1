package dk.itu.spcl.dex.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class UITools {

  public interface PromptResultHandler {
    public void closed(boolean accepted, String value);
  }

  public static void promptForString(Context context, String caption,
      String label, final PromptResultHandler resultHandler) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle(caption);
    alert.setMessage(label);
    final EditText input = new EditText(context);
    alert.setView(input);

    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String value = input.getText().toString();
        resultHandler.closed(true, value);
      }
    });

    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int whichButton) {
        resultHandler.closed(false, null);
      }
    });
    alert.show();
  }
}
