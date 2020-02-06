package com.rye.catcher.frags.panel;

import android.view.View;

import com.rye.catcher.R;
import com.rye.catcher.common.widget.recycler.RecyclerAdapter;
import com.rye.catcher.face.Face;

import java.util.List;

/**
 * CreateBy ShuQin
 * at 2020/2/4
 */
public class FaceAdapter extends RecyclerAdapter<Face.Bean> {
    public FaceAdapter(List<Face.Bean> beans,AdapterListener<Face.Bean> listener){
        super(beans,listener);
    }


    @Override
    protected int getItemViewType(int position, Face.Bean bean) {
        return R.layout.cell_face;
    }

    @Override
    protected ViewHolder<Face.Bean> onCreateViewHolder(View root, int viewType) {
        return new FaceHolder(root);
    }
}
