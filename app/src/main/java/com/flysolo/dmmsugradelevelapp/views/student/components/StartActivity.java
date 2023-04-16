package com.flysolo.dmmsugradelevelapp.views.student.components;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flysolo.dmmsugradelevelapp.MainActivity;
import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentStartActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Answer;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.StudentQuestionAdapter;
import com.flysolo.dmmsugradelevelapp.views.dialogs.FinishActivity;
import com.flysolo.dmmsugradelevelapp.views.dialogs.StudentViewActivity;
import com.flysolo.dmmsugradelevelapp.views.teacher.tabs.ViewLessonTabArgs;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class StartActivity extends Fragment {
    private Quiz quiz;
    private FragmentStartActivityBinding binding;
    private int position = 0;
    private int score = 0;
    String ans = "";
    private Respond respond;
    private FirebaseUser user;
    //timer
    private long START_TIME_IN_MILLIS = 0;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private CountDownTimer mCountDownTimer;
    private List<Answer> answerList = new ArrayList<>();
    //timer
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quiz = StartActivityArgs.fromBundle(getArguments()).getActivity();
            START_TIME_IN_MILLIS = quiz.getTimer() * 60000L;
            mTimeLeftInMillis = START_TIME_IN_MILLIS;
            respond = new Respond("",quiz.getId(),"",answerList,0,0L);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStartActivityBinding.inflate(inflater,container,false);
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
        if (!question.getImage().isEmpty()) {
            Glide.with(binding.getRoot().getContext()).load(question.getImage()).into(binding.imageQuestion);
        } else {
            binding.imageQuestion.setVisibility(View.GONE);
        }
        if (question.getQuestion().isEmpty()) {
            binding.textQuestion.setVisibility(View.GONE);
        }
        binding.textPoints.setText(String.format("+%s",question.getPoints()));
        binding.textQuestionPosition.setText(String.valueOf(position + 1));
        binding.textQuestion.setText(question.getQuestion());
        binding.layoutLetters.removeAllViews();
        binding.layoutAnswer.removeAllViews();
        String result = Constants.shuffle(question.getAnswer());
        for (String s: result.split("")) {
            displayChoices(s,question.getAnswer());
        }
    }
    private void displayAnswer(String letter,int pos) {
        View view = LayoutInflater.from(binding.layoutAnswer.getContext()).inflate(R.layout.layout_letters,binding.layoutAnswer,false);
        TextView text = view.findViewById(R.id.textLetter);
        MaterialCardView cardLetter = view.findViewById(R.id.cardLetter);
        cardLetter.setOnClickListener(view1 -> {
            binding.layoutAnswer.removeView(view);
            displayChoices(letter,quiz.getQuestions().get(position).getAnswer());
            ans = Constants.deleteCharAt(ans,pos);
        });
        text.setText(letter);
        text.setTextSize(12);
        binding.layoutAnswer.addView(view);
    }

    private void displayChoices(String letter,String answer) {;
        View view = LayoutInflater.from(binding.layoutAnswer.getContext()).inflate(R.layout.layout_letters,binding.layoutLetters,false);
        TextView text = view.findViewById(R.id.textLetter);
        MaterialCardView cardLetter = view.findViewById(R.id.cardLetter);
        text.setText(letter);
        binding.layoutLetters.addView(view);
        cardLetter.setOnClickListener(view1 -> {
            ans += letter;
            displayAnswer(letter,ans.length() - 1);
            binding.layoutLetters.removeView(view);
            if (ans.length() == answer.length()) {
                answerList.add(new Answer(quiz.getQuestions().get(position).getId(),ans));
                displayToast(answer,ans);
                if (ans.equals(answer)) {
                    score += quiz.getQuestions().get(position).getPoints();
                    binding.textScore.setText(String.valueOf(score));
                }
                ans = "";
                position += 1;
                if (quiz.getQuestions().size() > position) {
                    displayQuestion(quiz.getQuestions().get(position));
                } else {
                    openCongratsDialog(quiz,respond);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        position = 0;
        score = 0;
        ans = "";
        if (mTimerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

//    private TextWatcher positionWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            if (position == quiz.getQuestions().size()) {
//                openCongratsDialog(quiz,respond);
//            } else {
//                displayQuestion(quiz.getQuestions().get(position));
//            }
//        }
//    };



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
        NavDirections directions = StartActivityDirections.actionStartActivityToFinishActivity(quiz,respond);
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