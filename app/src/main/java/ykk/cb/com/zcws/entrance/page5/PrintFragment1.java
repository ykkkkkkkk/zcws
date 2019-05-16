package ykk.cb.com.zcws.entrance.page5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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
import ykk.cb.com.zcws.basics.Dept_DialogActivity;
import ykk.cb.com.zcws.bean.Department;
import ykk.cb.com.zcws.bean.pur.ProdOrder;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.comm.Comm;
import ykk.cb.com.zcws.entrance.page5.adapter.PrintFragment1Adapter;
import ykk.cb.com.zcws.util.JsonUtil;
import ykk.cb.com.zcws.util.LogUtil;
import ykk.cb.com.zcws.util.basehelper.BaseRecyclerAdapter;
import ykk.cb.com.zcws.util.xrecyclerview.XRecyclerView;

public class PrintFragment1 extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.tv_deptSel)
    TextView tvDeptSel;
    @BindView(R.id.tv_dateSel)
    TextView tvDateSel;
    @BindView(R.id.et_prodNo)
    EditText etProdNo;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.cbAll)
    CheckBox cbAll;
    @BindView(R.id.xRecyclerView)
    XRecyclerView xRecyclerView;

    private PrintFragment1 context = this;
    private List<ProdOrder> listDatas = new ArrayList<>();
    private static final int SEL_DEPT = 10;
    private static final int SUCC1 = 200, UNSUCC1 = 500, SUCC2 = 201, UNSUCC2 = 501, SUCC3 = 202, UNSUCC3 = 502;
    private PrintFragment1Adapter mAdapter;
    private Department department; // 部门
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Activity mContext;
    private PrintMainActivity parent;
    private int limit = 1;
    private boolean isRefresh, isLoadMore, isNextPage;
    private List<ProdOrder> prodOrderList = new ArrayList<>();

    // 消息处理
    final PrintFragment1.MyHandler mHandler = new PrintFragment1.MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<PrintFragment1> mActivity;

        public MyHandler(PrintFragment1 activity) {
            mActivity = new WeakReference<PrintFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            PrintFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                String errMsg = null;
                String msgObj = (String) msg.obj;
                switch (msg.what) {
                    case SUCC1: // 成功
                        String json = (String) msg.obj;
                        List<ProdOrder> list = JsonUtil.strToList2(json, ProdOrder.class);
                        m.listDatas.addAll(list);
                        m.mAdapter.notifyDataSetChanged();

                        if (m.isRefresh) {
                            m.xRecyclerView.refreshComplete(true);
                        } else if (m.isLoadMore) {
                            m.xRecyclerView.loadMoreComplete(true);
                        }

//                        m.xRecyclerView.setPullRefreshEnabled(true); // 上啦刷新开启
                        m.xRecyclerView.setLoadingMoreEnabled(m.isNextPage);

                        break;
                    case UNSUCC1: // 数据加载失败！
                        m.toasts("抱歉，没有加载到数据！");

                        break;
                    case SUCC2: // 生码成功
                        m.prodOrderList.clear();
                        List<ProdOrder> list2 = JsonUtil.strToList(msgObj, ProdOrder.class);
                        m.prodOrderList.addAll(list2);
                        m.toasts("生码成功✔");
                        m.initLoadDatas();

                        break;
                    case UNSUCC2: // 生码失败
                        errMsg = JsonUtil.strToString((String)msg.obj);
                        if(m.isNULLS(errMsg).length() == 0) errMsg = "服务器繁忙，请稍候再试！";
                        Comm.showWarnDialog(m.mContext, errMsg);

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.ab_print_fragment1, container, false);
    }

    @Override
    public void initView() {
        mContext = getActivity();
        parent = (PrintMainActivity) mContext;

        xRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        xRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PrintFragment1Adapter(mContext, listDatas);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setLoadingListener(context);

        xRecyclerView.setPullRefreshEnabled(false); // 上啦刷新禁用
        xRecyclerView.setLoadingMoreEnabled(false); // 不显示下拉刷新的view

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int pos) {
                ProdOrder prodOrder = listDatas.get(pos - 1);
                prodOrder.setIsCheck(prodOrder.getIsCheck() == 0 ? 1 : 0);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initData() {
        hideKeyboard(etProdNo);
        tvDateSel.setText(Comm.getSysDate(7));
        // initLoadDatas();
    }

    @OnClick({R.id.btn_search, R.id.tv_deptSel, R.id.tv_dateSel, R.id.btn_prodCode, R.id.btn_print})
    public void onViewClicked(View v) {
        Bundle bundle = null;
        switch (v.getId()) {
            case R.id.btn_search: // 查询数据
                initLoadDatas();

                break;
            case R.id.tv_deptSel: // 选择生产车间
                bundle = new Bundle();
                bundle.putInt("departmentProperty", 1070); // 属性：1070-车间,1071-非车间
                showForResult(Dept_DialogActivity.class, SEL_DEPT, bundle);

                break;
            case R.id.tv_dateSel: // 日期
                Comm.showDateDialog(mContext, v, 0);

                break;
            case R.id.btn_prodCode: // 生产条码
                createCodeBefore();

                break;
            case R.id.btn_print: // 打印
                if(prodOrderList != null && prodOrderList.size() > 0) {
                    parent.setFragment1Data(1, prodOrderList);
                } else {
                    Comm.showWarnDialog(mContext, "请先生码，再打印！");
                }


                break;
        }
    }

    /**
     * 生码之前的处理
     */
    private void createCodeBefore() {
        int size = listDatas.size();
        StringBuilder strIds = new StringBuilder();
        for(int i=0; i<size; i++) {
            ProdOrder p = listDatas.get(i);
            if(p.getIsCheck() == 1) {
                strIds.append(p.getProdId()+",");
            }
        }
        int len = strIds.length();
        if(len == 0) {
            Comm.showWarnDialog(mContext, "请选中生码的行！");
            return;
        }
        strIds.delete(len-1, len);
        run_prodOrderCreate_app(strIds.toString());
    }

    @Override
    public void setListener() {
        cbAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int dataSize = listDatas.size();
                if (dataSize > 0) {
                    for (int i = 0; i < dataSize; i++) {
                        listDatas.get(i).setIsCheck(isChecked ? 1 : 0);
                    }
                    cbAll.setText(isChecked ? "反选" : "全选");
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEL_DEPT: //查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = (Department) data.getSerializableExtra("obj");
                    LogUtil.e("onActivityResult --> SEL_DEPT", department.getDepartmentName());
                    tvDeptSel.setText(department.getDepartmentName());
                }

                break;
        }
    }

    private void initLoadDatas() {
        limit = 1;
        listDatas.clear();
        run_okhttpDatas();
    }

    /**
     * 通过okhttp加载数据
     * 仓库信息，库区，库位，部门，物料
     */
    private void run_okhttpDatas() {
        showLoadDialog("加载中...");
        String mUrl = getURL("prodOrder/findProdOrderByPage");
        String prodNo = getValues(etProdNo).trim();
        String date = getValues(tvDateSel);
        FormBody formBody = new FormBody.Builder()
                .add("fbillNo", prodNo) // 生产单号
                .add("deptNumber", department != null ? department.getDepartmentNumber() : "")
                .add("prodFdateBeg", date) // 开始日期
                .add("prodFdateEnd", date) // 结束日期
                .add("createCodeStatus", "0") // 生码状态 0：未生码，1：已生码
                .add("limit", String.valueOf(limit))
                .add("pageSize", "30")
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(request);

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_okhttpDatas --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNSUCC1);
                    return;
                }
                isNextPage = JsonUtil.isNextPage(result, limit);

                Message msg = mHandler.obtainMessage(SUCC1, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 生产任务单生码（app调用）
     */
    private void run_prodOrderCreate_app(String strIds) {
        showLoadDialog("加载中...");
        String mUrl = getURL("barCodeCreate/prodOrderCreate_app");
        FormBody formBody = new FormBody.Builder()
                .add("fIds", strIds) // 生产id拼接
                .build();

        Request request = new Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build();

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(request);

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(UNSUCC2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                LogUtil.e("run_prodOrderCreate_app --> onResponse", result);
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

    @Override
    public void onRefresh() {
        isRefresh = true;
        isLoadMore = false;
        initLoadDatas();
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        isLoadMore = true;
        limit += 1;
        run_okhttpDatas();
    }

    @Override
    public void onDestroyView() {
        closeHandler(mHandler);
        mBinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mContext.unregisterReceiver(mReceiver);
    }
}
