package org.worldbank.armm.app.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.annotations.Type;
import ca.dalezak.androidbase.fragments.BaseFragment;
import ca.dalezak.androidbase.utils.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public abstract class WidgetFragment extends BaseFragment {

    private static final Map<String, Class<? extends WidgetFragment>> widgets = new HashMap<>();

    @Control("label_name")
    public TextView labelName;

    @Control("label_description")
    public TextView labelDescription;

    @Control("label_required")
    public TextView labelRequired;

    public WidgetFragment(int layout) {
        super(layout);
    }

    public WidgetFragment(int layout, int menu) {
        super(layout, menu);
    }

    public boolean init(Survey survey, Question question, Submission submission, Answer answer) {
        if (question != null) {
            Log.i(this, "init %d %s %s", question.cid, question.name, answer.value);
            labelName.setText(Html.fromHtml(question.name));
            if (Strings.isNullOrEmpty(question.description)) {
                labelDescription.setVisibility(View.GONE);
            }
            else {
                labelDescription.setVisibility(View.VISIBLE);
                labelDescription.setText(Html.fromHtml(question.description));
            }
            if (question.required) {
                labelRequired.setVisibility(View.VISIBLE);
            }
            else {
                labelRequired.setVisibility(View.GONE);
            }
            return true;
        }
        else {
            labelName.setText(R.string.question_error);
            labelDescription.setVisibility(View.VISIBLE);
            labelDescription.setText(R.string.question_error_description);
            labelRequired.setVisibility(View.GONE);
            return false;
        }
    }

    public abstract boolean load(Survey survey, Question question, Submission submission, Answer answer);

    public abstract boolean save(Survey survey, Question question, Submission submission, Answer answer);

    protected RadioButton getRadioButton(Integer id, String text, String tag, String value) {
        RadioButton radioButton = new RadioButton(getActivity());
        radioButton.setId(id);
        radioButton.setText(text);
        radioButton.setTag(tag);
        radioButton.setTextAppearance(getActivity(), R.style.RadioButton_Full);
        radioButton.setLayoutParams(getLayoutParams(labelDescription.getPaddingLeft(), labelDescription.getPaddingLeft() / 2, labelDescription.getPaddingRight(), labelDescription.getPaddingLeft() / 2));
        if (value != null && value.equals(tag)) {
            radioButton.setChecked(true);
        }
        else {
            radioButton.setChecked(false);
        }
        return radioButton;
    }

    protected TextView getTextView(String text, int left, int top, int right, int bottom) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setTextAppearance(getActivity(), R.style.TextView_Label);
        textView.setPadding(left, top, right, bottom);
        return textView;
    }

    protected RadioGroup getRadioGroup(String tag, int left, int top, int right, int bottom) {
        RadioGroup radioGroup = new RadioGroup(getActivity());
        radioGroup.setPadding(left, top, right, bottom);
        radioGroup.setTag(tag);
        return radioGroup;
    }

    public static Class<? extends WidgetFragment> classForType(Context context, String type) {
        if (widgets.size() == 0) {
            try {
                for (Class<? extends WidgetFragment> widgetClass : ca.dalezak.androidbase.utils.Runtime.getClasses(context, WidgetFragment.class))  {
                    Annotation annotation = widgetClass.getAnnotation(Type.class);
                    if (annotation instanceof Type) {
                        String widgetType = ((Type)annotation).value().toLowerCase();
                        widgets.put(widgetType, widgetClass);
                    }
                }
            }
            catch (PackageManager.NameNotFoundException e) {
                Log.w(WidgetFragment.class, "NameNotFoundException", e);
            }
            catch (IOException e) {
                Log.w(WidgetFragment.class, "IOException", e);
            }
        }
        return widgets.get(type);
    }
}