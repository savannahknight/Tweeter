package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {
    private static final int PAGE_SIZE = 10;

    private Status lastStatus;

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
        void addStatuses(List<Status> statuses);
    }

    private View view;
    private StatusService statusService;

    public FeedPresenter(View view) {
        this.view = view;
        this.statusService = new StatusService();
    }

    public void getUser(String userAlias) {
        statusService.getUser(userAlias, new GetUserObserver());
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingStatus(true);

            statusService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastStatus, new GetFeedObserver());
        }
    }

    public class GetUserObserver implements StatusService.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            view.redirectUser(user);
        }

        @Override
        public void handleError(String message) {
            view.displayErrorMessage(message);
        }
    }

    public class GetFeedObserver implements StatusService.GetFeedObserver {

        @Override
        public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingStatus(false);

            lastStatus = (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addStatuses(statuses);
        }

        @Override
        public void handleError(String message) {
            isLoading = false;
            view.setLoadingStatus(false);
            view.displayErrorMessage(message);
        }
    }
}
