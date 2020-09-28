package ykk.cb.com.zcws.warehouse.adapter

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ykk.cb.com.zcws.R
import ykk.cb.com.zcws.bean.ICStockBill
import ykk.cb.com.zcws.util.basehelper.BaseArrayRecyclerAdapter
import ykk.cb.com.zcws.util.basehelper.BaseRecyclerAdapter
import java.text.DecimalFormat

class OutInStockSearchFragment1_OtherInStock_Adapter(private val context: Activity, datas: List<ICStockBill>) : BaseArrayRecyclerAdapter<ICStockBill>(datas) {
    private val df = DecimalFormat("#.######")
    private var callBack: MyCallBack? = null

    override fun bindView(viewtype: Int): Int {
        return R.layout.ware_outin_stock_search_fragment1__other_in_stock_item
    }

    override fun onBindHoder(holder: BaseRecyclerAdapter.RecyclerHolder, entity: ICStockBill, pos: Int) {
        // 初始化id
        val tv_pdaNo = holder.obtainView<TextView>(R.id.tv_pdaNo)
        val tv_fdate = holder.obtainView<TextView>(R.id.tv_fdate)
        val tv_deptName = holder.obtainView<TextView>(R.id.tv_deptName)
        val tv_suppName = holder.obtainView<TextView>(R.id.tv_suppName)
        val tv_baoguanMan = holder.obtainView<TextView>(R.id.tv_baoguanMan)
        val tv_upload = holder.obtainView<TextView>(R.id.tv_upload)
        val tv_del = holder.obtainView<TextView>(R.id.tv_del)
        val lin_button = holder.obtainView<LinearLayout>(R.id.lin_button)

        // 赋值
        tv_pdaNo.text = entity.pdaNo
        tv_fdate.text = entity.fdate
        tv_deptName?.text = entity.deptName
        tv_suppName?.text = entity.suppName
        tv_baoguanMan.text = entity.baoguanMan

        if (entity.isShowButton) {
            lin_button!!.setVisibility(View.VISIBLE)
        } else {
            lin_button!!.setVisibility(View.GONE)
        }

        val click = View.OnClickListener { v ->
            when (v.id) {
                R.id.tv_upload -> {// 上传
                    if (callBack != null) {
                        callBack!!.onUpload(entity, pos)
                    }
                }
                R.id.tv_del -> { // 删除行
                    if (callBack != null) {
                        callBack!!.onDelete(entity, pos)
                    }
                }
            }
        }
        tv_upload!!.setOnClickListener(click)
        tv_del!!.setOnClickListener(click)
    }

    fun setCallBack(callBack: MyCallBack) {
        this.callBack = callBack
    }

    interface MyCallBack {
        fun onUpload(entity: ICStockBill, position: Int)
        fun onDelete(entity: ICStockBill, position: Int)
    }

}
