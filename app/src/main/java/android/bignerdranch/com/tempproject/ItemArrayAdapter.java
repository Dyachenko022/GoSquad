package android.bignerdranch.com.tempproject;

import android.bignerdranch.com.tempproject.Objects.Cards;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemArrayAdapter extends android.widget.ArrayAdapter<Cards> {
    Context mContext;

    public ItemArrayAdapter(Context context, int resourceId, List<Cards> items)
    {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Cards cardItem = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.swipe_card_name);
        ImageView image = (ImageView) convertView.findViewById(R.id.swipe_card_image);

        name.setText(cardItem.getName());
        if(!cardItem.getImageUrl().equals("default")) {
            Glide.with(getContext()).load(cardItem.getImageUrl()).error(R.mipmap.ic_launcher).into(image);
        }
        else
        {
            image.setImageResource(R.mipmap.ic_launcher);
        }
    return convertView;
    }
}
