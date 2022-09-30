package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.ResponseObserver;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The presenter for the login functionality of the application.
 */
public class LoginPresenter {

    private View view;
    private UserService userService;

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        //TODO how to know what goes in here
//        void loginSuccessful(User user, AuthToken authToken);
//        void loginUnsuccessful(String message);
        void displayInfoMessage(String message);
        void clearInfoMessage();

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void navigateToUser(User user);
    }

    /**
     * Creates an instance.
     *
     * @param view the view for which this class is the presenter.
     */
    public LoginPresenter(View view) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(view == null) {
            throw new NullPointerException();
        }
        this.view = view;
        this.userService = new UserService();
    }

    /**
     * Initiates the login process.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public void initiateLogin(String username, String password) {
        String message = validateLogin(username, password);
        if (message == null) {
            view.clearErrorMessage();
            view.displayInfoMessage("Logging in ...");
            UserService userService = new UserService();
            userService.login(username, password, new LoginObserver());
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }

    }

    public String validateLogin(String username, String password) {
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    public class LoginObserver implements ResponseObserver<User> {

        @Override
        public void handleFailure(String message) {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }

        @Override
        public void handleException(Exception exception) {
            view.clearInfoMessage();
            view.displayErrorMessage(exception.getMessage());
        }

        @Override
        public void handleSuccess(User user) {
            view.displayInfoMessage("Hello " + Cache.getInstance().getCurrUser().getName());
            view.clearErrorMessage();
            view.navigateToUser(user);
        }
    }
}
