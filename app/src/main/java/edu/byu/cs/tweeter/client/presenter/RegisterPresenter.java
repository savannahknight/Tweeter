package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {

    public interface View {
        void loginNewUser(User user, AuthToken authToken, String message);
        void displayErrorMessage(String message);

        void displayInfoMessage(String s);
        void clearErrorMessage();
        void clearInfoMessage();

        void loginNewUser(User user, String s);
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
            userService.register(firstName, lastName, alias, password, image, new RegisterObserver());
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }
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

    public class RegisterObserver implements ResponseObserver<User> {

        @Override
        public void handleFailure(String message) {
            view.displayErrorMessage(message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayErrorMessage(exception.getMessage());
        }

        @Override
        public void handleSuccess(User user) {
            view.loginNewUser(user, "Hello " + Cache.getInstance().getCurrUser().getName());
        }
    }
}
