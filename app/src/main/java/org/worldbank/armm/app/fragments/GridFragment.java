package org.worldbank.armm.app.fragments;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;
import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.annotations.Type;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Toast;

import java.util.HashMap;
import java.util.Map;

@Type(GridFragment.TYPE)
public class GridFragment extends WidgetFragment {

    public static final String TYPE = "grid";

    @Control("grid_container")
    public LinearLayout gridContainer;

    public GridFragment() {
        super(R.layout.fragment_grid, R.menu.menu_grid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
            for (int i = 0; i < gridContainer.getChildCount(); i++) {
                View view = gridContainer.getChildAt(i);
                if (view instanceof RadioGroup) {
                    RadioGroup radioGroup = (RadioGroup)view;
                    radioGroup.clearCheck();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "load %d %s = %s", question.cid, question.name, answer.value);
        Map<String, Object> values = answer.valueMap();
        gridContainer.removeAllViews();
        for (Map.Entry<String, String> entry : question.questions().entrySet()) {
            TextView textView = getTextView(
                    entry.getValue(),
                    labelDescription.getPaddingLeft(),
                    labelDescription.getPaddingLeft()/2,
                    labelDescription.getPaddingRight(),
                    0);
            gridContainer.addView(textView);
            String value = (String)values.get(entry.getKey());
            RadioGroup radioGroup = getRadioGroup(
                    entry.getKey(),
                    labelDescription.getPaddingLeft(),
                    labelDescription.getPaddingLeft()/2,
                    labelDescription.getPaddingRight(),
                    labelDescription.getPaddingLeft()/2);
            for (Map.Entry<String, String> option : question.options().entrySet()) {
                RadioButton radioButton = getRadioButton(
                        radioGroup.getChildCount(),
                        option.getValue(),
                        option.getKey(),
                        value);
                radioGroup.addView(radioButton);
            }
            gridContainer.addView(radioGroup);
        }
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < gridContainer.getChildCount(); i++) {
            View view = gridContainer.getChildAt(i);
            if (view instanceof RadioGroup) {
                RadioGroup radioGroup = (RadioGroup)view;
                String key = radioGroup.getTag().toString();
                int buttonID = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(buttonID);
                if (radioButton != null) {
                    Log.i(this, "save %s %s %s=%s", question.name, radioGroup.getTag(), radioButton.getTag(), radioButton.getText());
                    String value = radioButton.getTag().toString();
                    values.put(key, value);
                }
            }
        }
        JSONObject json = new JSONObject(values);
        answer.value = json.toString();
        answer.save();
        if (question.required && values.size() == 0) {
            Toast.showShort(getActivity(), R.string.answer_required);
            return false;
        }
        return true;
    }

}