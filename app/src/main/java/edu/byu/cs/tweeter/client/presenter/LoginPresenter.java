package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The presenter for the login functionality of the application.
 */
public class LoginPresenter implements UserService.LoginObserver {

    private static final String LOG_TAG = "LoginPresenter";

    private View view;

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
            userService.login(username, password, this);
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

    /**
     * Invoked when the login request completes if the login was successful. Notifies the view of
     * the successful login.
     *
     * @param user the logged-in user.
     * @param authToken the session auth token.
     */
    @Override
    public void loginSuccess(User user, AuthToken authToken) {
        // Cache user session information
//        Cache.getInstance().setCurrUser(user);
//        Cache.getInstance().setCurrUserAuthToken(authToken);
        view.displayInfoMessage("Hello " + user.getFirstName());
        view.clearErrorMessage();
        view.navigateToUser(user);

        //view.loginSuccessful(user, authToken);
    }

    /**
     * Invoked when the login request completes if the login request was unsuccessful. Notifies the
     * view of the unsuccessful login.
     *
     * @param message error message.
     */
    @Override
    public void loginFailure(String message) {
        view.clearInfoMessage();
        view.displayErrorMessage(message);
//        String errorMessage = "Failed to login: " + message;
//        Log.e(LOG_TAG, errorMessage);
//        view.loginUnsuccessful(errorMessage);
    }
}
