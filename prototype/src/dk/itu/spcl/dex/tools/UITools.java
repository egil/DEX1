package dk.itu.spcl.dex.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ToggleButton;

public class UITools {

  public interface PromptResultHandler<T> {
    public void closed(boolean accepted, T value);
  }

  public static void promptForString(Context context, String caption,
      String label, final PromptResultHandler<String> resultHandler) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle(caption);
    alert.setMessage(label);
    final EditText input = new EditText(context);
    input.setSingleLine();
    input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
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
  
  public static void promptForBoolean(Context context, String caption,
      String label, final PromptResultHandler<Boolean> resultHandler) {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle(caption);
    alert.setMessage(label);
    final ToggleButton input = new ToggleButton(context);
    input.setTextOn("On/open");
    input.setTextOff("Off/closed");
    input.setChecked(false);
    alert.setView(input);

    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        boolean value = input.isChecked();
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
