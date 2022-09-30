package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.template.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

//    public void setLoading(boolean loading) {
//        isLoading = loading;
//    }

    private StatusService statusService;

    public FeedPresenter(PagedView<Status> view) {
        super(view);
        this.statusService = new StatusService();
    }

//    public void getUser(String userAlias) {
//        statusService.getUsers(userAlias, new GetUserObserver());
//    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        statusService.loadMoreItems(authToken, targetUser, pageSize, lastItem, new GetItemsObserver());
    }
}

