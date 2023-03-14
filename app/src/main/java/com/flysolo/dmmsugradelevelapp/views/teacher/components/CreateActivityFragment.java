package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
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
    private QuestionViewModel questionViewModel;
    private ArrayList<Question> questions = new ArrayList<>();
    private String lessonID;
    private String classroomID;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = CreateActivityFragmentArgs.fromBundle(getArguments()).getClassroomID();
            lessonID = CreateActivityFragmentArgs.fromBundle(getArguments()).getLessonID();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateActivityBinding.inflate(inflater,container,false);
        MenuHost menuHost  = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.teacher_activity_menu,menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_save) {
                    validateQuiz();
                } else  {
                    Navigation.findNavController(binding.getRoot()).popBackStack();
                }
                return true;
            }
        },getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lessonService =new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
        questionViewModel = new ViewModelProvider(requireActivity()).get(QuestionViewModel.class);
        questionViewModel.getQuestion().observe(requireActivity(), question -> {
            addQuestion(question);
        });
        binding.buttonAddQuestion.setOnClickListener(view13 -> {
            AddQuestionFragment fragment = new AddQuestionFragment();
            if (!fragment.isAdded()) {
                fragment.show(getChildFragmentManager(), "add question");
            }
        });

    }
    private void addQuestion(Question question) {
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.layout_question, null, false);
        TextView textQuestion = view.findViewById(R.id.textQuestion);
        TextView textAnswer = view.findViewById(R.id.textAnswer);
        TextView textChoices = view.findViewById(R.id.textChoices);
        ImageButton buttonDeleteQuestion = view.findViewById(R.id.buttonDeleteQuestion);
        buttonDeleteQuestion.setOnClickListener(view1 -> {
            binding.layoutQuestions.removeView(view1);
            questions.remove(question);
        });
        textQuestion.setText(question.getQuestion());
        textAnswer.setText(question.getAnswer());
        if (!question.getChoices().isEmpty() && question.getChoices() != null) {
            textChoices.setText(question.getChoices().toString());
        }
        questions.add(question);
        binding.layoutQuestions.addView(view);

    }
    private void validateQuiz() {
        String name = binding.inoutName.getText().toString();
        String desc = binding.inputDesc.getText().toString();
        if (name.isEmpty()) {
            binding.layoutName.setError("This field is required");
        } else if (desc.isEmpty()) {
            binding.inputDesc.setError("this field is required");
        } else if (questions.isEmpty()) {
            Toast.makeText(binding.getRoot().getContext(), "Questions can't be empty", Toast.LENGTH_SHORT).show();
        } else {
            Quiz quiz = new Quiz("",lessonID,name,desc,questions,System.currentTimeMillis());
            saveQuiz(quiz);
        }
    }
    private void saveQuiz(Quiz quiz) {
        lessonService.createActivity(quiz, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Creating new activity...");
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

}