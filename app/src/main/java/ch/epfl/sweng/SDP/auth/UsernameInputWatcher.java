package ch.epfl.sweng.SDP.auth;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;

import static java.lang.String.format;

/**
 * This class is a listener which can be applied to the username input
 * box to check if the entered username contains problematic chars or not.
 */
final class UsernameInputWatcher implements TextWatcher {

    private static final int MIN_CHARS = 3;
    private static final int MAX_CHARS = 12;

    private final TextView feedback;
    private final Button createAccount;
    private final Resources resources;

    UsernameInputWatcher(TextView feedback, Button createAccount, Resources resources) {
        this.feedback = feedback;
        this.createAccount = createAccount;
        this.resources = resources;
    }

    @Override
    public void beforeTextChanged(CharSequence username, int start, int count, int after) {
        disableButton();
    }

    @Override
    public void onTextChanged(CharSequence username, int start, int before, int count) {
        // Needs to be implemented, but we don't need it.
    }

    @Override
    public void afterTextChanged(Editable username) {
        validate(username.toString());
    }

    private void validate(String username) {
        if (checkAll(username)) {
            feedback.setText(resources.getString(R.string.usernameValid));
            enableButton(true, R.color.colorDrawYellow);
        } else {
            disableButton();
        }
    }

    private boolean check(boolean condition, int errorCode, String append) {
        if (!condition) {
            feedback.setText(format("%s%s", resources.getString(errorCode), append));
        }
        return condition;
    }

    private boolean checkAll(String username) {
        return check(username != null, R.string.usernameMustNotBeEmpty, "")
                && check(!username.isEmpty(), R.string.usernameMustNotBeEmpty, "")
                && check(username.length() >= MIN_CHARS, R.string.usernameTooShort, "")
                && check(username.length() <= MAX_CHARS, R.string.usernameTooLong, "")
                && check(!username.contains("  "),
                R.string.usernameIllegalChar, " double spaces")
                && check(!username.contains("\\"), R.string.usernameIllegalChar, " \\")
                && check(!username.contains("%"), R.string.usernameIllegalChar, " %")
                && check(!username.contains("\""), R.string.usernameIllegalChar, " \"")
                && check(!username.contains("'"), R.string.usernameIllegalChar, " '")
                && check(!username.contains("."), R.string.usernameIllegalChar, " points");
    }

    private void enableButton(boolean enable, int colorId) {
        createAccount.setEnabled(enable);
        createAccount.setBackgroundColor(resources.getColor(colorId));
    }

    void disableButton() {
        enableButton(false, R.color.colorLightGrey);
    }
}
