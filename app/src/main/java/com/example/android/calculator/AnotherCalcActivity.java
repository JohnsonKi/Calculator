package com.example.android.calculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class AnotherCalcActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // 入力項目
    private EditText numberInput1;
    private EditText numberInput2;

    // 演算方法
    private Spinner operatorSelector;

    // 計算結果表示
    private TextView calcResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_calc);

        // 画面項目初期化
        numberInput1 = findViewById(R.id.numberInput1);
        numberInput2 = findViewById(R.id.numberInput2);
        operatorSelector = findViewById(R.id.operatorSelector);
        calcResult = findViewById(R.id.calcResult);

        // イベント登録
        findViewById(R.id.backButton).setOnClickListener(this);
        numberInput1.addTextChangedListener(this);
        numberInput2.addTextChangedListener(this);
        operatorSelector.setOnItemSelectedListener(new SpinnerOnItemChanged());

        setInitData();
    }

    private void setInitData() {

        // 遷移元の計算値を設定する
        Intent it = getIntent();
        Bundle be = it.getExtras();
        if (be.containsKey(MainActivity.SHOW_ANOTHER_CALC_BY_BUTTON) && be.containsKey(MainActivity.CALC_RESULT_KEY_STR)) {
            String btnType = be.getString(MainActivity.SHOW_ANOTHER_CALC_BY_BUTTON);

            switch (btnType) {
                case MainActivity.BUTTON_KEY_UP:
                    numberInput1.setText(be.getString(MainActivity.CALC_RESULT_KEY_STR));
                    break;
                case MainActivity.BUTTON_KEY_DOWN:
                    numberInput2.setText(be.getString(MainActivity.CALC_RESULT_KEY_STR));
                    break;
            }
        }
    }

    private void showToastMessage(String str) {
        // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Log.e("### DEBUG TAG ###", str);
    }

    @Override
    public void onClick(View v) {
        showToastMessage(">>> AnotherCalcActivity -> Button Event -> onClick <<<");
        Button btn = (Button)v;
        int btn_id = v.getId();

        String resultStr ="";
        if (checkEditTextInput()) {
            resultStr = String.valueOf(calc());
        }

        switch (btn_id) {
            case R.id.backButton:
                showToastMessage(">>> Button [ " + btn.getText().toString() + " ] が押されました。 <<<");
                showToastMessage(">>> 現在の計算結果[ " + calcResult.getText().toString() + " ] を全画面へ戻す。 <<<");

                if (checkEditTextInput()) {
                    // ほかの画面へ遷移
                    Intent it1 = new Intent();
                    it1.putExtra(MainActivity.CALC_RESULT_KEY_STR, resultStr);

                    this.setResult(RESULT_OK, it1);
                } else {
                    // 計算しなかった場合
                    this.setResult(RESULT_CANCELED);
                }

                finish();

                break;
        }
    }

    private boolean checkEditTextInput() {
        return !TextUtils.isEmpty(numberInput1.getText().toString()) && !TextUtils.isEmpty(numberInput2.getText().toString());
    }

    private int calc() {
        int input1 = Integer.parseInt(numberInput1.getText().toString());
        int input2 = Integer.parseInt(numberInput2.getText().toString());

        int operator = operatorSelector.getSelectedItemPosition();
        showToastMessage(">>> 演算子 [" + operatorSelector.getSelectedItem().toString() + "] より計算する <<<");

        switch (operator) {
            case 0:
                return input1 + input2;
            case 1:
                return input1 - input2;
            case 2:
                return input1 * input2;
            case 3:
                return input1 / input2;
            default:
                throw new RuntimeException();
        }
    }

    private void refreshResult() {
        if (checkEditTextInput()) {

            int result = calc();

            // 表示文字列組み立て
            String showStr = getString(R.string.calc_result_text, result);

            calcResult.setText(showStr);

        } else {

            // 入力が未完成と表示
            calcResult.setText(R.string.calc_result_unfinished);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        showToastMessage(">>> AnotherCalcActivity -> EditText Event -> beforeTextChanged <<<");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        showToastMessage(">>> AnotherCalcActivity -> EditText Event -> onTextChanged <<<");
    }

    @Override
    public void afterTextChanged(Editable s) {
        showToastMessage(">>> AnotherCalcActivity -> EditText Event -> afterTextChanged <<<");
        showToastMessage(">>> 入力値 [ " + s.toString() + " ] <<<");
        refreshResult();
    }

    class SpinnerOnItemChanged implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            showToastMessage(">>> MainActivity -> Spinner Event -> onItemSelected <<<");
            TextView tv = (TextView)view;
            showToastMessage(">>> 演算子を[ " + tv.getText().toString() + " ]へ変更する <<<");

            refreshResult();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            showToastMessage(">>> MainActivity -> Spinner Event -> onNothingSelected <<<");
        }
    }
}
