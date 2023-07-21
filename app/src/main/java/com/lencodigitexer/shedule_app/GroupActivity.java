package com.lencodigitexer.shedule_app;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        groupButtonsLayout = findViewById(R.id.groupButtonsLayout);
        groups = new ArrayList<>();

        // Получаем выбранный университет из Intent
        selectedUniversity = getIntent().getParcelableExtra("selected_university");

        // Выполните HTTP GET запрос для получения данных о группах из API
        new GetGroupsTask().execute("https://lencodigitexer.github.io/schedule-api/" + selectedUniversity.getName() + "/groups.json");
    }

    private void createGroupButtons() {
        for (Group group : groups) {
            Button button = new Button(this);
            button.setText(group.getDescription());
            button.setOnClickListener(v -> {
                // Обработчик клика по кнопке группы
                // Открываем новую активити с расписанием выбранной группы
                Intent intent = new Intent(GroupActivity.this, ScheduleActivity.class);
                intent.putExtra("selected_university", selectedUniversity);
                intent.putExtra("selected_group", group);
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