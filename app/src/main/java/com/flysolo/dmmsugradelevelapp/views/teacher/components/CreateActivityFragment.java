package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;

import com.flysolo.dmmsugradelevelapp.databinding.FragmentCreateActivityBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.model.QuizType;
import com.flysolo.dmmsugradelevelapp.services.activity.ActivityServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.classroom.ClassroomServiceImpl;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonService;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.viewmodels.QuestionViewModel;
import com.flysolo.dmmsugradelevelapp.views.teacher.nav.TeacherClassroomFragmentArgs;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class CreateActivityFragment extends Fragment {

    private FragmentCreateActivityBinding binding;
    private LoadingDialog loadingDialog;
    private ActivityServiceImpl activityService;
    private String LESSON_ID;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            LESSON_ID = CreateActivityFragmentArgs.fromBundle(getArguments()).getLessonID();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateActivityBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activityService = new ActivityServiceImpl(FirebaseFirestore.getInstance(),FirebaseStorage.getInstance());
        binding.buttonCreateActivity.setOnClickListener(view1 -> {
            String title = binding.inputTitle.getText().toString();
            String desc = binding.inputDesc.getText().toString();
            String timer = binding.inputTimer.getText().toString();
            QuizType quizType = getQuizType(binding.radioGroup.getCheckedRadioButtonId());
            if (title.isEmpty()) {
                binding.inputTitle.setError("This field is required");
            } else if (timer.isEmpty()) {
                binding.inputTimer.setError("this field is required");
            } else {
                Quiz quiz = new Quiz("",LESSON_ID,title,desc,Integer.parseInt(timer),quizType,new ArrayList<>(),System.currentTimeMillis());
                createActivity(quiz);
            }
        });
    }
    private void createActivity(Quiz quiz) {
        activityService.addActvity(quiz, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Creating activity....");
            }
            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), data, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private QuizType getQuizType(int selected) {
        QuizType type ;
        if (binding.radioButton1.getId() == selected) {
            type = QuizType.WORD_HUNT;
        } else {
            type = QuizType.FILL_IN_THE_BLANK;
        }
        return type;
    }
}