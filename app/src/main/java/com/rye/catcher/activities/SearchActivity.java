package com.rye.catcher.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rye.catcher.R;
import com.rye.catcher.common.app.BaseActivity;
import com.rye.catcher.common.app.ToolbarActivity;
import com.rye.catcher.frags.search.SearchGroupFragment;
import com.rye.catcher.frags.search.SearchUserFragment;

public class SearchActivity extends ToolbarActivity {
    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1;
    public static final int TYPE_GROUP = 2;
    private int type;
    private SearchFragment mSearchFragment;

    public static void show(Context context, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt(EXTRA_TYPE);
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        // TODO: 2020/1/19 需要修改，这样每次都New一个没必要
        Fragment fragment;
        if (type==TYPE_USER){
            SearchUserFragment searchUserFragment=new SearchUserFragment();
            fragment=searchUserFragment;
            mSearchFragment=searchUserFragment;
        }else{
            SearchGroupFragment searchGroupFragment=new SearchGroupFragment();
            fragment=searchGroupFragment;
            mSearchFragment=searchGroupFragment;
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container,fragment)
                .commit();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search,menu);
        MenuItem searchItem=menu.findItem(R.id.action_search);
        // TODO: 2020/1/23 待了解SearchView 
        SearchView searchView=(SearchView)searchItem.getActionView();
        if (searchView!=null){
            SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            //添加搜索监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                //当点击了提交按钮的时候
                @Override
                public boolean onQueryTextSubmit(String query) {
                    search(query);
                    return true;
                }
                //当文字改变的时候，不及时搜索，只有为null的情况下搜索
                @Override
                public boolean onQueryTextChange(String newText) {
                    // TODO: 2020/1/24 加个本地查询
                    if (TextUtils.isEmpty(newText)){
                        search("");
                        return  true;
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 搜索的发起点
     * @param query
     */
    private void search(String query){
        if (mSearchFragment==null){
            return;
        }
        mSearchFragment.search(query);
    }

    /**
     * 搜索的Fragment必须实现的接口
     */
    public interface SearchFragment{
        void search(String content);
    }

}

