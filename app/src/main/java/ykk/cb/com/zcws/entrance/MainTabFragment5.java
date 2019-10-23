package ykk.cb.com.zcws.entrance;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import butterknife.OnClick;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.chart.OrderSearchMainActivity;
import ykk.cb.com.zcws.comm.BaseFragment;

/**
 * 图表
 */
public class MainTabFragment5 extends BaseFragment {

    private MainTabFragment5 context = this;
    private static final int SUCC1 = 200, UNSUCC1 = 500;
    private Activity mContext = null;


    public MainTabFragment5() {
    }


    // 消息处理
    final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainTabFragment5> mActivity;

        public MyHandler(MainTabFragment5 activity) {
            mActivity = new WeakReference<MainTabFragment5>(activity);
        }

        public void handleMessage(Message msg) {
            MainTabFragment5 m = mActivity.get();
            if (m != null) {
                m.hideLoadDialog();
                switch (msg.what) {
                    case SUCC1: // 得到更新信息

                        break;
                    case UNSUCC1: // 得到更新信息失败
                        m.toasts("已经是最新版本了！！！");

                        break;
                }
            }
        }
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item5, container, false);
    }

    @Override
    public void initData() {
        mContext = getActivity();
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 订单报表
                show(OrderSearchMainActivity.class, null);

                break;
            case R.id.relative2: // 服务器设置
//                show(ServiceSetActivity.class, null);

                break;
            case R.id.relative3: // 网络测试
                toasts("网络通畅！！！");
//                run_test();

                break;
        }
    }

}
