package org.worldbank.armm.app.tasks;

import android.content.Context;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Submission;

import java.util.Date;

import ca.dalezak.androidbase.tasks.LoadTask;
import ca.dalezak.androidbase.utils.Log;

public class LoadSubmissions extends LoadTask<Submission> {

    public LoadSubmissions(Context context) {
        super(context);
    }

    @Override
    protected Exception doInBackground(Object...args) {
        for (Submission submission : Submission.all()) {
            submission.updateAnswers();
            submission.updateQuestions();
            submission.updateCompleted();
        }
        return null;
    }
}