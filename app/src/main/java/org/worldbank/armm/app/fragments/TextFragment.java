package org.worldbank.armm.app.fragments;

import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.annotations.Type;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;
import ca.dalezak.androidbase.utils.Toast;

@Type(TextFragment.TYPE)
public class TextFragment extends WidgetFragment {

    public static final String TYPE = "textfield";

    @Control("edit_text")
    public EditText editText;

    @Control("label_prefix")
    public TextView labelPrefix;

    @Control("label_suffix")
    public TextView labelSuffix;

    public TextFragment() {
        super(R.layout.fragment_text, R.menu.menu_text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_keyboard_title) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            return true;
        }
        if (item.getItemId() == R.id.menu_keyboard_sentence) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            return true;
        }
        if (item.getItemId() == R.id.menu_keyboard_lowercase) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            return true;
        }
        if (item.getItemId() == R.id.menu_keyboard_uppercase) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            return true;
        }
        if (item.getItemId() == R.id.menu_keyboard_name) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME|InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            return true;
        }
        if (item.getItemId() == R.id.menu_keyboard_email) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            return true;
        }
        if (item.getItemId() == R.id.menu_keyboard_phone) {
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "load %d %s %s", question.cid, question.name, answer.value);
        if (Strings.isNullOrEmpty(question.placeholder)) {
            editText.setHint(R.string.enter_text);
        }
        else {
            editText.setHint(question.placeholder);
        }
        if (Strings.isNullOrEmpty(question.prefix)) {
            labelPrefix.setVisibility(View.GONE);
        }
        else {
            labelPrefix.setVisibility(View.VISIBLE);
            labelPrefix.setText(question.prefix);
        }
        if (Strings.isNullOrEmpty(question.suffix)) {
            labelSuffix.setVisibility(View.GONE);
        }
        else {
            labelSuffix.setVisibility(View.VISIBLE);
            labelSuffix.setText(question.suffix);
        }
        editText.setText(answer.value);
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        answer.value = editText.getText().toString();
        answer.save();
        if (question.required && Strings.isNullOrEmpty(answer.value)) {
            editText.requestFocus();
            Toast.showShort(getActivity(), R.string.text_required);
            return false;
        }
        return true;
    }

}