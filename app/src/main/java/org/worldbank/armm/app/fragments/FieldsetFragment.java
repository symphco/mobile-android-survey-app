package org.worldbank.armm.app.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.LinearLayout;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.annotations.Type;
import ca.dalezak.androidbase.fragments.BaseFragment;
import ca.dalezak.androidbase.utils.Log;

import java.util.HashMap;
import java.util.Map;

@Type(FieldsetFragment.TYPE)
public class FieldsetFragment
        extends WidgetFragment
        implements BaseFragment.Callback {

    public static final String TYPE = "fieldset";

    private Map<WidgetFragment, Question> questions = new HashMap<WidgetFragment, Question>();

    @Control("fieldset")
    public LinearLayout fieldset;

    private Survey survey;
    private Submission submission;

    public FieldsetFragment() {
        super(R.layout.fragment_fieldset, R.menu.menu_fieldset);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "load %d %s, %s", question.cid, question.name, answer.value);
        this.survey = survey;
        this.submission = submission;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (WidgetFragment widgetFragment : questions.keySet()) {
            fragmentTransaction.remove(widgetFragment);
        }
        questions.clear();
        fieldset.removeAllViews();
        for (Question subQuestion : question.children()) {
            Log.i(this, "Sub Question %d %s", subQuestion.cid, subQuestion.name);
            Class<? extends WidgetFragment> widget = WidgetFragment.classForType(getActivity(), subQuestion.type);
            WidgetFragment widgetFragment = (WidgetFragment)Fragment.instantiate(getActivity(), widget.getName());
            widgetFragment.setCallback(this);
            widgetFragment.setMenuVisibility(false);
            Log.i(this, "Sub Fragment %s %s", subQuestion.type, widgetFragment);
            fragmentTransaction.add(R.id.fieldset, widgetFragment);
            questions.put(widgetFragment, subQuestion);
        }
        fragmentTransaction.commit();
        setMenuVisibility(false);
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        boolean valid = true;
        for (WidgetFragment widgetFragment : questions.keySet()) {
            Question subQuestion = questions.get(widgetFragment);
            Answer subAnswer = submission.answerForQuestion(subQuestion);
            if (!widgetFragment.save(survey, subQuestion, submission, subAnswer)) {
                valid = false;
            }
        }
        if (!valid) {
            fieldset.removeAllViews();
        }
        return valid;
    }

    @Override
    public void onFragmentInflate(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentAttach(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentCreate(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentViewCreated(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentActivityCreated(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentConfigurationChanged(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentStart(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentResume(BaseFragment baseFragment) {
        WidgetFragment widgetFragment = (WidgetFragment)baseFragment;
        Question question = questions.get(widgetFragment);
        Answer answer = submission.answerForQuestion(question);
        if (question != null && answer != null) {
            Log.i(this, "Fragment %s Question %s Answer %s", baseFragment, question, answer);
            widgetFragment.init(survey, question, submission, answer);
            widgetFragment.load(survey, question, submission, answer);
        }
        else {
            Log.i(this, "Fragment %s Question NULL Answer NULL", baseFragment);
        }
    }

    @Override
    public void onFragmentVisible(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentPause(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentStop(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentDestroy(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentDetach(BaseFragment baseFragment) {

    }

    @Override
    public void onFragmentHidden(BaseFragment baseFragment) {

    }
}