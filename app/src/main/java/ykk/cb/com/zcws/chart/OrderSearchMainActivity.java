package ykk.cb.com.zcws.chart;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.comm.BaseActivity;
import ykk.cb.com.zcws.comm.Comm;
import ykk.cb.com.zcws.entrance.page5.PrintMainActivity;
import ykk.cb.com.zcws.sales.Sal_ScOutFragment2;
import ykk.cb.com.zcws.util.MyViewPager;
import ykk.cb.com.zcws.util.adapter.BaseFragmentAdapter;
import ykk.cb.com.zcws.util.blueTooth.BluetoothDeviceListDialog;
import ykk.cb.com.zcws.util.blueTooth.Constant;
import ykk.cb.com.zcws.util.blueTooth.DeviceConnFactoryManager;
import ykk.cb.com.zcws.util.blueTooth.ThreadPool;
import ykk.cb.com.zcws.util.blueTooth.Utils;
import ykk.cb.com.zcws.util.interfaces.IFragmentExec;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static ykk.cb.com.zcws.util.blueTooth.Constant.MESSAGE_UPDATE_PARAMETER;
import static ykk.cb.com.zcws.util.blueTooth.DeviceConnFactoryManager.CONN_STATE_FAILED;

public class OrderSearchMainActivity extends BaseActivity {

    @BindView(R.id.radio1)
    View radio1;
    @BindView(R.id.radio2)
    View radio2;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;

    private OrderSearchMainActivity context = this;
    private static final String TAG = "OrderSearchMainActivity";
    private View curRadio;
    public boolean isChange; // 返回的时候是否需要判断数据是否保存了
    OrderSearchFragment1 fragment1 = new OrderSearchFragment1();

    @Override
    public int setLayoutResID() {
        return R.layout.chart_order_search_main;
    }

    @Override
    public void initData() {
//        Bundle bundle = context.getIntent().getExtras();
//        if (bundle != null) {
//            customer = (Customer) bundle.getSerializable("customer");
//        }

        curRadio = radio1;
        List<Fragment> listFragment = new ArrayList<Fragment>();
//        Bundle bundle2 = new Bundle();
//        bundle2.putSerializable("customer", customer);
//        fragment1.setArguments(bundle2); // 传参数
//        fragment2.setArguments(bundle2); // 传参数

        listFragment.add(fragment1);
//        viewPager.setScanScroll(false); // 禁止左右滑动
        //ViewPager设置适配器
        viewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), listFragment));
        //设置ViewPage缓存界面数，默认为1
        viewPager.setOffscreenPageLimit(2);
        //ViewPager显示第一个Fragment
        viewPager.setCurrentItem(0);

        //ViewPager页面切换监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabChange(radio1, "线下出库-自提", 0);

                        break;
                    case 1:
                        tabChange(radio2, "线下出库-快递单", 1);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void bundle() {
        Bundle bundle = context.getIntent().getExtras();
        if (bundle != null) {
//            customer = bundle.getParcelable("customer");
        }
    }

    @OnClick({R.id.btn_close, R.id.btn_search})
    public void onViewClicked(View view) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        switch (view.getId()) {
            case R.id.btn_close: // 关闭
//                if(isChange) {
//                    AlertDialog.Builder build = new AlertDialog.Builder(context);
//                    build.setIcon(R.drawable.caution);
//                    build.setTitle("系统提示");
//                    build.setMessage("您有未保存的数据，继续关闭吗？");
//                    build.setPositiveButton("是", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            context.finish();
//                        }
//                    });
//                    build.setNegativeButton("否", null);
//                    build.setCancelable(false);
//                    build.show();
//                } else {
                    context.finish();
//                }

                break;
            case R.id.btn_search: // 查询
                fragment1.findFun();

                break;
//            case R.id.lin_tab1:
//                tabChange(viewRadio1, "线下出库-自提", 0);
//
//                break;
//            case R.id.lin_tab2:
//                tabChange(viewRadio2, "线下出库-快递单", 1);
//
//                break;
        }
    }

    /**
     * 选中之后改变样式
     */
    private void tabSelected(View v) {
        curRadio.setBackgroundResource(R.drawable.check_off2);
        v.setBackgroundResource(R.drawable.check_on);
        curRadio = v;
    }

    private void tabChange(View view, String str, int page) {
        tabSelected(view);
        viewPager.setCurrentItem(page, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            context.finish();
        }
        return false;
    }

}
