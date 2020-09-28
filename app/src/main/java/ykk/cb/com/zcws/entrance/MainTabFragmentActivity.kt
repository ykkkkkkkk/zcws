package ykk.cb.com.zcws.entrance

import android.app.AlertDialog
import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.OnClick
import kotlinx.android.synthetic.main.aa_main.*
import ykk.cb.com.zcws.R
import ykk.cb.com.zcws.comm.ActivityCollector
import ykk.cb.com.zcws.comm.BaseActivity
import ykk.cb.com.zcws.entrance.page5.PrintMainActivity
import ykk.cb.com.zcws.util.adapter.BaseFragmentAdapter
import java.util.*


class MainTabFragmentActivity : BaseActivity() {

    private val context = this
    private var curRelative: RelativeLayout? = null
    private var curTv: TextView? = null
    private var curRadio: RadioButton? = null

    override fun setLayoutResID(): Int {
        return R.layout.aa_main
    }

    override fun initData() {
        curRelative = relative1
        curTv = tab1
        curRadio = radio1

        val user = showUserByXml()
        tv_title!!.text = "操作员：" + user!!.username

        val listFragment = ArrayList<Fragment>()
        listFragment.add(MainTabFragment1())
        listFragment.add(MainTabFragment2())
        listFragment.add(MainTabFragment3())
        listFragment.add(MainTabFragment4())
        //        listFragment.add(new MainTabFragment5());
        listFragment.add(MainTabFragment6())
        //ViewPager设置适配器
        viewPager!!.adapter = BaseFragmentAdapter(supportFragmentManager, listFragment)
        //ViewPager显示第一个Fragment
        viewPager!!.currentItem = 0
        //ViewPager页面切换监听
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> tabChange(relative1, tab1, radio1, 0)
                    1 -> tabChange(relative2, tab2, radio2, 1)
                    2 -> tabChange(relative3, tab3, radio3, 2)
                    3 -> tabChange(relative4, tab4, radio4, 3)
                    4 ->
                        //                        tabChange(relative5, tab5, radio5, 4);
                        //                        break;
                        //                    case 5:
                        tabChange(relative6, tab6, radio6, 4)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    @OnClick(R.id.btn_close, R.id.btn_print, R.id.relative1, R.id.relative2, R.id.relative3, R.id.relative4, R.id.relative5, R.id.relative6, R.id.radio1, R.id.radio2, R.id.radio3, R.id.radio4, R.id.radio5, R.id.radio6)
    fun onViewClicked(view: View) {
        // setCurrentItem第二个参数控制页面切换动画
        //  true:打开/false:关闭
        //  viewPager.setCurrentItem(0, false);

        when (view.id) {
            R.id.btn_close // 退出
            -> {
                val build = AlertDialog.Builder(context)
                build.setIcon(R.drawable.caution)
                build.setTitle("系统提示")
                build.setMessage("主人，确定要离开我吗？")
                build.setPositiveButton("是的") { dialog, which ->
                    ActivityCollector.finishAll()
                    System.exit(0) //凡是非零都表示异常退出!0表示正常退出!
                }
                build.setNegativeButton("取消", null)
                build.setCancelable(false)
                build.show()
            }
            R.id.btn_print // 打印
            -> show(PrintMainActivity::class.java, null)
            R.id.relative1 -> tabChange(relative1, tab1, radio1, 0)
            R.id.relative2 -> tabChange(relative2, tab2, radio2, 1)
            R.id.relative3 -> tabChange(relative3, tab3, radio3, 2)
            R.id.relative4 -> tabChange(relative4, tab4, radio4, 3)
            R.id.relative5 -> tabChange(relative5, tab5, radio5, 4)
            R.id.relative6 -> tabChange(relative6, tab6, radio6, 4)
            // --------------------------------------------
            R.id.radio1 // RadioButton
            -> tabChange(relative1, tab1, radio1, 0)
            R.id.radio2 -> tabChange(relative2, tab2, radio2, 1)
            R.id.radio3 -> tabChange(relative3, tab3, radio3, 2)
            R.id.radio4 -> tabChange(relative4, tab4, radio4, 3)
            R.id.radio5 -> tabChange(relative5, tab5, radio5, 4)
            R.id.radio6 -> tabChange(relative6, tab6, radio6, 4)
        }
    }

    /**
     * 选中之后改变样式
     */
    private fun tabSelected(relative: RelativeLayout, tv: TextView, rb: RadioButton) {
        curRelative!!.setBackgroundColor(Color.parseColor("#EAEAEA"))
        curRadio!!.isChecked = false
        curTv!!.setTextColor(Color.parseColor("#1a1a1a"))
        relative.setBackgroundResource(R.drawable.back_style_blue)
        rb.isChecked = true
        tv.setTextColor(Color.parseColor("#6a5acd"))
        curRelative = relative
        curRadio = rb
        curTv = tv
    }

    private fun tabChange(relative: RelativeLayout?, tv: TextView?, radio: RadioButton?, page: Int) {
        tabSelected(relative!!, tv!!, radio!!)
        viewPager!!.setCurrentItem(page, false)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 点击返回不销毁
        //        if((keyCode == KeyEvent.KEYCODE_BACK)&&(event.getAction() == KeyEvent.ACTION_DOWN)) {
        //            return false;
        //        }
        return false
    }

}
