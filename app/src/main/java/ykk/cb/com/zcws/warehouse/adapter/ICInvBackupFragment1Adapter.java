package ykk.cb.com.zcws.warehouse.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ykk.cb.com.zcws.R;
import ykk.cb.com.zcws.bean.k3Bean.ICInvBackup;
import ykk.cb.com.zcws.comm.Comm;
import ykk.cb.com.zcws.util.basehelper.BaseArrayRecyclerAdapter;

public class ICInvBackupFragment1Adapter extends BaseArrayRecyclerAdapter<ICInvBackup> {
    private DecimalFormat df = new DecimalFormat("#.######");
    private Activity context;
    private MyCallBack callBack;

    public ICInvBackupFragment1Adapter(Activity context, List<ICInvBackup> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.ware_icinvbackup_fragment1_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final ICInvBackup entity, final int pos) {
        // 初始化id
        TextView tv_row = holder.obtainView(R.id.tv_row);
        TextView tv_mtlNumber = holder.obtainView(R.id.tv_mtlNumber);
        TextView tv_mtlName = holder.obtainView(R.id.tv_mtlName);
        TextView tv_auxQty = holder.obtainView(R.id.tv_auxQty);
        TextView tv_auxCheckQty = holder.obtainView(R.id.tv_auxCheckQty);
        TextView tv_batchNo = holder.obtainView(R.id.tv_batchNo);
        TextView tv_fmode = holder.obtainView(R.id.tv_fmode);
        TextView tv_del = holder.obtainView(R.id.tv_del);

        // 赋值
        tv_row.setText(String.valueOf(pos + 1));
        tv_mtlNumber.setText(entity.getIcItem().getFnumber());
        tv_mtlName.setText(entity.getIcItem().getFname());
        tv_batchNo.setText(entity.getFbatchNo());
        tv_fmode.setText(entity.getIcItem().getFmodel());
        tv_auxQty.setText(df.format(entity.getFauxQty())+entity.getIcItem().getUnitName());
        tv_auxCheckQty.setText(Html.fromHtml("<small><font color='#777777'>已盘</font>"+df.format(entity.getFauxCheckQty())+"</small><br><font color='#009900'>" + df.format(entity.getRealQty()) + "</font>"));


        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_auxCheckQty: // 盘点数
                        if (callBack != null) {
                            callBack.onClick_num(entity, pos);
                        }

                        break;
                    case R.id.tv_fmode: // 规格
                        if(Comm.isNULLS(entity.getIcItem().getFmodel()).length() > 0) {
                            Comm.showWarnDialog(context, entity.getIcItem().getFmodel());
                        }

                        break;
                    case R.id.tv_del: // 删除行
                        if (callBack != null) {
                            callBack.onDelete(entity, pos);
                        }

                        break;
                }
            }
        };
        tv_auxCheckQty.setOnClickListener(click);
        tv_fmode.setOnClickListener(click);
        tv_del.setOnClickListener(click);
    }

    public void setCallBack(MyCallBack callBack) {
        this.callBack = callBack;
    }

    public interface MyCallBack {
        void onClick_num(ICInvBackup entity, int position);
        void onDelete(ICInvBackup entity, int position);
    }

}
