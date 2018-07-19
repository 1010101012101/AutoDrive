package com.icegps.autodrive.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.icegps.autodrive.R;
import com.icegps.autodrive.ble.BleWriteHelper;
import com.icegps.autodrive.ble.Cmds;

import java.util.List;

/**
 * Created by 111 on 2018/1/17.
 */
//signal
public class SystemSettingAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private Context context;

    public SystemSettingAdapter(int layoutResId, @Nullable List<String> data, Context context) {
        super(layoutResId, data);
        this.context=context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, String item) {
        Drawable drawable=null;
        switch (helper.getPosition()){
            case 0:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_radio);
                break;
            case 1:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_work_width);
                break;
            case 2:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_differential_source);
                break;
            case 3:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_clear_data);
                break;
            case 4:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_about);
                break;
            case 5:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_factory_calibration);
                break;
            case 6:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_work_parameter);
                break;
            case 7:
                drawable= ContextCompat.getDrawable(context,R.mipmap.menu_work_parameter);
                break;

        }

        helper.setText(R.id.tv_menu,item).setImageDrawable(R.id.iv_menu,drawable);
    }
}
