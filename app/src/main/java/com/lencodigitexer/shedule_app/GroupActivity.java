package com.lencodigitexer.shedule_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.lencodigitexer.shedule_app.databinding.ActivityGroupBinding;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    private LinearLayout groupButtonsLayout;
    private ArrayList<Group> groups;
    private University selectedUniversity;

    // Объект SharedPreferences для сохранения данных
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupButtonsLayout = findViewById(R.id.groupButtonsLayout);
        groups = new ArrayList<>();

        // Получаем объект SharedPreferences по имени "mypref"
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Получаем выбранный университет из SharedPreferences
        String selectedUniversity = sharedPreferences.getString("selected_university", null);
        String selectedGroup = sharedPreferences.getString("selected_group", null);


        if (selectedUniversity != null && selectedGroup != null){
            Intent intent = new Intent(GroupActivity.this, ScheduleActivity.class);
            startActivity(intent);
        } else {
            // Выполните HTTP GET запрос для получения данных о группах из API
            new GetGroupsTask().execute("https://lencodigitexer.github.io/schedule-api/" + selectedUniversity + "/groups.json");
        }



    }

    private void createGroupButtons() {
        for (Group group : groups) {
            Button button = new Button(this);
            button.setText(group.getDescription());
            button.setOnClickListener(v -> {
                // Обработчик клика по кнопке университета
                String selectedGroupName = group.getName();

                // Сохранение выбранного университета и группы в SharedPreferences
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selected_group", selectedGroupName);
                editor.apply();

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Группа сохранена!", Toast.LENGTH_SHORT);
                toast.show();

                // Обработчик клика по кнопке группы
                // Открываем новую активити с расписанием выбранной группы
                Intent intent = new Intent(GroupActivity.this, ScheduleActivity.class);
                startActivity(intent);
            });

            groupButtonsLayout.addView(button);
        }
    }

    private class GetGroupsTask extends AsyncTask<String, Void, String> {
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
                    JSONArray groupArray = jsonObject.getJSONArray("groups");
                    for (int i = 0; i < groupArray.length(); i++) {
                        JSONObject groupObj = groupArray.getJSONObject(i);
                        String name = groupObj.getString("name");
                        String description = groupObj.getString("description");

                        Group group = new Group(name, description);
                        groups.add(group);
                    }

                    createGroupButtons();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}