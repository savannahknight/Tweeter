package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    public interface GetFollowingObserver {
        void handleSuccess(List<User> followees, boolean hasMorePages);
        void handleError(String message);
    }

    public interface GetUserObserver {
        void handleSuccess(User user);
        void handleError(String message);
    }

    public interface GetFollowersObserver {
        void handleSuccess(List<User> followers, boolean hasMorePages);
        void handleError(String message);
    }

    public interface UnfollowObserver {
        void handleSuccess();
        void handleError(String message);
    }

    public interface FollowObserver {
        void handleSuccess();
        void handleError(String message);
    }

    public interface GetFollowersCountObserver {
        void handleSuccess(int count);
        void handleError(String message);
    }

    public interface GetFollowingCountObserver {
        void handleSuccess(int count);
        void handleError(String message);
    }

    public interface IsFollowerObserver {
        void handleSuccess(boolean isFollower);
        void handleError(String message);
    }


    public FollowService() {}

    /**
     * Returns an instance of {@link GetFollowingTask}. Allows mocking of the
     * GetFollowingTask class for testing purposes. All usages of GetFollowingTask
     * should get their instance from this method to allow for proper mocking.
     *
     * @return the instance.
     */
    public void getFollowing(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, GetFollowingObserver getFollowingObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new GetFollowingHandler(getFollowingObserver));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }

    public void getUsers(String userAlias, GetUserObserver getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                userAlias, new GetUserHandler(getUserObserver));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    public void getFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, GetFollowersObserver getFollowersObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new GetFollowersHandler(getFollowersObserver));
        BackgroundTaskUtils.runTask(getFollowersTask);

    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, MainActivityPresenter.FollowObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new FollowHandler(followObserver));
        BackgroundTaskUtils.runTask(followTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, UnfollowObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new UnfollowHandler(unfollowObserver));
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    public void isFollower(AuthToken currUserAuthToken, User selectedUser, User currUser, MainActivityPresenter.IsFollowerObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    public void getFollowersCount(AuthToken currUserAuthToken, User selectedUser, MainActivityPresenter.GetFollowersCountObserver getFollowersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new GetFollowersCountHandler(getFollowersCountObserver));
        BackgroundTaskUtils.runTask(followersCountTask);
    }

    public void getFollowingCount(AuthToken currUserAuthToken, User selectedUser, MainActivityPresenter.GetFollowingCountObserver getFollowingCountObserver) {
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new GetFollowingCountHandler(getFollowingCountObserver));
        BackgroundTaskUtils.runTask(followingCountTask);
    }


    /**
     * Message handler (i.e., observer) for GetFollowingTask.
     */
    private class GetFollowingHandler extends Handler {
        private GetFollowingObserver observer;

        public GetFollowingHandler(GetFollowingObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.handleSuccess(followees, hasMorePages);

            } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
                observer.handleError("Failed to get user's profile: " + message);

            } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.handleError("Failed to get following because of exception: " + ex.getMessage());

            }
        }
    }
    private class GetUserHandler extends Handler {
        private GetUserObserver observer;

        public GetUserHandler(GetUserObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                observer.handleSuccess(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                observer.handleError(message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                observer.handleError("Failed to get user's profile because of exception: " + ex);
            }
        }
    }
    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private class GetFollowersHandler extends Handler {
        GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                observer.handleSuccess(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.handleError("Failed to get followers: " + message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.handleError("Failed to get followers because of exception: " + ex.getMessage());
            }
        }
    }

    private class UnfollowHandler extends Handler {
        UnfollowObserver observer;

        public UnfollowHandler(UnfollowObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                observer.handleSuccess();
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.handleError("Failed to unfollow: " + message);
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.handleError("Failed to unfollow because of exception: " + ex.getMessage());
            }
        }
    }
    private class FollowHandler extends Handler {
        FollowObserver observer;

        public FollowHandler(FollowObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                observer.handleSuccess();
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.handleError("Failed to follow: " + message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.handleError("Failed to follow because of exception: " + ex.getMessage());
            }
        }
    }

    private class IsFollowerHandler extends Handler {
        IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
                observer.handleSuccess(isFollower);
            } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
                observer.handleError("Failed to determine following relationship: " + message);
            } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.handleError("Failed to determine following relationship because of exception: " + ex.getMessage());
            }
        }
    }

    private class GetFollowersCountHandler extends Handler {
        GetFollowersCountObserver observer;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.handleSuccess(count);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.handleError("Failed to get followers count: " + message);
            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.handleError("Failed to get followers count because of exception: " + ex.getMessage());
            }
        }
    }

    private class GetFollowingCountHandler extends Handler {
        GetFollowingCountObserver observer;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            this.observer = observer;
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
                observer.handleSuccess(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
                observer.handleError("Failed to get following count: " + message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
                observer.handleError("Failed to get following count because of exception: " + ex.getMessage());
            }
        }
    }
}
