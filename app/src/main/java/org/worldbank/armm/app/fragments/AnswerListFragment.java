package org.worldbank.armm.app.fragments;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.activities.QuestionTabActivity;
import org.worldbank.armm.app.adapters.AnswerListAdapter;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.fragments.BaseListFragment;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;
import ca.dalezak.androidbase.utils.Toast;

public class AnswerListFragment extends BaseListFragment<Answer, AnswerListAdapter> {

    private Submission submission;

    public AnswerListFragment() {
        super(AnswerListAdapter.class, R.menu.popup_incomplete_cards);
    }

    @Override
    public void onResume() {
        super.onResume();
        submission = Submission.find(getStringExtra(Submission.class));
        if (submission != null) {
            if (!Strings.isNullOrEmpty(submission.title)) {
                setTitle(submission.title);
            }
            else if (!Strings.isNullOrEmpty(submission.survey.title)) {
                setTitle(submission.survey.title);
            }
            else {
                setTitle(getString(R.string.survey));
            }
            getListAdapter().setSubmission(submission);
        }
        getListAdapter().refresh();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuEdit = menu.findItem(R.id.action_edit);
        MenuItem menuOpen = menu.findItem(R.id.action_open);
        if (submission != null) {
            if (submission.uploaded != null) {
                menuEdit.setVisible(false);
            }
            else {
                menuEdit.setVisible(true);
            }
            if (Strings.isNullOrEmpty(submission.uri)) {
                menuOpen.setVisible(false);
            }
            else {
                menuOpen.setVisible(true);
            }
        }
        else {
            menuEdit.setVisible(false);
            menuOpen.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_discard) {
            new Alert(getActivity(), R.string.discard_submission, R.string.discard_submission_description) {
                @Override
                public void discard() {
                    for (Answer answer : submission.answers()) {
                        answer.delete();
                    }
                    submission.delete();
                    getActivity().finish();
                    Toast.showLong(getActivity(), R.string.submission_discarded);
                }
            }.showCancelDiscard(R.string.cancel, R.string.discard);
            return true;
        }
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(getActivity(), QuestionTabActivity.class);
            intent.putExtra(Survey.class.getName(), submission.survey.nid);
            intent.putExtra(Submission.class.getName(), submission.uuid);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.action_open) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(submission.uri));
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(this, "onItemClick %d", position);
    }

    @Override
    public void onRefresh() {
        hideRefreshing();
    }

}