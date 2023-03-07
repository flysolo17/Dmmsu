package com.flysolo.dmmsugradelevelapp.utils;

import org.jetbrains.annotations.NotNull;

public interface UiState<T> {
    void Loading();
    void Successful(T data);
    void Failed(String message);
}
