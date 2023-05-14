package com.flysolo.dmmsugradelevelapp.views.student.components;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStartActivity2Binding;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStartActivity3Binding;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStartActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class StartActivity3 extends Fragment {

    private FragmentStartActivity3Binding binding;
    private Quiz quiz;
    private int position = 0;
    private int score = 0;

    private Respond respond;
    private FirebaseUser user;
    //timer
    private long START_TIME_IN_MILLIS = 0;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private CountDownTimer mCountDownTimer;
    private List<Answer> answerList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quiz = StartActivity3Args.fromBundle(getArguments()).getQuiz();
            START_TIME_IN_MILLIS = quiz.getTimer() * 60000L;
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            respond = new Respond("",quiz.getId(),"",answerList,0,0L);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStartActivity3Binding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textQuestionMax.setText(String.valueOf(quiz.getQuestions().size()));
        binding.textScore.setText(String.valueOf(score));
        displayQuestion(quiz.getQuestions().get(position));

        view.setOnKeyListener((view1, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(getActivity(), "Back Pressed", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                pauseTimer();
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Exit Game")
                        .setMessage("Are you sure you want to quit the game?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> Navigation.findNavController(view).popBackStack()).setNegativeButton("Cancel", (dialogInterface, i) -> {
                            startTimer();
                            dialogInterface.dismiss();
                        })
                        .show();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void displayQuestion(Question question) {
        if (question.getQuestion().isEmpty()) {
            binding.textQuestion.setVisibility(View.GONE);
        }
        binding.textPoints.setText(String.format("+%s",question.getPoints()));
        binding.textQuestionPosition.setText(String.valueOf(position + 1));
        binding.textQuestion.setText(question.getQuestion());
        binding.layoutChoices.removeAllViews();
        Collections.shuffle(question.getChoices());
        for (String s: question.getChoices()) {
            displayChoices(s,question.getAnswer());
        }

    }
    private void displayChoices(String choice,String answer) {;
        View view = LayoutInflater.from(binding.layoutChoices.getContext()).inflate(R.layout.layout_image_choices,binding.layoutChoices,false);

        MaterialCardView cardLetter = view.findViewById(R.id.cardLetter);
        ImageView imageView = view.findViewById(R.id.imageChoice);
        Glide.with(view.getContext()).load(choice).into(imageView);
        cardLetter.setOnClickListener(view1 -> {
            answerList.add(new Answer(quiz.getQuestions().get(position).getId(),choice));
            if (choice.equals(answer)) {
                score += quiz.getQuestions().get(position).getPoints();
                binding.textScore.setText(String.valueOf(score));
            }
            displayToast(answer,choice);
            position += 1;
            if (quiz.getQuestions().size() > position) {
                displayQuestion(quiz.getQuestions().get(position));
            } else {
                openCongratsDialog(quiz,respond);
            }
        });
        binding.layoutChoices.addView(view);
    }
    @Override
    public void onStart() {
        super.onStart();
        position = 0;
        score = 0;
        if (mTimerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }
    /**
     * Timer functions
     */
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                openCongratsDialog(quiz,respond);
            }
        }.start();
        mTimerRunning = true;
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }


    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.textTime.setText(timeLeftFormatted);
    }
    private void openCongratsDialog(Quiz quiz,Respond respond) {
        pauseTimer();
        NavDirections directions = StartActivity3Directions.actionStartActivity3ToFinishActivity(quiz,respond);
        Navigation.findNavController(binding.getRoot()).navigate(directions);
    }
    private void displayToast(String answer ,String yourAnswer) {
        Toast toast = new Toast(binding.getRoot().getContext());
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        if (answer.equals(yourAnswer)) {
            toast.setView(LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.layout_answer_correct,binding.getRoot(),false));
        } else {
            toast.setView(LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.layout_answer_wrong,binding.getRoot(),false));
        }
        toast.show();
    }
}