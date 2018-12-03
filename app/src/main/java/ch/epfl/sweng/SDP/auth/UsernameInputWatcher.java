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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Needs to be implemented, but we don't need it.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Needs to be implemented, but we don't need it.
    }

    @Override
    public void afterTextChanged(Editable s) {
        validate(s.toString());
    }

    private void validate(String username) {
        disableButton();
        if (username == null || username.isEmpty()) {
            feedback.setText(getString(R.string.usernameMustNotBeEmpty));
            return;
        }
        if (username.length() < 5) {
            feedback.setText(getString(R.string.usernameTooShort));
            return;
        }
        if (username.length() > 20) {
            feedback.setText(getString(R.string.usernameTooLong));
            return;
        }
        if (username.contains("  ")) {
            feedback.setText(getString(R.string.usernameNoDoubleSpace));
            return;
        }
        if (username.contains("\\")) {
            feedback.setText(getString(R.string.usernameNoBackSlash));
            return;
        }
        feedback.setText(username);
        enableButton(true, R.color.colorDrawYellow);
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
