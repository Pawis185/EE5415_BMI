package com.example.bmi.mvc;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bmi.R;

import java.text.DecimalFormat;

public class ReportActivity extends AppCompatActivity {

    // 声明UI控件
    private ImageView reportImage;
    private TextView reportResult;
    private TextView reportAdvice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定结果页面布局
        setContentView(R.layout.activity_report);

        // 初始化UI控件（通过ID找到布局中的元素）
        reportImage = findViewById(R.id.report_image);
        reportResult = findViewById(R.id.report_result);
        reportAdvice = findViewById(R.id.report_advice);

        // 获取从MainActivity传递过来的数据
        Bundle bundle = getIntent().getExtras();
        String heightStr = bundle.getString("height");
        String weightStr = bundle.getString("weight");
        String ageStr = bundle.getString("age");
        String gender = bundle.getString("gender");

        // 将字符串转换为数值（cm转m，所以除以100）
        double height = Double.parseDouble(heightStr) / 100;
        double weight = Double.parseDouble(weightStr);
        int age = Integer.parseInt(ageStr);

        // 计算BMI：BMI = 体重(kg) / (身高(m) * 身高(m))
        double bmi = weight / (height * height);

        // 保留1位小数
        DecimalFormat df = new DecimalFormat("0.0");
        String bmiValue = df.format(bmi);

        // 显示BMI数值
        reportResult.setText(getString(R.string.bmi_result) + " " + bmiValue);

        // 根据年龄和BMI值判断健康状态
        if (age > 18) {
            // 成年人标准
            handleAdultBMI(bmi);
        } else {
            // 未成年人标准
            handleChildBMI(bmi, age, gender);
        }
    }

    // 处理成年人BMI判断
    private void handleAdultBMI(double bmi) {
        // 新增：将BMI四舍五入保留一位小数后再进行比较
        bmi = Math.round(bmi * 10) / 10.0;

        if (bmi >= 25) {
            // 肥胖
            reportImage.setImageResource(R.drawable.bot_fat);
            reportAdvice.setText(R.string.advice_obese);
        } else if (bmi >= 23) {
            // 超重
            reportImage.setImageResource(R.drawable.bot_fat);
            reportAdvice.setText(R.string.advice_overweight);
        } else if (bmi >= 18.5) {
            // 正常范围
            reportImage.setImageResource(R.drawable.bot_fit);
            reportAdvice.setText(R.string.advice_normal);
        } else {
            // 偏瘦
            reportImage.setImageResource(R.drawable.bot_thin);
            reportAdvice.setText(R.string.advice_underweight);
        }
    }

    // 处理未成年人BMI判断
    private void handleChildBMI(double bmi, int age, String gender) {
        // 新增：将BMI四舍五入保留一位小数后再进行比较
        bmi = Math.round(bmi * 10) / 10.0;

        // 标记BMI所属等级
        boolean isSeverelyUnderweight = false;
        boolean isUnderweight = false;
        boolean isNormal = false;
        boolean isOverweight = false;
        boolean isSeverelyOverweight = false;

        // 男孩（Male）的BMI标准（严格对应图表6-18岁数据）
        if (gender.equals("male")) {
            switch (age) {
                case 6:
                    isSeverelyUnderweight = bmi <= 12.8;
                    isUnderweight = bmi >= 12.9 && bmi <= 13.1;
                    isNormal = bmi >= 13.2 && bmi <= 18.8;
                    isOverweight = bmi >= 18.9 && bmi <= 21.4;
                    isSeverelyOverweight = bmi >= 21.5;
                    break;
                case 7:
                    isSeverelyUnderweight = bmi <= 13.0;
                    isUnderweight = bmi >= 13.1 && bmi <= 13.3;
                    isNormal = bmi >= 13.4 && bmi <= 19.8;
                    isOverweight = bmi >= 19.9 && bmi <= 23.0;
                    isSeverelyOverweight = bmi >= 23.1;
                    break;
                case 8:
                    isSeverelyUnderweight = bmi <= 13.2;
                    isUnderweight = bmi >= 13.3 && bmi <= 13.6;
                    isNormal = bmi >= 13.7 && bmi <= 20.9;
                    isOverweight = bmi >= 21.0 && bmi <= 24.6;
                    isSeverelyOverweight = bmi >= 24.7;
                    break;
                case 9:
                    isSeverelyUnderweight = bmi <= 13.5;
                    isUnderweight = bmi >= 13.6 && bmi <= 13.8;
                    isNormal = bmi >= 13.9 && bmi <= 21.8;
                    isOverweight = bmi >= 21.9 && bmi <= 26.0;
                    isSeverelyOverweight = bmi >= 26.1;
                    break;
                case 10:
                    isSeverelyUnderweight = bmi <= 13.8;
                    isUnderweight = bmi >= 13.9 && bmi <= 14.1;
                    isNormal = bmi >= 14.2 && bmi <= 22.7;
                    isOverweight = bmi >= 22.8 && bmi <= 27.3;
                    isSeverelyOverweight = bmi >= 27.4;
                    break;
                case 11:
                    isSeverelyUnderweight = bmi <= 14.1;
                    isUnderweight = bmi >= 14.2 && bmi <= 14.5;
                    isNormal = bmi >= 14.6 && bmi <= 23.6;
                    isOverweight = bmi >= 23.7 && bmi <= 28.3;
                    isSeverelyOverweight = bmi >= 28.4;
                    break;
                case 12:
                    isSeverelyUnderweight = bmi <= 14.4;
                    isUnderweight = bmi >= 14.5 && bmi <= 14.8;
                    isNormal = bmi >= 14.9 && bmi <= 24.3;
                    isOverweight = bmi >= 24.4 && bmi <= 29.2;
                    isSeverelyOverweight = bmi >= 29.3;
                    break;
                case 13:
                    isSeverelyUnderweight = bmi <= 14.7;
                    isUnderweight = bmi >= 14.8 && bmi <= 15.1;
                    isNormal = bmi >= 15.2 && bmi <= 25.0;
                    isOverweight = bmi >= 25.1 && bmi <= 30.0;
                    isSeverelyOverweight = bmi >= 30.1;
                    break;
                case 14:
                    isSeverelyUnderweight = bmi <= 15.0;
                    isUnderweight = bmi >= 15.1 && bmi <= 15.4;
                    isNormal = bmi >= 15.5 && bmi <= 25.5;
                    isOverweight = bmi >= 25.6 && bmi <= 30.6;
                    isSeverelyOverweight = bmi >= 30.7;
                    break;
                case 15:
                    isSeverelyUnderweight = bmi <= 15.3;
                    isUnderweight = bmi >= 15.4 && bmi <= 15.8;
                    isNormal = bmi >= 15.9 && bmi <= 26.1;
                    isOverweight = bmi >= 26.2 && bmi <= 31.2;
                    isSeverelyOverweight = bmi >= 31.3;
                    break;
                case 16:
                    isSeverelyUnderweight = bmi <= 15.6;
                    isUnderweight = bmi >= 15.7 && bmi <= 16.1;
                    isNormal = bmi >= 16.2 && bmi <= 26.5;
                    isOverweight = bmi >= 26.6 && bmi <= 31.7;
                    isSeverelyOverweight = bmi >= 31.8;
                    break;
                case 17:
                    isSeverelyUnderweight = bmi <= 15.9;
                    isUnderweight = bmi >= 16.0 && bmi <= 16.3;
                    isNormal = bmi >= 16.4 && bmi <= 27.0;
                    isOverweight = bmi >= 27.1 && bmi <= 32.1;
                    isSeverelyOverweight = bmi >= 32.2;
                    break;
                case 18:
                    isSeverelyUnderweight = bmi <= 16.1;
                    isUnderweight = bmi >= 16.2 && bmi <= 16.6;
                    isNormal = bmi >= 16.7 && bmi <= 27.4;
                    isOverweight = bmi >= 27.5 && bmi <= 32.4;
                    isSeverelyOverweight = bmi >= 32.5;
                    break;
                default:
                    // 若年龄不在6-18岁（图表范围），提示"年龄超出参考范围"
                    reportAdvice.setText("Age out of reference range (6-18)");
                    return;
            }
        }
        // 女孩（Female）的BMI标准（严格对应图表6-18岁数据，精准区间与比较符）
        else if (gender.equals("female")) {
            switch (age) {
                case 6:
                    isSeverelyUnderweight = bmi <= 12.6;
                    isUnderweight = bmi >= 12.7 && bmi <= 12.8;
                    isNormal = bmi >= 12.9 && bmi <= 18.3;
                    isOverweight = bmi >= 18.4 && bmi <= 20.5;
                    isSeverelyOverweight = bmi >= 20.6;
                    break;
                case 7:
                    isSeverelyUnderweight = bmi <= 12.8;
                    isUnderweight = bmi >= 12.9 && bmi <= 13.1;
                    isNormal = bmi >= 13.2 && bmi <= 19.1;
                    isOverweight = bmi >= 19.2 && bmi <= 21.8;
                    isSeverelyOverweight = bmi >= 21.9;
                    break;
                case 8:
                    isSeverelyUnderweight = bmi <= 13.1;
                    isUnderweight = bmi >= 13.2 && bmi <= 13.4;
                    isNormal = bmi >= 13.5 && bmi <= 20.1;
                    isOverweight = bmi >= 20.2 && bmi <= 23.1;
                    isSeverelyOverweight = bmi >= 23.2;
                    break;
                case 9:
                    isSeverelyUnderweight = bmi <= 13.4;
                    isUnderweight = bmi >= 13.5 && bmi <= 13.7;
                    isNormal = bmi >= 13.8 && bmi <= 21.0;
                    isOverweight = bmi >= 21.1 && bmi <= 24.4;
                    isSeverelyOverweight = bmi >= 24.5;
                    break;
                case 10:
                    isSeverelyUnderweight = bmi <= 13.7;
                    isUnderweight = bmi >= 13.8 && bmi <= 14.1;
                    isNormal = bmi >= 14.2 && bmi <= 21.9;
                    isOverweight = bmi >= 22.0 && bmi <= 25.6;
                    isSeverelyOverweight = bmi >= 25.7;
                    break;
                case 11:
                    isSeverelyUnderweight = bmi <= 14.1;
                    isUnderweight = bmi >= 14.2 && bmi <= 14.4;
                    isNormal = bmi >= 14.5 && bmi <= 22.7;
                    isOverweight = bmi >= 22.8 && bmi <= 26.6;
                    isSeverelyOverweight = bmi >= 26.7;
                    break;
                case 12:
                    isSeverelyUnderweight = bmi <= 14.4;
                    isUnderweight = bmi >= 14.5 && bmi <= 14.8;
                    isNormal = bmi >= 14.9 && bmi <= 23.4;
                    isOverweight = bmi >= 23.5 && bmi <= 27.5;
                    isSeverelyOverweight = bmi >= 27.6;
                    break;
                case 13:
                    isSeverelyUnderweight = bmi <= 14.8;
                    isUnderweight = bmi >= 14.9 && bmi <= 15.2;
                    isNormal = bmi >= 15.3 && bmi <= 24.0;
                    isOverweight = bmi >= 24.1 && bmi <= 28.3;
                    isSeverelyOverweight = bmi >= 28.4;
                    break;
                case 14:
                    isSeverelyUnderweight = bmi <= 15.1;
                    isUnderweight = bmi >= 15.2 && bmi <= 15.5;
                    isNormal = bmi >= 15.6 && bmi <= 24.6;
                    isOverweight = bmi >= 24.7 && bmi <= 28.9;
                    isSeverelyOverweight = bmi >= 29.0;
                    break;
                case 15:
                    isSeverelyUnderweight = bmi <= 15.4;
                    isUnderweight = bmi >= 15.5 && bmi <= 15.8;
                    isNormal = bmi >= 15.9 && bmi <= 25.0;
                    isOverweight = bmi >= 25.1 && bmi <= 29.4;
                    isSeverelyOverweight = bmi >= 29.5;
                    break;
                case 16:
                    isSeverelyUnderweight = bmi <= 15.7;
                    isUnderweight = bmi >= 15.8 && bmi <= 16.1;
                    isNormal = bmi >= 16.2 && bmi <= 25.4;
                    isOverweight = bmi >= 25.5 && bmi <= 29.7;
                    isSeverelyOverweight = bmi >= 29.8;
                    break;
                case 17:
                    isSeverelyUnderweight = bmi <= 15.9;
                    isUnderweight = bmi >= 16.0 && bmi <= 16.3;
                    isNormal = bmi >= 16.4 && bmi <= 25.7;
                    isOverweight = bmi >= 25.8 && bmi <= 30.0;
                    isSeverelyOverweight = bmi >= 30.1;
                    break;
                case 18:
                    isSeverelyUnderweight = bmi <= 16.1;
                    isUnderweight = bmi >= 16.2 && bmi <= 16.5;
                    isNormal = bmi >= 16.6 && bmi <= 25.9;
                    isOverweight = bmi >= 26.0 && bmi <= 30.3;
                    isSeverelyOverweight = bmi >= 30.4;
                    break;
                default:
                    // 若年龄不在6-18岁（图表范围），提示"年龄超出参考范围"
                    reportAdvice.setText("Age out of reference range (6-18)");
                    return;
            }
        }

        // 设置结果显示
        if (isSeverelyUnderweight) {
            // 严重偏瘦 - 增强警告样式
            reportImage.setImageResource(R.drawable.bot_thin);
            reportAdvice.setText(R.string.advice_severe_underweight);
            reportAdvice.setTextAppearance(this, R.style.SevereWarningText);
            reportAdvice.setBackgroundResource(R.drawable.severe_warning_bg);
            // 添加闪烁动画
            startBlinkAnimation(reportAdvice);
        } else if (isUnderweight) {
            reportImage.setImageResource(R.drawable.bot_thin);
            reportAdvice.setText(R.string.advice_underweight);
        } else if (isNormal) {
            reportImage.setImageResource(R.drawable.bot_fit);
            reportAdvice.setText(R.string.advice_normal);
        } else if (isOverweight) {
            reportImage.setImageResource(R.drawable.bot_fat);
            reportAdvice.setText(R.string.advice_overweight);
        } else if (isSeverelyOverweight) {
            // 严重超重 - 增强警告样式
            reportImage.setImageResource(R.drawable.bot_fat);
            reportAdvice.setText(R.string.advice_severe_overweight);
            reportAdvice.setTextAppearance(this, R.style.SevereWarningText);
            reportAdvice.setBackgroundResource(R.drawable.severe_warning_bg);
            // 添加闪烁动画
            startBlinkAnimation(reportAdvice);
        }
    }
    // 添加闪烁动画方法
    private void startBlinkAnimation(TextView textView) {
        android.view.animation.Animation blink = new android.view.animation.AlphaAnimation(0.3f, 1.0f);
        blink.setDuration(500); // 闪烁间隔时间（毫秒）
        blink.setRepeatMode(android.view.animation.Animation.REVERSE);
        blink.setRepeatCount(android.view.animation.Animation.INFINITE);
        textView.startAnimation(blink);
    }
}