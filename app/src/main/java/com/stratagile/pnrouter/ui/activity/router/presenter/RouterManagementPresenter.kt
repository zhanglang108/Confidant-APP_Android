package com.stratagile.pnrouter.ui.activity.router.presenter
import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.support.annotation.NonNull
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.data.api.HttpAPIWrapper
import com.stratagile.pnrouter.ui.activity.router.contract.RouterManagementContract
import com.stratagile.pnrouter.ui.activity.router.RouterManagementActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import javax.inject.Inject
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: presenter of RouterManagementActivity
 * @date 2018/09/26 10:29:17
 */
class RouterManagementPresenter @Inject
constructor(internal var httpAPIWrapper: HttpAPIWrapper, private val mView: RouterManagementContract.View) : RouterManagementContract.RouterManagementContractPresenter {

    private val mCompositeDisposable: CompositeDisposable

    init {
        mCompositeDisposable = CompositeDisposable()
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }
    override fun getScanPermission() {
        AndPermission.with(mView as Activity)
                .requestCode(101)
                .permission(
                        Manifest.permission.CAMERA
                )
                .rationale({ requestCode, rationale ->
                    AndPermission
                            .rationaleDialog(mView as Activity, rationale)
                            .setTitle(AppConfig.instance.getResources().getString(R.string.Permission_Requeset))
                            .setMessage(AppConfig.instance.getResources().getString(R.string.We_Need_Some_Permission_to_continue))
                            .setNegativeButton(AppConfig.instance.getResources().getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->  AppConfig.instance.toast(R.string.permission_denied) })
                            .show()
                }
                )
                .callback(permission)
                .start()
    }

    private val permission = object : PermissionListener {
        override fun onSucceed(requestCode: Int, grantedPermissions: List<String>) {
            // 权限申请成功回调。
            if (requestCode == 101) {
                mView.getScanPermissionSuccess()
            }
        }

        override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
            // 权限申请失败回调。
            if (requestCode == 101) {
                KLog.i("权限申请失败")
                AppConfig.instance.toast(R.string.permission_denied)
            }
        }
    }
}