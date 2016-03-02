package com.tomclaw.molecus.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.tomclaw.molecus.R;
import com.tomclaw.molecus.core.PleaseWaitTask;
import com.tomclaw.molecus.core.TaskExecutor;
import com.tomclaw.molecus.core.UserHolder;
import com.tomclaw.molecus.molecus.AuthRequest;
import com.tomclaw.molecus.molecus.AuthResponse;
import com.tomclaw.molecus.molecus.UserInfoRequest;
import com.tomclaw.molecus.molecus.UserInfoResponse;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * A login screen that offers login via email/password.
 */
@EActivity(R.layout.activity_login)
@OptionsMenu(R.menu.login)
public class LoginActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    EditText email;

    @ViewById
    EditText password;

    @Bean
    TaskExecutor taskExecutor;

    @Bean
    UserHolder userHolder;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
    }

    @EditorAction(R.id.password)
    boolean onEditorActionsOnPasswordTextView(TextView view, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            authenticate();
            return true;
        }
        return false;
    }

    @OptionsItem
    void homeSelected() {
        finish();
    }

    @OptionsItem
    boolean actionLogin() {
        authenticate();
        return true;
    }

    private void authenticate() {
        String emailString = email.getText().toString();
        String passwordString = password.getText().toString();

        if (!isValidEmail(emailString)) {
            email.setError(getString(R.string.error_invalid_email));
            return;
        }
        if (TextUtils.isEmpty(passwordString)) {
            password.setError(getString(R.string.prompt_password));
            return;
        }

        AuthTask authTask = new AuthTask(this, emailString, passwordString);
        taskExecutor.execute(authTask);
    }

    private static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void onAuthenticationOk() {
        MainActivity_.intent(this).start();
        finish();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void onAuthenticationFailed() {
        password.setError(getString(R.string.error_incorrect_password));
    }

    public static class AuthTask extends PleaseWaitTask<LoginActivity> {

        private final String login;
        private final String password;

        public AuthTask(LoginActivity activity, String login, String password) {
            super(activity);
            this.login = login;
            this.password = password;
        }

        @Override
        public void executeBackground() throws Throwable {
            LoginActivity activity = getWeakObject();
            if (activity != null) {
                UserHolder userHolder = activity.userHolder;
                AuthRequest authRequest = new AuthRequest(login, password);
                AuthResponse authResponse = authRequest.onRequest(userHolder, activity);
                if (authResponse.isSuccess()) {
                    userHolder.getUser().setData(
                            login,
                            authResponse.getAccessToken(),
                            authResponse.getRefreshToken(),
                            authResponse.getExpiresIn());
                    UserInfoRequest infoRequest = new UserInfoRequest();
                    UserInfoResponse infoResponse = infoRequest.onRequest(userHolder, activity);
                    userHolder.getUser().setInfo(infoResponse.getAvatar(), infoResponse.getNick());
                    userHolder.store();
                    // Continue authorization negotiation.
                    activity.onAuthenticationOk();
                } else {
                    activity.onAuthenticationFailed();
                }
            }
        }
    }
}

