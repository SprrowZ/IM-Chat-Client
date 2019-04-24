package com.rye.catcher.help.permissions;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import com.rye.catcher.R;
import com.rye.catcher.frags.main.ContactFragment;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Setting;

import java.util.List;

/**
 * Created at 2018/10/26.
 * 6.0权限适配
 * @author Zzg
 */
public class PermissionUtils {
  public static  void requestPermission(Context context, String errorMsg,Action<List<String>> action, String... permissions){
      AndPermission.with(context)
              .runtime()
              .permission(permissions)
              .rationale(new RuntimeRationale())
              .onGranted(action)
              .onDenied(data->{
                  if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                      showSettingDialog(context,errorMsg, permissions);
                  }
              })
              .start();
  }

    /**
     * Display setting dialog.
     */
    public static void showSettingDialog(Context context, String errorMsg, String... permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);


        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage(errorMsg)
                .setPositiveButton("设置",(dialog,which)->{
                  setPermission(context);
                })
                .setNegativeButton("取消", ((dialog, which)->{
                  Toast.makeText(context,permissionNames+"权限申请失败，请重试！",Toast.LENGTH_SHORT).show();
                }))
                .show();
    }

    /**
     * Set permissions.
     */
    private static void setPermission(Context context) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback(()->{

                })
                .start();
    }
}
