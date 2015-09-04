package com.jp.ponto;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class MarcacoesAdapter extends ArrayAdapter<Marcacoes> {

    Context context;
    LayoutInflater inflater;
    ArrayList<Marcacoes> list;
    private SparseBooleanArray mSelectedItemsIds;

    public MarcacoesAdapter(Context context, ArrayList<Marcacoes> m ) {
        super(context, 0, m);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.list = m;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_marcacao, null);
            holder.data = (TextView) convertView.findViewById(R.id.textData);
            holder.hora = (TextView) convertView.findViewById(R.id.textMarcacao);
            holder.id = (TextView) convertView.findViewById(R.id.textID);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.data.setText(list.get(position).getData());
        holder.hora.setText(list.get(position).getHora());
        holder.id.setText(String.valueOf(list.get(position).getId()));
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        cb.setChecked(mSelectedItemsIds.get(position));
        return convertView;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
    private class ViewHolder {
        TextView data;
        TextView hora;
        TextView id;
    }

}
