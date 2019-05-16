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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.bean.Department;
import ykk.cb.com.zcws.bean.Organization;
import ykk.cb.com.zcws.bean.ScanningRecord;
import ykk.cb.com.zcws.bean.User;
import ykk.cb.com.zcws.bean.k3Bean.ICItem;
import ykk.cb.com.zcws.bean.k3Bean.IcStockBill;
import ykk.cb.com.zcws.bean.k3Bean.Icstockbillentry;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.comm.Comm;
import ykk.cb.com.zcws.sales.adapter.Sal_DsOutReturnFragment1Adapter;
import ykk.cb.com.zcws.util.JsonUtil;
import ykk.cb.com.zcws.util.LogUtil;

/**
 * 销售订单出库
 */
public class Sal_DsOutReturnFragment1 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_pass)
    Button btnPass;

    private Sal_DsOutReturnFragment1 context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 1, RESULT_NUM = 2, SAOMA = 3;
    private Sal_DsOutReturnFragment1Adapter mAdapter;
    private List<ScanningRecord> checkDatas = new ArrayList<>();
    private String mtlBarcode; // 对应的条码号
    private char curViewFlag = '1'; // 1：仓库，2：库位， 3：车间， 4：物料 ，箱码
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Sal_DsOutReturnMainActivity parent;
    private boolean isTextChange; // 是否进入TextChange事件
    private List<String> listBarcode = new ArrayList<>();

    // 消息处理
    private Sal_DsOutReturnFragment1.MyHandler mHandler = new Sal_DsOutReturnFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Sal_DsOutReturnFragment1> mActivity;

        public MyHandler(Sal_DsOutReturnFragment1 activity) {
            mActivity = new WeakReference<Sal_DsOutReturnFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Sal_DsOutReturnFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                String msgObj = (String) msg.obj;
                switch (msg.what) {
                    case SUCC1:
                        m.resetSon();
//
//                        m.checkDatas.clear();
//                        m.getBarCodeTableBefore(true);
//                        m.mAdapter.notifyDataSetChanged();
//                        m.btnSave.setVisibility(View.GONE);
//                        m.mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                        Comm.showWarnDialog(m.mContext,"保存成功，请点击“审核按钮”！");

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case PASS: // 审核成功 返回
                        m.btnSave.setVisibility(View.VISIBLE);
                        m.reset('0');

                        m.checkDatas.clear();
                        m.mAdapter.notifyDataSetChanged();
                        Comm.showWarnDialog(m.mContext,"审核成功✔");

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString((String)msg.obj);
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
                                List<Icstockbillentry> list = JsonUtil.strToList(msgObj, Icstockbillentry.class);
                                m.getScanAfterData_1(list);

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
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.sal_ds_out_return_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Sal_DsOutReturnMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Sal_DsOutReturnFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setCallBack(new Sal_DsOutReturnFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                showInputDialog("数量", String.valueOf(entity.getRealQty()), "0.0", RESULT_NUM);
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
        getUserInfo();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        }
    }

    @OnClick({R.id.btn_save, R.id.btn_pass, R.id.btn_clone })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_save: // 保存
                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore()) {
                    return;
                }
//                run_findInStockSum();
                run_save();

                break;
            case R.id.btn_pass: // 审核
//                run_submitAndPass();

                break;
            case R.id.btn_clone: // 重置
                hideKeyboard(mContext.getCurrentFocus());
                if (checkDatas != null && checkDatas.size() > 0) {
                    AlertDialog.Builder build = new AlertDialog.Builder(mContext);
                    build.setIcon(R.drawable.caution);
                    build.setTitle("系统提示");
                    build.setMessage("您有未保存的数据，继续重置吗？");
                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetSon();
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    resetSon();
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

        // 检查数据
        for (int i = 0, size = checkDatas.size(); i < size; i++) {
            ScanningRecord sr = checkDatas.get(i);
//            if (sr.getSourceQty() > sr.getRealQty()) {
//                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行货还没捡完货！");
//                return false;
//            }
        }
        return true;
    }

    @OnFocusChange({R.id.et_mtlCode})
    public void onViewFocusChange(View v, boolean hasFocus) {
        if (hasFocus) hideKeyboard(v);
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
                }
            }
        };
        etMtlCode.setOnClickListener(click);

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
    }

    /**
     * 0：重置全部，1：重置物料部分
     *
     * @param flag
     */
    private void reset(char flag) {
        // 清空物料信息
        etMtlCode.setText(""); // 物料
    }

    private void resetSon() {
        btnSave.setVisibility(View.VISIBLE);
        checkDatas.clear();
        reset('0');
        curViewFlag = '1';
        mtlBarcode = null;

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
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessageDelayed(SETFOCUS,200);
                    }
                }

                break;
        }
    }

    /**
     * 得到快递单号扫码的数据
     */
    private void getScanAfterData_1(List<Icstockbillentry> list) {
        int size = list.size();
        for(int i=0; i<size; i++) {
            Icstockbillentry stockBillEntry = list.get(i);
            IcStockBill stockOrder = stockBillEntry.getStockBill();
            ICItem icItem = stockBillEntry.getIcItem();
            ScanningRecord sr = new ScanningRecord();

            sr.setType(12); // 1：电商销售出库，10：生产产品入库，11：生产销售出库，12：电商销售退货
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
            sr.setStockNumber("");
            sr.setStockName("");
            sr.setStockPositionNumber("");
            sr.setStockPositionName("");
            sr.setDeliveryWay("");
            sr.setSourceQty(stockBillEntry.getFqtymust());
            sr.setRealQty(stockBillEntry.getFqty());
            sr.setCreateUserId(user.getId());
            sr.setCreateUserName(user.getUsername());
            sr.setSourceObj(JsonUtil.objectToString(stockBillEntry));

            checkDatas.add(sr);
        }
        mAdapter.notifyDataSetChanged();
        setFocusable(etMtlCode);
    }

    /**
     * 保存方法
     */
    private void run_save() {
        showLoadDialog("保存中...");

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
                    mHandler.sendEmptyMessage(UNSUCC1);
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
        showLoadDialog("加载中...");
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
                .add("sourceType", "12") // 1：电商销售出库，10：生产产品入库，11：发货通知单销售出库，12：电商销售退货
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
        showLoadDialog("加载中...");
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
     * 提交并审核
     */
    private void run_submitAndPass() {
        showLoadDialog("正在审核...");
        String mUrl = getURL("scanningRecord/submitAndPass");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("type", "2")
                .add("kdAccount", user.getKdAccount())
                .add("kdAccountPassword", user.getKdAccountPassword())
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
                Log.e("run_submitAndPass --> onResponse", result);
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
