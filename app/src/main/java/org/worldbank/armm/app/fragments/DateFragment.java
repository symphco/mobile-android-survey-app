package org.worldbank.armm.app.fragments;

import android.view.MenuItem;
import android.widget.DatePicker;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Type(DateFragment.TYPE)
public class DateFragment extends WidgetFragment {

    public static final String TYPE = "date";

    private static final String YYYY_MM_DD = "yyyy-MM-dd";

    @Control("date_picker")
    public DatePicker datePicker;

    public DateFragment() {
        super(R.layout.fragment_date, R.menu.menu_date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
            datePicker.updateDate(0, 0, 0);
            return true;
        }
        if (item.getItemId() == R.id.menu_today) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            datePicker.updateDate(year, month, day);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "load %d %s %s", question.cid, question.name, answer.value);
        if (Strings.isNullOrEmpty(answer.value)) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            datePicker.updateDate(year, month, day);
        }
        else {
            Log.i(this, "Date %s", answer.value);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
                Date date = formatter.parse(answer.value);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                datePicker.updateDate(year, month, day);
            }
            catch (ParseException e) {
                datePicker.updateDate(0, 0, 0);
            }
        }
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        if (datePicker != null) {
            if (datePicker.getYear() > 1900) {
                int day  = datePicker.getDayOfMonth();
                int month= datePicker.getMonth();
                int year = datePicker.getYear();
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
                answer.value = formatter.format(calendar.getTime());
                Log.i(this, "save %s %s", question.name, answer.value);
            }
            else {
                answer.value = null;
            }
            answer.save();
            if (question.required && Strings.isNullOrEmpty(answer.value)) {
                datePicker.requestFocus();
                Toast.showShort(getActivity(), R.string.date_required);
                return false;
            }
            return true;
        }
        return false;
    }

}