package com.flysolo.dmmsugradelevelapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.flysolo.dmmsugradelevelapp.model.Question;

public class QuestionViewModel extends ViewModel {
    MutableLiveData<Question> selected = new MutableLiveData<>();
    public void setQuestion(Question question) {
        selected.postValue(question);
    }
    public LiveData<Question> getQuestion() {
        return selected;
    }
}
