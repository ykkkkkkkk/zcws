package ykk.cb.com.zcws.entrance;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.bean.AppInfo;
import ykk.cb.com.zcws.bean.MsgCount;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.purchase.Pur_ScInMainActivity;
import ykk.cb.com.zcws.util.IDownloadContract;
import ykk.cb.com.zcws.util.IDownloadPresenter;
import ykk.cb.com.zcws.util.JsonUtil;

import static android.os.Process.killProcess;

/**
 * 采购
 */
public class MainTabFragment1 extends BaseFragment implements IDownloadContract.View {

    private MainTabFragment1 context = this;
    private Activity mContext;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private static final int UPDATE = 201, UNUPDATE = 501, UPDATE_PLAN = 1;
    private IDownloadPresenter mPresenter;
    private boolean isCheckUpdate = false; // 是否已经检查过更新

    public MainTabFragment1() {
    }

    // 消息处理
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<MainTabFragment1> mActivity;

        public MyHandler(MainTabFragment1 activity) {
            mActivity = new WeakReference<MainTabFragment1>(activity);
        }

        public void handleMessage(Message msg) {
            MainTabFragment1 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();

                switch (msg.what) {
                    case UPDATE: // 更新版本  成功
                        m.isCheckUpdate = true;
                        AppInfo appInfo = JsonUtil.strToObject((String) msg.obj, AppInfo.class);
                        if (m.getAppVersionCode(m.mContext) != appInfo.getAppVersion()) {
                            m.showNoticeDialog(appInfo.getAppRemark());
                        }

                        break;
                    case UNUPDATE: // 更新版本  失败！

                        break;
                    case UPDATE_PLAN: // 更新进度
                        m.progressBar.setProgress(m.progress);
                        m.tvDownPlan.setText(String.format(Locale.CHINESE,"%d%%", m.progress));

                        break;
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }

    //SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mContext = activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item1, container, false);
    }

    @Override
    public void initData() {
        mPresenter = new IDownloadPresenter(context);
        if(!isCheckUpdate) {
            // 执行更新版本请求
            run_findAppInfo();
        }
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 采购订单
//                show(Pur_OrderSearchActivity.class, null);

                break;
            case R.id.relative2: // 采购入库
                show(Pur_ScInMainActivity.class, null);

                break;
            case R.id.relative3: // 生产入库
//                show(Prod_InActivity.class,null);

                break;
            case R.id.relative4: // 生产装箱
//                show(Pur_ProdBoxMainActivity.class, null);

                break;
        }
    }

    /**
     * 获取服务端的App信息
     */
    private void run_findAppInfo() {
        String mUrl = getURL("appInfo/findAppInfo");
        FormBody formBody = new FormBody.Builder()
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
                mHandler.sendEmptyMessage(UNUPDATE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String result = body.string();
                Log.e("run_findAppInfo --> onResponse", result);
                if (!JsonUtil.isSuccess(result)) {
                    mHandler.sendEmptyMessage(UNUPDATE);
                    return;
                }
                Message msg = mHandler.obtainMessage(UPDATE, result);
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 显示下载的进度
     */
    private Dialog downloadDialog;
    private ProgressBar progressBar;
    private TextView tvDownPlan;
    private int progress;
    private void showDownloadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("软件更新");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        progressBar = (ProgressBar)v.findViewById(R.id.progress);
        tvDownPlan = (TextView)v.findViewById(R.id.tv_downPlan);
        builder.setView(v);
        // 开发员用的，长按进度条，就关闭下载框
        tvDownPlan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                downloadDialog.dismiss();
                return true;
            }
        });
        // 如果用户点击取消就销毁掉这个系统
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void delClick(DialogInterface dialog, int which) {
////                mContext.finish();
//                dialog.dismiss();
//            }
//        });
        downloadDialog = builder.create();
        downloadDialog.show();
        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 提示下载框
     */
    private void showNoticeDialog(String remark) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("更新版本").setMessage(remark)
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 得到ip和端口
                        SharedPreferences spfConfig = spf(getResStr(R.string.saveConfig));
                        String ip = spfConfig.getString("ip", "192.168.3.198");
                        String port = spfConfig.getString("port", "8080");
                        String url = "http://"+ip+":"+port+"/apks/zcws.apk";

                        showDownloadDialog();
                        mPresenter.downApk(mContext, url);
                        dialog.dismiss();
                    }
                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    public void delClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
                .create();// 创建
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();// 显示
    }

    /**
     * 得到本机的版本信息
     */
    private int getAppVersionCode(Context context) {
        PackageManager pack;
        PackageInfo info;
        // String versionName = "";
        try {
            pack = context.getPackageManager();
            info = pack.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
            // versionName = info.versionName;
        } catch (Exception e) {
            Log.e("getAppVersionName(Context context)：", e.toString());
        }
        return 0;
    }

    @Override
    public void showUpdate(String version) {
    }

    @Override
    public void showProgress(int progress) {
        context.progress = progress;
        mHandler.sendEmptyMessage(UPDATE_PLAN);
    }

    @Override
    public void showFail(String msg) {
        toasts(msg);
    }

    @Override
    public void showComplete(File file) {
        if(downloadDialog != null) downloadDialog.dismiss();

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            //7.0以上需要添加临时读取权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authority = mContext.getApplicationContext().getPackageName() + ".fileProvider";
                Uri fileUri = FileProvider.getUriForFile(mContext, authority, file);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");

            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);

            //弹出安装窗口把原程序关闭。
            //避免安装完毕点击打开时没反应
            killProcess(android.os.Process.myPid());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
