package edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.shuynh08.tcss450clientside.R;
import edu.uw.tcss450.shuynh08.tcss450clientside.databinding.FragmentContactsBinding;
import edu.uw.tcss450.shuynh08.tcss450clientside.model.UserInfoViewModel;
import edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts.friend_list.FriendListRecyclerViewAdapter;
import edu.uw.tcss450.shuynh08.tcss450clientside.ui.contacts.friend_list.FriendListViewModel;

public class ContactsFragment extends Fragment {

    private FriendListViewModel mContactsModel;
    private ContactsGetInfoViewModel mContactsGetInfoModel;
    private FragmentContactsBinding binding;
    private RecyclerView recyclerView;
    private int mMemberID;
    private UserInfoViewModel mUserInfoModel;
    private ContactsViewPagerAdapter mContactsViewPagerAdapter;


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactsModel = new ViewModelProvider(getActivity())
                .get(FriendListViewModel.class);
        mContactsGetInfoModel = new ViewModelProvider(getActivity())
                .get(ContactsGetInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContactsViewPagerAdapter = new ContactsViewPagerAdapter(this);
        binding.viewPagerContacts.setAdapter(mContactsViewPagerAdapter);

        binding.tabContactsLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPagerContacts.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        binding.viewPagerContacts.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabContactsLayout.getTabAt(position).select();
            }
        });

//        recyclerView = binding.recyclerContacts;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        mUserInfoModel = new ViewModelProvider(getActivity())
//                .get(UserInfoViewModel.class);
//
//        mContactsGetInfoModel.addResponseObserver(
//                getViewLifecycleOwner(),
//                this::observeMemberInfo);
//        mContactsModel.addResponseObserver(
//                getViewLifecycleOwner(),
//                this::observeContacts);
//
//        mContactsGetInfoModel.connectMemberInfo(mUserInfoModel.getmJwt());
    }



    private void observeContacts(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.textContacts.setError(
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
                    binding.textContacts.setError(
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
        mContactsModel.connectContacts(mMemberID, mUserInfoModel.getmJwt());
    }

    private void setUpContacts(JSONObject response) {

        try {
            List<Contacts> contactsList = new ArrayList<>();
            JSONArray keys = response.names();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i);
                JSONObject obj = response.getJSONObject(key);
                String email = obj.getString("username");
                String name = obj.getString("firstname") + " " + obj.getString("lastname");
                contactsList.add(new Contacts(email, name, R.drawable.ic_rainychat_launcher_foreground));
            }
            recyclerView.setAdapter(new FriendListRecyclerViewAdapter(contactsList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}