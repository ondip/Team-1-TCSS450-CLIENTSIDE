package edu.uw.tcss450.shuynh08.tcss450clientside.ui.weather;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.shuynh08.tcss450clientside.R;
import edu.uw.tcss450.shuynh08.tcss450clientside.databinding.FragmentWeatherBinding;


public class WeatherFragment extends Fragment {

    private FragmentWeatherBinding binding;
    private WeatherCurrentViewModel mWeatherCurrentModel;
    private Weather24HourViewModel mWeather24HourModel;
    private Weather5DayViewModel mWeather5DayModel;
    private RecyclerView recyclerView;
    private String ip;

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherCurrentModel = new ViewModelProvider(getActivity())
                .get(WeatherCurrentViewModel.class);
        mWeather24HourModel = new ViewModelProvider(getActivity())
                .get(Weather24HourViewModel.class);
        mWeather5DayModel = new ViewModelProvider(getActivity())
                .get(Weather5DayViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*Context context = requireContext().getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());*/

        ip = "2601:603:1a7f:84d0:60bf:26b3:c5ba:4de";


        binding.buttonCurrent.setOnClickListener(this::attemptCurrentWeather);

        binding.button24hour.setOnClickListener(this::attempt24HourWeather);

        binding.button5day.setOnClickListener(this::attempt5DayWeather);

        recyclerView = binding.listWeather;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mWeatherCurrentModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeWeatherCurrent);

        mWeather24HourModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeWeather24Hour);

        mWeather5DayModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeWeather5Day);
        mWeatherCurrentModel.connectCurrent(ip);
    }

    private void attemptCurrentWeather(final View button) {
        mWeatherCurrentModel.connectCurrent(ip);
    }

    private void attempt24HourWeather(final View button) {
        mWeather24HourModel.connect24Hour(ip);
    }

    private void attempt5DayWeather(final View button) {
        mWeather5DayModel.connect5Days(ip);
    }

    private void setUpCurrent(JSONObject response) {
        System.out.println(response);
        try {
            JSONArray weather = response.getJSONArray("weather");
            System.out.println("weather :" + weather);
            JSONObject weatherObject = weather.getJSONObject(0);

            //JSONObject weatherTypeObject = weather.getJSONObject(1);
            String weatherType = weatherObject.getString("main");

            //JSONObject weatherDescriptionObject = weather.getJSONObject(2);
            String weatherDescription = weatherObject.getString("description");

            JSONObject tempObject = response.getJSONObject("main");
            double temp = tempObject.getDouble("temp");

            String city = response.getString("name");
            Weather weatherThing = new Weather(weatherType, weatherDescription, temp, city, "", R.drawable.ic_rainychat_launcher_foreground);
            List<Weather> weatherList = new ArrayList<>();
            weatherList.add(weatherThing);
            recyclerView.setAdapter(new WeatherRecyclerViewAdapter(weatherList));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUp24Hour(JSONObject response) {
        System.out.println(response);
        try {
            List<Weather> weatherList = new ArrayList<>();

            JSONObject cityObj = response.getJSONObject("city");
            String city = cityObj.getString("name");

            JSONArray arrayOfWeather = response.getJSONArray("list");
            for (int i = 0; i < arrayOfWeather.length(); i++) {
                JSONObject listObj = arrayOfWeather.getJSONObject(i);
                JSONObject mainObj = listObj.getJSONObject("main");
                Double temp = mainObj.getDouble("temp");
                String time = listObj.getString("dt_txt");

                JSONArray weatherArray = listObj.getJSONArray("weather");
                JSONObject weatherObj = weatherArray.getJSONObject(0);
                String weatherType = weatherObj.getString("main");
                String weatherDescription = weatherObj.getString("description");

                weatherList.add(new Weather(weatherType, weatherDescription, temp, city, time, R.drawable.ic_rainychat_launcher_foreground));
            }
            recyclerView.setAdapter(new WeatherRecyclerViewAdapter(weatherList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUp5Day(JSONObject response) {
        System.out.println(response);
        try {
            List<Weather> weatherList = new ArrayList<>();

            JSONObject cityObj = response.getJSONObject("city");
            String city = cityObj.getString("name");

            JSONArray arrayOfWeather = response.getJSONArray("list");
            for (int i = 0; i < arrayOfWeather.length(); i = i + 8) {
                JSONObject listObj = arrayOfWeather.getJSONObject(i);
                JSONObject mainObj = listObj.getJSONObject("main");
                Double temp = mainObj.getDouble("temp");
                String time = listObj.getString("dt_txt");

                JSONArray weatherArray = listObj.getJSONArray("weather");
                JSONObject weatherObj = weatherArray.getJSONObject(0);
                String weatherType = weatherObj.getString("main");
                String weatherDescription = weatherObj.getString("description");

                weatherList.add(new Weather(weatherType, weatherDescription, temp, city, time, R.drawable.ic_rainychat_launcher_foreground));
            }
            recyclerView.setAdapter(new WeatherRecyclerViewAdapter(weatherList));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void observeWeatherCurrent(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editLocation.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                setUpCurrent(response);
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    private void observeWeather24Hour(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editLocation.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                setUp24Hour(response);
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    private void observeWeather5Day(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editLocation.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                setUp5Day(response);
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

}