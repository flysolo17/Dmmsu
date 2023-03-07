package com.flysolo.dmmsugradelevelapp.views.teacher.components;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.databinding.FragmentAddQuestionBinding;
import com.flysolo.dmmsugradelevelapp.model.Question;
import com.flysolo.dmmsugradelevelapp.viewmodels.QuestionViewModel;

import java.util.ArrayList;
import java.util.List;


public class AddQuestionFragment extends DialogFragment {
    private FragmentAddQuestionBinding binding;
    private QuestionViewModel questionViewModel;
    private ArrayList<String> choices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddQuestionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        choices = new ArrayList<>();
        questionViewModel =new ViewModelProvider(requireActivity()).get(QuestionViewModel.class);
        binding.buttonAddChoice.setOnClickListener(view1 -> {
            if (binding.cardAddChoice.getVisibility() == View.VISIBLE){
                binding.cardAddChoice.setVisibility(View.GONE);
            } else {
                binding.cardAddChoice.setVisibility(View.VISIBLE);
            }
        });
        binding.buttonBack.setOnClickListener(view14 -> dismiss());
        binding.buttonSaveChoice.setOnClickListener(view12 -> {
            String choice = binding.inputChoice.getText().toString();
            if (choice.isEmpty()) {
                binding.inputChoice.setError("This field is required");
            } else {
                if (choices.contains(choice)) {
                    Toast.makeText(view.getContext(), choice + " already added!", Toast.LENGTH_SHORT).show();
                } else {
                    addChoice(choice);
                }
            }
        });
        binding.buttonSaveQuestion.setOnClickListener(view13 -> {
            String question = binding.inputQuestion.getText().toString();
            String answer = binding.inputAnswer.getText().toString();
            String points = binding.inputPoints.getText().toString();
            if (question.isEmpty()) {
                binding.layoutQuestion.setError("This field is required!");
            } else if (answer.isEmpty()) {
                binding.layoutAnswer.setError("This field is required");
            } else if (points.isEmpty()){
                binding.layoutPoints.setError("This field is required");
            } else {
                Question questions = new Question(question,answer,choices,Integer.parseInt(points));
                questionViewModel.setQuestion(questions);
                dismiss();
            }
        });
    }
    private void addChoice(String choice) {
        View view = LayoutInflater.from(binding.getRoot().getContext()).inflate(R.layout.layout_choice,null,false);
        TextView textChoice = view.findViewById(R.id.textChoice);
        textChoice.setText(choice);
        ImageButton buttonDeleteChoice = view.findViewById(R.id.buttonDeleteChoice);
        buttonDeleteChoice.setOnClickListener(view1 ->{
            choices.remove(choice);
            binding.layoutChoices.removeView(view);
                }
        );
        choices.add(choice);
        binding.layoutChoices.addView(view);
        binding.cardAddChoice.setVisibility(View.GONE);
        binding.inputChoice.setText("");
    }

}