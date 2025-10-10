package com.example.bmi.mvi.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bmi.R;
import com.example.bmi.mvi.intent.ReportIntent;
import com.example.bmi.mvi.model.BmiResult;
import com.example.bmi.mvi.state.ReportViewState;
import com.example.bmi.mvi.viewmodel.ReportViewModel;

import java.text.DecimalFormat;

public class ReportMviActivity extends AppCompatActivity {

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
        observeViewState();

        // 获取传递的数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String height = bundle.getString("height");
            String weight = bundle.getString("weight");
            String age = bundle.getString("age");
            String gender = bundle.getString("gender");

            // 发送Intent给ViewModel计算BMI
            viewModel.processIntent(
                    new ReportIntent.CalculateResult(height, weight, age, gender)
            );
        }
    }

    private void initViews() {
        reportImage = findViewById(R.id.report_image);
        reportResult = findViewById(R.id.report_result);
        reportAdvice = findViewById(R.id.report_advice);
    }

    private void observeViewState() {
        // 观察ViewState的变化
        viewModel.getViewState().observe(this, this::render);
    }

    // 根据ViewState渲染UI
    private void render(ReportViewState state) {
        if (state == null) return;

        switch (state.getStatus()) {
            case IDLE:
                // 空闲状态 - 不做任何操作
                break;

            case LOADING:
                // 加载状态 - 可以显示进度条
                break;

            case SUCCESS:
                // 成功状态 - 显示结果
                if (state.getResult() != null) {
                    displayResult(state.getResult());
                }
                break;

            case ERROR:
                // 错误状态 - 显示错误消息
                showError(state.getErrorMessage());
                break;
        }
    }

    private void displayResult(BmiResult result) {
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

    private void showError(String message) {
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