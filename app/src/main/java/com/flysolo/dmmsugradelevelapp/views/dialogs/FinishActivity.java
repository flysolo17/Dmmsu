package com.flysolo.dmmsugradelevelapp.views.dialogs;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentFinishActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.services.activity.ActivityServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.leaderboard.LeaderBoardServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.student.components.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class FinishActivity extends Fragment {

    private static final String ARG_QUIZ = "quiz";
    private static final String ARG_RESPOND = "respond";
    private Quiz quiz;
    private Respond respond;
    private FragmentFinishActivityBinding binding;
    private LoadingDialog loadingDialog;
    private LeaderBoardServiceImpl leaderBoardService;
    private FirebaseUser user;
    public FinishActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            quiz = FinishActivityArgs.fromBundle(getArguments()).getQuiz();
            respond = FinishActivityArgs.fromBundle(getArguments()).getRespond();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFinishActivityBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leaderBoardService = new LeaderBoardServiceImpl(FirebaseFirestore.getInstance());
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            respond.setStudentID(user.getUid());
            respond.setDateAnswered(System.currentTimeMillis());
            respond.setTotal(Constants.getMyScore(quiz.getQuestions(),respond));
            saveResponse(respond);
        }
        displayData(quiz,respond);
        binding.buttonDone.setOnClickListener(view1 -> {
            Navigation.findNavController(view).popBackStack();
            Navigation.findNavController(view).popBackStack();
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack();
                Navigation.findNavController(view).popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void saveResponse(Respond respond) {
        leaderBoardService.submitAnswer(respond, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Submitting score....");
            }
            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayData(Quiz quiz,Respond respond) {
        binding.textCorrect.setText(Constants.getCorrectAnswer(quiz.getQuestions(),respond) + " Questions");
        binding.textPoints.setText("+" + Constants.getMyScore(quiz.getQuestions(),respond));
        binding.textCompletion.setText(Constants.getCompletion(Constants.getCorrectAnswer(quiz.getQuestions(),respond),quiz.getQuestions().size()) + "%");
    }

}