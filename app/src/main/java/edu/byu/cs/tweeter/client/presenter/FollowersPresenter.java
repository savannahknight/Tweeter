package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter {
    private static final int PAGE_SIZE = 10;
    private User lastFollower;

    private boolean hasMorePages;
    private boolean isLoading = false;

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public interface View {
        void displayErrorMessage(String message);
        void redirectUser(User user);
        void setLoadingStatus(boolean status);
        void addFollowers(List<User> followers);
    }

    private View view;
    private FollowService followService;

    public FollowersPresenter(View view) {
        this.view = view;
        this.followService = new FollowService();
    }

    public void getUsers(String userAlias) {
        followService.getUsers(userAlias, new GetUserObserver());
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingStatus(true);

            followService.getFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollower, new GetFollowersObserver());
        }
    }


    public class GetFollowersObserver implements FollowService.GetFollowersObserver {

        @Override
        public void handleSuccess(List<User> followers, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingStatus(false);

            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
            setHasMorePages(hasMorePages);

            view.addFollowers(followers);
        }

        @Override
        public void handleError(String message) {
            isLoading = false;
            view.setLoadingStatus(false);
            view.displayErrorMessage(message);
        }
    }

    public class GetUserObserver implements FollowService.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            view.redirectUser(user);
        }
        @Override
        public void handleError(String message) {
            view.displayErrorMessage(message);
        }
    }
}
