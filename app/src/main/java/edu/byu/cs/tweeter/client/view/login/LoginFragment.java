package edu.byu.cs.tweeter.client.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Implements the login screen.
 */
public class LoginFragment extends Fragment implements LoginPresenter.View {
    private static final String LOG_TAG = "LoginFragment";

    private Toast infoToast;
    private EditText alias;
    private EditText password;
    private TextView errorView;
    private LoginPresenter presenter = new LoginPresenter(this);

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        alias = view.findViewById(R.id.loginUsername);
        password = view.findViewById(R.id.loginPassword);
        errorView = view.findViewById(R.id.loginError);
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Login and move to MainActivity.
                String uname = alias.getText().toString();
                String pword = password.getText().toString();

                presenter.initiateLogin(uname, pword);
            }
        });

        return view;
    }

    @Override
    public void displayInfoMessage(String message) {
        clearInfoMessage();

        infoToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        infoToast.show();
    }

    @Override
    public void clearInfoMessage() {
        if (infoToast != null) {
            infoToast.cancel();
            infoToast = null;
        }
    }

    @Override
    public void displayErrorMessage(String message) {
        errorView.setText(message);
    }

    @Override
    public void clearErrorMessage() {
        errorView.setText("");
    }

    @Override
    public void navigateToUser(User user) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        startActivity(intent);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_login, container, false);
//
//        alias = view.findViewById(R.id.loginUsername);
//        password = view.findViewById(R.id.loginPassword);
//        errorView = view.findViewById(R.id.loginError);
//        Button loginButton = view.findViewById(R.id.loginButton);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                // Login and move to MainActivity.
//                try {
//                    validateLogin();
//                    errorView.setText(null);
//
//                    loginInToast = Toast.makeText(getContext(), "Logging In...", Toast.LENGTH_LONG);
//                    loginInToast.show();
//
//                    // Send the login request.
//                    LoginTask loginTask = new LoginTask(alias.getText().toString(),
//                            password.getText().toString(),
//                            new LoginHandler());
//                    ExecutorService executor = Executors.newSingleThreadExecutor();
//                    executor.execute(loginTask);
//                } catch (Exception e) {
//                    errorView.setText(e.getMessage());
//                }
//            }
//        });
//
//        return view;
//    }
//
//    public void validateLogin() {
//        if (alias.getText().charAt(0) != '@') {
//            throw new IllegalArgumentException("Alias must begin with @.");
//        }
//        if (alias.getText().length() < 2) {
//            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
//        }
//        if (password.getText().length() == 0) {
//            throw new IllegalArgumentException("Password cannot be empty.");
//        }
//    }
//
//    /**
//     * Message handler (i.e., observer) for LoginTask
//     */
//    private class LoginHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
//            if (success) {
//                User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
//                AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);
//
//                // Cache user session information
//                Cache.getInstance().setCurrUser(loggedInUser);
//                Cache.getInstance().setCurrUserAuthToken(authToken);
//
//                Intent intent = new Intent(getContext(), MainActivity.class);
//                intent.putExtra(MainActivity.CURRENT_USER_KEY, loggedInUser);
//
//                loginInToast.cancel();
//
//                Toast.makeText(getContext(), "Hello " + Cache.getInstance().getCurrUser().getName(), Toast.LENGTH_LONG).show();
//                startActivity(intent);
//            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
//                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);
//                Toast.makeText(getContext(), "Failed to login: " + message, Toast.LENGTH_LONG).show();
//            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
//                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);
//                Toast.makeText(getContext(), "Failed to login because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}
