package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter {
    private FollowService followService;
    private UserService userService;
    private StatusService statusService;
    private View view;

    public MainActivityPresenter(View view) {
        this.followService = new FollowService();
        this.userService = new UserService();
        this.statusService = new StatusService();
        this.view = view;
    }

    public interface View {
        void handleFollowSuccess();
        void handleUnFollowSuccess();
        void handleLogoutSuccess();
        void handleGetFollowersCountSuccess(int count);
        void handleGetFollowingCountSuccess(int count);
        void handleIsFollowerSuccess(boolean isFollower);
        void handlePostStatusSuccess();
        void handleError(String message);
        void resetFollowButton();
    }

    public void unfollow(User selectedUser) {
        followService.unfollow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new UnfollowObserver());
    }

    public void follow(User selectedUser) {
        followService.follow(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new FollowObserver());
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), selectedUser, Cache.getInstance().getCurrUser(), new IsFollowerObserver());
    }

    public void logout() {
        userService.logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver());
    }

    public void postStatus(Status newStatus) {
        statusService.postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver());

    }

    public void getFollowersCount(User selectedUser) {
        followService.getFollowersCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowersCountObserver());
    }

    public void getFollowingCount(User selectedUser) {
        followService.getFollowingCount(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new GetFollowingCountObserver());
    }

    public User getCurrUser() {
        return Cache.getInstance().getCurrUser();
    }

    public void clearCache() {
        Cache.getInstance().clearCache();
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }
    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public class UnfollowObserver implements FollowService.UnfollowObserver {

        @Override
        public void handleSuccess() {
            view.handleUnFollowSuccess();
            view.resetFollowButton();
        }

        @Override
        public void handleError(String message) {
            view.handleError(message);
        }
    }

    public class FollowObserver implements FollowService.FollowObserver {

        @Override
        public void handleSuccess() {
            view.handleFollowSuccess();
            view.resetFollowButton();
        }

        @Override
        public void handleError(String message) {
            view.handleError(message);
        }
    }

    public class IsFollowerObserver implements FollowService.IsFollowerObserver {

        @Override
        public void handleSuccess(boolean isFollower) {
            view.handleIsFollowerSuccess(isFollower);
        }

        @Override
        public void handleError(String message) {
            view.handleError(message);
        }
    }

    public class LogoutObserver implements UserService.LogoutObserver {

        @Override
        public void handleSuccess() {
            view.handleLogoutSuccess();
        }

        @Override
        public void handleError(String message) {
            view.handleError(message);
        }
    }

    public class PostStatusObserver implements StatusService.PostStatusObserver {

        @Override
        public void handleSuccess() {
            view.handlePostStatusSuccess();
        }

        @Override
        public void handleError(String message) {
            view.handleError(message);
        }
    }

    public class GetFollowersCountObserver implements FollowService.GetFollowersCountObserver {

        @Override
        public void handleSuccess(int count) {
            view.handleGetFollowersCountSuccess(count);
        }

        @Override
        public void handleError(String message) {
            view.handleError(message);
        }
    }

    public class GetFollowingCountObserver implements FollowService.GetFollowingCountObserver {

        @Override
        public void handleSuccess(int count) {
            view.handleGetFollowingCountSuccess(count);
        }

        @Override
        public void handleError(String message) {
            view.handleError(message);
        }
    }
}
