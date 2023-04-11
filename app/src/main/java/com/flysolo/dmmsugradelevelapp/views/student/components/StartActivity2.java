package com.flysolo.dmmsugradelevelapp.views.student.components;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStartActivity2Binding;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStartActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentQuestionAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class StartActivity2 extends Fragment {


    private FragmentStartActivity2Binding binding;
    private Quiz quiz;
    private LoadingDialog loadingDialog;
    private FirebaseUser user;
    //timer
    private long START_TIME_IN_MILLIS = 0;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private CountDownTimer mCountDownTimer;
    private List<Answer> answerList = new ArrayList<>();
    private  StudentQuestionAdapter questionAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quiz = StartActivity2Args.fromBundle(getArguments()).getQuiz();
            START_TIME_IN_MILLIS = quiz.getTimer() * 60000L;
            mTimeLeftInMillis = START_TIME_IN_MILLIS;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartActivity2Binding.inflate(inflater,container,false);
        binding.recyclerviewQuestions.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
        for (Question question: quiz.getQuestions()) {
            displayAnswers(question.getAnswer());
        }
        binding.textTitle.setText(quiz.getName());
        binding.textDesc.setText(quiz.getDescription());
        questionAdapter = new StudentQuestionAdapter(view.getContext(),quiz.getQuestions());
        binding.recyclerviewQuestions.setAdapter(questionAdapter);
        binding.buttonSubmit.setOnClickListener(view1 -> {
            pauseTimer();
            new MaterialAlertDialogBuilder(view1.getContext())
                    .setTitle("Submit answer")
                    .setMessage("Make sure all questions have an answer.")
                    .setPositiveButton("Continue", (dialogInterface, i) -> openCongratsDialog(quiz))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                        startTimer();
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                pauseTimer();
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("Exit Game")
                        .setMessage("Are you sure you want to quit the game?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> Navigation.findNavController(view).popBackStack())
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {
                            startTimer();
                            dialogInterface.dismiss();
                        })
                        .show();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
    private void displayAnswers(String ans) {
        View view= LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.layout_letters,binding.layoutAnswer,false);
        TextView text = view.findViewById(R.id.textLetter);
        text.setAllCaps(false);
        MaterialCardView cardView = view.findViewById(R.id.cardLetter);
        cardView.setCardBackgroundColor(Color.WHITE);
        text.setTextSize(12);
        text.setText(ans);
        binding.layoutAnswer.addView(view);
    }

    @Override
    public void onStart() {
        super.onStart();
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
                openCongratsDialog(quiz);
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

    private void openCongratsDialog(Quiz quiz) {
        pauseTimer();
        if (user != null) {
            Respond respond = new Respond("",quiz.getId(),user.getUid(),questionAdapter.getAnswer(),0,0L);
            NavDirections directions = StartActivity2Directions.actionStartActivity2ToFinishActivity(quiz,respond);
            Navigation.findNavController(binding.getRoot()).navigate(directions);
        }

    }
}