package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentQuestionsTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.model.Quiz;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.TeacherQuestionAdapter;
import com.flysolo.dmmsugradelevelapp.views.dialogs.UpdateQuestionDIalog;
import com.flysolo.dmmsugradelevelapp.views.teacher.components.AddQuestionFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class QuestionsTab extends Fragment implements TeacherQuestionAdapter.QuestionClickListener {

    private static final String ARG_CLASSROOM_ID = "classroomID";
    private static final String ARG_ACTIVITY_ID = "activityID";
    private String classroomID;
    private String activityID;
    private LessonServiceImpl lessonService;
    private LoadingDialog loadingDialog;
    private FragmentQuestionsTabBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_CLASSROOM_ID);
            activityID = getArguments().getString(ARG_ACTIVITY_ID);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuestionsTabBinding.inflate(inflater,container,false);
        loadingDialog= new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerviewQuestions.setLayoutManager(new LinearLayoutManager(view.getContext()));
        binding.buttonCreateQuestion.setOnClickListener(view1 -> {
            AddQuestionFragment questionFragment = AddQuestionFragment.newInstance(activityID,classroomID);
            if (!questionFragment.isAdded()) {
                questionFragment.show(getChildFragmentManager(),"Add Question");
            }
        });
        if (activityID != null) {
            getActivity(activityID);
        }
    }
    private void getActivity(String activityID) {
        lessonService.getQuestionsByID(activityID, new UiState<List<Question>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting Activity....");
            }
            @Override
            public void Successful(List<Question> data) {
                loadingDialog.stopLoading();
                TeacherQuestionAdapter questionAdapter = new TeacherQuestionAdapter(binding.getRoot().getContext(),data,QuestionsTab.this);
                binding.recyclerviewQuestions.setAdapter(questionAdapter);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
            }
        });
    }

    @Override
    public void onDelete(String questionID) {
        lessonService.deleteQuestion(questionID, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Deleting Question...");
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

    @Override
    public void onEdit(Question question) {
        UpdateQuestionDIalog dIalog = UpdateQuestionDIalog.newInstance(classroomID,question);
        if (!dIalog.isAdded()) {
            dIalog.show(getChildFragmentManager(),"Update question");
        }
    }
}