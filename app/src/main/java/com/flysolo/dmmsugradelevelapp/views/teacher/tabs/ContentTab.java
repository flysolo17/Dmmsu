package com.flysolo.dmmsugradelevelapp.views.teacher.tabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentContentTabBinding;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.services.lesson.LessonServiceImpl;
import com.flysolo.dmmsugradelevelapp.utils.LoadingDialog;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.flysolo.dmmsugradelevelapp.views.adapters.ContentAdapter;
import com.flysolo.dmmsugradelevelapp.views.dialogs.UpdateContentDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;


public class ContentTab extends Fragment implements ContentAdapter.ContentClickListener {


    private static final String ARG_PARAM1 = "classroomID";
    private static final String ARG_PARAM2 = "lessonID";
    private FragmentContentTabBinding binding;

    private String lessonID,classroomID;
    private LoadingDialog loadingDialog;
    private LessonServiceImpl lessonService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classroomID = getArguments().getString(ARG_PARAM1);
            lessonID = getArguments().getString(ARG_PARAM2);
            lessonService = new LessonServiceImpl(FirebaseFirestore.getInstance(), FirebaseStorage.getInstance(),classroomID);
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
        binding.buttonCreateContent.setOnClickListener(view1 -> {
            NavDirections directions = ViewLessonTabDirections.actionViewLessonTabToCreateContent(classroomID,lessonID);
            Navigation.findNavController(view1).navigate(directions);
        });
        binding.recyclerviewContent.setLayoutManager(new LinearLayoutManager(view.getContext()));
        getLessonContent(lessonID);
        }
        private void getLessonContent(String lessonID) {
        lessonService.getAllContent(lessonID, new UiState<List<Content>>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Getting all contents");
            }

            @Override
            public void Successful(List<Content> data) {
                loadingDialog.stopLoading();
                ContentAdapter contentAdapter = new ContentAdapter(binding.getRoot().getContext(),data,ContentTab.this);
                binding.recyclerviewContent.setAdapter(contentAdapter);
            }

            @Override
            public void Failed(String message) {
                loadingDialog.stopLoading();
                Toast.makeText(binding.getRoot().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDelete(String id) {
        lessonService.deleteContent(id, new UiState<String>() {
            @Override
            public void Loading() {
                loadingDialog.showLoadingDialog("Deleting content...");
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
    public void onEdit(Content content) {
        UpdateContentDialog dialog = UpdateContentDialog.newInstance(content,classroomID);
        if (!dialog.isAdded()) {
            dialog.show(getChildFragmentManager(),"Update content");
        }
    }
}