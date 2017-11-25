# -View-
自定义组合控件
我们知道在android的世界各种优美而又炫酷控件和动画精彩纷呈，这个得益于大Google将android开源，市场上android智能手机也是大方色彩，不仅有华为、联想、三星、小米，魅族还有现在异军突起的vivo、oppo，他们基于android系统定制属于自己的系统，这使得android在不到十年的时间疯狂掠夺手机市场份额，预计未来andriod智能手机仍然占领智能手机主要市场，android系统的快速普及使得Google对Android系统变革步伐加快，拿几年前和现在的android手机对比使用，不难发现老版本的android手机真是卡到家了，这是Google被吐槽最多的缺憾，而如今的android已经在很多方面都进行过几次优化，例如2012年的黄油计划，以及针对4.4版本对电池性能的优化，Android Runtim虚拟机，Material Design风格，运行时权限，以及针对未来智能设备的Android Go系统！！！

纳闷，说了那么多，好像跟本节要讲的内容有毛线干系，，，（不假思索 ） 好像是耶，说了那么多把android几次改变捋一下下而已，那我们就此入题吧....  （废话那么多！！！---抱怨         大虾，见谅，见谅）

自定义控件是每个android工程师成长的必经之路，狭路相逢勇者胜，看到自定义控件不要怕，抽出神刀果断亮剑，多次交锋下来，你会发现你越来越厉害了，自定义View需要练习，现在的我也经常看相关的内容，时间长了不用不写就忘了，跟大家目前一块学习自定义View，请大虾以后多多关照....

自定义一般分为三种，难易程度由易到难，首先难度系数较低的是自定义扩展控件，就是对已有控件进行再次封装，以满足自己特殊“癖好”，接着就是自定义组合控件，这个有的时候还是比较容易，有的需要多动动脑筋，那么高潮来了，最难的就是完全自定义View,我们通过直接继承View/ViewGroup(其实也是继承View)，对控件进行完全自定义，那么今天我们一块分享难度系数比较低的自定义组合控件吧，后面跟大家在分享一些对已有控件进行拓展，以及最后一起学习和探讨姿势高难度的完全自定义View.

假设一个应用场景，我们需要一个控件，这个控件能够输入内容并且根据接口校验输入数据正确与否，并对结果不同提示也不同，大家有时候会用到的输入校验框，不说话上图

输入状态

校验状态

1不可用状态 2检验状态

2 检验成功状态


整个过程如上图，接下来我们一点一点分析：

首先自定义一个控件继承自RelativeLayout，然后自定义属性，在values目录下创建attrs资源文件，自定义属性根节点为

定义的属性如下，如图：

自定义属性

我们来看一下比较重要的属性，

input_hintText：提示文字，跟EditText属性hint一样

input_hintColor:提示文字颜色

inputType：输入内容类型

input_state：枚举输入状态

接着看如何自定义控件：

接触过自定义控件的同学都知道，构造函数有多个重载，

public WmsInputView(Context context) {    super(context);    this.context = context;    init(null, 0);}

public WmsInputView(Context context, AttributeSet attrs) {    super(context, attrs);    this.context = context;    init(attrs, 0);}

public WmsInputView(Context context, AttributeSet attrs, int defStyle) {

super(context, attrs, defStyle);  this.context = context;    init(attrs, defStyle);}

第一个构造是在代码中直接创建而调用，第二个以及第三个是在布局文件中创建调用，这里分attrs是我们在布局文件中定义的属性集，后面我们要从这个属性集里面取出属性，defStyleAttr是定义在theme中的一个引用，这个引用指向一个style资源，而这个style资源包含了一些TypedArray的默认值，一般我们默认就行。

注意，我们分别在三个构造函数中init()来初始化属性，

private void init(AttributeSet attrs, int defStyle) {

LayoutInflater.from(context).inflate(R.layout.layout_wms_input_view, this, true);

rlInputView = (RelativeLayout) findViewById(R.id.rl_input);

iconInner = (ImageView) findViewById(R.id.icon_checked_inner);

progressBar = (ProgressBar) findViewById(R.id.progress_bar);

edtInput = (EditText) findViewById(R.id.et_input);    // Load attributes

final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WmsInputView, defStyle, 0);

inputState = a.getInt(R.styleable.WmsInputView_input_state, STATE_INPUT);

hintText = a.getString(R.styleable.WmsInputView_input_hintText);

hintColor = a.getColor(R.styleable.WmsInputView_input_hintColor, hintColor);

inputColor = a.getColor(R.styleable.WmsInputView_inputColor, getResources().getColor(R.color.home_header_check_store));

inputSize = a.getDimensionPixelSize(R.styleable.WmsInputView_inputSize, inputSize);

inputType = a.getInteger(R.styleable.WmsInputView_android_inputType, InputType.TYPE_CLASS_TEXT);

maxLength = a.getInt(R.styleable.WmsInputView_android_maxLength, maxLength);

maxLines = a.getInt(R.styleable.WmsInputView_android_maxLines, maxLines);

inputText = a.getString(R.styleable.WmsInputView_android_text);

a.recycle();

initViews();

}

这里我们在布局文件写了一个该控件的布局，该控件是由textView,EditText,ProgressBar和imageView组成，然后通过布局加载器加载该布局，

LayoutInflater.from(context).inflate(R.layout.layout_wms_input_view, this, true);

然后我们将布局里面定义的属性通过getContext().obtainStyledAttributes（）拿到TypedArray，再从这个属性集中拿出对应的属性。

记得后面要recycler一下，那为什么呢？？

仁兄注意：使用recycle过后，是将我们之前创建的attrs属性进行回收等待下一次复用，这样，每次引用到我们自定义View的组件重新创建的时候，我们的自定义属性就不会重新的重建，GC就不用频繁的操作这个对象，防止了OOM的出现。

接着把我们自定义控件xml文件中对应属性值设置个控件中各个组件进行初始化，这里强行贴图，这样大家必须要动手操作了，咻咻~~~(动作要快，姿势要对)

这里我们进行简单分析内容，大家看到很多-1，到底是啥，我们再开始对变量进行了初始化是设置了默认值，当我们再xml文件中没有设置对应的属性时候，我们get的值就是这个默认值，要回举一反三哦，

inputFilters.add(new InputFilter.LengthFilter(maxLength));

edtInput.setFilters(inputFilters.toArray(new InputFilter[inputFilters.size()]));

这里的InputFilter是过滤器，我们对EditText要多输入框进行长度控制时就需要创建这个LengthFilter过滤器，当然过滤器有很多，这里就不一一列举，


最后，我们对editText设置了键盘输入监听器（OnEditorActionListener），也就当我们按下确认的时候会进行回调，我们再回调中对输入的内容进行校验，所以这个监听很重要，想要对这个监听器了解更多的可以自行搜索，“多动动手哦，老司机都是撸出来的，，，，”(咻咻~~)

重点来了，墙裂敲黑板！！！

switchState(inputState);

这又是什么鬼？？？ 还记得·····那年大明湖畔~~~

oh myGod  串词了， 事情的经过是这样的，，， 还记得前面几张图片吗？

我们的控件有很多种状态，正常输入状态，不能输入状态，加载状态，和加载完成以及加载错误状态，没错，就是这个方法“惹的祸”，那我接下来就来“一一拷问”它究竟是如何作案的，肯定也没那么神奇，毕竟我们都猜到结局~~

立刻上码，扬鞭奔腾起来，当我们设置STATE_INPUT状态时都有哪些操作，我只分析一种情况后面的大家应该都能理解，首先我们对控件的背景颜色进行设置，例如控件是输入状态时背景设置成蓝色的，这个rlInputView是啥呢？



原来是个布局呀，也就是我们整个控件的外面的布局，

当控件状态是STATE_INPUT时，我们把其他验证成功和进度都设置为GONE,输入框通过postDelayed发送一个延时操作获取焦点，

edtInput.postDelayed(new Runnable() {

@Override

public void run() {

edtInput.requestFocus();

}}, 200);

接下来的状态设置跟这个差别不是很大，就是对布局种各组件进行状态设置，隐藏还是显示，颜色和背景以及字体等等的设置，大家细看就会的，

不过我们再设置状态为STATE_ERROR时候用到了高亮和全选，

void setErrorText(String errorText) {

if (TextUtils.isEmpty(errorText)) {

return;

}

edtInput.setTextColor(ContextCompat.getColor(context, R.color.text_error));

edtInput.selectAll();

edtInput.setHighlightColor(context.getResources().getColor(R.color.bg_text_error));}

这个作用时给用户一个显眼的提示，并且自动全选后可以点删除键一键全删，用户体验还是很好的，还是看图吧，

是不是看起来还不错呢，当你删除的时候就可以全部删掉，不用再长按选择全选或者不停的“抖手”，当然，我们这样在布局里面吧所有属性写死是不是感觉扩展性太差，我们对外也暴露了一些方法用来修改控件状态和属性值，

public void setText(String text) {

edtInput.setText(TextUtils.isEmpty(text) ? "" : text);

edtInput.setSelection(text.length());}/** * 获取输入文本 * * @return */

public String getInputText() {

return edtInput.getText().toString().trim();}/** * 获取输入控件 * @return */

public EditText getEditInputView() {

if (edtInput != null) return edtInput;

return null;

}

public interface OnCompleteListener {

void onComplete(String inputText);

}

看到这里我们控件基本说完了，是不是感觉没那么复杂吧，“真相”终究会大告天下，讲解的过程中我只是将一些重要的地方拿出来说了一下，当然还有其他地方没有说明，如果你还有点疑惑就看看代码吧


祝大家周末愉快！！！

