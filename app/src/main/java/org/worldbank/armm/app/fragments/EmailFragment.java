package org.worldbank.armm.app.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.EditText;

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

@Type(EmailFragment.TYPE)
public class EmailFragment extends WidgetFragment {

    public static final String TYPE = "email";

    private static final int PICK_CONTACT_REQUEST = 1;

    @Control("edit_text")
    public EditText editText;

    public EmailFragment() {
        super(R.layout.fragment_email, R.menu.menu_email);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_contacts) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent ) {
        super.onActivityResult( requestCode, resultCode, intent );
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = intent.getData();
                Log.i(this, "Contact %s", uri);
                ContentResolver contentResolver = getActivity().getContentResolver();
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS));
                    Log.i(this, "Email %s", email);
                    editText.setText(email);
                }
            }
        }
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
            Toast.showShort(getActivity(), R.string.email_required);
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(answer.value).matches()) {
            editText.requestFocus();
            Toast.showShort(getActivity(), R.string.valid_email_required);
            return false;
        }
        return true;
    }

}
