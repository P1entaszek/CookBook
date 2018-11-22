package com.jaszczurowskip.cookbook.features.mealdetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jaszczurowskip.cookbook.R;
import com.jaszczurowskip.cookbook.datasource.model.IngredientApiModel;

import java.util.List;

/**
 * Created by jaszczurowskip on 16.11.2018
 */
public class IngredientsDetailsGridAdapter extends BaseAdapter {
    private Context mContext;
    private final List<IngredientApiModel> list;

    public IngredientsDetailsGridAdapter(Context mContext, List<IngredientApiModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            grid = new View(mContext);
            grid = layoutInflater.inflate(R.layout.item_gridview_list, null);
            TextView txtview = grid.findViewById(R.id.ingredient);
            txtview.setText(list.get(position).getName());
        }
        else{
            grid = convertView;
        }

        return grid;
    }
}
