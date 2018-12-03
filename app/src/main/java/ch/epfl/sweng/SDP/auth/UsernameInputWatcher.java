package ch.epfl.sweng.SDP.auth;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

import ch.epfl.sweng.SDP.R;

class UsernameInputWatcher implements TextWatcher {

    private final TextView feedback;
    private final Button createAccount;
    private final Resources resources;

    public UsernameInputWatcher(TextView feedback, Button createAccount, Resources resources) {
        this.feedback = feedback;
        this.createAccount = createAccount;
        this.resources = resources;
    }

    @Override
    public void beforeTextChanged(CharSequence username, int start, int count, int after) {
        // Needs to be implemented, but we don't need it.
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
        disableButton();
        if (check(username != null, R.string.usernameMustNotBeEmpty, "")
                && check(!username.isEmpty(), R.string.usernameMustNotBeEmpty, "")
                && check(username.length() >= 5, R.string.usernameTooShort, "")
                && check(username.length() <= 20, R.string.usernameTooLong, "")
                && check(!username.contains("  "),
                            R.string.usernameIllegalChar, " double spaces")
                && check(!username.contains("\\"), R.string.usernameIllegalChar, " \\")
                && check(!username.contains("%"), R.string.usernameIllegalChar, " %")
                && check(!username.contains("\""), R.string.usernameIllegalChar, " \"")
                && check(!username.contains("'"), R.string.usernameIllegalChar, " '")) {
            feedback.setText(username);
            enableButton(true, R.color.colorDrawYellow);
        }
    }

    private boolean check(boolean legal, int errorCode, String append) {
        if (!legal) {
            feedback.setText(getString(errorCode) + append);
            return false;
        }
        return true;
    }

    private void enableButton(boolean enable, int colorId) {
        createAccount.setEnabled(enable);
        createAccount.setBackgroundColor(resources.getColor(colorId));
    }

    private void disableButton() {
        enableButton(false, R.color.colorLightGrey);
    }

    private String getString(int id) {
        return resources.getString(id);
    }
}
