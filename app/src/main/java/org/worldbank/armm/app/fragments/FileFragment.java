package org.worldbank.armm.app.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;

import org.worldbank.armm.app.R;
import org.worldbank.armm.app.models.Answer;
import org.worldbank.armm.app.models.Question;
import org.worldbank.armm.app.models.Submission;
import org.worldbank.armm.app.models.Survey;
import ca.dalezak.androidbase.annotations.Control;
import ca.dalezak.androidbase.annotations.Type;
import ca.dalezak.androidbase.utils.Alert;
import ca.dalezak.androidbase.utils.Files;
import ca.dalezak.androidbase.utils.Log;
import ca.dalezak.androidbase.utils.Strings;
import ca.dalezak.androidbase.utils.Toast;

import java.io.File;

@Type(FileFragment.TYPE)
public class FileFragment extends WidgetFragment {

    public static final String TYPE = "file";

    @Control("image_button")
    public ImageButton imageButton;

    private Survey survey;
    private Question question;

    private Submission submission;
    private Answer answer;

    @Override
    public void onStart() {
        super.onStart();
    }

    public FileFragment() {
        super(R.layout.fragment_file, R.menu.menu_file);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuDiscard = menu.findItem(R.id.action_discard);
        if (answer != null && answer.hasValue()) {
            menuDiscard.setVisible(true);
        }
        else {
            menuDiscard.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_camera) {
            showPhotoOptions(submission, answer);
            return true;
        }
        if (item.getItemId() == R.id.action_discard) {
            imageButton.setImageBitmap(getPlaceholder());
            answer.value = null;
            answer.save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean load(final Survey survey, final Question question, final Submission submission, final Answer answer) {
        Log.i(this, "load %d %s %s", question.cid, question.name, answer.value);
        this.survey = survey;
        this.question = question;
        this.submission = submission;
        this.answer = answer;
        if (answer.hasFile()) {
            Log.i(this, "Has File %s", answer.value);
            int targetWidth = Math.max(imageButton.getWidth(), 200);
            int targetHeight = Math.max(imageButton.getHeight(), 200);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(answer.value, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = Math.min(options.outWidth/targetWidth, options.outHeight/targetHeight);
            Bitmap bitmap = BitmapFactory.decodeFile(answer.value, options);
            imageButton.setImageBitmap(bitmap);
        }
        else {
            Log.i(this, "No File");
            imageButton.setImageBitmap(getPlaceholder());
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlphaAnimation animation = new AlphaAnimation(1F, 0.5F);
                animation.setDuration(300);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        showPhotoOptions(submission, answer);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                view.startAnimation(animation);
            }
        });
        getActivity().invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean save(Survey survey, Question question, Submission submission, Answer answer) {
        Log.i(this, "save %d %s %s", question.cid, question.name, answer.value);
        if (question.required && Strings.isNullOrEmpty(answer.value)) {
            Toast.showShort(getActivity(), R.string.photo_required);
            return false;
        }
        return true;
    }

    private Bitmap getPlaceholder() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.ic_button_camera);
    }

    private void showPhotoOptions(final Submission submission, final Answer answer) {
        new Alert(getActivity(), R.string.select_photo) {
            @Override
            public void ok() {
                String name = String.format("%s-%s.jpg", submission.uuid, answer.cid);
                File file = Files.getImageFile(getActivity(), name);
                Log.i(this, "File %s", file);
                answer.value = file.getPath();
                answer.save();
                submission.save();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                Intent intentChooser = Intent.createChooser(intent, getString(R.string.take_photo));
                getActivity().startActivityForResult(intentChooser, question.position);
            }
            @Override
            public void cancel() {
                answer.save();
                submission.save();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent intentChooser = Intent.createChooser(intent, getString(R.string.select_photo));
                getActivity().startActivityForResult(intentChooser, question.position);
            }
        }.showOkCancel(R.string.use_camera, R.string.photo_gallery);
    }
}