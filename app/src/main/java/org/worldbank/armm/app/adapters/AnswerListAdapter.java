package org.worldbank.armm.app.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.views.AnswerRow;
import ca.dalezak.androidbase.adapters.BaseListAdapter;

import ca.dalezak.androidbase.utils.Strings;

import java.util.ArrayList;
import java.util.List;

public class AnswerListAdapter extends BaseListAdapter<Answer, AnswerRow> {

    private Submission submission;

    public AnswerListAdapter(Activity activity) {
        super(activity, Answer.class, AnswerRow.class);
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Answer answer = getItem(position);
        AnswerRow row = getView(getActivity(), convertView);
        row.labelQuestion.setText(answer.name);
        if (!Strings.isNullOrEmpty(answer.uri)) {
            row.labelAnswer.setText(answer.uri);
            row.labelAnswer.setVisibility(View.VISIBLE);
        }
        else if (!Strings.isNullOrEmpty(answer.value)) {
            row.labelAnswer.setText(answer.value);
            row.labelAnswer.setVisibility(View.VISIBLE);
        }
        else {
            row.labelAnswer.setVisibility(View.GONE);
        }
        return row;
    }

    @Override
    public List<Answer> getItems() {
        return submission != null ? submission.answers() : new ArrayList<Answer>();
    }

}