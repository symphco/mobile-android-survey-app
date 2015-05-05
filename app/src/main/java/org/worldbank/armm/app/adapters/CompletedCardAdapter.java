package org.worldbank.armm.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.views.SubmissionCard;
import ca.dalezak.androidbase.adapters.BaseCardAdapter;
import ca.dalezak.androidbase.utils.Dates;
import ca.dalezak.androidbase.utils.Strings;

import java.util.List;

public class CompletedCardAdapter extends BaseCardAdapter<Submission, SubmissionCard> {

    public CompletedCardAdapter(Context context) {
        super(context, Submission.class, SubmissionCard.class, R.layout.fragment_submission_card);
    }

    @Override
    public void onBindViewHolder(SubmissionCard card, int index) {
        Submission submission = getItem(index);
        if (submission != null) {
            if (Strings.isNullOrEmpty(submission.survey.title)) {
                card.labelTitle.setText(R.string.survey_no_title);
            }
            else {
                card.labelTitle.setText(submission.survey.title);
            }
            card.labelSaved.setText(R.string.completed);
            if (submission.completed != null) {
                card.dateSaved.setText(Dates.toDateTimeString(submission.completed).replace("AM","am").replace("PM", "pm"));
                card.dateSaved.setTypeface(null, Typeface.NORMAL);
            }
            else if (submission.changed != null) {
                card.dateSaved.setText(Dates.toDateTimeString(submission.changed).replace("AM","am").replace("PM", "pm"));
                card.dateSaved.setTypeface(null, Typeface.NORMAL);
            }
            card.labelSubmitted.setText(R.string.submitted);
            if (submission.uploaded != null) {
                card.dateSubmitted.setText(Dates.toDateTimeString(submission.uploaded).replace("AM","am").replace("PM", "pm"));
                card.rowSubmitted.setVisibility(View.VISIBLE);
                card.rowRequired.setVisibility(View.GONE);
                card.rowTotal.setVisibility(View.GONE);
            }
            else {
                card.rowSubmitted.setVisibility(View.GONE);

                card.rowRequired.setVisibility(View.VISIBLE);
                card.rowTotal.setVisibility(View.VISIBLE);
                if (submission.requiredQuestions == 0) {
                    card.progressRequired.setProgress(1);
                    card.progressRequired.setMax(1);
                }
                else {
                    card.progressRequired.setProgress(submission.requiredAnswers());
                    card.progressRequired.setMax(submission.requiredQuestions());
                }
                card.progressTotal.setProgress(submission.answerCount());
                card.progressTotal.setMax(submission.survey.allQuestionCount());
            }
        }
    }

    @Override
    public List<Submission> getItems() {
        return Submission.completed();
    }

}