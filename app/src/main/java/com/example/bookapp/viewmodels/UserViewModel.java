package com.example.bookapp.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookapp.models.User;
import com.example.bookapp.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository repository = new UserRepository();

    public MutableLiveData<User> userLiveData = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void login(String email, String password) {
        repository.login(email, password, new UserRepository.Callback<User>() {
            @Override
            public void onSuccess(User result) {
                userLiveData.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
    }

    public void signUp(String email, String password, String role) {
        repository.signUp(email, password, role, new UserRepository.Callback<User>() {
            @Override
            public void onSuccess(User result) {
                userLiveData.setValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                errorLiveData.setValue(e.getMessage());
            }
        });
    }
}
