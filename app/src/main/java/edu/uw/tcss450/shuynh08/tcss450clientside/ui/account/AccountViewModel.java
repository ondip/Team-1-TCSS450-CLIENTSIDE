package edu.uw.tcss450.shuynh08.tcss450clientside.ui.account;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

public class AccountViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }



}
