package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.presenter.template.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User>{


    public void setLoading(boolean loading) {
        isLoading = loading;
    }


    private FollowService followService;

    public FollowersPresenter(PagedView<User> view) {
        super(view);
        this.followService = new FollowService();
    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService.getFollowers(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }

//    public void getUsers(String userAlias) {
//        followService.getUsers(userAlias, new GetUserObserver());
//    }
//
//    public void loadMoreItems(User user) {
//        if (!isLoading) {
//            isLoading = true;
//            view.setLoadingStatus(true);
//
//            followService.getFollowers(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollower, new GetFollowersObserver());
//        }
//    }


//    public class GetFollowersObserver implements PagedObserver<User> {
//
//        @Override
//        public void handleSuccess(List<User> followers, boolean hasMorePages) {
//            isLoading = false;
//            view.setLoadingStatus(false);
//
//            lastFollower = (followers.size() > 0) ? followers.get(followers.size() - 1) : null;
//            setHasMorePages(hasMorePages);
//
//            view.addFollowers(followers);
//        }
//
//        @Override
//        public void handleFailure(String message) {
//            isLoading = false;
//            view.setLoadingStatus(false);
//            view.displayErrorMessage(message);
//        }
//
//        @Override
//        public void handleException(Exception exception) {
//            isLoading = false;
//            view.setLoadingStatus(false);
//            view.displayErrorMessage(exception.getMessage());
//        }
//    }

//    public class GetUserObserver implements ResponseObserver<User> {
//
//        @Override
//        public void handleSuccess(User user) {
//            view.redirectUser(user);
//        }
//
//        @Override
//        public void handleFailure(String message) {
//            view.displayErrorMessage(message);
//        }
//
//        @Override
//        public void handleException(Exception exception) {
//            view.displayErrorMessage(exception.getMessage());
//        }
//    }
}
