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
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements TextWatcher,View.OnClickListener {

    // 入力項目
    private EditText numberInput1;
    private EditText numberInput2;

    // 演算方法
    private Spinner operatorSelector;

    // 計算結果表示
    private TextView calcResult;

    // ボタン押下判別コード
    public static final int REQUEST_CODE_FOR_EVENT_BUTTON_UP = 1;
    public static final int REQUEST_CODE_FOR_EVENT_BUTTON_DOWN = 2;

    // 計算値キー
    public static final String CALC_RESULT_KEY_STR = "calc_result";
    public static final String SHOW_ANOTHER_CALC_BY_BUTTON = "from_button_key";
    public static final String BUTTON_KEY_UP = "BUTTON_UP";
    public static final String BUTTON_KEY_DOWN = "BUTTON_DOWN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 画面項目初期化
        numberInput1 = findViewById(R.id.numberInput1);
        numberInput2 = findViewById(R.id.numberInput2);
        operatorSelector = findViewById(R.id.operatorSelector);
        calcResult = findViewById(R.id.calcResult);

        // イベントへ登録
        numberInput1.addTextChangedListener(this);
        numberInput2.addTextChangedListener(this);
        operatorSelector.setOnItemSelectedListener(new SpinnerOnItemChanged());
        findViewById(R.id.calcButton1).setOnClickListener(this);
        findViewById(R.id.calcButton2).setOnClickListener(this);
        findViewById(R.id.nextButton).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 戻り値結果確認
        if (resultCode != RESULT_OK) {
            showToastMessage(">>> 遷移先から戻り 処理結果 [ NG ] <<<");
            return;
        } else {
            showToastMessage(">>> 遷移先から戻り 処理結果 [ OK ] <<<");
        }

        // 戻り値を取得する
        Bundle resultBundle = data.getExtras();

        // 戻り値が設定されているか確認
        if (!resultBundle.containsKey(CALC_RESULT_KEY_STR)) {
            showToastMessage(">>> 戻り値設定していない、処理中止する <<<");
        }

        String result = resultBundle.getString(CALC_RESULT_KEY_STR);
        showToastMessage(">>> 戻り値設定は [" + result + "] です。 <<<");

        if (requestCode == REQUEST_CODE_FOR_EVENT_BUTTON_UP) {
            numberInput1.setText(result);
        } else if (requestCode == REQUEST_CODE_FOR_EVENT_BUTTON_DOWN) {
            numberInput2.setText(result);
        }

        refreshResult();
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

    private void showToastMessage(String str) {
        // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Log.e("### DEBUG TAG ###", str);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        showToastMessage(">>> MainActivity -> EditText Event -> beforeTextChanged <<<");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        showToastMessage(">>> MainActivity -> EditText Event -> onTextChanged <<<");

    }

    @Override
    public void afterTextChanged(Editable s) {
        showToastMessage(">>> MainActivity -> EditText Event -> afterTextChanged <<<");
        showToastMessage(">>> 入力値 [ " + s.toString() + " ] <<<");
        refreshResult();
    }

    @Override
    public void onClick(View v) {
        showToastMessage(">>> MainActivity -> Button Event -> onClick <<<");
        Button btn = (Button)v;
        int btn_id = v.getId();

        String resultStr ="";
        if (checkEditTextInput()) {
            resultStr = String.valueOf(calc());
        }

        switch (btn_id) {
            case R.id.calcButton1:
                showToastMessage(">>> 上の Button [ " + btn.getText().toString() + " ] が押されました。 <<<");

                // ほかの画面へ遷移
                Intent it1 = new Intent(this, AnotherCalcActivity.class);
                it1.putExtra(CALC_RESULT_KEY_STR, resultStr);
                it1.putExtra(SHOW_ANOTHER_CALC_BY_BUTTON, BUTTON_KEY_UP);
                this.startActivityForResult(it1, REQUEST_CODE_FOR_EVENT_BUTTON_UP);

                break;
            case R.id.calcButton2:
                showToastMessage(">>> 上の Button [ " + btn.getText().toString() + " ] が押されました。 <<<");

                // ほかの画面へ遷移
                Intent it2 = new Intent(this, AnotherCalcActivity.class);
                Bundle be = new Bundle();
                be.putString(CALC_RESULT_KEY_STR, resultStr);
                be.putString(SHOW_ANOTHER_CALC_BY_BUTTON, BUTTON_KEY_DOWN);
                it2.putExtras(be);
                this.startActivityForResult(it2, REQUEST_CODE_FOR_EVENT_BUTTON_DOWN);

                break;
            case R.id.nextButton:
                showToastMessage(">>> Button [ " + btn.getText().toString() + " ] が押されました。 <<<");
                numberInput1.setText(resultStr);
                refreshResult();

                break;
        }

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
