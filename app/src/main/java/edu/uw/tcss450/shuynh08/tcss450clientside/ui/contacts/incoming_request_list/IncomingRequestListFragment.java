package edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts.incoming_request_list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.shuynh08.tcss450clientside.R;
import edu.uw.tcss450.shuynh08.tcss450clientside.databinding.FragmentFriendListBinding;
import edu.uw.tcss450.shuynh08.tcss450clientside.databinding.FragmentIncomingRequestCardBinding;
import edu.uw.tcss450.shuynh08.tcss450clientside.databinding.FragmentIncomingRequestListBinding;
import edu.uw.tcss450.shuynh08.tcss450clientside.model.UserInfoViewModel;
import edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts.Contacts;
import edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts.ContactsGetInfoViewModel;
import edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts.friend_list.FriendListRecyclerViewAdapter;
import edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts.friend_list.FriendListViewModel;

/**
 * create an instance of this fragment.
 */
public class IncomingRequestListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FragmentIncomingRequestListBinding binding;
    private UserInfoViewModel mUserInfoViewModel;
    private IncomingRequestViewModel mIncomingRequestModel;
    private ContactsGetInfoViewModel mContactsGetInfoModel;
    private int mMemberID;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIncomingRequestModel = new ViewModelProvider(getActivity()).get(IncomingRequestViewModel.class);
        mContactsGetInfoModel = new ViewModelProvider(getActivity()).get(ContactsGetInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIncomingRequestListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.listIncoming;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUserInfoViewModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        mContactsGetInfoModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeMemberInfo);
        mIncomingRequestModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeContacts);
        mContactsGetInfoModel.connectMemberInfo(mUserInfoViewModel.getmJwt());


    }

    private void observeContacts(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.textIncomingRequestList.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                setUpContacts(response);
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    private void observeMemberInfo(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.textIncomingRequestList.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                setUpInfo(response);
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    private void setUpInfo(JSONObject response) {
        try {
            mMemberID = response.getInt("memberid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIncomingRequestModel.connectContacts(mMemberID, mUserInfoViewModel.getmJwt());
    }

    private void setUpContacts(JSONObject response) {
        System.out.println(response.names());

        try {
            List<Contacts> contactsList = new ArrayList<>();
            JSONObject incoming = response.getJSONObject("incoming_requests");
            JSONArray incomingKeys = incoming.names();
            for (int i = 0; i < incomingKeys.length(); i++) {
                String key = incomingKeys.getString(i);
                JSONObject obj = incoming.getJSONObject(key);
                System.out.println(obj);
                String verified = obj.getString("verified");
                String email = obj.getString("username");
                String name = obj.getString("firstname") + " " + obj.getString("lastname");
                contactsList.add(new Contacts(email, name, R.drawable.ic_rainychat_launcher_foreground));

          }
            recyclerView.setAdapter(new IncomingRequestListViewAdapter(contactsList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}