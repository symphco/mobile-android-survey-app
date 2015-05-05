package org.worldbank.armm.app.views;

import android.content.Context;
import android.widget.TextView;

import org.worldbank.armm.app.R;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.views.BaseView;

public class AnswerRow extends BaseView {

    @Control("question_name")
    public TextView labelQuestion;

    @Control("answer_value")
    public TextView labelAnswer;

    public AnswerRow(Context context) {
        super(context, R.layout.fragment_answer_row);
    }
}