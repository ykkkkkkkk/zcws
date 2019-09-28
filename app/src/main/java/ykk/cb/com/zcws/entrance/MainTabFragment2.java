package ykk.cb.com.zcws.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.produce.Mtl_SmSearchMainActivity;
import ykk.cb.com.zcws.produce.Prod_ScInMainActivity;
import ykk.cb.com.zcws.produce.Prod_ScInOtherMainActivity;

/**
 * 生产
 */
public class MainTabFragment2 extends BaseFragment {

    public MainTabFragment2() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item2, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 生产入库
                show(Prod_ScInMainActivity.class,null);

                break;
            case R.id.relative2: // 生产入库
                show(Prod_ScInOtherMainActivity.class,null);

                break;
            case R.id.relative3: // 工艺查看
                show(Mtl_SmSearchMainActivity.class,null);

                break;
            case R.id.relative4: // 工序汇报
//                show(Prod_ProcedureReportActivity.class,null);

                break;
        }
    }
}
