package com.example.scus.inputtext;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * TODO: document your custom view class.
 */
public class WmsInputView extends RelativeLayout {

    /**
     * 正常输入状态
     **/
    public static final int STATE_INPUT = 0x00;
    /**
     * 正常输入状态
     **/
    public static final int STATE_DISABLE = 0x01;
    /**
     * 正常输入状态
     **/
    public static final int STATE_LOADING = 0x02;
//    /** 正常输入状态 **/
//    public static final int STATE_CHECKED = 0x03;
    /**
     * 正常输入状态
     **/
    public static final int STATE_COMPLETE = 0x03;
    /**
     * 正常输入状态
     **/
    public static final int STATE_ERROR = 0x04;
    /**
     * 边框disable状态
     */
    public static final int STATE_DISABLE_STOKE = 0x05;
    /**
     * 蓝边框无焦点状态
     */
    public static final int STATE_BLUE_DISABLE_STOKE = 0x06;

    private Context context;
    private ArrayList<InputFilter> inputFilters = new ArrayList<>();
    private OnCompleteListener onCompleteListener;

    protected ImageView iconInner;
    protected ProgressBar progressBar;
    protected EditText edtInput;

    RelativeLayout rlInputView;

    protected String hintText;
    protected int hintColor = -1;
    protected int maxLength = -1;
    protected int maxLines = 1;
    protected int inputSize = -1;
    private int inputColor = -1;
    private int inputType = -1;
    private String inputText;

    protected int inputState = STATE_INPUT;

    public WmsInputView(Context context) {
        super(context);
        this.context = context;
        init(null, 0);
    }

    public WmsInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs, 0);
    }

    public WmsInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.layout_wms_input_view, this, true);
        rlInputView = (RelativeLayout) findViewById(R.id.rl_input);
        iconInner = (ImageView) findViewById(R.id.icon_checked_inner);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        edtInput = (EditText) findViewById(R.id.et_input);
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.WmsInputView, defStyle, 0);
        inputState = a.getInt(
                R.styleable.WmsInputView_input_state, STATE_INPUT);
        hintText = a.getString(
                R.styleable.WmsInputView_input_hintText);
        hintColor = a.getColor(
                R.styleable.WmsInputView_input_hintColor, hintColor);
        inputColor = a.getColor(
                R.styleable.WmsInputView_inputColor, getResources().getColor(R.color.home_header_check_store));
        inputSize = a.getDimensionPixelSize(R.styleable.WmsInputView_inputSize, inputSize);
        inputType = a.getInteger(R.styleable.WmsInputView_android_inputType, InputType.TYPE_CLASS_TEXT);
        maxLength = a.getInt(
                R.styleable.WmsInputView_android_maxLength, maxLength);
        maxLines = a.getInt(
                R.styleable.WmsInputView_android_maxLines, maxLines);
        inputText = a.getString(
                R.styleable.WmsInputView_android_text);
        a.recycle();
        initViews();
    }

    private void initViews() {

        if (hintColor != -1) {
            edtInput.setTextColor(hintColor);
        }
        if (!TextUtils.isEmpty(hintText)) {
            edtInput.setHint(hintText);
        }
        if (inputSize != -1) {
            edtInput.setTextSize(inputSize);
        }
        edtInput.setInputType(inputType);

        if (!TextUtils.isEmpty(inputText)) {
            edtInput.setText(inputText);
        }

        //设置长度限制
        if (maxLength > 0) {
            inputFilters.add(new InputFilter.LengthFilter(maxLength));
            edtInput.setFilters(inputFilters.toArray(new InputFilter[inputFilters.size()]));
        }

        edtInput.setOnEditorActionListener(editorActionListener);

        switchState(inputState);
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                if (onCompleteListener != null) {
                    onCompleteListener.onComplete(getInputText());
                }
                return true;
            }

            return false;
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 设置输入框状态
     *
     * @param inputState
     */
    public void setInputState(int inputState) {
        this.inputState = inputState;
        switchState(inputState);
    }

    public int getInputState(){
        return this.inputState;
    }

    private void switchState(int inputState) {
        edtInput.setEnabled(true);
        edtInput.setFocusable(true);
        edtInput.setFocusableInTouchMode(true);
        edtInput.setTextColor(inputColor);

        switch (inputState) {
            case STATE_INPUT:
                rlInputView.setBackground(context.getResources().getDrawable(R.drawable.bg_input_normal));
                iconInner.setVisibility(GONE);
                progressBar.setVisibility(GONE);
                edtInput.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        edtInput.requestFocus();
                    }
                }, 200);
                break;
            case STATE_DISABLE:
                rlInputView.setBackground(context.getResources().getDrawable(R.drawable.bg_input_disable));
                edtInput.setEnabled(false);
                edtInput.setTextColor(getResources().getColor(R.color.black_a54));
                iconInner.setVisibility(GONE);
                progressBar.setVisibility(GONE);
                break;
            case STATE_DISABLE_STOKE:
                rlInputView.setBackground(context.getResources().getDrawable(R.drawable.bg_input_deliver_disable));
                iconInner.setVisibility(GONE);
                progressBar.setVisibility(GONE);
                break;
            case STATE_LOADING:
                rlInputView.setBackground(context.getResources().getDrawable(R.drawable.bg_input_normal));
                iconInner.setVisibility(GONE);
                edtInput.setEnabled(false);
                progressBar.setVisibility(VISIBLE);
                break;
            case STATE_COMPLETE:
                rlInputView.setBackground(context.getResources().getDrawable(R.drawable.bg_input_complete));
                edtInput.setTextColor(getResources().getColor(R.color.home_header_check_store));
                edtInput.setEnabled(false);
                iconInner.setVisibility(VISIBLE);
                progressBar.setVisibility(GONE);
                break;
            case STATE_ERROR:
                rlInputView.setBackground(context.getResources().getDrawable(R.drawable.bg_input_error));
                iconInner.setVisibility(GONE);
                progressBar.setVisibility(GONE);
                setErrorText(getInputText());
                break;
            case STATE_BLUE_DISABLE_STOKE:
                rlInputView.setBackground(context.getResources().getDrawable(R.drawable.bg_input_normal));
                iconInner.setVisibility(GONE);
                edtInput.setEnabled(false);
                progressBar.setVisibility(GONE);
                break;
        }
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    /**
     * 设置文本
     *
     * @param text
     */
    public void setText(String text) {
        edtInput.setText(TextUtils.isEmpty(text) ? "" : text);
        edtInput.setSelection(text.length());
    }

    /**
     * 获取输入文本
     *
     * @return
     */
    public String getInputText() {
        return edtInput.getText().toString().trim();
    }

    /**
     * 获取输入控件
     * @return
     */
    public EditText getEditInputView() {
        if (edtInput != null) return edtInput;
        return null;
    }

    public interface OnCompleteListener {
        void onComplete(String inputText);
    }

    /**
     * 设置错误文本样式
     *
     * @param errorText
     */
    void setErrorText(String errorText) {
        if (TextUtils.isEmpty(errorText)) {
            return;
        }
        edtInput.setTextColor(ContextCompat.getColor(context, R.color.text_error));
        edtInput.selectAll();
        edtInput.setHighlightColor(context.getResources().getColor(R.color.bg_text_error));
    }

    private void showKeyBoard() {

        if (context instanceof Activity) {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        }

    }

    public void setEditorActionListener(TextView.OnEditorActionListener editorActionListener) {
        this.editorActionListener = editorActionListener;
        edtInput.setOnEditorActionListener(editorActionListener);
    }

    public void addFilter(InputFilter filter) {
        inputFilters.add(filter);
        edtInput.setFilters(inputFilters.toArray(new InputFilter[inputFilters.size()]));
    }

}
