package ykk.cb.com.zcws.sales;

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
import android.text.Html;
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
import ykk.cb.com.zcws.basics.ReturnReason_DialogActivity;
import ykk.cb.com.zcws.bean.Department;
import ykk.cb.com.zcws.bean.Organization;
import ykk.cb.com.zcws.bean.ScanningRecord;
import ykk.cb.com.zcws.bean.Stock;
import ykk.cb.com.zcws.bean.User;
import ykk.cb.com.zcws.bean.k3Bean.ICItem;
import ykk.cb.com.zcws.bean.k3Bean.ICStockBillEntry_K3;
import ykk.cb.com.zcws.bean.k3Bean.ICStockBill_K3;
import ykk.cb.com.zcws.bean.k3Bean.ReturnReason;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.comm.Comm;
import ykk.cb.com.zcws.sales.adapter.Sal_NxOutReturnFragment1Adapter;
import ykk.cb.com.zcws.util.BigdecimalUtil;
import ykk.cb.com.zcws.util.JsonUtil;
import ykk.cb.com.zcws.util.LogUtil;
import ykk.cb.com.zcws.util.zxing.android.CaptureActivity;

/**
 *  内销帐号退货
 */
public class Sal_NxOutReturnFragment1 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.lin_focus1)
    LinearLayout linFocus1;
    @BindView(R.id.lin_focus2)
    LinearLayout linFocus2;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.et_expressCode)
    EditText etExpressCode;
    @BindView(R.id.btn_scan)
    Button btnScan;
    @BindView(R.id.btn_scan2)
    Button btnScan2;
    @BindView(R.id.tv_custInfo)
    TextView tvCustInfo;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;
    @BindView(R.id.tv_okNum)
    TextView tvOkNum;

    private Sal_NxOutReturnFragment1 context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 1, RESULT_NUM = 2, SAOMA = 3, PRICE = 4, RETURN_REASON = 5, WRITE_CODE = 6, WRITE_CODE2 = 7, DELAYED_CLICK = 8;
    private Sal_NxOutReturnFragment1Adapter mAdapter;
    private List<ScanningRecord> checkDatas = new ArrayList<>();
    private String mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos = -1; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Organization cust; // 客户
    private Activity mContext;
    private Sal_NxOutReturnMainActivity parent;
    private boolean isTextChange; // 是否进入TextChange事件
    private List<String> listBarcode = new ArrayList<>();
    private String strK3Number; // 保存k3返回的单号
    private DecimalFormat df = new DecimalFormat("#.####");
    private String timesTamp; // 时间戳
    private boolean isClickButton; // 是否点击了按钮

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_NxOutReturnFragment1> mActivity;

        public MyHandler(Sal_NxOutReturnFragment1 activity) {
            mActivity = new WeakReference<Sal_NxOutReturnFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_NxOutReturnFragment1 m = mActivity.get();
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

                        m.setEnables(m.etMtlCode, R.drawable.back_style_gray3, false);
                        m.setEnables(m.etExpressCode, R.drawable.back_style_gray3, false);
                        m.btnScan.setVisibility(View.GONE);
                        m.btnScan2.setVisibility(View.GONE);
                        m.btnSave.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.VISIBLE);
                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

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
                        errMsg = JsonUtil.strToString(msgObj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "审核失败！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        if(m.listBarcode.contains(m.mtlBarcode)) {
                            Comm.showWarnDialog(m.mContext,"条码已存在！");
                            return;
                        }
                        m.listBarcode.add(m.mtlBarcode);

                        switch (m.curViewFlag) {
                            case '1': // 快递单
                                List<ICStockBillEntry_K3> list = JsonUtil.strToList(msgObj, ICStockBillEntry_K3.class);
                                ICStockBillEntry_K3 stockBillEntry = list.get(0);
                                ICStockBill_K3 stockOrder = stockBillEntry.getStockBill();
                                Organization cust = stockOrder.getCust();
                                Organization deliCust = stockOrder.getDeliCust();
                                if(cust != null && cust.getFitemId() == 33067) {
                                    Comm.showWarnDialog(m.mContext,"该条码对应的客户是电商客户，请在电商退货页面操作！");
                                    return;
                                }
                                // 显示客户
                                if(m.cust != null && !(m.cust.getfNumber().equals(cust.getfNumber()))) {
                                    Comm.showWarnDialog(m.mContext,"扫描的客户不一致，请检查！");
                                    return;
                                }
                                m.cust = cust;
                                m.tvCustInfo.setText(Html.fromHtml("客户：<font color='#000000'>"+cust.getfName()+"</font>"));
//                                m.getScanAfterData_1(list);
                                // 填充数据
                                int size = m.checkDatas.size();
                                boolean addRow = true;
                                for (int i = 0; i < size; i++) {
                                    ScanningRecord sr = m.checkDatas.get(i);
                                    // 有相同的，就不新增了
                                    if (sr.getSourceId() == stockOrder.getFinterid() && sr.getSourceEntryId() == stockBillEntry.getFentryid()) {
                                        addRow = false;
                                        break;
                                    }
                                }
                                m.parent.isChange = true;
                                if (addRow) {
                                    m.getScanAfterData_1(stockBillEntry);
                                } else {
                                    m.getMtlAfter(stockBillEntry);
                                }


                                break;
                        }

                        break;
                    case UNSUCC2:
                        errMsg = JsonUtil.strToString(msgObj);
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
                            case '1': // 物料
                                m.setFocusable(m.etMtlCode);
                                break;
                        }

                        break;
                    case SAOMA: // 扫码之后
                        String etName = null;
                        switch (m.curViewFlag) {
                            case '1': // 物料
                                etName = m.getValues(m.etMtlCode);
                                if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                                    if (m.mtlBarcode.equals(etName)) {
                                        m.mtlBarcode = etName;
                                    } else m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                                } else m.mtlBarcode = etName;
                                m.setTexts(m.etMtlCode, m.mtlBarcode);
                                // 执行查询方法
                                m.run_smGetDatas(m.mtlBarcode);

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
        return inflater.inflate(R.layout.sal_nx_out_return_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Sal_NxOutReturnMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_NxOutReturnFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        // 设值listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Sal_NxOutReturnFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord entity, int position) {
                curPos = position;
                double useableQty = checkDatas.get(curPos).getUseableQty();
                String showInfo = "<font color='#666666'>可退数：</font>"+useableQty;
                showInputDialog("退货数", showInfo, String.valueOf(useableQty), "0.0", RESULT_NUM);
            }

            @Override
            public void onClick_price(View v, ScanningRecord entity, int position) {
                curPos = position;
                showInputDialog("单价", String.valueOf(entity.getPrice()), "0.0", PRICE);
            }

            @Override
            public void sel_returnReason(View v, ScanningRecord entity, int position) {
                curPos = position;
                Bundle bundle = new Bundle();
                bundle.putString("flag", "ZH"); // 查询内销账号的数据
                showForResult(ReturnReason_DialogActivity.class, RETURN_REASON, bundle);
            }

            @Override
            public void onClick_del(View v, ScanningRecord entity, int position) {
                String barcode = checkDatas.get(position).getStrBarcodes();
                listBarcode.remove(barcode); // 把条码记录也删除掉
                checkDatas.remove(position);
                mAdapter.notifyDataSetChanged();
            }

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

        hideSoftInputMode(mContext, etMtlCode);
        hideSoftInputMode(mContext, etExpressCode);
        getUserInfo();
        timesTamp = user.getId()+"-"+Comm.randomUUID();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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

    @OnClick({R.id.btn_scan, R.id.btn_scan2, R.id.btn_save, R.id.btn_pass, R.id.btn_clone, R.id.btn_batchAdd })
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
                curViewFlag = '2';
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
            case R.id.btn_scan2: // 调用摄像头扫描（快递单）
                curViewFlag = '1';
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
            case R.id.btn_batchAdd: // 批量填充
                if (checkDatas == null || checkDatas.size() == 0) {
                    Comm.showWarnDialog(mContext, "请先扫描要退货的条码！");
                    return;
                }
                if(curPos == -1) {
                    Comm.showWarnDialog(mContext, "请选择任意一行的退货理由！");
                    return;
                }
                ScanningRecord srTemp = checkDatas.get(curPos);
                int id = srTemp.getReturnReasonId();
                String name = srTemp.getReturnReasonName();
                for(int i=curPos; i<checkDatas.size(); i++) {
                    ScanningRecord sr = checkDatas.get(i);
                    if (sr.getReturnReasonId() == 0) {
                        sr.setReturnReasonId(id);
                        sr.setReturnReasonName(name);
                    }
                }
                mAdapter.notifyDataSetChanged();

                break;
            case R.id.btn_save: // 保存
//                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
                String expressCode = getValues(etExpressCode).trim();
                if(expressCode.length() == 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("当前快递单为空，是否继续保存？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            run_save();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();

                } else {
//                run_findInStockSum();
                    run_save();
                }

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
            Comm.showWarnDialog(mContext,"请扫描有效条码！");
            return false;
        }
        String expressCode = getValues(etExpressCode).trim();
        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord sr = checkDatas.get(i);
            sr.setExpressNo(expressCode); // 赋值快递单
            if(sr.getReturnReasonId() == 0) {
                Comm.showWarnDialog(mContext,"第（"+(i+1)+"）行，请选择退货理由！");
                return false;
            }
            if(sr.getRealQty() > sr.getUseableQty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，退货数不能大于可退数！");
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
                    case R.id.et_mtlCode:
                        setFocusable(etMtlCode);
                        break;
                    case R.id.et_expressCode:
                        setFocusable(etExpressCode);
                        break;
                }
            }
        };
        etMtlCode.setOnClickListener(click);
        etExpressCode.setOnClickListener(click);

        // 物料
        etMtlCode.addTextChangedListener(new TextWatcher() {
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
        etMtlCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInputDialog("输入条码", "", "none", WRITE_CODE2);
                return true;
            }
        });

        // 长按输入条码
        etExpressCode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showInputDialog("输入快递号", "", "none", WRITE_CODE);
                return true;
            }
        });

        etMtlCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    linFocus1.setBackgroundResource(R.drawable.back_style_red_focus);
                    linFocus2.setBackgroundResource(R.drawable.back_style_gray4);
                } else {
                    if(linFocus1 != null) {
                        linFocus1.setBackgroundResource(R.drawable.back_style_gray4);
                    }
                }
            }
        });

        etExpressCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    linFocus2.setBackgroundResource(R.drawable.back_style_red_focus);
                    linFocus1.setBackgroundResource(R.drawable.back_style_gray4);
                } else {
                    if(linFocus2 != null) {
                        linFocus2.setBackgroundResource(R.drawable.back_style_gray4);
                    }
                }
            }
        });
    }

    private void reset() {
        isClickButton = true;
        timesTamp = user.getId()+"-"+Comm.randomUUID();
        cust = null;
        tvCustInfo.setText("客户：");
        setEnables(etMtlCode, R.color.transparent, true);
        setEnables(etExpressCode, R.color.transparent, true);
        btnScan.setVisibility(View.VISIBLE);
        btnScan2.setVisibility(View.VISIBLE);
        strK3Number = null;
        etMtlCode.setText(""); // 物料
        btnSave.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.GONE);
        checkDatas.clear();
        curViewFlag = '1';
        mtlBarcode = null;
        curPos = -1;
        tvOkNum.setText("0");

        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_NUM: // 数量
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
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
                        etExpressCode.setText(value.toUpperCase());
                    }
                }

                break;
            case WRITE_CODE2: // 输入条码返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        etMtlCode.setText(value.toUpperCase());
                    }
                }

                break;
            case PRICE: // 单价
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        double num = parseDouble(value);
                        if(num <= 0) {
                            Comm.showWarnDialog(mContext,"单价必须大于0！");
                            return;
                        }
                        checkDatas.get(curPos).setPrice(num);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
            case RETURN_REASON: // 退货理由
                if (resultCode == Activity.RESULT_OK) {
                    ReturnReason returnReason = (ReturnReason) data.getSerializableExtra("obj");
                    checkDatas.get(curPos).setReturnReasonId(returnReason.getFitemId());
                    checkDatas.get(curPos).setReturnReasonName(returnReason.getFname());
                    mAdapter.notifyDataSetChanged();
                }

                break;
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        switch (curViewFlag) {
                            case '1': // 快递单
                                setTexts(etExpressCode, code);
                                break;
                            case '2': // 物料
                                setTexts(etMtlCode, code);
                                break;
                        }
                    }
                }

                break;
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 300);
    }

    /**
     * 得到快递单号扫码的数据
     */
    private void getScanAfterData_1(ICStockBillEntry_K3 stockBillEntry) {
//        int size = list.size();
//        for(int i=0; i<size; i++) {
//            Icstockbillentry stockBillEntry = list.get(i);
            ICStockBill_K3 stockOrder = stockBillEntry.getStockBill();
            ICItem icItem = stockBillEntry.getIcItem();
            ScanningRecord sr = new ScanningRecord();
            sr.setId(stockBillEntry.getScanningRecordId()); // 这个值为了插入到退货记录表中
            sr.setType(16); // 1：（电商）销售出库，10：（生产）生产产品入库，11：（生产）发货通知单销售出库，12：（电商）电商销售退货，13：（电商）电商外购入库，14：（生产）生产产品入库(选单入库)，15：（生产）采购订单入库，16：（内销）销售退货
            sr.setSourceId(stockBillEntry.getFinterid());
            sr.setSourceNumber(stockBillEntry.getFbillNo());
            sr.setSourceEntryId(stockBillEntry.getFentryid());
//            sr.setExpressNo(expressBarcode);
            sr.setIcItemId(icItem.getFitemid());
            sr.setIcItemNumber(icItem.getFnumber());
            sr.setIcItemName(icItem.getFname());
            Organization cust = stockOrder.getCust();
            if(cust != null) {
                sr.setCustNumber(cust.getfNumber());
                sr.setCustName(cust.getfName());
            }
            Department department = stockOrder.getDepartment();
            if(department != null) {
                sr.setDeptNumber(department.getDepartmentNumber());
                sr.setDeptName(department.getDepartmentName());
            }
//            Stock stock = icItem.getStock();
            Stock stock = new Stock();
            stock.setFnumber("SC.02.01");
            stock.setFname("不良品仓（售后）");
            if(stock != null) {
                sr.setStock(stock);
                sr.setStockNumber(stock.getFnumber());
                sr.setStockName(stock.getFname());
            }
//            StockPosition stockPos = icItem.getStockPos();
//            if(stockPos != null && stockPos.getFspId() > 0) {
//                sr.setStockPos(stockPos);
//                sr.setStockPositionNumber(stockPos.getFnumber());
//                sr.setStockPositionName(stockPos.getFname());
//            }
            sr.setDeliveryWay("");
            sr.setSourceQty(stockBillEntry.getFqtymust());
            sr.setUseableQty(stockBillEntry.getUseableQty());
            sr.setRealQty(stockBillEntry.getFqty());
//            sr.setPrice(stockBillEntry.getFprice());
            sr.setPrice(stockBillEntry.getCustSalesPrice());
            sr.setCreateUserId(user.getId());
            sr.setEmpId(user.getEmpId());
            sr.setCreateUserName(user.getUsername());
            sr.setDataTypeFlag("APP");
            sr.setTempTimesTamp(timesTamp);
            sr.setSourceObj(JsonUtil.objectToString(stockBillEntry));
            sr.setStrBarcodes(mtlBarcode);
//            sr.setIsUniqueness('N');
            // 临时字段
            sr.setSalOrderNo(stockBillEntry.getSalOrderNo());
            sr.setReturnReasonId(0);
            sr.setReturnReasonName("");

            // 启用序列号，批次号；    990156：启用批次号，990156：启用序列号
            if(icItem.getSnManager() == 990156 || icItem.getBatchManager() == 990156) {
                sr.setStrBarcodes(mtlBarcode);
                sr.setIsUniqueness('Y');
                sr.setRealQty(1);

            } else { // 未启用序列号， 批次号
                sr.setRealQty(stockBillEntry.getFqty());
                sr.setIsUniqueness('N');
                // 不存在条码，就加入
                sr.setStrBarcodes(mtlBarcode);
            }

            checkDatas.add(sr);
//        }

        mAdapter.notifyDataSetChanged();
        setFocusable(etMtlCode);
        countNum();

        if(icItem.getBatchManager() == 990156) {
            // 使用弹出框确认数量
            curPos = checkDatas.size()-1;
            double useableQty = checkDatas.get(curPos).getUseableQty();
            String showInfo = "<font color='#666666'>可退数：</font>"+useableQty;
            showInputDialog("退货数", showInfo, String.valueOf(useableQty), "0.0", RESULT_NUM);
        }
    }

    /**
     * 得到扫码物料 数据
     */
    private void getMtlAfter(ICStockBillEntry_K3 stockBillEntry) {
        ICItem tmpICItem = stockBillEntry.getIcItem();

        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord sr = checkDatas.get(i);
            String srBarcode = isNULLS(sr.getStrBarcodes());
            // 如果扫码相同
            if (sr.getSourceId() == stockBillEntry.getFinterid() && sr.getSourceEntryId() == stockBillEntry.getFentryid()) {
                isFlag = true;
                if (sr.getRealQty() >= sr.getSourceQty()) {
                    Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已扫完！");
                    return;
                }

                // 启用序列号，批次号；    990156：启用批次号，990156：启用序列号
                if(tmpICItem.getSnManager() == 990156 || tmpICItem.getBatchManager() == 990156) {
                    if (srBarcode.indexOf(mtlBarcode) > -1) {
                        Comm.showWarnDialog(mContext, "条码已使用！");
                        return;
                    }
                    if(srBarcode.length() == 0) {
                        sr.setStrBarcodes(mtlBarcode);
                    } else {
                        sr.setStrBarcodes(srBarcode +","+ mtlBarcode);
                    }
                    sr.setIsUniqueness('Y');
                    sr.setRealQty(sr.getRealQty() + 1);

                } else { // 未启用序列号， 批次号
                    sr.setRealQty(sr.getSourceQty());
                    sr.setIsUniqueness('N');
                    // 不存在条码，就加入
                    if (srBarcode.indexOf(mtlBarcode) == -1) {
                        if (srBarcode.length() == 0) {
                            sr.setStrBarcodes(mtlBarcode);
                        } else {
                            sr.setStrBarcodes(srBarcode + "," + mtlBarcode);
                        }
                    }
                }
                break;
            }
        }
        if (!isFlag) {
            Comm.showWarnDialog(mContext, "该条码与行数据不匹配！");
            return;
        }
        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        countNum();

        if(tmpICItem.getBatchManager() == 990156) {
            // 使用弹出框确认数量
            curPos = checkDatas.size()-1;
            double useableQty = checkDatas.get(curPos).getUseableQty();
            String showInfo = "<font color='#666666'>可退数：</font>"+useableQty;
            showInputDialog("退货数", showInfo, String.valueOf(useableQty), "0.0", RESULT_NUM);
        }
    }

    /**
     * 统计数量
     */
    private void countNum() {
        double okNum = 0;
        for(int i=0; i<checkDatas.size(); i++) {
            ScanningRecord sc = checkDatas.get(i);
            okNum = BigdecimalUtil.add(okNum, sc.getRealQty());
        }
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
        String strCaseId = null;
        switch (curViewFlag) {
            case '1': // 物料查询
                mUrl = getURL("scanningRecord/findBarcode");
                barcode = mtlBarcode;
                strCaseId = "11";
                break;
        }
        FormBody formBody = new FormBody.Builder()
                .add("barcode", barcode)
                .add("targetType", "11") // 目标数据类型
                .add("sourceType", "16") // 1：（电商）销售出库，10：（生产）生产产品入库，11：（生产）发货通知单销售出库，12：（电商）电商销售退货，13：（电商）电商外购入库，14：（生产）生产产品入库(选单入库)，15：（生产）采购订单入库，16：（内销）销售退货
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { mHandler.sendEmptyMessage(UNSUCC2); }

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
