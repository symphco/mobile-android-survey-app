package org.worldbank.armm.app.fragments;

import android.widget.TextView;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;

public class ErrorFragment extends WidgetFragment {

    @Control("label_error")
    public TextView labelError;

    public ErrorFragment() {
        super(R.layout.fragment_error, R.menu.menu_error);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        if (question != null) {
            labelError.setText(String.format("Unable to load widget for questionByPosition type '%s'.", question.type));
        }
        else {
            labelError.setText("Unable to load widget for questionByPosition.");
        }
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        return true;
    }
}