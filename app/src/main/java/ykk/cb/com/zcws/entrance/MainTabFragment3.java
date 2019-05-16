package ykk.cb.com.zcws.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.sales.Sal_DsOutMainActivity;
import ykk.cb.com.zcws.sales.Sal_DsOutReturnMainActivity;
import ykk.cb.com.zcws.sales.Sal_ScOutMainActivity;

public class MainTabFragment3 extends BaseFragment {

    public MainTabFragment3() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item3, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 电商出库
                show(Sal_DsOutMainActivity.class, null);

                break;
            case R.id.relative2: // 销售出库
                show(Sal_ScOutMainActivity.class, null);

                break;
            case R.id.relative3: // 单据下推
                show(Sal_DsOutReturnMainActivity.class, null);

                break;
            case R.id.relative4: // 拣货单
//                show(Sal_PickingListActivity.class, null);

                break;
            case R.id.relative5: // 复核单
//                show(Sal_RecombinationActivity.class, null);

                break;
            case R.id.relative6: // 销售装箱
//                show(Sal_BoxActivity.class, null);

                break;
        }
    }
}
