package com.example.bmi.mvvm.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bmi.R;
import com.example.bmi.mvvm.model.BmiModel;
import com.example.bmi.mvvm.viewmodel.ReportViewModel;

import java.text.DecimalFormat;

public class ReportMvvmActivity extends AppCompatActivity {

    private ImageView reportImage;
    private TextView reportResult;
    private TextView reportAdvice;

    private ReportViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        initViews();
        observeViewModel();

        // 获取传递的数据并计算BMI
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String height = bundle.getString("height");
            String weight = bundle.getString("weight");
            String age = bundle.getString("age");
            String gender = bundle.getString("gender");

            viewModel.calculateBmi(height, weight, age, gender);
        }
    }

    private void initViews() {
        reportImage = findViewById(R.id.report_image);
        reportResult = findViewById(R.id.report_result);
        reportAdvice = findViewById(R.id.report_advice);
    }

    private void observeViewModel() {
        // 观察BMI结果
        viewModel.getBmiResultLiveData().observe(this, this::displayResult);

        // 观察错误消息
        viewModel.getErrorMessageLiveData().observe(this, this::showError);
    }

    private void displayResult(BmiModel.BmiResult result) {
        if (result != null) {
            // 格式化BMI值
            DecimalFormat df = new DecimalFormat("0.0");
            String bmiValue = df.format(result.bmiValue);

            // 显示BMI值
            reportResult.setText(getString(R.string.bmi_result) + " " + bmiValue);

            // 设置图片
            reportImage.setImageResource(result.imageResource);

            // 设置建议
            reportAdvice.setText(result.adviceResource);

            // 如果是严重情况，添加特殊样式和动画
            if (result.isSevere) {
                reportAdvice.setTextAppearance(this, R.style.SevereWarningText);
                reportAdvice.setBackgroundResource(R.drawable.severe_warning_bg);
                startBlinkAnimation(reportAdvice);
            }
        }
    }

    private void showError(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    // 添加闪烁动画方法
    private void startBlinkAnimation(TextView textView) {
        android.view.animation.Animation blink =
                new android.view.animation.AlphaAnimation(0.3f, 1.0f);
        blink.setDuration(500);
        blink.setRepeatMode(android.view.animation.Animation.REVERSE);
        blink.setRepeatCount(android.view.animation.Animation.INFINITE);
        textView.startAnimation(blink);
    }
}