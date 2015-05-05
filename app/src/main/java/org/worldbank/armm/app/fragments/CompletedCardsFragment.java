package org.worldbank.armm.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.activities.AnswerListActivity;
import org.worldbank.armm.app.adapters.CompletedCardAdapter;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.tasks.LoginUser;
import org.worldbank.armm.app.tasks.UploadSubmission;
import org.worldbank.armm.app.utils.Prefs;
import org.worldbank.armm.app.views.SubmissionCard;

import ca.dalezak.androidbase.fragments.BaseCardsFragment;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.tasks.CardTask;
import ca.dalezak.androidbase.tasks.HttpQueue;
import ca.dalezak.androidbase.tasks.HttpTask;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Internet;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Toast;

public class CompletedCardsFragment
        extends BaseCardsFragment<Submission, SubmissionCard, CompletedCardAdapter>
        implements HttpQueue.Callback {

    public CompletedCardsFragment() {
        super(CompletedCardAdapter.class, R.string.no_completed_surveys, R.menu.menu_completed_cards);
        setColumns(1, 1, 2, 2);
    }

    @Override
    public void onVisible() {
        super.onVisible();
        HttpQueue.getInstance().register(this);
    }

    @Override
    public void onHidden() {
        super.onHidden();
        HttpQueue.getInstance().unregister(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_upload);
        if (getListAdapter().getItemCount() > 0) {
            menuItem.setEnabled(true);
            menuItem.getIcon().setAlpha(255);
        }
        else {
            menuItem.setEnabled(false);
            menuItem.getIcon().setAlpha(50);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (Internet.isAvailable(getActivity())) {
            if (!Prefs.hasCookie()) {
                HttpQueue.getInstance().add(new LoginUser(getActivity(), Prefs.getUsername(), Prefs.getPassword()));
            }
            for (Submission submission : Submission.pending()) {
                HttpQueue.getInstance().add(new UploadSubmission(getActivity(), submission));
            }
            if (HttpQueue.getInstance().size() > 0) {
                HttpQueue.getInstance().start();
                showRefreshing(R.string.posting_);
            }
            else {
                hideRefreshing();
            }
        }
        else {
            hideRefreshing();
            new Alert(getActivity(),
                    R.string.no_internet_connection,
                    R.string.verify_internet_connection).showOk(R.string.ok);
        }
    }

    @Override
    public void onCardSelected(SubmissionCard card, Submission submission) {
        new ShowAnswerList(getActivity(), card, submission).execute();
    }

    @Override
    public void onCardPressed(final SubmissionCard card, final Submission submission) {
        PopupMenu popup = new PopupMenu(getActivity(), card.cardView);
        popup.getMenuInflater().inflate(R.menu.popup_completed_cards, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_open) {
                    new ShowAnswerList(getActivity(), card, submission).execute();
                }
                else if (item.getItemId() == R.id.action_discard) {
                    new Alert(getActivity(), R.string.discard_submission, R.string.discard_submission_description) {
                        @Override
                        public void discard() {
                            submission.deleteAnswers();
                            submission.delete();
                            getListAdapter().refresh();
                            Toast.showLong(getActivity(), R.string.submission_discarded);
                        }
                    }.showCancelDiscard(R.string.cancel, R.string.discard);
                }
                return true;
            }
        });
        popup.show();
    }

    @Override
    public void onQueueStarted(int total) {

    }

    @Override
    public void onQueueResumed() {

    }

    @Override
    public void onQueueProgress(int total, int progress) {
        if (getListAdapter().getItemCount() == 0) {
            showLoading(R.string.posting_submissions_, total, progress);
        }
    }

    @Override
    public void onQueuePaused() {

    }

    @Override
    public void onQueueCancelled() {

    }

    @Override
    public void onQueueFinished() {
        onRefreshed();
        hideLoading();
        hideRefreshing();
    }

    @Override
    public void onQueueFailed(Exception exception) {
        onRefreshed();
        hideLoading();
        hideRefreshing();
        new Alert(getActivity(), exception).showOk(R.string.ok);
    }

    @Override
    public void onTaskStarted(HttpTask task) {
        Log.i(this, "onTaskStarted %s", task);
        if (task.isLoading()) {
            showLoading(task.getMessage());
        }
        else {
            showRefreshing(task.getMessage());
        }
    }

    @Override
    public void onTaskCancelled(HttpTask task) {

    }

    @Override
    public void onTaskProgress(HttpTask task, BaseModel model, int total, int progress) {
        Log.i(this, "onTaskStarted %s %d / %d", task, progress, total);
        if (task.isLoading()) {
            showLoading(task.getMessage(), total, progress);
        }
        else {
            showRefreshing(task.getMessage());
        }
    }

    @Override
    public void onTaskFinished(HttpTask task) {

    }

    @Override
    public void onTaskFailed(HttpTask task, Exception exception) {

    }

    private class ShowAnswerList extends CardTask<Submission, SubmissionCard> {

        public ShowAnswerList(Activity activity, SubmissionCard card, Submission submission) {
            super(activity, card, submission, R.string.loading_);
        }

        @Override
        protected Intent doInBackground(Void... voids) {
            Intent intent = new Intent(getActivity(), AnswerListActivity.class);
            intent.putExtra(Submission.class.getName(), getModel().uuid);
            return intent;
        }
    }

}