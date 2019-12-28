package com.rye.catcher.frags.main;


import android.app.Fragment;

import com.rye.catcher.R;
import com.rye.catcher.common.app.BaseFragment;
import com.rye.catcher.common.widget.GalleryView;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends BaseFragment {
    @BindView(R.id.galleyView)
    GalleryView mGally;

    public ActiveFragment() {
        // Required empty public constructor
    }
    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();
//        mGally.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
//            @Override
//            public void onSelectedCountChanged(int count) {
//
//            }
//        });
    }
}
