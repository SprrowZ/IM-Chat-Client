package com.rye.catcher.frags.assist;


import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rye.catcher.R;
import com.rye.catcher.common.app.zApplication;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 *  权限申请弹出框.
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {
    //权限申请标识
    private static final  int RC=0X0100;
    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_permissions, container, false);
        root.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //授权
                requestPerm();
            }
        });
        return root;
    }

    /**
     * 刷新我们的布局中的图片的状态--是否已经授权
     * @param root
     */
    private void refreshState(View root){
        if (root==null) return;
        Context context=getContext();
        root.findViewById(R.id.im_state_permission_network).setVisibility(hasNetPer(context)?View.VISIBLE:View.GONE);

        root.findViewById(R.id.im_state_permission_read).setVisibility(hasReadPer(context)?View.VISIBLE:View.GONE);

        root.findViewById(R.id.im_state_permission_write).setVisibility(hasWritePer(context)?View.VISIBLE:View.GONE);

        root.findViewById(R.id.im_state_permission_record_audio).setVisibility(hasAudioPer(context)?View.VISIBLE:View.GONE);

    }

    /**
     * 是否有网络权限
     * @param context
     * @return
     */
    private static boolean hasNetPer(Context context){
        String[] perms=new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(context,perms);
    }


    private static boolean hasReadPer(Context context){
        String[] perms=new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context,perms);
    }

    private static boolean hasWritePer(Context context){
        String[] perms=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context,perms);
    }

    private static boolean hasAudioPer(Context context){
        String[] perms=new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context,perms);
    }


    private static void show(FragmentManager manager){
        new PermissionsFragment().show(manager,PermissionsFragment.class.getName());
    }

    /**
     * 检查是否具有所有的权限----唯一对外公布方法
     * @param context
     * @param manager
     * @return
     */
    public static boolean haveAll(Context context,FragmentManager manager){
        boolean haveAll=hasNetPer(context)&&
                hasReadPer(context)&&
                hasWritePer(context)
                &&hasAudioPer(context);
        //如果没有则显示当前申请权限的界面
        if (!haveAll){
            show(manager);
        }
        return  haveAll;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());
    }

    /**
     * 申请权限
     */
    @AfterPermissionGranted(RC)//权限申请完会进入到当前界面
    private void requestPerm(){
        String[] perms=new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        if (EasyPermissions.hasPermissions(getContext(),perms)){//如果有权限
            zApplication.showToast(R.string.label_permission_ok);
            //getView必须再根布局之后
            refreshState(getView());
        }else{
            EasyPermissions.requestPermissions(this,getString(R.string.title_assist_permissions),RC,perms);
        }




    }

    /**
     * 复写Callback
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        /**
         * 官网推荐---谷歌的，可以复写
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this)
                    .build()
                    .show();
        }
    }

    /**
     * 权限申请时候回调的方法，在这个方法中把对应的权限申请状态交给EasyPermission
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //传递对应参数，并且告知接受权限的处理者是自己
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}
