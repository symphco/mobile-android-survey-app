package org.worldbank.armm.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.PopupMenu;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.activities.AnswerListActivity;
import org.worldbank.armm.app.activities.QuestionTabActivity;
import org.worldbank.armm.app.adapters.IncompleteCardAdapter;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import org.worldbank.armm.app.views.SubmissionCard;
import ca.dalezak.androidbase.fragments.BaseCardsFragment;
import ca.dalezak.androidbase.tasks.CardTask;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Toast;

public class IncompleteCardsFragment
        extends BaseCardsFragment<Submission, SubmissionCard, IncompleteCardAdapter> {

    public IncompleteCardsFragment() {
        super(IncompleteCardAdapter.class, R.string.no_incomplete_surveys, R.menu.menu_incomplete_cards);
        setColumns(1, 1, 2, 2);
    }

    @Override
    public void onRefresh() {
        hideRefreshing();
    }

    @Override
    public void onCardSelected(SubmissionCard card, Submission submission) {
        new ShowAnswerList(getActivity(), card, submission).execute();
    }

    @Override
    public void onCardPressed(final SubmissionCard card, final Submission submission) {
        PopupMenu popup = new PopupMenu(getActivity(), card.cardView);
        popup.getMenuInflater().inflate(R.menu.popup_incomplete_cards, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_open) {
                    new ShowAnswerList(getActivity(), card, submission).execute();
                }
                else if (item.getItemId() == R.id.action_edit) {
                    new ShowQuestionTab(getActivity(), card, submission).execute();
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

    private class ShowQuestionTab extends CardTask<Submission, SubmissionCard> {

        public ShowQuestionTab(Activity activity, SubmissionCard card, Submission submission) {
            super(activity, card, submission, R.string.loading_);
        }

        @Override
        protected Intent doInBackground(Void... voids) {
            Intent intent = new Intent(getActivity(), QuestionTabActivity.class);
            intent.putExtra(Survey.class.getName(), getModel().survey.nid);
            intent.putExtra(Submission.class.getName(), getModel().uuid);
            return intent;
        }
    }
}