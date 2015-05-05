package org.worldbank.armm.app.fragments;

import android.view.MenuItem;
import android.widget.TimePicker;

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

@Type(TimeFragment.TYPE)
public class TimeFragment extends WidgetFragment {

    public static final String TYPE = "time";

    private static final String HH_MM_SS = "HH:mm:ss";

    @Control("time_picker")
    public TimePicker timePicker;

    public TimeFragment() {
        super(R.layout.fragment_time, R.menu.menu_time);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear) {
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(0);
            return true;
        }
        if (item.getItemId() == R.id.menu_now) {
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR);
            int minutes = calendar.get(Calendar.MINUTE);
            timePicker.setCurrentHour(hours);
            timePicker.setCurrentMinute(minutes);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean load(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "load %d %s %s", question.cid, question.name, answer.value);
        if (Strings.isNullOrEmpty(answer.value)) {
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(0);
        }
        else {
            Log.i(this, "Date %s", answer.value);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(HH_MM_SS);
                Date date = formatter.parse(answer.value);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hours = calendar.get(Calendar.HOUR);
                int minutes = calendar.get(Calendar.MINUTE);
                timePicker.setCurrentHour(hours);
                timePicker.setCurrentMinute(minutes);
            }
            catch (ParseException e) {
                timePicker.setCurrentHour(0);
                timePicker.setCurrentMinute(0);
            }
        }
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        if (timePicker.getCurrentHour() > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat(HH_MM_SS);
            Calendar calendar = Calendar.getInstance();
            calendar.set(0, 0, 0, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            answer.value = formatter.format(calendar.getTime());
            Log.i(this, "save %s %s", question.name, answer.value);
        }
        else {
            answer.value = null;
        }
        answer.save();
        if (question.required && Strings.isNullOrEmpty(answer.value)) {
            timePicker.requestFocus();
            Toast.showShort(getActivity(), R.string.time_required);
            return false;
        }
        return true;
    }
}