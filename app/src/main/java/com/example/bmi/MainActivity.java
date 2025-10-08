package com.example.bmi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 声明UI控件
    private EditText heightET;
    private EditText weightET;
    private EditText ageET;
    private EditText genderET;
    private Button reportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定主页面布局
        setContentView(R.layout.activity_main);

        // 初始化UI控件
        heightET = findViewById(R.id.heightET);
        weightET = findViewById(R.id.weightET);
        ageET = findViewById(R.id.ageET);
        genderET = findViewById(R.id.genderET);
        reportBtn = findViewById(R.id.reportBtn);

        // 给按钮设置点击事件（点击后计算BMI并跳转到结果页）
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入框中的内容
                String height = heightET.getText().toString().trim();
                String weight = weightET.getText().toString().trim();
                String age = ageET.getText().toString().trim();
                String gender = genderET.getText().toString().trim().toLowerCase();

                // 数据验证：判断身高/体重是否为空
                if (height.isEmpty() || weight.isEmpty() || age.isEmpty() || gender.isEmpty()) {
                    // 弹出提示框
                    Toast.makeText(MainActivity.this, R.string.bmi_warning, Toast.LENGTH_SHORT).show();
                    return; // 为空则不执行后续操作
                }

                // 简单验证性别输入
                if (!gender.equals("male") && !gender.equals("female")) {
                    Toast.makeText(MainActivity.this, "Please enter 'male' or 'female' for gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 验证年龄范围（未成年人不支持小于6岁）
                int ageNum = Integer.parseInt(age);
                if (ageNum < 6) {
                    Toast.makeText(MainActivity.this, "For minors, age must be between 6-18", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 保存输入的身高体重（下次打开App不用重新输入）
                savePreferences(height, weight, age, gender);

                // 跳转到结果页：通过Intent传递数据
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("height", height); // 传递身高
                bundle.putString("weight", weight); // 传递体重
                bundle.putString("age", age);
                bundle.putString("gender", gender);
                intent.putExtras(bundle);
                startActivity(intent); // 启动结果页
            }
        });
    }

    // 保存数据到本地（SharedPreferences：轻量级存储）
    private void savePreferences(String height, String weight, String age, String gender) {
        SharedPreferences pref = getSharedPreferences("BMI_DATA", MODE_PRIVATE);
        pref.edit()
                .putString("saved_height", height)
                .putString("saved_weight", weight)
                .putString("saved_age", age)
                .putString("saved_gender", gender)
                .apply();
    }

    // 从本地加载数据（App启动时调用）
    private void loadPreferences() {
        SharedPreferences pref = getSharedPreferences("BMI_DATA", MODE_PRIVATE);
        String savedHeight = pref.getString("saved_height", "");
        String savedWeight = pref.getString("saved_weight", "");
        String savedAge = pref.getString("saved_age", "");
        String savedGender = pref.getString("saved_gender", "");
        // 显示到输入框
        heightET.setText(savedHeight);
        weightET.setText(savedWeight);
        ageET.setText(savedAge);
        genderET.setText(savedGender);
    }

    // App启动或从后台回到前台时调用，加载历史数据
    @Override
    protected void onStart() {
        super.onStart();
        loadPreferences();
    }
}