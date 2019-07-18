package ykk.cb.com.zcws.produce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.basics.StockPos_DialogActivity;
import ykk.cb.com.zcws.basics.Stock_DialogActivity;
import ykk.cb.com.zcws.bean.BarCodeTable;
import ykk.cb.com.zcws.bean.Department;
import ykk.cb.com.zcws.bean.Organization;
import ykk.cb.com.zcws.bean.ScanningRecord;
import ykk.cb.com.zcws.bean.Stock;
import ykk.cb.com.zcws.bean.StockPosition;
import ykk.cb.com.zcws.bean.User;
import ykk.cb.com.zcws.bean.k3Bean.ICItem;
import ykk.cb.com.zcws.bean.prod.ProdOrder;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.comm.Comm;
import ykk.cb.com.zcws.produce.adapter.Prod_ScInFragment1Adapter;
import ykk.cb.com.zcws.util.BigdecimalUtil;
import ykk.cb.com.zcws.util.JsonUtil;
import ykk.cb.com.zcws.util.LogUtil;
import ykk.cb.com.zcws.util.zxing.android.CaptureActivity;

/**
 * 生产任务单入库
 */
public class Prod_ScInFragment1 extends BaseFragment {

    @BindView(R.id.btn_clone)
    Button btnClone;
    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.lin_focus1)
    LinearLayout linFocus1;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.tv_stockSel)
    TextView tvStockSel;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.tv_needNum)
    TextView tvNeedNum;
    @BindView(R.id.tv_okNum)
    TextView tvOkNum;

    private Prod_ScInFragment1 context = this;
    private static final int SEL_STOCK1 = 10, SEL_STOCK = 11, SEL_STOCKPOS = 12;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 1, RESULT_NUM = 2, SAOMA = 3, WRITE_CODE = 4, DELAYED_CLICK = 5;
    private Prod_ScInFragment1Adapter mAdapter;
    private List<ScanningRecord> checkDatas = new ArrayList<>();
    private Stock stock1, stock;
    private StockPosition stockPos;
    private String barcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Prod_ScInMainActivity parent;
    private boolean isTextChange; // 是否进入TextChange事件
    private String strK3Number; // 保存k3返回的单号
    private DecimalFormat df = new DecimalFormat("#.####");
    private String timesTamp; // 时间戳
    private boolean isClickButton; // 是否点击了按钮

    // 消息处理
    private Prod_ScInFragment1.MyHandler mHandler = new Prod_ScInFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Prod_ScInFragment1> mActivity;

        public MyHandler(Prod_ScInFragment1 activity) {
            mActivity = new WeakReference<Prod_ScInFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Prod_ScInFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                String msgObj = null;
                if(msg.obj instanceof String) {
                    msgObj = (String) msg.obj;
                }
                switch (msg.what) {
                    case SUCC1:
                        m.strK3Number = JsonUtil.strToString(msgObj);

//                        m.setEnables(m.etCode, R.drawable.back_style_gray3, false);
//                        m.btnScan.setVisibility(View.GONE);
//                        m.btnSave.setVisibility(View.GONE);
//                        m.btnPass.setVisibility(View.VISIBLE);
//                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");
                        m.reset();
                        Comm.showWarnDialog(m.mContext,"保存成功✔");

                        break;
                    case UNSUCC1:
                        errMsg = JsonUtil.strToString(msgObj);
                        if(Comm.isNULLS(errMsg).length() == 0) errMsg = "服务器繁忙，请稍候再试！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case PASS: // 审核成功 返回
                        m.reset();
                        Comm.showWarnDialog(m.mContext,"审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "审核失败！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        BarCodeTable bt = null;
                        switch (m.curViewFlag) {
                            case '1': // 生产条码
                                bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                                ProdOrder prodOrder = JsonUtil.stringToObject(bt.getRelationObj(), ProdOrder.class);

                                // 填充数据
                                int size = m.checkDatas.size();
                                boolean addRow = true;
                                for (int i = 0; i < size; i++) {
                                    ScanningRecord sr = m.checkDatas.get(i);
                                    // 有相同的，就不新增了
                                    if (sr.getSourceId() == prodOrder.getProdId()) {
                                        addRow = false;
                                        break;
                                    }
                                }
                                m.parent.isChange = true;
                                if (addRow) {
                                    m.getScanAfterData_1(bt, prodOrder);
                                } else {
                                    m.getMtlAfter(bt, prodOrder);
                                }

                                break;
                        }

                        break;
                    case UNSUCC2:
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC3: // 判断是否存在返回
                        m.run_save();

                        break;
                    case UNSUCC3: // 判断是否存在返回
                        m.run_save();

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        switch (m.curViewFlag) {
                            case '1': // 生产条码
                                m.setFocusable(m.etCode);
                                break;
                        }

                        break;
                    case SAOMA: // 扫码之后
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 生产条码
                                etName = m.getValues(m.etCode);
                                if (m.barcode != null && m.barcode.length() > 0) {
                                    if (m.barcode.equals(etName)) {
                                        m.barcode = etName;
                                    } else m.barcode = etName.replaceFirst(m.barcode, "");

                                } else m.barcode = etName;
                                m.setTexts(m.etCode, m.barcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.barcode);

                                break;
                        }

                        break;
                    case DELAYED_CLICK: // 延时进入点击后的操作
                        View btnView = (View) msg.obj;
                        m.btnClickAfter(btnView);

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.prod_sc_in_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Prod_ScInMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Prod_ScInFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        // 设值listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Prod_ScInFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                double useableQty = checkDatas.get(curPos).getUseableQty();
                String showInfo = "<font color='#666666'>可用数：</font>"+useableQty;
                showInputDialog("入库数", showInfo, String.valueOf(useableQty), "0.0", RESULT_NUM);
            }

//            @Override
//            public void onClick_selStock(View v, ScanningRecord entity, int position) {
//                curPos = position;
//                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);
//            }

        });
    }

    @Override
    public void initData() {
        if (okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(30, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(30, TimeUnit.SECONDS) //设置读取超时时间
                    .build();
        }

        hideSoftInputMode(mContext, etCode);
        getUserInfo();
        timesTamp = user.getId()+"-"+Comm.randomUUID();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e("setUserVisibleHint", "冒泡麻婆。。。。。");
        if(isVisibleToUser) {
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isClickButton = true;
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }

    @OnClick({R.id.btn_scan, R.id.tv_stockSel, R.id.btn_save, R.id.btn_pass, R.id.btn_clone })
    public void onViewClicked(View view) {
        if(isClickButton && view.getId() == R.id.btn_save) {
            isClickButton = false;
            view.setEnabled(false);
            view.setClickable(false);
            showLoadDialog("稍等哈...",false);

            Message msgView = mHandler.obtainMessage(DELAYED_CLICK, view);
            mHandler.sendMessageDelayed(msgView,1000);
        } else {
            btnClickAfter(view);
        }

    }

    private void btnClickAfter(View view) {
        hideLoadDialog();
        isClickButton = true;
        view.setEnabled(true);
        view.setClickable(true);

        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_scan: // 调用摄像头扫描（物料）
                curViewFlag = '1';
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
            case R.id.tv_stockSel: // 选择仓库
                showForResult(Stock_DialogActivity.class, SEL_STOCK1, null);

                break;
            case R.id.btn_save: // 保存
//                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
//                run_findInStockSum();
                run_save();

                break;
            case R.id.btn_pass: // 审核
                if(strK3Number == null) {
                    Comm.showWarnDialog(mContext,"请先保存数据！");
                    return;
                }
                run_passSC();

                break;
            case R.id.btn_clone: // 重置
//                hideKeyboard(mContext.getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reset();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    reset();
                }

                break;
        }
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore() {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext,"请先扫描条码！");
            return false;
        }

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord sr = checkDatas.get(i);
            if (isNULLS(sr.getStockName()).length() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
            if (sr.getRealQty() > sr.getUseableQty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行（入库数）不能大于（可用数）！");
                return false;
            }
        }
        return true;
    }

    @Override
    public void setListener() {
        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocusable(etGetFocus);
                switch (v.getId()) {
                    case R.id.et_code:
                        setFocusable(etCode);
                        break;
                }
            }
        };
        etCode.setOnClickListener(click);

        // 生产条码
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) return;
                curViewFlag = '1';
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });

        // 长按输入条码
        etCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInputDialog("输入条码", "", "none", WRITE_CODE);
                return true;
            }
        });

        etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    linFocus1.setBackgroundResource(R.drawable.back_style_red_focus);
                } else {
                    if(linFocus1 != null) {
                        linFocus1.setBackgroundResource(R.drawable.back_style_gray4);
                    }
                }
            }
        });
    }

    private void reset() {
        isClickButton = true;
        timesTamp = user.getId()+"-"+Comm.randomUUID();
        setEnables(etCode, R.color.transparent, true);
        btnScan.setVisibility(View.VISIBLE);
        strK3Number = null;
        etCode.setText(""); // 生产条码号
        btnSave.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.GONE);
        checkDatas.clear();
        curViewFlag = '1';
        barcode = null;
        tvNeedNum.setText("0");
        tvOkNum.setText("0");
        stock1 = null;
        tvStockSel.setText("");

        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_STOCK1: //选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock1 = (Stock) data.getSerializableExtra("obj");
                    if(checkDatas != null && checkDatas.size() > 0) {
                        for(int i=0; i<checkDatas.size(); i++) {
                            ScanningRecord sr2 = checkDatas.get(i);
                            sr2.setStock(stock1);
                            sr2.setStockNumber(stock1.getFnumber());
                            sr2.setStockName(stock1.getFname());
                            sr2.setStockPositionNumber("");
                            sr2.setStockPositionName("");
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    tvStockSel.setText(stock1.getFname());
                }

                break;
            case SEL_STOCK: //行事件选择仓库	返回
                if (resultCode == Activity.RESULT_OK) {
                    stock = (Stock) data.getSerializableExtra("obj");
                    // 启用了库位管理
                    if (stock.getFisStockMgr() == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("fspGroupId", stock.getFspGroupId());
                        showForResult(StockPos_DialogActivity.class, SEL_STOCKPOS, bundle);
                    } else {
                        stockAllFill(false);
                    }
                }

                break;
            case SEL_STOCKPOS: //行事件选择库位	返回
                if (resultCode == Activity.RESULT_OK) {
                    stockPos = (StockPosition) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_STOCKP", stockPos.getFname());
                    stockAllFill(true);
                }

                break;
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        if(num == 0) {
                            toasts("入库数量必须大于0！");
                            double useableQty = checkDatas.get(curPos).getUseableQty();
                            String showInfo = "<font color='#666666'>可用数：</font>"+useableQty;
                            showInputDialog("入库数", showInfo, String.valueOf(useableQty), "0.0", RESULT_NUM);
                            return;
                        }
                        double useableQty = checkDatas.get(curPos).getUseableQty();
                        if(num > useableQty) {
                            toasts("入库数不能大于可用数！");
                            double useableQty2 = checkDatas.get(curPos).getUseableQty();
                            String showInfo = "<font color='#666666'>可用数：</font>"+useableQty2;
                            showInputDialog("入库数", showInfo, String.valueOf(useableQty2), "0.0", RESULT_NUM);
                            return;
                        }
                        checkDatas.get(curPos).setRealQty(num);
                        checkDatas.get(curPos).setIsUniqueness('N');
                        mAdapter.notifyDataSetChanged();
                        countNum();
                    }
                }

                break;
            case WRITE_CODE: // 输入条码返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        etCode.setText(value.toUpperCase());
                    }
                }

                break;
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        switch (curViewFlag) {
                            case '1': // 箱码
                                setTexts(etCode, code);
                                break;
                        }
                    }
                }

                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300);
    }

    /**
     * 仓库数据全部填充
     */
    private void stockAllFill(boolean inStockPosData) {
        int size = checkDatas.size();
        boolean isBool = false;
        for(int i=0; i<size; i++) {
            ScanningRecord sr = checkDatas.get(i);
            if(isNULLS(sr.getStockNumber()).length() > 0) {
                isBool = true;
                break;
            }
        }
//        if(isBool) {
        ScanningRecord sr2 = checkDatas.get(curPos);
        sr2.setStockNumber(stock.getFnumber());
        sr2.setStockName(stock.getFname());
        sr2.setStock(stock);
        if(inStockPosData) {
            sr2.setStockPos(stockPos);
            sr2.setStockPositionNumber(stockPos.getFnumber());
            sr2.setStockPositionName(stockPos.getFname());
        }
//        } else { // 全部都为空的时候，选择任意全部填充
//            for (int i = 0; i < size; i++) {
//                ScanningRecord2 sr2 = checkDatas.get(i);
//                sr2.setStockId(stock2.getfStockid());
//                sr2.setStockFnumber(stock2.getfNumber());
//                sr2.setStockName(stock2.getfName());
//                sr2.setStock(stock2);
//                if(inStockPosData) {
//                    sr2.setStockPos(stockP2);
//                    sr2.setStockPositionId(stockP2.getId());
//                    sr2.setStockPName(stockP2.getFname());
//                }
//            }
//        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 得到生产条码号扫码的数据
     */
    private void getScanAfterData_1(BarCodeTable bt, ProdOrder prodOrder) {
        ICItem icItem = prodOrder.getIcItem();
        ScanningRecord sr = new ScanningRecord();

        sr.setType(10); // 1：电商销售出库，10：生产产品入库，11：发货通知单销售出库，12：电商销售退货，13：电商外购入库
        sr.setSourceId(prodOrder.getProdId());
        sr.setSourceNumber(prodOrder.getProdNo());
        sr.setSourceEntryId(prodOrder.getProdId());
//        sr.setExpressNo(barcode);
        sr.setIcItemId(icItem.getFitemid());
        sr.setIcItemNumber(icItem.getFnumber());
        sr.setIcItemName(icItem.getFname());
        Organization cust = prodOrder.getCust();
        if(cust != null) {
            sr.setCustNumber(cust.getfNumber());
            sr.setCustName(cust.getfName());
        }
        Department department = prodOrder.getDepartment();
        if(department != null) {
            sr.setDeptNumber(department.getDepartmentNumber());
            sr.setDeptName(department.getDepartmentName());
        }
        // 用物料默认的仓库
        Stock stock = icItem.getStock();
        if(stock != null) {
            sr.setStock(stock);
            sr.setStockNumber(stock.getFnumber());
            sr.setStockName(stock.getFname());
        } else {
            Stock stock2 = new Stock();
            stock2.setFitemId(254);
            stock2.setFnumber("CC.01.01");
            stock2.setFname("忠诚卫士成品仓");

            sr.setStock(stock2);
            sr.setStockNumber(stock2.getFnumber());
            sr.setStockName(stock2.getFname());
        }
        // 默认的仓位
        StockPosition stockPos = icItem.getStockPos();
        if(stockPos != null && stockPos.getFspId() > 0) {
            sr.setStockPos(stockPos);
            sr.setStockPositionNumber(stockPos.getFnumber());
            sr.setStockPositionName(stockPos.getFname());
        }

        // 用选择的仓库
//        if(stock1 != null) {
//            sr.setStock(stock1);
//            sr.setStockNumber(stock1.getFnumber());
//            sr.setStockName(stock1.getFname());
//
//        } else if(prodOrder.getGoodsType() == 990169) { // 990168代表非定制，990169代表定制
//            Stock stock = new Stock();
//            stock.setFitemId(38263);
//            stock.setFnumber("CC.01.05");
//            stock.setFname("定制产品仓");
//
//            sr.setStock(stock);
//            sr.setStockNumber(stock.getFnumber());
//            sr.setStockName(stock.getFname());
//
//        } else { // 990168代表非定制
//            Stock stock = new Stock();
//            stock.setFitemId(254);
//            stock.setFnumber("CC.01.01");
//            stock.setFname("忠诚卫士成品仓");
//
//            sr.setStock(stock);
//            sr.setStockNumber(stock.getFnumber());
//            sr.setStockName(stock.getFname());
//        }

        sr.setDeliveryWay("");
        sr.setSourceQty(prodOrder.getFqty());
        sr.setUseableQty(prodOrder.getUseableQty());
        sr.setPrice(0);
//        sr.setRealQty(1);
        sr.setCreateUserId(user.getId());
        sr.setEmpId(user.getEmpId());
        sr.setCreateUserName(user.getUsername());
        sr.setDataTypeFlag("APP");
        sr.setTempTimesTamp(timesTamp);
        sr.setSourceObj(JsonUtil.objectToString(prodOrder));

        boolean isBool = false; // 是否使用弹出框来确认数量

        // 启用序列号，批次号；    990156：启用批次号，990156：启用序列号
        if(icItem.getSnManager() == 990156 || icItem.getBatchManager() == 990156) {
            sr.setStrBarcodes(bt.getBarcode());
            sr.setIsUniqueness('Y');
            if(icItem.getBatchManager() == 990156 && icItem.getSnManager() == 990155 ) {
                sr.setRealQty(sr.getRealQty() + bt.getBarcodeQty());
                isBool = true;
            } else {
                sr.setRealQty(sr.getRealQty() + 1);
            }
        } else { // 未启用序列号， 批次号
            sr.setRealQty(sr.getUseableQty());
            sr.setIsUniqueness('N');
            // 不存在条码，就加入
            sr.setStrBarcodes(bt.getBarcode());
        }

        checkDatas.add(sr);
        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        countNum();

        if(isBool) {
            // 使用弹出框确认数量
            curPos = checkDatas.size() - 1;
            double useableQty = checkDatas.get(curPos).getUseableQty();
            String showInfo = "<font color='#666666'>可用数：</font>"+useableQty;
            showInputDialog("入库数", showInfo, String.valueOf(useableQty), "0.0", RESULT_NUM);
        }
    }

    /**
     * 得到扫码物料 数据
     */
    private void getMtlAfter(BarCodeTable bt, ProdOrder prodOrder) {
        ICItem tmpICItem = prodOrder.getIcItem();

        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        boolean isBool = false; // 是否使用弹出框来确认数量
        boolean isOkNum = false; // 相同的物料不同的条码是否扫完数
        int pos = -1;
        for (int i = 0; i < size; i++) {
            ScanningRecord sr = checkDatas.get(i);
            String srBarcode = isNULLS(sr.getStrBarcodes());
            // 如果扫码相同
            if (bt.getRelationBillId()  == sr.getSourceId()) {
                isFlag = true;
                pos = i;

                // 启用序列号，批次号；    990156：启用批次号，990156：启用序列号
                if(tmpICItem.getSnManager() == 990156 || tmpICItem.getBatchManager() == 990156) {
                    if (srBarcode.indexOf(bt.getBarcode()) > -1) {
                        Comm.showWarnDialog(mContext, "条码已经使用！");
                        return;
                    }
                    if (sr.getRealQty() >= sr.getUseableQty()) {
//                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已拣完！");
//                        return;
                        isOkNum = true;
                        continue;
                    }
                    if(srBarcode.length() == 0) {
                        sr.setStrBarcodes(bt.getBarcode());
                    } else {
                        sr.setStrBarcodes(srBarcode +","+ bt.getBarcode());
                    }
//                    sr.setIsUniqueness('Y');
                    if(tmpICItem.getBatchManager() == 990156 && tmpICItem.getSnManager() == 990155 ) {
                        sr.setStrBarcodes(bt.getBarcode());
                        sr.setRealQty(sr.getRealQty() + bt.getBarcodeQty());
                        isBool = true;
                    } else {
                        sr.setRealQty(sr.getRealQty() + 1);
                    }
                    isOkNum = false;
                } else { // 未启用序列号， 批次号
                    if (sr.getRealQty() >= sr.getUseableQty()) {
                        continue;
                    }
                    sr.setRealQty(sr.getUseableQty());
//                    sr.setIsUniqueness('N');
                    // 不存在条码，就加入
                    if (srBarcode.indexOf(bt.getBarcode()) == -1) {
                        if (srBarcode.length() == 0) {
                            sr.setStrBarcodes(bt.getBarcode());
                        } else {
                            sr.setStrBarcodes(srBarcode + "," + bt.getBarcode());
                        }
                    }
                }
                break;
            }
        }
        if (!isFlag) {
            Comm.showWarnDialog(mContext, "该物料与订单不匹配！");
            return;
        }
        if(isOkNum) {
            Comm.showWarnDialog(mContext, "该物料条码在订单中数量已扫完！");
            return;
        }

        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        countNum();

        if(isBool) {
            // 使用弹出框确认数量
            curPos = pos;
            double useableQty = checkDatas.get(curPos).getUseableQty();
            String showInfo = "<font color='#666666'>可用数：</font>"+useableQty;
            showInputDialog("入库数", showInfo, String.valueOf(useableQty), "0.0", RESULT_NUM);
        }

    }

    /**
     * 统计数量
     */
    private void countNum() {
        double needNum = 0;
        double okNum = 0;
        for(int i=0; i<checkDatas.size(); i++) {
            ScanningRecord sc = checkDatas.get(i);
            needNum = BigdecimalUtil.add(needNum, sc.getUseableQty());
            okNum = BigdecimalUtil.add(okNum, sc.getRealQty());
        }
        tvNeedNum.setText(df.format(needNum));
        tvOkNum.setText(df.format(okNum));
    }

    /**
     * 保存方法
     */
    private void run_save() {
        showLoadDialog("保存中...",false);

        String mJson = JsonUtil.objectToString(checkDatas);
        FormBody formBody = new FormBody.Builder()
                .add("strJson", mJson)
                .build();

        String mUrl = getURL("scanningRecord/addScanningRecord");
        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
//                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_save --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC1, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 扫码查询对应的方法
     */
    private void run_smGetDatas(String val) {
        isTextChange = false;
        if(val.length() == 0) {
            Comm.showWarnDialog(mContext,"请对准条码！");
            return;
        }
        showLoadDialog("加载中...",false);
        String mUrl = null;
        String barcode = null;
        switch (curViewFlag) {
            case '1': // 生产条码查询订单
                mUrl = getURL("prodOrder/findBarcode");
                barcode = this.barcode;
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("barcode", barcode)
                .add("strCaseId", "21")
                .add("sourceType", "10") // 1：电商销售出库，10：生产产品入库，11：发货通知单销售出库
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_smGetDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC2, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC2, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 判断表中存在该物料
     */
    private void run_findInStockSum() {
        showLoadDialog("加载中...",false);
        StringBuilder strFbillno = new StringBuilder();
        StringBuilder strEntryId = new StringBuilder();
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
//            ScanningRecord2 sr2 = checkDatas.get(i);
//            if((i+1) == size) {
//                strFbillno.append(sr2.getPoFbillno());
//                strEntryId.append(sr2.getEntryId());
//            } else {
//                strFbillno.append(sr2.getPoFbillno() + ",");
//                strEntryId.append(sr2.getEntryId() + ",");
//            }
        }
        String mUrl = getURL("scanningRecord/findInStockSum");
        FormBody formBody = new FormBody.Builder()
                .add("fbillType", "4") // fbillType  1：采购订单入库，2：收料任务单入库，3：生产订单入库，4：销售订单出库，5：发货通知单出库
                .add("strFbillno", strFbillno.toString())
                .add("strEntryId", strEntryId.toString())
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC3);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC3);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
                Log.e("run_findInStockSum --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 生产账号审核
     */
    private void run_passSC() {
        showLoadDialog("正在审核...",false);
        String mUrl = getURL("stockBill/passSC");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strFbillNo", strK3Number)
                .add("empId", user != null ? String.valueOf(user.getEmpId()) : "0")
//                .add("outInType", "2") // 出入库类型：（1、生产账号--采购订单入库，2、生产账号--生产任务单入库，3、生产账号--发货通知单出库）
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNPASS);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNPASS, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(PASS, result);
                Log.e("run_passSC --> onResponse", result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     *  得到用户对象
     */
    private void getUserInfo() {
        if(user == null) user = showUserByXml();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

}
