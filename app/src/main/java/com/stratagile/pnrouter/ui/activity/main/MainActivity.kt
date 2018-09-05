package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import androidx.core.net.toUri
import com.stratagile.pnrouter.ui.activity.main.contract.MainContract
import com.stratagile.pnrouter.ui.activity.main.module.MainModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MainPresenter
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMainComponent
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


/**
 * https://blog.csdn.net/Jeff_YaoJie/article/details/79164507
 */
class MainActivity : BaseActivity(), MainContract.View {
    var isTrue = false
    override fun showToast() {
        toast("点击啦。。。。哈哈哈")
        showProgressDialog()

    }

    @Inject
    internal lateinit var mPresenter: MainPresenter

    override fun setPresenter(presenter: MainContract.MainContractPresenter) {
        mPresenter = presenter as MainPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun initData() {
        tv_hello.text = "hahhaha"
        tv_hello.setOnClickListener {
            mPresenter.showToast()
        }
        tv_hello.typeface.style

    }

    override fun initView() {
        setContentView(R.layout.activity_main)
        setTitle("MainActivity")
    }

    override fun setupActivityComponent() {
        DaggerMainComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppConfig.instance!!.applicationComponent!!.httpApiWrapper
    }
}
