package ykk.cb.com.zcws.warehouse

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ware_sc_other_in_stock_fragment1.*
import okhttp3.*
import ykk.cb.com.zcws.R
import ykk.cb.com.zcws.bean.*
import ykk.cb.com.zcws.bean.k3Bean.ICInventory
import ykk.cb.com.zcws.comm.BaseFragment
import ykk.cb.com.zcws.comm.Comm
import ykk.cb.com.zcws.util.BigdecimalUtil
import ykk.cb.com.zcws.util.JsonUtil
import ykk.cb.com.zcws.util.LogUtil
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 日期：2019-10-16 09:50
 * 描述：其他入库
 * 作者：ykk
 */
class Sc_OtherInStockFragment1 : BaseFragment() {

    private val SEL_DEPT = 10
    private val SEL_SUPP = 11
    private val SEL_STOCK = 12
    private val SEL_STOCKPOS = 13
    private val SEL_MTL = 14
    private val context = this
    private val SUCC1 = 200
    private val UNSUCC1 = 500
    private val SUCC2 = 201
    private val UNSUCC2 = 501
    private val SAVE = 202
    private val UNSAVE = 502
    private val RESULT_NUM = 2

    private val checkDatas = ArrayList<ICInventory>()
    private var okHttpClient: OkHttpClient? = null
    private var user: User? = null
    private var department:Department? = null
    private var supplier:Supplier? = null
    private var stock:Stock? = null
    private var stockPos:StockPosition? = null
    private var mContext: Activity? = null
    private val df = DecimalFormat("#.######")
    private var parent: Sc_OtherInStockMainActivity? = null
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var curPos:Int = 0 // 当前行
    private var timesTamp:String? = null // 时间戳


    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: Sc_OtherInStockFragment1) : Handler() {
        private val mActivity: WeakReference<Sc_OtherInStockFragment1>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val m = mActivity.get()
            if (m != null) {
                m.hideLoadDialog()

                var errMsg: String? = null
                var msgObj: String? = null
                if (msg.obj is String) {
                    msgObj = msg.obj as String
                }
                when (msg.what) {
                    m.SUCC1 -> { // 扫码成功 进入
                    }
                    m.UNSUCC1 -> { // 扫码失败
                    }
                    m.SUCC2 -> { // 历史查询 进入
                    }
                    m.UNSUCC2 -> { // 历史查询  失败
                    }
                    m.SAVE // 保存成功 进入
                    -> {
                        m.reset(false)
                        m.toasts("保存成功✔")
                    }
                    m.UNSAVE -> { // 保存失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "保存失败！"
                        Comm.showWarnDialog(m.mContext, errMsg)
                    }
                }
            }
        }
    }

    override fun setLayoutResID(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.ware_sc_other_in_stock_fragment1, container, false)
    }

    override fun initView() {
        mContext = getActivity()
        parent = mContext as Sc_OtherInStockMainActivity

    }

    override fun initData() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                    //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(30, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(30, TimeUnit.SECONDS) //设置读取超时时间
                    .build()
        }

        getUserInfo()
        timesTamp = user!!.getId().toString() + "-" + Comm.randomUUID()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
        }
    }

//    @OnClick(R.id.tv_deptSel, R.id.tv_suppSel, R.id.tv_stockSel, R.id.btn_scan_stockPos, R.id.btn_stockPosSel, R.id.btn_scan, R.id.btn_mtlSel, R.id.btn_save, R.id.btn_clone)
//    fun onViewClicked(view: View) {
//        var bundle: Bundle? = null
//        when (view.id) {
//            R.id.tv_deptSel -> { // 选择部门
//                showForResult(Dept_DialogActivity::class.java, SEL_DEPT, null)
//            }
//            R.id.tv_suppSel -> { // 选择供应商
//                showForResult(Supplier_DialogActivity::class.java, SEL_SUPP, null)
//            }
//            R.id.tv_stockSel -> { // 选择收货仓库
//                showForResult(Stock_DialogActivity::class.java, SEL_STOCK, null)
//            }
//            R.id.btn_stockPosSel -> { // 选择调出库位
//                if(!checkSelStockPos()) return
//                var bundle = Bundle()
//                bundle.putInt("fspGroupId", stock!!.fspGroupId);
//                showForResult(StockPos_DialogActivity::class.java, SEL_STOCKPOS, bundle)
//            }
//            R.id.btn_mtlSel -> { // 选择物料
//                if(!checkSelStockPos()) return
//                bundle = Bundle()
//                bundle.putInt("fStockId", stock!!.fitemId)
//                bundle.putInt("fStockPlaceID", if(stockPos != null) stockPos!!.fspId else 0)
//                showForResult(StockTransfer_Material_DialogActivity::class.java, SEL_MTL, bundle)
//            }
//            R.id.btn_scan_stockPos -> { // 调用摄像头扫描（调出库位）
//                if(!checkSelStockPos()) return
//                showForResult(CaptureActivity::class.java, BaseFragment.CAMERA_SCAN, null)
//            }
//            R.id.btn_scan -> { // 调用摄像头扫描（物料）
//                if(!checkSelStockPos()) return
//                showForResult(CaptureActivity::class.java, BaseFragment.CAMERA_SCAN, null)
//            }
//            R.id.btn_save -> { // 保存
//                if(checkDatas.size == 0) {
//                    Comm.showWarnDialog(mContext,"请选择物料或者扫码条码！")
//                    return
//                }
//                run_save();
//            }
//            R.id.btn_clone -> { // 重置
//                if (parent!!.isChange) {
//                    val build = AlertDialog.Builder(mContext)
//                    build.setIcon(R.drawable.caution)
//                    build.setTitle("系统提示")
//                    build.setMessage("您有未保存的数据，继续重置吗？")
//                    build.setPositiveButton("是") { dialog, which -> reset(true) }
//                    build.setNegativeButton("否", null)
//                    build.setCancelable(false)
//                    build.show()
//
//                } else {
//                    reset(true)
//                }
//            }
//        }
//    }

    /**
     * 选择库位判断
     */
    fun checkSelStockPos() : Boolean {
        if (stock == null) {
            Comm.showWarnDialog(mContext, "请选择收货仓库！")
            return false;
        }
        if (stock!!.fisStockMgr == 0) {
            Comm.showWarnDialog(mContext, "收货仓库没有启用库位！")
            return false;
        }
        return true;
    }

    override fun setListener() {

    }

    private fun reset(isAll : Boolean) {
//        if(isAll) {
//            tv_stockSel.text = ""
//            tv_stockPosName.text = ""
//            stock = null;
//            stockPos = null;
//            smqFlag = '2'
//        }
//        tv_countNum.text = ""
        timesTamp = user!!.getId().toString() + "-" + Comm.randomUUID()
        parent!!.isChange = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SEL_DEPT -> {//查询部门	返回
                if (resultCode == Activity.RESULT_OK) {
                    department = data!!.getSerializableExtra("obj") as Department
                    tv_deptSel.text = department!!.departmentName
                }
            }
            SEL_SUPP -> {//查询供应商	返回
                if (resultCode == Activity.RESULT_OK) {
                    supplier = data!!.getSerializableExtra("obj") as Supplier
                    tv_suppSel.text = supplier!!.fname
                }
            }
//            SEL_STOCK -> {// 收货仓库	返回
//                if (resultCode == Activity.RESULT_OK) {
//                    stock = data!!.getSerializableExtra("obj") as Stock
//                    tv_stockSel.text = stock!!.fname
//                    // 如果启用了库位
//                    if(stock!!.fisStockMgr == 1) {
//                        lin_stockPos.visibility = View.VISIBLE
//                        var bundle = Bundle()
//                        bundle.putInt("fspGroupId", stock!!.fspGroupId);
//                        showForResult(StockPos_DialogActivity::class.java, SEL_STOCKPOS, bundle)
//                    } else {
//                        lin_stockPos.visibility = View.GONE
//                    }
//                    stockPos = null
//                    tv_stockPosName.text = ""
//                }
//            }
//            SEL_STOCKPOS -> {// 收货仓库库位	返回
//                if (resultCode == Activity.RESULT_OK) {
//                    stockPos = data!!.getSerializableExtra("obj") as StockPosition
//                    tv_stockPosName.text = stockPos!!.fname
//                }
//            }
            SEL_MTL //查询物料	返回
            -> if (resultCode == Activity.RESULT_OK) {
                val list = data!!.getSerializableExtra("obj") as List<ICInventory>

                getMtlAfter(list)
            }

        }
    }

    /**
     * 得到扫码或选择数据
     */
    private fun getMtlAfter(list: List<ICInventory>) {
        parent!!.isChange = true
        val size = checkDatas.size
        var addRow = true
        var curPosition = 0
        // 循环判断业务
        for (icInventory in list) {
            // 填充数据
            addRow = true
            curPosition = 0
            for (i in 0 until size) {
                val sr = checkDatas.get(i)
                // 有相同的，就不新增了
                if (sr.getfItemID() == icInventory.getfItemID()) {
                    curPosition = i
                    addRow = false
                    break
                }
            }
            if (addRow) {
                icInventory.realQty = 1.0
                icInventory.createUserId = user!!.id
                checkDatas.add(icInventory)

            } else {
                // 已有相同物料行，就叠加数量
                val fqty = checkDatas[curPosition].realQty
                val addVal = BigdecimalUtil.add(fqty, 1.0);
                checkDatas[curPosition].realQty = addVal
            }
        }
    }

    /**
     * 扫码查询对应的方法
     */
    private fun run_smDatas() {
        isTextChange = false
        showLoadDialog("加载中...", false)
        var mUrl:String? = null
        var barcode:String? = null
//        when(smqFlag) {
//            '1' -> {
//                mUrl = getURL("stockPosition/findBarcode")
//                barcode = getValues(et_stockPos)
//            }
//            '2' -> {
//                mUrl = getURL("icInventory/findBarcode")
//                barcode = getValues(et_code)
//            }
//        }
        val formBody = FormBody.Builder()
                .add("fStockId", stock!!.fitemId.toString())
                .add("fStockPlaceID", if(stockPos != null) stockPos!!.fspId.toString() else "")
                .add("FSPGroupId", stock!!.fspGroupId.toString())
                .add("barcode", barcode)
                .build()

        val request = Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC1)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_smDatas --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC1, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC1, result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 保存
     */
    private fun run_save() {
        var saveList = ArrayList<StockTransferRecord>()
        // for循环
        checkDatas.forEachIndexed { index, icInventory ->
            if(icInventory.getfQty() < icInventory.realQty) {
                Comm.showWarnDialog(mContext,"第（"+(index+1)+"）行，调拨数不能大于库存数！")
                return
            }
            val save = StockTransferRecord()
//            save.outStockId = stock!!.fitemId
//            save.outStockNumber = stock!!.fnumber
//            save.outStockName = stock!!.fname
//            save.inStockId = inStock!!.fitemId
//            save.inStockNumber = inStock!!.fnumber
//            save.inStockName = inStock!!.fname
//            if(stockPos != null) {
//                save.outStockPosId = stockPos!!.fspId
//                save.outStockPosNumber = stockPos!!.fnumber
//                save.outStockPosName = stockPos!!.fname
//            }
//            if(inStockPos != null) {
//                save.inStockPosId = inStockPos!!.fspId
//                save.inStockPosNumber = inStockPos!!.fnumber
//                save.inStockPosName = inStockPos!!.fname
//            }
            save.icItemId = icInventory.getfItemID()
            save.icItemNumber = icInventory!!.mtlNumber
            save.icItemName = icInventory!!.mtlName
            save.timesTamp = timesTamp
            save.allotQty = icInventory.realQty
            save.createUserId = user!!.id
            save.createUserName = user!!.username
            saveList.add(save)
        }
        showLoadDialog("保存中...", false)
        val mUrl = getURL("stockTransferRecord/addList")
        val mJson = JsonUtil.objectToString(saveList)
        val formBody = FormBody.Builder()
                .add("strJson", mJson)
                .build()

        val request = Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSAVE)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSAVE, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SAVE, result)
                LogUtil.e("run_save --> onResponse", result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 历史查询
     */
    private fun run_findListByParamWms() {
        isTextChange = false
        showLoadDialog("加载中...", false)
        val mUrl = getURL("ICInventory/findListByParamWms")
        val formBody = FormBody.Builder()
//                .add("finterId", if (icStockCheckProcess != null) icStockCheckProcess!!.fid.toString() else "") // 方案id
                .add("toK3", "1")
                .add("userId", user!!.id.toString())
                .build()

        val request = Request.Builder()
                .addHeader("cookie", getSession())
                .url(mUrl)
                .post(formBody)
                .build()

        val call = okHttpClient!!.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                mHandler.sendEmptyMessage(UNSUCC2)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                val result = body.string()
                LogUtil.e("run_findListByParamWms --> onResponse", result)
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC2, result)
                    mHandler.sendMessage(msg)
                    return
                }
                val msg = mHandler.obtainMessage(SUCC2, result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 得到用户对象
     */
    private fun getUserInfo() {
        if (user == null) user = showUserByXml()
    }

    override fun onDestroyView() {
        closeHandler(mHandler)
        mBinder!!.unbind()
        super.onDestroyView()
    }
}