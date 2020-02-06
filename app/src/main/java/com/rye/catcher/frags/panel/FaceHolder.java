package com.rye.catcher.frags.panel;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.rye.catcher.R;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.face.Face;


import butterknife.BindView;

/**
 * CreateBy ShuQin
 * at 2020/2/4
 */
public class FaceHolder extends RecyclerAdapter.ViewHolder<Face.Bean> {
    @BindView(R.id.im_face)
    ImageView mFace;
    public FaceHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void onBind(Face.Bean bean) {
        //zip包资源或者是drawable资源
      if (bean!=null &&( bean.preview instanceof Integer||
              bean.preview instanceof String)){

          RequestOptions options=new RequestOptions();
          options.format(DecodeFormat.PREFER_ARGB_8888);
          Glide.with(mFace.getContext())
                  .load(bean.preview)
                  .apply(options)
                  .into(mFace);


      }
    }
}
