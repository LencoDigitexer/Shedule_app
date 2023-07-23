package com.lencodigitexer.shedule_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LinearLayout universityButtonsLayout;
    private ArrayList<University> universities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Загрузка сохраненных данных
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String selectedUniversityName = preferences.getString("selected_university", null);

        // Проверка, есть ли сохраненные данные
        if (selectedUniversityName != null) {
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            startActivity(intent);
        }
        else {
            setContentView(R.layout.activity_main);

            universityButtonsLayout = findViewById(R.id.universityButtonsLayout);
            universities = new ArrayList<>();

            // Выполните HTTP GET запрос для получения данных из API
            new GetUniversitiesTask().execute("https://lencodigitexer.github.io/schedule-api/university.json");
        }
    }

    private void createUniversityButtons() {
        for (University university : universities) {
            Button button = new Button(this);
            button.setText(university.getDescription());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Обработчик клика по кнопке университета
                    String selectedUniversityName = university.getName();

                    // Сохранение выбранного университета и группы в SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("selected_university", selectedUniversityName);
                    editor.apply();

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Университет сохранен!", Toast.LENGTH_SHORT);
                    toast.show();

                    // Обработчик клика по кнопке университета
                    // Открываем новую активити с выбором групп для выбранного университета
                    Intent intent = new Intent(MainActivity.this, GroupActivity.class);
                    intent.putExtra("selected_university", university);
                    startActivity(intent);
                }
            });

            universityButtonsLayout.addView(button);
        }
    }

    private class GetUniversitiesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray universityArray = jsonObject.getJSONArray("university");
                    for (int i = 0; i < universityArray.length(); i++) {
                        JSONObject universityObj = universityArray.getJSONObject(i);
                        String name = universityObj.getString("name");
                        String description = universityObj.getString("description");

                        University university = new University(name, description);
                        universities.add(university);
                    }

                    createUniversityButtons();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}