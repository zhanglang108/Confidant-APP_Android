package com.stratagile.pnrouter.ui.activity.file

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.chad.library.adapter.base.BaseQuickAdapter
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.data.fileInfo.FileInfo
import com.stratagile.pnrouter.data.fileInfo.FileInfosRepository
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileInfosComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileInfosContract
import com.stratagile.pnrouter.ui.activity.file.module.FileInfosModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileInfosPresenter
import com.stratagile.pnrouter.ui.adapter.file.FileInfosAdapter

import org.greenrobot.eventbus.EventBus

import java.util.ArrayList

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import io.julian.appchooser.BuildConfig
import io.julian.common.Preconditions
import io.julian.common.widget.LoadingLayout
import kotlinx.android.synthetic.main.fragment_file_infos.*

/**
 * @author zl
 * @Package com.stratagile.qlink.ui.activity.file
 * @Description: $description
 * @date 2018/09/28 16:46:15
 */

class FileInfosFragment : BaseFragment(), FileInfosContract.View {
    @Inject
    internal lateinit var mPresenter: FileInfosPresenter

    internal lateinit var fileInfosAdapter: FileInfosAdapter
    internal var view: View? = null

    //文件类型，0 代表所有文件，1 代表图片，2 代表文件,不包含图片
    var fileType = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (view != null) {
            return view
        }
        view = inflater.inflate(R.layout.fragment_file_infos, null)
        //ButterKnife.bind(this, view!!)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val absolutePath = arguments!!.getString(EXTRA_ABSOLUTE_PATH)
        fileType = arguments!!.getInt(FILETYPE)
        if (TextUtils.isEmpty(absolutePath)) {
            throw IllegalStateException("Absolute path is null")
        }
        mPresenter.init(FileInfosRepository(), FileInfo(absolutePath))
        mPresenter.loadFileInfos()
        fileInfosAdapter = FileInfosAdapter(ArrayList())
        recycler_view_file_infos!!.layoutManager = LinearLayoutManager(activity)
        recycler_view_file_infos!!.adapter = fileInfosAdapter
        fileInfosAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position -> EventBus.getDefault().post(adapter.getItem(position)) }
        KLog.i("onCreateView一次。。")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KLog.i("onCreate一次。。")
    }

    override fun setupFragmentComponent() {
        DaggerFileInfosComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .fileInfosModule(FileInfosModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileInfosContract.FileInfosContractPresenter) {
        mPresenter = presenter as FileInfosPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun showNoFileInfos() {
        if (getView() == null) {
            return
        }
        activity!!.runOnUiThread {
            val loadingLayout = getView()!!.findViewById<View>(R.id.loadingLayout) as LoadingLayout
            loadingLayout.setStatus(LoadingLayout.EMPTY)
        }
    }


   override fun showFileInfos(fileInfos: List<FileInfo>) {
        if (getView() == null) {
            return
        }
        KLog.i("数据获取成功: " + fileInfos.size)
        try {
            activity!!.runOnUiThread {
                val loadingLayout = getView()!!.findViewById<View>(R.id.loadingLayout) as LoadingLayout
                loadingLayout.setStatus(LoadingLayout.SUCCESS)
                for (i in fileInfos)
                {
                }
                var it = fileInfos.iterator();
                var fileInfosNew = ArrayList<FileInfo>()

                while (it.hasNext())
                {
                    val x = it.next()
                    if (fileType == 0) {
                        fileInfosNew.add(x)
                    } else if (fileType == 1) {
                        //图片
                        if (x.absolutePath.indexOf(".png") > 0 || x.absolutePath.indexOf(".jpg") > 0 || x.absolutePath.indexOf(".jpeg") > 0 || x.absolutePath.indexOf(".mp4") > 0 ||x.absolutePath.indexOf(".amr") > 0 || x.absolutePath.indexOf("DCIM") > 0) {
                            fileInfosNew.add(x)
                        }
                    } else if (fileType == 2){
                        //文件，不包含图片
                        if (x.absolutePath.indexOf(".png") < 0 && x.absolutePath.indexOf(".jpg") < 0 && x.absolutePath.indexOf(".jpeg") < 0 && x.absolutePath.indexOf(".mp4") < 0 && x.absolutePath.indexOf(".amr") < 0&& x.absolutePath.indexOf("DCIM") < 0) {
                            fileInfosNew.add(x)
                        }
                    }
                }
                fileInfosAdapter.setNewData(fileInfosNew)
                fileInfosAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private val EXTRA_ABSOLUTE_PATH = BuildConfig.APPLICATION_ID + ".extra.ABSOLUTE_PATH"
        private val FILETYPE = "fileType"
        fun newInstance(absolutePath: String, fileType : Int): FileInfosFragment {
            Preconditions.checkNotNull(absolutePath)
            val args = Bundle()
            args.putString(EXTRA_ABSOLUTE_PATH, absolutePath)
            args.putInt(FILETYPE, fileType)

            val fragment = FileInfosFragment()
            fragment.arguments = args
            return fragment
        }
    }
}