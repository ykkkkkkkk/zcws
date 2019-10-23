package ykk.cb.com.zcws.entrance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.OnClick;
import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.comm.BaseFragment;
import ykk.cb.com.zcws.warehouse.Ds_PurInStockPassMainActivity;
import ykk.cb.com.zcws.warehouse.Sc_ICInvBackupMainActivity;
import ykk.cb.com.zcws.warehouse.Sc_ProdInStockPassMainActivity;

/**
 * 仓库
 */
public class MainTabFragment4 extends BaseFragment {


    public MainTabFragment4() {
    }

    @Override
    public View setLayoutResID(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.aa_main_item4, container, false);
    }

    @OnClick({R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6, R.id.relative7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relative1: // 生产入库审核
                show(Sc_ProdInStockPassMainActivity.class, null);

                break;
            case R.id.relative2: // 电商入库审核
                show(Ds_PurInStockPassMainActivity.class, null);

                break;
            case R.id.relative3: // 盘点
                show(Sc_ICInvBackupMainActivity.class, null);

                break;
            case R.id.relative4:
                showLoadDialog("连接服务器...");
                break;
            case R.id.relative5:
                showLoadDialog("连接服务器...");
                break;
            case R.id.relative6: // 调拨拣货
//                show(Allot_PickingListActivity.class, null);
                break;
            case R.id.relative7: // 库存查询
//                show(InventoryNowSearchActivity.class, null);
                break;
        }
    }
}
