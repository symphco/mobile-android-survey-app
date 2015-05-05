package org.worldbank.armm.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.fragments.BaseTabFragment;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Files;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.UUID;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class QuestionTabFragment
        extends BaseTabFragment<WidgetFragment> {

    private Survey survey;
    private Submission submission;
    private int position;

    @Control("label_page")
    public TextView labelPage;

    @Control("button_previous")
    public Button buttonPrevious;

    @Control("button_next")
    public Button buttonNext;

    public QuestionTabFragment() {
        super(R.layout.fragment_question_tab, R.menu.menu_question_tab);
    }

    @Override
    public void onResume() {
        super.onResume();
        survey = Survey.find(getIntExtra(Survey.class, 0));
        Log.i(this, "Survey %s", survey.title);
        submission = Submission.find(getStringExtra(Submission.class));
        if (submission == null) {
            submission = new Submission();
            submission.uuid = UUID.getRandom();
            submission.created = new Date();
            submission.changed = new Date();
            submission.survey = survey;
            submission.title = survey.title;
            submission.save();
            Log.i(this, "Submission New %s", submission.title);
        }
        else {
            Log.i(this, "Submission %s", submission.title);
        }
        setTitle(survey.title);
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation animation = new AlphaAnimation(1F, 0.5F);
                animation.setDuration(400);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int previous = viewPager.getCurrentItem() - 1;
                        if (previous >= 0) {
                            setTabSelected(previous, true);
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                view.startAnimation(animation);
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation animation = new AlphaAnimation(1F, 0.5F);
                animation.setDuration(400);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        int next = viewPager.getCurrentItem() + 1;
                        if (next < getTabsAdapter().getCount()) {
                            setTabSelected(next, true);
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                view.startAnimation(animation);
            }
        });
        getTabsAdapter().refresh();
        if (position > 0) {
            setTabSelected(position, false);
            position = 0;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (survey.allQuestionCount() > 0) {
            int position = viewPager.getCurrentItem();
            Question question = survey.questionByPosition(position);
            Answer answer = submission.answerForQuestion(question);
            WidgetFragment fragment = getTabsAdapter().getFragment(position);
            fragment.save(survey, question, submission, answer);
            if (submission.hasAnswers()) {
                submission.updateChanged();
                submission.updateAnswers();
                submission.updateQuestions();
                submission.updateCompleted();
                Log.i(this, "Submission Has Answers");
                Intent intent = new Intent();
                intent.putExtra(Submission.class.getName(), submission.uuid);
                getActivity().setResult(Activity.RESULT_OK, intent);
            }
            else {
                submission.deleteAnswers();
                submission.delete();
                Log.i(this, "Submission Deleted");
                getActivity().setResult(Activity.RESULT_OK);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i(this, "onActivityResult %d %d %s", requestCode, resultCode, intent);
        position = requestCode;
        Log.i(this, "Position %s", position);
        if (resultCode == Activity.RESULT_OK && requestCode > -1) {
            survey = Survey.find(getIntExtra(Survey.class, 0));
            Log.i(this, "Survey %s", survey.title);
            submission = Submission.find(getStringExtra(Submission.class));
            Log.i(this, "Submission %s", submission);
            Question question = survey.questionByPosition(requestCode);
            Log.i(this, "Question %s", question.name);
            Answer answer = submission.answerForQuestion(question);
            Log.i(this, "Answer %s", answer.value);
            if (intent != null && intent.getData() != null) {
                Uri uri = intent.getData();
                Log.i(this, "Uri %s", uri);
                String name = String.format("%s-%s.jpg", submission.uuid, answer.cid);
                File file = Files.getImageFile(getActivity(), name);
                Files.copyUriToFile(getActivity(), uri, file);
                answer.value = file.getPath();
                Log.i(this, "File %s", answer.value);
            }
            else if (answer.hasFile()) {
                try {
                    Log.i(this, "File %s", answer.value);
                    Bitmap original = BitmapFactory.decodeFile(answer.value);
                    int width = 640;
                    int height = (int)((double)original.getHeight()/(double)original.getWidth()*(double)width);
                    Log.i(this, "%dx%d > %dx%d", original.getWidth(), original.getHeight(), width, height);
                    Bitmap resized = Bitmap.createScaledBitmap(original, width, height, false);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    resized.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    byte[] data = bytes.toByteArray();
                    FileOutputStream output = new FileOutputStream(answer.value);
                    output.write(data);
                    output.close();
                }
                catch (IOException exception) {
                    Log.w(this, "IOException", exception);
                    new Alert(getActivity(), exception).showOk(R.string.ok);
                }
            }
            else {
                Log.i(this, "Value NULL");
                answer.value = null;
            }
            answer.save();
        }
    }

    @Override
    protected int getTabCount() {
        return survey != null ? survey.parentQuestionCount() : 0;
    }

    @Override
    protected String getTabTitle(int position) {
        return null;
    }

    @Override
    protected Class<? extends WidgetFragment> getTabClass(int position) {
        Question question = survey.questionByPosition(position);
        if (question != null) {
            Class<? extends WidgetFragment> widget = WidgetFragment.classForType(getActivity(), question.type);
            return (widget != null) ? widget : ErrorFragment.class;
        }
        return ErrorFragment.class;
    }

    @Override
    protected boolean onTabSelected(int position, WidgetFragment fragment) {
        Log.i(this, "onTabSelected %d %s", position, fragment);
        if (position > 0) {
            buttonPrevious.setVisibility(View.VISIBLE);
        }
        else {
            buttonPrevious.setVisibility(View.INVISIBLE);
        }
        if (position+1 < getTabsAdapter().getCount()) {
            buttonNext.setVisibility(View.VISIBLE);
        }
        else {
            buttonNext.setVisibility(View.INVISIBLE);
        }
        labelPage.setText(String.format("%d %s %d", position+1, getString(R.string.of), getTabsAdapter().getCount()));
        Question question = survey.questionByPosition(position);
        Answer answer = submission.answerForQuestion(question);
        return fragment.init(survey, question, submission, answer) &&
               fragment.load(survey, question, submission, answer);
    }

    @Override
    protected boolean onTabUnselected(int position, WidgetFragment fragment) {
        Log.i(this, "onTabUnselected %d %s", position, fragment);
        Question question = survey.questionByPosition(position);
        Answer answer = submission.answerForQuestion(question);
        boolean saved = fragment.save(survey, question, submission, answer);
        submission.changed = new Date();
        submission.save();
        return saved;
    }

}