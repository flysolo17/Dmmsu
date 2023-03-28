package com.flysolo.dmmsugradelevelapp.services.leaderboard;

import com.flysolo.dmmsugradelevelapp.model.Respond;
import com.flysolo.dmmsugradelevelapp.utils.UiState;

public interface LeaderBoardService {
    void submitAnswer(String classroomID,Respond respond, UiState<String> result);
}
