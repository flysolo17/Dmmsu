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

import com.flysolo.dmmsugradelevelapp.databinding.FragmentContentTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.model.Lesson;
import com.flysolo.dmmsugradelevelapp.views.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ContentAdapter;
import com.flysolo.dmmsugradelevelapp.views.dialogs.UpdateContentDialog;
import com.flysolo.dmmsugradelevelapp.views.teacher.components.CreateContent;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class ContentTab extends Fragment implements ContentAdapter.ContentClickListener {


    private static final String ARG_PARAM1 = "classroomID";
    private static final String ARG_PARAM2 = "lesson";
    private FragmentContentTabBinding binding;
    private String classroomID;
    private Lesson lesson;
    private LoadingDialog loadingDialog;
    private LessonServiceImpl lessonService;
    private List<Content> contents;
    private ContentAdapter contentAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_PARAM1);
            lesson = getArguments().getParcelable(ARG_PARAM2);

            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContentTabBinding.inflate(inflater,container,false);
        loadingDialog = new LoadingDialog(binding.getRoot().getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLesson(lesson.getId());
        binding.buttonCreateContent.setOnClickListener(view1 -> {
            if (lesson != null) {
                CreateContent content = CreateContent.newInstance(classroomID,lesson.getId());
                if (!content.isAdded()) {
                    content.show(getChildFragmentManager(),"Create Content");
                }
            }
        });
        binding.recyclerviewContent.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }

    @Override
    public void onDelete(int position,Content content) {
        lessonService.deleteContent(lesson.getId(), content,new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Deleting content...");
            }

            @Override
            public void Successful(String data) {
                loadingDialog.stopLoading();
                contentAdapter.notifyItemRemoved(position);
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
    public void onEdit(int position) {
            UpdateContentDialog dialog = UpdateContentDialog.newInstance(position,lesson);
            if (!dialog.isAdded()) {
                dialog.show(getChildFragmentManager(),"Update content");
            }
    }
    private void getLesson(String lessonID) {
        lessonService.getLesson(lessonID, new UiState<Lesson>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting lesson...");
            }

            @Override
            public void Successful(Lesson data) {
                loadingDialog.stopLoading();
                lesson = data;
                contents = data.getContents();
                contentAdapter = new ContentAdapter(binding.getRoot().getContext(),contents,ContentTab.this);
                binding.recyclerviewContent.setAdapter(contentAdapter);
            }
            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}