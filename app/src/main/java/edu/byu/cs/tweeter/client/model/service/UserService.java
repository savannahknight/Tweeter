package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic to support the login operation.
 */
public class UserService {

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface LoginObserver {
        void loginSuccess(User user, AuthToken authToken);
        void loginFailure(String message);
    }

    public interface RegisterObserver {
        void registerSuccess(User user, AuthToken authToken);
        void registerError(String message);
    }

    public interface LogoutObserver {
        void handleSuccess();
        void handleError(String message);
    }
    /**
     * Creates an instance.
     */
    public UserService() {
    }

    public void login(String username, String password, LoginObserver loginObserver) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(username,
                password, new LoginHandler(loginObserver));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void register(String firstName, String lastName, String username, String password, Bitmap image, RegisterObserver registerObserver) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        RegisterTask registerTask = new RegisterTask(firstName, lastName, username, password, imageBytesBase64, new RegisterHandler(registerObserver));
        BackgroundTaskUtils.runTask(registerTask);
    }

    public void logout(AuthToken currUserAuthToken, MainActivityPresenter.LogoutObserver logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new LogoutHandler(logoutObserver));
        BackgroundTaskUtils.runTask(logoutTask);
    }

    /**
     * Handles messages from the background task indicating that the task is done, by invoking
     * methods on the observer.
     */
    private static class LoginHandler extends Handler {

        private LoginObserver observer;

        public LoginHandler(LoginObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) bundle.getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) bundle.getSerializable(LoginTask.AUTH_TOKEN_KEY);

                //Cache user session information
                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                observer.loginSuccess(loggedInUser, authToken);
            } else if (bundle.containsKey(LoginTask.MESSAGE_KEY)) {
                String errorMessage = bundle.getString(LoginTask.MESSAGE_KEY);
                observer.loginFailure("Failed to login: " + errorMessage);
            } else if (bundle.containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) bundle.getSerializable(LoginTask.EXCEPTION_KEY);
                observer.loginFailure("Failed to login because of exception: " + ex.getMessage());
            }
        }
    }

    private class RegisterHandler extends Handler {
        private RegisterObserver registerObserver;

        public RegisterHandler(RegisterObserver registerObserver) {
            this.registerObserver = registerObserver;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(RegisterTask.SUCCESS_KEY);
            if (success) {
                User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(registeredUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                registerObserver.registerSuccess(registeredUser, authToken);
            } else if (msg.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(RegisterTask.MESSAGE_KEY);
                registerObserver.registerError("Failed to register: " + message);
            } else if (msg.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                registerObserver.registerError("Failed to register because of exception: " + ex.getMessage());
            }
        }
    }
    /**
     * Message handler (i.e., observer) for LogoutTask
     */
    private class LogoutHandler extends Handler {
        LogoutObserver observer;

        public LogoutHandler(LogoutObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LogoutTask.SUCCESS_KEY);
            if (success) {
                observer.handleSuccess();
            } else if (msg.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LogoutTask.MESSAGE_KEY);
                observer.handleError("Failed to logout: " + message);
            } else if (msg.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
                observer.handleError("Failed to logout because of exception: " + ex.getMessage());
            }
        }
    }

}

