package org.worldbank.armm.app.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Submission;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.views.BaseCard;

public class SubmissionCard extends BaseCard<Submission> {

    @Control("label_title")
    public TextView labelTitle;

    @Control("row_saved")
    public TableRow rowSaved;

    @Control("label_saved")
    public TextView labelSaved;

    @Control("date_saved")
    public TextView dateSaved;

    @Control("row_submitted")
    public TableRow rowSubmitted;

    @Control("label_submitted")
    public TextView labelSubmitted;

    @Control("date_submitted")
    public TextView dateSubmitted;

    @Control("row_required")
    public TableRow rowRequired;

    @Control("label_required")
    public TextView labelRequired;

    @Control("progress_required")
    public ProgressBar progressRequired;

    @Control("row_total")
    public TableRow rowTotal;

    @Control("label_total")
    public TextView labelTotal;

    @Control("progress_total")
    public ProgressBar progressTotal;

    public SubmissionCard(Context context, View view) {
        super(context, view);
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.card_selected));
        }
        else {
            cardView.setCardBackgroundColor(getContext().getResources().getColor(R.color.white_dark));
        }
    }
}