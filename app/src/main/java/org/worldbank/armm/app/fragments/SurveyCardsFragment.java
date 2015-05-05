package org.worldbank.armm.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.activities.QuestionTabActivity;
import org.worldbank.armm.app.adapters.SurveyCardAdapter;
import org.worldbank.armm.app.models.Barangay;
import org.worldbank.armm.app.models.Municipality;
import org.worldbank.armm.app.models.Province;
import org.worldbank.armm.app.models.Region;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import org.worldbank.armm.app.tasks.DownloadRegions;
import org.worldbank.armm.app.tasks.DownloadSurveys;
import org.worldbank.armm.app.tasks.DownloadSurvey;
import org.worldbank.armm.app.views.SurveyCard;

import ca.dalezak.androidbase.fragments.BaseCardsFragment;
import ca.dalezak.androidbase.models.BaseModel;
import ca.dalezak.androidbase.tasks.CardTask;
import ca.dalezak.androidbase.tasks.HttpQueue;
import ca.dalezak.androidbase.tasks.HttpTask;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Internet;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.UUID;

import java.util.Date;

public class SurveyCardsFragment
        extends BaseCardsFragment<Survey, SurveyCard, SurveyCardAdapter>
        implements HttpQueue.Callback {

    public SurveyCardsFragment() {
        super(SurveyCardAdapter.class, R.string.no_surveys, R.menu.menu_survey_cards);
        setColumns(1, 1, 2, 2);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLoading();
        hideRefreshing();
    }

    @Override
    public void onRefresh() {
        if (Internet.isAvailable(getActivity())) {
            if (!HttpQueue.getInstance().contains(DownloadRegions.class)) {
                HttpQueue.getInstance().add(new DownloadRegions(getActivity()));
            }
            if (!HttpQueue.getInstance().contains(DownloadSurveys.class)) {
                HttpQueue.getInstance().add(new DownloadSurveys(getActivity()));
            }
            if (HttpQueue.getInstance().size() > 0) {
                HttpQueue.getInstance().start();
                showRefreshing(R.string.refreshing_);
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
    public void onVisible() {
        super.onVisible();
        HttpQueue.getInstance().register(this);
        if (Region.count() == 0 ||
            Province.count() == 0 ||
            Municipality.count() == 0 ||
            Barangay.count() == 0) {
            HttpQueue.getInstance().add(new DownloadRegions(getActivity(), true));
            showLoading(R.string.downloading_psgc_);
        }
        if (Survey.count() == 0) {
            HttpQueue.getInstance().add(new DownloadSurveys(getActivity(), true));
        }
        if (HttpQueue.getInstance().size() > 0) {
            if (Internet.isAvailable(getActivity())) {
                HttpQueue.getInstance().start();
            }
            else {
                hideLoading();
                HttpQueue.getInstance().clear();
                new Alert(getActivity(),
                        R.string.no_internet_connection,
                        R.string.verify_internet_connection).showOk(R.string.ok);
            }
        }
    }

    @Override
    public void onHidden() {
        super.onHidden();
        HttpQueue.getInstance().unregister(this);
    }

    @Override
    public void onCardSelected(SurveyCard card, Survey survey) {
        new ShowSurvey(getActivity(), card, survey).execute();
    }

    @Override
    public void onCardPressed(SurveyCard card, final Survey survey) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQueueStarted(int total) {
    }

    @Override
    public void onQueueResumed() {
    }

    @Override
    public void onQueueProgress(int total, int progress) {
        Log.i(this, "onQueueProgress %d / %d", progress, total);
        showLoading(R.string.downloading_surveys_, total, progress);
    }

    @Override
    public void onQueuePaused() {
        hideLoading();
        hideRefreshing();
    }

    @Override
    public void onQueueCancelled() {
        hideLoading();
        hideRefreshing();
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
        if (task instanceof DownloadSurveys && model instanceof Survey) {
            Survey survey = (Survey)model;
            HttpQueue.getInstance().add(new DownloadSurvey(getActivity(), survey));
        }
    }

    @Override
    public void onTaskFinished(HttpTask task) {

    }

    @Override
    public void onTaskFailed(HttpTask task, Exception exception) {

    }

    private class ShowSurvey extends CardTask<Survey, SurveyCard> {

        public ShowSurvey(Activity activity, SurveyCard surveyCard, Survey survey) {
            super(activity, surveyCard, survey, R.string.loading_);
            setRequestCode(1);
        }

        @Override
        protected Intent doInBackground(Void... voids) {
            Submission submission = new Submission();
            submission.uuid = UUID.getRandom();
            submission.created = new Date();
            submission.changed = new Date();
            submission.survey = getModel();
            submission.title = getModel().title;
            submission.save();
            Intent intent = new Intent(getActivity(), QuestionTabActivity.class);
            intent.putExtra(Survey.class.getName(), getModel().nid);
            intent.putExtra(Submission.class.getName(), submission.uuid);
            return intent;
        }
    }
}