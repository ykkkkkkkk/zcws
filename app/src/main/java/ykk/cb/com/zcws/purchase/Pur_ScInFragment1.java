package ykk.cb.com.zcws.purchase;

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
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
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
import ykk.cb.com.zcws.basics.StockPos_DialogActivity;
import ykk.cb.com.zcws.basics.Stock_DialogActivity;
import ykk.cb.com.zcws.basics.Supplier_DialogActivity;
import ykk.cb.com.zcws.bean.BarCodeTable;
import ykk.cb.com.zcws.bean.Department;
import ykk.cb.com.zcws.bean.ScanningRecord;
import ykk.cb.com.zcws.bean.Stock;
import ykk.cb.com.zcws.bean.StockPosition;
import ykk.cb.com.zcws.bean.Supplier;
import ykk.cb.com.zcws.bean.User;
import ykk.cb.com.zcws.bean.k3Bean.ICItem;
import ykk.cb.com.zcws.bean.pur.POOrder;
import ykk.cb.com.zcws.bean.pur.POOrderEntry;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.comm.Comm;
import ykk.cb.com.zcws.purchase.adapter.Pur_ScInFragment1Adapter;
import ykk.cb.com.zcws.util.JsonUtil;
import ykk.cb.com.zcws.util.LogUtil;
import ykk.cb.com.zcws.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.zcws.util.zxing.android.CaptureActivity;

/**
 * 销售订单出库
 */
public class Pur_ScInFragment1 extends BaseFragment {

    @BindView(R.id.et_getFocus)
    EditText etGetFocus;
    @BindView(R.id.tv_suppSel)
    TextView tvSuppSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.tv_purNo)
    TextView tvPurNo;
    @BindView(R.id.et_mtlCode)
    EditText etMtlCode;
    @BindView(R.id.btn_scan)
    Button btnScan;
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

    private Pur_ScInFragment1 context = this;
    private static final int SEL_STOCK = 10, SEL_STOCKPOS = 11, SEL_SUPP = 12, SEL_WRITE = 13;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502, PASS = 203, UNPASS = 503;
    private static final int SETFOCUS = 1, RESULT_NUM = 2, SAOMA = 3;
    private Pur_ScInFragment1Adapter mAdapter;
    private List<ScanningRecord> checkDatas = new ArrayList<>();
    private Stock stock;
    private StockPosition stockPos;
    private Supplier supplier; // 供应商
    private int curPos; // 当前行
    private OkHttpClient okHttpClient = null;
    private User user;
    private Activity mContext;
    private Pur_ScInMainActivity parent;
    private String strK3Number; // 保存k3返回的单号
    private String mtlBarcode; // 对应的条码号
    private boolean isTextChange; // 是否进入TextChange事件
    private boolean isAutoSubmitDate; // 是否自动提交数据
    private boolean isAllSM; // 是否全部扫完条码
    private DecimalFormat df = new DecimalFormat("#.####");

    // 消息处理
    private Pur_ScInFragment1.MyHandler mHandler = new Pur_ScInFragment1.MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<Pur_ScInFragment1> mActivity;

        public MyHandler(Pur_ScInFragment1 activity) {
            mActivity = new WeakReference<Pur_ScInFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            Pur_ScInFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                String msgObj = (String) msg.obj;
                switch (msg.what) {
                    case SUCC1:
                        m.strK3Number = JsonUtil.strToString(msgObj);

                        m.setEnables(m.etMtlCode, R.drawable.back_style_gray3, false);
                        m.btnScan.setVisibility(View.GONE);
                        m.btnSave.setVisibility(View.GONE);
                        m.btnPass.setVisibility(View.VISIBLE);

                        if(m.isAutoSubmitDate) {
                            m.run_passSC(true);
                        } else {
                            m.run_passSC(false);
//                            Comm.showWarnDialog(m.mContext, "保存成功，请点击“审核按钮”！");
                        }

                        break;
                    case UNSUCC1:
                        Comm.showWarnDialog(m.mContext,"服务器繁忙，请稍候再试！");

                        break;
                    case PASS: // 审核成功 返回
                        m.reset(false);
                        // 如果没有全部扫完的，审核后继续查询
                        if(!m.isAllSM) {
                            m.run_findPurOrderInStock();
                        } else {
                            m.mAdapter.notifyDataSetChanged();
                        }

                        if(m.isAutoSubmitDate) {
                            m.toasts("自动提交数据成功✔");
                        } else {
                            Comm.showWarnDialog(m.mContext, "审核成功✔");
                        }

                        break;
                    case UNPASS: // 审核失败 返回
                        errMsg = JsonUtil.strToString(msgObj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "审核失败！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC2: // 扫码成功后进入
                        List<POOrderEntry> list = JsonUtil.strToList(msgObj, POOrderEntry.class);
                        m.parent.isChange = true;
                        m.getScanAfterData_1(list);

                        break;
                    case UNSUCC2:
                        m.mAdapter.notifyDataSetChanged();
                        errMsg = JsonUtil.strToString(msgObj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SUCC3: // 扫码成功后进入
                        BarCodeTable bt = JsonUtil.strToObject((String) msg.obj, BarCodeTable.class);
                        m.getMtlAfter(bt);

                        break;
                    case UNSUCC3:
                        errMsg = JsonUtil.strToString(msgObj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "很抱歉，没能找到数据！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                    case SETFOCUS: // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.etGetFocus);
                        m.setFocusable(m.etMtlCode);

                        break;
                    case SAOMA: // 扫码之后
                        if(m.checkDatas.size() == 0) {
                            m.isTextChange = false;
                            Comm.showWarnDialog(m.mContext,"请查询数据！");
                            return;
                        }
                        String etName = m.getValues(m.etMtlCode);
                        if (m.mtlBarcode != null && m.mtlBarcode.length() > 0) {
                            if (m.mtlBarcode.equals(etName)) {
                                m.mtlBarcode = etName;
                            } else m.mtlBarcode = etName.replaceFirst(m.mtlBarcode, "");

                        } else m.mtlBarcode = etName;
                        m.setTexts(m.etMtlCode, m.mtlBarcode);
                        // 执行查询方法
                        m.run_smGetDatas();

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.pur_sc_in_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (Pur_ScInMainActivity) mContext;

        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new Pur_ScInFragment1Adapter(mContext, checkDatas);
        recyclerView.setAdapter(mAdapter);
        // 设值listview空间失去焦点
        recyclerView.setFocusable(false);
        mAdapter.setCallBack(new Pur_ScInFragment1Adapter.MyCallBack() {
            @Override
            public void onClick_num(View v, ScanningRecord entity, int position) {
                Log.e("num", "行：" + position);
                curPos = position;
                String showInfo = "<font color='#666666'>可用数：</font>"+checkDatas.get(curPos).getUseableQty();
                showInputDialog("入库数", showInfo, "", "0.0", RESULT_NUM);
            }

            @Override
            public void onClick_selStock(View v, ScanningRecord entity, int position) {
                curPos = position;
                showForResult(Stock_DialogActivity.class, SEL_STOCK, null);
            }

        });

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                ScanningRecord sr = checkDatas.get(pos);
                sr.setIsCheck(sr.getIsCheck() == 0 ? 1 : 0);
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

        getUserInfo();
        tvDateSel.setText(Comm.getSysDate(7));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
//            mHandler.sendEmptyMessageDelayed(SETFOCUS, 200);
        }
    }

    @OnClick({R.id.btn_scan, R.id.tv_purNo, R.id.tv_suppSel, R.id.tv_dateSel, R.id.btn_save, R.id.btn_pass, R.id.btn_clone })
    public void onViewClicked(View view) {
        Bundle bundle = null;
        switch (view.getId()) {
            case R.id.btn_scan: // 调用摄像头扫描（快递单）
                showForResult(CaptureActivity.class, CAMERA_SCAN, null);

                break;
            case R.id.tv_purNo: // 选择采购单号
                showInputDialog("采购单号", getValues(tvPurNo), "none", SEL_WRITE);

                break;
            case R.id.tv_suppSel: // 选择供应商
                showForResult(Supplier_DialogActivity.class, SEL_SUPP, null);

                break;
            case R.id.tv_dateSel: // 日期
                Comm.showDateDialog(mContext, view, 0);

                break;
            case R.id.btn_save: // 保存
                hideKeyboard(mContext.getCurrentFocus());
                if(!saveBefore(false)) {
                    return;
                }
                isAutoSubmitDate = false;
//                run_findInStockSum();
                run_save(false);

                break;
            case R.id.btn_pass: // 审核
                if(strK3Number == null) {
                    Comm.showWarnDialog(mContext,"请先保存数据！");
                    return;
                }
                run_passSC(false);

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
                            reset(true);
                        }
                    });
                    build.setNegativeButton("否", null);
                    build.setCancelable(false);
                    build.show();
                    return;
                } else {
                    reset(true);
                }

                break;
        }
    }

    /**
     * 查询方法
     */
    public void findFun() {
        if(checkDatas != null && checkDatas.size() > 0) {
            Comm.showWarnDialog(mContext,"请先保存当前行数据！");
            return;
        }
        run_findPurOrderInStock();
    }

    /**
     * 选择保存之前的判断
     */
    private boolean saveBefore(boolean isAutoCheck) {
        if (checkDatas == null || checkDatas.size() == 0) {
            Comm.showWarnDialog(mContext,"请先查询数据！");
            return false;
        }
        int count = 0;
        // 检查数据
        int size = checkDatas.size();
        for (int i = 0; i < size; i++) {
            ScanningRecord sr = checkDatas.get(i);
            if (isNULLS(sr.getStockName()).length() == 0) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，请选择（仓库）！");
                return false;
            }
            if (sr.getRealQty() > sr.getUseableQty()) {
                Comm.showWarnDialog(mContext,"第" + (i + 1) + "行，入库数不能大于可用数！");
                return false;
            }

            if(isAutoCheck && sr.getRealQty() == sr.getUseableQty()) {
                count += 1;
            }
        }
        // 自动检查数据，并且全部扫完了
        if(isAutoCheck) {
            if (count == size) return true;
            else return false;

        } else {
            return true;
        }
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
                if(!isTextChange) {
                    isTextChange = true;
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300);
                }
            }
        });
    }

    private void reset(boolean isRefresh) {
        setEnables(etMtlCode, R.color.transparent, true);
        btnScan.setVisibility(View.VISIBLE);
        strK3Number = null;
        btnSave.setVisibility(View.VISIBLE);
        btnPass.setVisibility(View.GONE);
        checkDatas.clear();

        if(isRefresh) mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
                            String showInfo = "<font color='#666666'>可用数：</font>"+checkDatas.get(curPos).getUseableQty();
                            showInputDialog("入库数", showInfo, "", "0.0", RESULT_NUM);
                            return;
                        }
                        double useableQty = checkDatas.get(curPos).getUseableQty();
                        if(num > useableQty) {
                            toasts("入库数不能大于可用数！");
                            String showInfo = "<font color='#666666'>可用数：</font>"+checkDatas.get(curPos).getUseableQty();
                            showInputDialog("入库数", showInfo, "", "0.0", RESULT_NUM);
                            return;
                        }
                        checkDatas.get(curPos).setRealQty(num);
                        checkDatas.get(curPos).setIsCheck(1);
                        mAdapter.notifyDataSetChanged();
                        countNum();
                    }
                }

                break;
            case SEL_SUPP: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    supplier = (Supplier) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_SUPP", supplier.getFname());
                    tvSuppSel.setText(supplier.getFname());
                }

                break;
            case CAMERA_SCAN: // 扫一扫成功  返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String code = bundle.getString(DECODED_CONTENT_KEY, "");
                        setTexts(etMtlCode, code);
                    }
                }
                break;
            case SEL_WRITE: // 输入采购单号返回
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String value = bundle.getString("resultValue", "");
                        tvPurNo.setText(value);
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
    private void getScanAfterData_1(List<POOrderEntry> list) {
        int size = list.size();
        for(int i=0; i<size; i++) {
            POOrderEntry purEntry = list.get(i);
            POOrder pur = purEntry.getPoOrder();
            ICItem icItem = purEntry.getIcItem();
            ScanningRecord sr = new ScanningRecord();

            sr.setType(15); // 1：电商销售出库，10：生产产品入库，11：发货通知单销售出库，12：电商销售退货，13：电商外购入库，14：生产产品入库(选单入库)，15：采购订单入库
            sr.setSourceId(purEntry.getFinterid());
            sr.setSourceNumber(pur.getFbillno());
            sr.setSourceEntryId(purEntry.getFentryid());
//        sr.setExpressNo(barcode);
            sr.setIcItemId(icItem.getFitemid());
            sr.setIcItemNumber(icItem.getFnumber());
            sr.setIcItemName(icItem.getFname());
            Supplier supplier = pur.getSupplier();
            if(supplier != null) {
                sr.setCustNumber(supplier.getFnumber());
                sr.setCustName(supplier.getFname());
            }
            Department department = pur.getDepartment();
            if(department != null) {
                sr.setDeptNumber(department.getDepartmentNumber());
                sr.setDeptName(department.getDepartmentName());
            }
            Stock stock = icItem.getStock();
            if(stock != null) {
                sr.setStock(stock);
                sr.setStockNumber(stock.getFnumber());
                sr.setStockName(stock.getFname());
            }
            StockPosition stockPos = icItem.getStockPos();
            if(stockPos != null && stockPos.getFspId() > 0) {
                sr.setStockPos(stockPos);
                sr.setStockPositionNumber(stockPos.getFnumber());
                sr.setStockPositionName(stockPos.getFname());
            }
            sr.setDeliveryWay("");
            sr.setSourceQty(purEntry.getFqty());
            sr.setUseableQty(purEntry.getUseableQty());
            sr.setRealQty(0);
            sr.setPrice(purEntry.getFprice());
            sr.setCreateUserId(user.getId());
            sr.setEmpId(user.getEmpId());
            sr.setCreateUserName(user.getUsername());
            sr.setDataTypeFlag("APP");
            sr.setSourceObj(JsonUtil.objectToString(purEntry));

            // 启用序列号，批次号；    990156：启用批次号，990156：启用序列号
            if(icItem.getSnManager() == 990156 || icItem.getBatchManager() == 990156) {
                sr.setStrBarcodes("");
                sr.setIsUniqueness('Y');
//                sr.setRealQty(sr.getUseableQty());
            } else { // 未启用序列号， 批次号
//                sr.setRealQty(sr.getUseableQty());
                sr.setIsUniqueness('N');
                // 不存在条码，就加入
                sr.setStrBarcodes("");
            }

            checkDatas.add(sr);
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 得到扫码物料 数据
     */
    private void getMtlAfter(BarCodeTable bt) {
        ICItem tmpICItem = JsonUtil.stringToObject(bt.getRelationObj(), ICItem.class);

        int size = checkDatas.size();
        boolean isFlag = false; // 是否存在该订单
        for (int i = 0; i < size; i++) {
            ScanningRecord sr = checkDatas.get(i);
            String srBarcode = isNULLS(sr.getStrBarcodes());
            // 如果扫码相同
            if (bt.getIcItemNumber().equals(sr.getIcItemNumber())) {
                isFlag = true;

                // 启用序列号，批次号；    990156：启用批次号，990156：启用序列号
                if(tmpICItem.getSnManager() == 990156 || tmpICItem.getBatchManager() == 990156) {
//                    if (srBarcode.indexOf(bt.getBarcode()) > -1) {
//                        Comm.showWarnDialog(mContext, "条码已经使用！");
//                        return;
//                    }
                    if (sr.getRealQty() >= sr.getUseableQty()) {
//                        Comm.showWarnDialog(mContext, "第" + (i + 1) + "行，已拣完！");
//                        return;
                        continue;
                    }
//                    if(srBarcode.length() == 0) {
//                        sr.setStrBarcodes(bt.getBarcode()+",");
//                    } else {
//                        sr.setStrBarcodes(srBarcode +","+ bt.getBarcode()+",");
//                    }
                    sr.setStrBarcodes(bt.getBarcode());
                    sr.setIsUniqueness('Y');
//                    if(tmpICItem.getBatchManager() == 990156 && tmpICItem.getSnManager() == 0 ) {
//                        sr.setRealQty(sr.getRealQty() + bt.getBarcodeQty());
//                    } else {
//                        sr.setRealQty(sr.getRealQty() + 1);
//                    }
                    sr.setRealQty(sr.getUseableQty());

                } else { // 未启用序列号， 批次号
                    sr.setRealQty(sr.getUseableQty());
                    sr.setIsUniqueness('N');
                    sr.setStrBarcodes(bt.getBarcode());
                    // 不存在条码，就加入
//                    if (srBarcode.indexOf(bt.getBarcode()) == -1) {
//                        if (srBarcode.length() == 0) {
//                            sr.setStrBarcodes(bt.getBarcode() + ",");
//                        } else {
//                            sr.setStrBarcodes(srBarcode + "," + bt.getBarcode() + ",");
//                        }
//                        sr.setStrBarcodes(sr.getStrBarcodes().substring(0, sr.getStrBarcodes().length()-1));
//                    }
                }
                break;
            }
        }
        if (!isFlag) {
            Comm.showWarnDialog(mContext, "该物料与订单不匹配！");
            return;
        }

        setFocusable(etMtlCode);
        mAdapter.notifyDataSetChanged();
        countNum();
        // 自动检查数据是否可以保存
        if(saveBefore(true)) {
            isAutoSubmitDate = true;
            run_save(true);
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
            needNum += sc.getUseableQty();
            okNum += sc.getRealQty();
        }
        tvNeedNum.setText(df.format(needNum));
        tvOkNum.setText(df.format(okNum));
    }

    /**
     * 保存方法
     */
    private void run_save(boolean isAutoSubmit) {
        List<ScanningRecord> listRecord = new ArrayList<>();
        int count = 0;
        int size = checkDatas.size();
        for(int i=0; i<size; i++) {
            ScanningRecord sr = checkDatas.get(i);

            if( sr.getRealQty() > 0) {
                listRecord.add(sr);
            }
            // 判断没行是否扫完数量
            if(sr.getRealQty() >= sr.getUseableQty()) {
                count += 1;
            }
        }
        if(listRecord.size() == 0) {
            Comm.showWarnDialog(mContext,"请至少输入一行数量！");
            return;
        }
        // 判断是否全部扫完
        if(size == count) isAllSM = true;
        else isAllSM = false;

        if(isAutoSubmit) showLoadDialog("自动保存中...", false);
        else showLoadDialog("保存中...", false);
        String mJson = JsonUtil.objectToString(listRecord);
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
    private void run_findPurOrderInStock() {
        showLoadDialog("加载中...");
        String mUrl = getURL("purOrder/findPurOrderInStock");
        String purNo = getValues(tvPurNo).trim();
        String date = getValues(tvDateSel);
        FormBody formBody = new FormBody.Builder()
                .add("fbillNo", purNo) // 生产单号
                .add("suppNumber", supplier != null ? supplier.getFnumber() : "")
                .add("prodFdateBeg", date) // 开始日期
                .add("prodFdateEnd", date) // 结束日期
                .add("purStatus", "1") // 0：未审核，1：审核，3：结案
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
     * 扫码查询对应的方法
     */
    private void run_smGetDatas() {
        isTextChange = false;
        showLoadDialog("加载中...");
        String mUrl = getURL("barCodeTable/findBarcode_SC");
        FormBody formBody = new FormBody.Builder()
                .add("barcode", mtlBarcode)
                .add("strCaseId", "11")
                .add("sourceType", "15") // 1：电商销售出库，10：生产产品入库，11：发货通知单销售出库，12：电商销售退货，13：电商外购入库，14：生产产品入库(选单入库)，15：采购订单入库
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
                LogUtil.e("run_smGetDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    Message msg = mHandler.obtainMessage(UNSUCC3, result);
                    mHandler.sendMessage(msg);
                    return;
                }
                Message msg = mHandler.obtainMessage(SUCC3, result);
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
     * 生产账号审核
     */
    private void run_passSC(boolean isAutoSubmit) {
        if(isAutoSubmit) showLoadDialog("自动审核中...", false);
        else showLoadDialog("正在审核...", false);

        String mUrl = getURL("stockBill/passSC");
        getUserInfo();
        FormBody formBody = new FormBody.Builder()
                .add("strFbillNo", strK3Number)
                .add("empId", user != null ? String.valueOf(user.getEmpId()) : "0")
//                .add("outInType", "1") // 出入库类型：（1、生产账号--采购订单入库，2、生产账号--生产任务单入库，3、生产账号--发货通知单出库）
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
