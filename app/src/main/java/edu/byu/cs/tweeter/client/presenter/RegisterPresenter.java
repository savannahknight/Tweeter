package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {

    public interface View {
        void loginNewUser(User user, AuthToken authToken, String message);
        void displayErrorMessage(String message);

        void displayInfoMessage(String s);
        void clearErrorMessage();
        void clearInfoMessage();
    }

    private RegisterPresenter.View view;
    private UserService userService;

    public RegisterPresenter(View view) {
        this.view = view;
        this.userService = new UserService();
    }
    public void register(String firstName, String lastName, String alias, String password, Bitmap image) {
        String message = validateRegistration(firstName, lastName, alias, password, image);
        if (message == null) {
            view.clearErrorMessage();
            view.displayInfoMessage("Registering...");
            userService.register(firstName, lastName, alias, password, image, this);
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }
    }
    @Override
    public void registerSuccess(User user, AuthToken authToken) {
        view.loginNewUser(user, authToken, "Hello " + Cache.getInstance().getCurrUser().getName());
    }

    @Override
    public void registerError(String message) {
        view.displayErrorMessage(message);
    }

    public String validateRegistration(String firstName, String lastName, String alias, String password, Bitmap imageToUpload ) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (alias.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (alias.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (alias.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        if (imageToUpload == null) {
            return "Profile image must be uploaded.";
        }
        return null;
    }
}
