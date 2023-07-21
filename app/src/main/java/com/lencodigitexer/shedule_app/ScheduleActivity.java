package com.lencodigitexer.shedule_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private University selectedUniversity;
    private Group selectedGroup;

    private TableLayout scheduleTable;
    private String currentScheduleJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Получаем выбранный университет и группу из Intent
        selectedUniversity = getIntent().getParcelableExtra("selected_university");
        selectedGroup = getIntent().getParcelableExtra("selected_group");


        scheduleTable = findViewById(R.id.scheduleTable);
        currentScheduleJSON = null;

        // Выполните HTTP GET запрос для получения расписания из API
        new GetScheduleTask().execute("https://lencodigitexer.github.io/schedule-api/" + selectedUniversity.getName() + "/" + selectedGroup.getName() + "/schedule.json");

        // Настройте обработчики для кнопок дней недели
        Button buttonMonday = findViewById(R.id.buttonMonday);
        Button buttonTuesday = findViewById(R.id.buttonTuesday);
        Button buttonWednesday = findViewById(R.id.buttonWednesday);
        Button buttonThursday = findViewById(R.id.buttonThursday);
        Button buttonFriday = findViewById(R.id.buttonFriday);
        Button buttonSaturday = findViewById(R.id.buttonSaturday); // Если нужно, добавьте кнопку для субботы
        // Настройте обработчики нажатия для каждой кнопки
        buttonMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySchedule(currentScheduleJSON, "monday");
            }
        });
        buttonTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySchedule(currentScheduleJSON, "tuesday");
            }
        });
        buttonWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySchedule(currentScheduleJSON, "wednesday");
            }
        });
        buttonThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySchedule(currentScheduleJSON, "thursday");
            }
        });
        buttonFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySchedule(currentScheduleJSON, "friday");
            }
        });
        buttonSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySchedule(currentScheduleJSON, "saturday");
            }
        });
    }

    private void loadSchedule(String universityName, String groupName) {
        // Загрузка расписания по заданному университету и группе
        String scheduleURL = "https://lencodigitexer.github.io/schedule-api/" + universityName + "/" + groupName + "/schedule.json";
        new GetScheduleTask().execute(scheduleURL);
    }

    private void displaySchedule(String scheduleJSON, String selectedDay) {
        try {
            JSONObject jsonObject = new JSONObject(scheduleJSON);
            JSONObject scheduleObj = jsonObject.getJSONObject("schedule");

            // Отображение расписания на экране
            scheduleTable.removeAllViews(); // Очистка предыдущего расписания

            // Получение расписания для выбранного дня
            JSONArray dayScheduleArray = scheduleObj.optJSONArray(selectedDay);

            if (dayScheduleArray != null) {
                // Добавление заголовка дня недели
                TableRow headerRow = new TableRow(this);
                TextView dayTextView = new TextView(this);
                dayTextView.setText(selectedDay.toUpperCase());
                dayTextView.setTextSize(16);
                headerRow.addView(dayTextView);
                scheduleTable.addView(headerRow);

                // Проход по паре в текущем дне недели
                for (int i = 0; i < dayScheduleArray.length(); i++) {
                    JSONObject pairObj = dayScheduleArray.getJSONObject(i);

                    int number = pairObj.getInt("number");
                    String discipline = pairObj.getString("discipline");
                    String office = pairObj.getString("office");

                    // Создание TextView для текущей пары
                    TableRow pairRow = new TableRow(this);
                    TextView numberTextView = new TextView(this);
                    TextView disciplineTextView = new TextView(this);
                    TextView officeTextView = new TextView(this);

                    numberTextView.setText(String.valueOf(number));
                    disciplineTextView.setText(discipline);
                    officeTextView.setText(office);

                    pairRow.addView(numberTextView);
                    pairRow.addView(disciplineTextView);
                    pairRow.addView(officeTextView);

                    scheduleTable.addView(pairRow);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class GetScheduleTask extends AsyncTask<String, Void, String> {
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
                currentScheduleJSON = response; // Обновление текущего расписания

                // Определение текущего дня недели
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                // Определение дня недели для метода displaySchedule
                String selectedDay;
                switch (dayOfWeek) {
                    case Calendar.MONDAY:
                        selectedDay = "monday";
                        break;
                    case Calendar.TUESDAY:
                        selectedDay = "tuesday";
                        break;
                    case Calendar.WEDNESDAY:
                        selectedDay = "wednesday";
                        break;
                    case Calendar.THURSDAY:
                        selectedDay = "thursday";
                        break;
                    case Calendar.FRIDAY:
                        selectedDay = "friday";
                        break;
                    case Calendar.SATURDAY:
                        selectedDay = "saturday";
                        break;
                    default:
                        selectedDay = "monday"; // Если текущий день недели неизвестен, показываем расписание на понедельник
                        break;
                }

                displaySchedule(currentScheduleJSON, selectedDay); // Отображение расписания для текущего дня недели
            }
        }
    }

}