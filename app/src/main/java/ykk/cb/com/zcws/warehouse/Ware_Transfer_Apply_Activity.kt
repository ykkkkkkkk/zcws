package ykk.cb.com.zcws.warehouse

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import butterknife.OnClick
import kotlinx.android.synthetic.main.ware_transfer_apply.*
import okhttp3.*
import ykk.cb.com.zcws.R
import ykk.cb.com.zcws.basics.Mtl_MoreStockDialogActivity
import ykk.cb.com.zcws.basics.Stock_DialogActivity
import ykk.cb.com.zcws.bean.TransferApplyEntry
import ykk.cb.com.zcws.bean.User
import ykk.cb.com.zcws.bean.k3Bean.ICItem
import ykk.cb.com.zcws.comm.BaseActivity
import ykk.cb.com.zcws.comm.BaseFragment
import ykk.cb.com.zcws.comm.Comm
import ykk.cb.com.zcws.util.JsonUtil
import ykk.cb.com.zcws.util.basehelper.BaseRecyclerAdapter
import ykk.cb.com.zcws.util.zxing.android.CaptureActivity
import ykk.cb.com.zcws.warehouse.adapter.Ware_Transfer_Apply_Adapter
import ykk.cb.com.zcws.warehouse.adapter.Ware_Transfer_Apply_SearchAdapter
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * 调拨申请
 */
class Ware_Transfer_Apply_Activity : BaseActivity() {

    companion object {
        private val SEL_STOCK = 61
        private val SEL_MTL = 62
        private val SUCC1 = 200
        private val UNSUCC1 = 500
        private val SAVE = 202
        private val UNSAVE = 502

        private val SETFOCUS = 1
        private val SAOMA = 2
        private val WRITE_CODE = 3
        private val RESULT_NUM = 4
    }
    private val context = this
    private var mAdapter: Ware_Transfer_Apply_Adapter? = null
    private var okHttpClient: OkHttpClient? = null
    private var accountType: String? = null // 账号类型（DS：电商，SC：生产）
    private var user: User? = null
    private var timesTamp:String? = null // 时间戳
    private val checkDatas = ArrayList<TransferApplyEntry>()
    private var isTextChange: Boolean = false // 是否进入TextChange事件
    private var curPos = -1
    private var smqFlag = '1' // 扫描类型1：位置扫描，2：物料扫描

    // 消息处理
    private val mHandler = MyHandler(this)

    private class MyHandler(activity: Ware_Transfer_Apply_Activity) : Handler() {
        private val mActivity: WeakReference<Ware_Transfer_Apply_Activity>

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
                    SUCC1 -> { // 扫描成功
                        /*when(m.smqFlag) {
                            '1'-> { // 仓库位置
                                val stock = JsonUtil.strToObject(msg.obj as String, Stock::class.java)
                                m.getStock(stock)
                            }
                            '2'-> { // 物料
                                val icitem = JsonUtil.strToObject(msg.obj as String, ICItem::class.java)
                                val list = ArrayList<ICItem>()
                                list.add(icitem)
                                m.tv_icItemName.text = icitem.fname
                                m.getMtlAfter(list)
                            }
                        }*/
                        val icitem = JsonUtil.strToObject(msg.obj as String, ICItem::class.java)
                        val list = ArrayList<ICItem>()
                        list.add(icitem)
                        m.tv_icItemName.text = icitem.fname
                        m.getMtlAfter(list)
                    }
                    UNSUCC1 -> { // 数据加载失败！
                        /*when(m.smqFlag) {
                            '1' -> m.tv_positionName.text = ""
                            '2' ->  m.tv_icItemName.text = ""
                        }*/
                        m.tv_icItemName.text = ""
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "很抱歉，没有加载到数据！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SAVE -> { // 保存成功 进入
                        m.timesTamp = m.user!!.getId().toString() + "-" + Comm.randomUUID()
                        m.toasts("保存成功")
                        m.checkDatas.clear()
                        m.mAdapter!!.notifyDataSetChanged()
                    }
                    UNSAVE -> { // 保存失败
                        errMsg = JsonUtil.strToString(msgObj)
                        if (m.isNULLS(errMsg).length == 0) errMsg = "保存失败！"
                        Comm.showWarnDialog(m.context, errMsg)
                    }
                    SETFOCUS -> { // 当弹出其他窗口会抢夺焦点，需要跳转下，才能正常得到值
                        m.setFocusable(m.et_getFocus)
                        /*when(m.smqFlag) {
                            '1'-> m.setFocusable(m.et_positionCode)
                            '2'-> m.setFocusable(m.et_code)
                        }*/
                        m.setFocusable(m.et_code)
                    }
                    SAOMA -> { // 扫码之后
                        /*if(m.smqFlag == '2' && m.stock == null) {
                            Comm.showWarnDialog(m.context,"请选择仓库！")
                            return
                        }*/
                        // 执行查询方法
                        m.run_smDatas()
                    }
                }
            }
        }

    }

    override fun setLayoutResID(): Int {
        return R.layout.ware_transfer_apply
    }

    override fun initView() {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder()
                    //                .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时时间（默认为10秒）
                    .writeTimeout(120, TimeUnit.SECONDS) // 设置写的超时时间
                    .readTimeout(120, TimeUnit.SECONDS) //设置读取超时时间
                    .build()
        }

        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = Ware_Transfer_Apply_Adapter(context, checkDatas)
        recyclerView.adapter = mAdapter
        // 设值listview空间失去焦点
        recyclerView.isFocusable = false

        // 行事件删除
        mAdapter!!.setCallBack(object : Ware_Transfer_Apply_Adapter.MyCallBack {
            override fun onDelete(entity: TransferApplyEntry, position: Int) {
                checkDatas.removeAt(position)
                mAdapter!!.notifyDataSetChanged()
            }
        })
        // 行事件输入数量
        mAdapter!!.onItemClickListener = BaseRecyclerAdapter.OnItemClickListener { adapter, holder, view, pos ->
            curPos = pos
            showInputDialog("调拨数", checkDatas[pos].fqty.toString(), "0.0", RESULT_NUM)
        }
    }

    override fun initData() {
        getUserInfo()
        timesTamp = user!!.getId().toString() + "-" + Comm.randomUUID()
//        hideSoftInputMode(et_positionCode)
        hideSoftInputMode(et_code)
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }


    // 监听事件
    @OnClick(R.id.btn_close, R.id.btn_search, R.id.btn_positionSel, R.id.btn_mtlSel, R.id.btn_positionScan, R.id.btn_scan, R.id.tv_positionName, R.id.tv_icItemName, R.id.btn_save)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                closeHandler(mHandler)
                context.finish()
            }
            R.id.btn_search -> {
                show(Ware_Transfer_Apply_SearchActivity::class.java,null)
            }
            R.id.btn_positionSel -> { // 选择仓库
                smqFlag = '1'
                showForResult(Stock_DialogActivity::class.java, SEL_STOCK, null)
            }
            R.id.btn_mtlSel -> { // 选择物料
                /*if(stock == null) {
                    Comm.showWarnDialog(context,"请选择仓库！")
                    return
                }*/
                smqFlag = '2'
                val bundle = Bundle()
                bundle.putString("mtlStockIdGt0", "1")
                bundle.putString("accountType", "ZH")
//                showForResult(Mtl_MoreDialogActivity::class.java, SEL_MTL, bundle)
                showForResult(Mtl_MoreStockDialogActivity::class.java, SEL_MTL, bundle)
            }
            R.id.btn_positionScan -> { // 调用摄像头扫描（位置）
                smqFlag = '1'
                showForResult(CaptureActivity::class.java, BaseFragment.CAMERA_SCAN, null)
            }
            R.id.btn_scan -> { // 调用摄像头
                smqFlag = '2'
                showForResult(CaptureActivity::class.java, BaseFragment.CAMERA_SCAN, null)
            }
            R.id.tv_positionName -> { // 位置点击
                smqFlag = '1'
                mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
            }
            R.id.tv_icItemName -> { // 物料点击
                smqFlag = '2'
                mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
            }
            R.id.btn_save -> { // 提交申请
                val size = checkDatas.size
                if (size == 0) {
                    Comm.showWarnDialog(context, "请扫描或选择物料信息！")
                    return
                }
                run_save(JsonUtil.objectToString(checkDatas))
            }
        }
    }

    override fun setListener() {
        val click = View.OnClickListener { v ->
            setFocusable(et_getFocus)
            when (v.id) {
                R.id.et_positionCode -> setFocusable(et_positionCode)
                R.id.et_code -> setFocusable(et_code)
            }
        }
//        et_code.setOnClickListener(click)
        et_positionCode.setOnClickListener(click)
        /*
        // 仓库---数据变化
        et_positionCode!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) return
                if (!isTextChange) {
                    isTextChange = true
                    smqFlag = '1'
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300)
                }
            }
        })
        // 仓库---长按输入条码
        et_positionCode!!.setOnLongClickListener {
            smqFlag = '1'
            showInputDialog("输入条码号", getValues(et_positionCode), "none", WRITE_CODE)
            true
        }
        // 仓库---焦点改变
        et_positionCode.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                lin_focusPosition.setBackgroundResource(R.drawable.back_style_red_focus)
            } else {
                if (lin_focusPosition != null) {
                    lin_focusPosition.setBackgroundResource(R.drawable.back_style_gray4)
                }
            }
        }
        */
        // 物料---数据变化
        et_code!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length == 0) return
                if (!isTextChange) {
                    isTextChange = true
                    smqFlag = '2'
                    mHandler.sendEmptyMessageDelayed(SAOMA, 300)
                }
            }
        })
        // 物料---长按输入条码
        et_code!!.setOnLongClickListener {
            smqFlag = '2'
            showInputDialog("输入条码号", getValues(et_code), "none", WRITE_CODE)
            true
        }
        // 物料---焦点改变
        et_code.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                lin_focusMtl.setBackgroundResource(R.drawable.back_style_red_focus)
            } else {
                if (lin_focusMtl != null) {
                    lin_focusMtl.setBackgroundResource(R.drawable.back_style_gray4)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                BaseFragment.CAMERA_SCAN -> {// 扫一扫成功  返回
                    if (resultCode == RESULT_OK) {
                        val bundle = data!!.extras
                        if (bundle != null) {
                            val code = bundle.getString(BaseFragment.DECODED_CONTENT_KEY, "")
                            /*when (smqFlag) {
                                '1' -> setTexts(et_positionCode, code)
                                '2' -> setTexts(et_code, code)
                            }*/
                            setTexts(et_code, code)
                        }
                    }
                }
                SEL_MTL -> { //查询物料	返回
                    val listICItem = data!!.getSerializableExtra("obj") as List<ICItem>
                    getMtlAfter(listICItem)
                }
                WRITE_CODE -> {// 输入条码  返回
                    val bundle = data!!.extras
                    if (bundle != null) {
                        val value = bundle.getString("resultValue", "")
                        /*when (smqFlag) {
                            '1' -> setTexts(et_positionCode, value.toUpperCase())
                            '2' -> setTexts(et_code, value.toUpperCase())
                        }*/
                        setTexts(et_code, value.toUpperCase())
                    }
                }
                RESULT_NUM -> { // 数量	返回
                    if (resultCode == RESULT_OK) {
                        val bundle = data!!.getExtras()
                        if (bundle != null) {
                            val value = bundle.getString("resultValue", "")
                            val num = parseDouble(value)
                            checkDatas[curPos].fqty = num
                            mAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(SETFOCUS, 200)
    }

    /**
     * 得到扫码或选择数据
     */
    private fun getMtlAfter(listICItem : List<ICItem>) {
        val mapICItem = HashMap<Int, Boolean>()
        checkDatas.forEach {
            if(!mapICItem.containsKey(it.icitemId)) {
                mapICItem.put(it.icitemId, true)
            }
        }

        listICItem.forEach {
            if(!mapICItem.containsKey(it.fitemid)) {
                val entry = TransferApplyEntry()
                entry.icitemId = it.fitemid
                entry.fqty = 1.0
                if(it.stock != null) {
                    entry.stockId = it.stock.fitemId
                    entry.stock = it.stock
                }
                if(it.stockPos != null && it.stockPos.fspId > 0) {
                    entry.stockPosId = it.stockPos.fspId
                    entry.stockPos = it.stockPos
                }

                entry.icItem = it
                checkDatas.add(entry)
            }
        }

        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * 扫描查询物料
     */
    private fun run_smDatas() {
        isTextChange = false
        showLoadDialog("加载中...", false)
        var mUrl = ""
        var barcode = ""
        var strCaseId = ""
        /*when(smqFlag) {
            '1' -> {
                mUrl = getURL("stock/findBarcode")
                barcode = getValues(et_positionCode).trim()
                strCaseId = "31"
            }
            '2' -> {
                mUrl = getURL("material/findBarcode")
                barcode = getValues(et_code).trim()
                strCaseId = "11,21"
            }
        }*/
        mUrl = getURL("material/findBarcode")
        barcode = getValues(et_code).trim()
        strCaseId = "11,21"
        val formBody = FormBody.Builder()
                .add("barcode", barcode)
                .add("strCaseId", strCaseId) // 物料，生产订单
                .add("checkMtlStock", "1") // 检查物料默认仓库
                .add("accountType", isNULL2(accountType, "ZH"))
                .build()
        val request = Request.Builder()
                .addHeader("cookie", session)
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
                if (!JsonUtil.isSuccess(result)) {
                    val msg = mHandler.obtainMessage(UNSUCC1, result)
                    mHandler.sendMessage(msg)
                    return
                }

                val msg = mHandler.obtainMessage(SUCC1, result)
                Log.e("run_smDatas --> onResponse", result)
                mHandler.sendMessage(msg)
            }
        })
    }

    /**
     * 保存
     */
    private fun run_save(strJson :String) {
        showLoadDialog("保存中...", false)
        val mUrl = getURL("transferApply/save")
        val formBody = FormBody.Builder()
                .add("strJson", strJson)
                .add("createUserId", user!!.id.toString()) // 物料，生产订单
                .add("accountType", isNULL2(accountType, "ZH"))
                .add("timesTamp", timesTamp)
                .build()

        val request = Request.Builder()
                .addHeader("cookie", session)
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
                    mHandler.sendEmptyMessage(UNSAVE)
                    return
                }

                val msg = mHandler.obtainMessage(SAVE, result)
                Log.e("run_smDatas --> onResponse", result)
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

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // 按了删除键，回退键
        //        if(!isKeyboard && (event.getKeyCode() == KeyEvent.KEYCODE_FORWARD_DEL || event.getKeyCode() == KeyEvent.KEYCODE_DEL)) {
        // 240 为PDA两侧面扫码键，241 为PDA中间扫码键
        return if (!(event.keyCode == 240 || event.keyCode == 241)) {
            false
        } else super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeHandler(mHandler)
            context.finish()
        }
        return false
    }

    override fun onDestroy() {
        closeHandler(mHandler)
        super.onDestroy()
    }

}
