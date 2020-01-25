package com.rye.factory.model.db;



import com.raizlabs.android.dbflow.structure.BaseModel;
import com.rye.factory.utils.DiffUiDataCallback;

/**
 * CreateBy ShuQin
 * at 2020/1/26
 * 本项目所有数据库文件的基类，为了扩展DbFlow中的BaseModel
 */
public abstract class BaseDbModel<Model> extends BaseModel  implements DiffUiDataCallback.UiDataDiffer<Model> {
}
