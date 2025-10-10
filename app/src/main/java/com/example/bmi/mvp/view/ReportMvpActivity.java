package com.example.bmi.mvp.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bmi.R;
import com.example.bmi.mvp.model.BmiModel;
import com.example.bmi.mvp.presenter.ReportPresenter;

import java.text.DecimalFormat;

public class ReportMvpActivity extends AppCompatActivity implements ReportContract.View {

    private ImageView reportImage;
    private TextView reportResult;
    private TextView reportAdvice;

    private ReportPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // 初始化Presenter
        presenter = new ReportPresenter(this);

        // 初始化UI控件
        reportImage = findViewById(R.id.report_image);
        reportResult = findViewById(R.id.report_result);
        reportAdvice = findViewById(R.id.report_advice);

        // 获取传递的数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String height = bundle.getString("height");
            String weight = bundle.getString("weight");
            String age = bundle.getString("age");
            String gender = bundle.getString("gender");

            // 通知Presenter进行计算
            presenter.onViewCreated(height, weight, age, gender);
        }
    }

    @Override
    public void displayResult(BmiModel.BmiResult result) {
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

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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