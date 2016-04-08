package com.gcloud.gaadi.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcloud.gaadi.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatListAdapter extends BaseAdapter {

    private View view = null;
    private LayoutInflater mInflater;
    private Context mContext;
    private ViewHolder holder;
    //	private ChatList mChatList;
    private ArrayList<ChatListItem> mChatListItems;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    private int mMaxId = 0;
    private SimpleDateFormat sdf;

    public ChatListAdapter(Context mContext, ArrayList<ChatListItem> list) {
        try {
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mContext = mContext;
//			mChatList = list;
            mChatListItems = list;
            sdf = new SimpleDateFormat("h:mm aa d MMM", Locale.US);

            options = getDisplayOption();
            imageLoader = ImageLoader.getInstance();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    mContext).threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .diskCacheSize(50 * 1024 * 1024)
                            // 50 Mb
                    .tasksProcessingOrder(QueueProcessingType.LIFO).build();
            // Initialize ImageLoader with configuration.
            //imageLoader.destroy();
            imageLoader.init(config);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_img)
                .cacheInMemory(true).cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.default_img)
                .showImageOnFail(R.drawable.default_img)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .build();

        return options;
    }

    public int getmMaxId() {
        return mMaxId;
    }

    public void setmMaxId(int id) {
        mMaxId = id;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mChatListItems.size();
    }

    @Override
    public ChatListItem getItem(int position) {
        // TODO Auto-generated method stub
        return mChatListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
//	public void removeItem(String id){
////		mChatListItems.r
//	}

    public void setDataInList(ArrayList<ChatListItem> items) {
        mChatListItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        view = convertView;
        //Log.e("getView ", "getView");

        if (convertView == null) {
            holder = new ViewHolder();

            view = mInflater.inflate(R.layout.chat_list_item, null,
                    false);
            holder.textViewChatItemHeading = (TextView) view
                    .findViewById(R.id.chatlist_heading);
            holder.textViewChatText = (TextView) view
                    .findViewById(R.id.chatlist_updated_text);

            holder.txtviewChatCount = (TextView) view
                    .findViewById(R.id.chatlist_updated_count);

            holder.txtViewChatTime = (TextView) view
                    .findViewById(R.id.chatlist_time);

            holder.imageViewCar = (ImageView) view
                    .findViewById(R.id.chatlist_car_image);

            holder.txtViewName = (TextView) view
                    .findViewById(R.id.chatlist_Name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (mChatListItems.get(position).getChat_with().equals("dealer")) {
            DealerChatListItem mItem = (DealerChatListItem) mChatListItems.get(position);
            holder.textViewChatItemHeading.setText(mItem.getVariantId());
            holder.textViewChatText.setText(mItem.getLastchatmsg()/*"i will go to wonderland hurray or awwwwwww"*/);
            if (mItem.getTimestamp() != null)
                holder.txtViewChatTime.setText(/*mItem.getLastchattime()*/sdf.format(new Date(Long.parseLong(mItem.getTimestamp()))));
            else
                holder.txtViewChatTime.setText(mItem.getLastchattime());

            holder.txtViewName.setText(mItem.getName()/*"10:00 pm"*/);
            if (mChatListItems.get(position).getid() != null) {
                if (mMaxId < Integer.parseInt(mChatListItems.get(position).getid())) {
                    mMaxId = Integer.parseInt(mChatListItems.get(position).getid());
                }
            }
            holder.txtviewChatCount.setVisibility(View.GONE);
            holder.textViewChatText.setTextColor(Color.BLACK);
            holder.textViewChatText.setTypeface(Typeface.DEFAULT);
            if (mItem.getUnreadmsgcount() != null && !mItem.getUnreadmsgcount().isEmpty() && Integer.parseInt(mItem.getUnreadmsgcount()) > 0 && mItem.getLastchatmsg().length() != 0) {
                holder.txtviewChatCount.setVisibility(View.VISIBLE);
                holder.txtviewChatCount.setText(mItem.getUnreadmsgcount());
                //holder.textViewChatText.setTextColor(Color.GREEN);
                holder.textViewChatText.setTypeface(Typeface.DEFAULT_BOLD);
            }
            imageLoader.displayImage(mItem.getImageurl(), holder.imageViewCar, options);
            Log.e("setDataInList ", "mItem.getName() " + mItem.getName());

        } else if (mChatListItems.get(position).getChat_with().equals("suppportuser")) {
            ExpertChatListItem mItem = (ExpertChatListItem) mChatListItems.get(position);
            holder.textViewChatItemHeading.setText(mItem.getSupportroomname());
            holder.textViewChatText.setText(mItem.getLastchatmsg());
            holder.txtViewChatTime.setText(mItem.getLastchattime());
            if (mItem.getUnreadmsgcount() != null && !mItem.getUnreadmsgcount().isEmpty() && Integer.parseInt(mItem.getUnreadmsgcount()) > 0) {
                holder.txtviewChatCount.setVisibility(View.VISIBLE);
                holder.txtviewChatCount.setText(mItem.getUnreadmsgcount());
            }
        }
        return view;
    }

    public ChatListItem getListItem(int position) {
        return mChatListItems.get(position);
    }

    public class ViewHolder {
        private TextView textViewChatItemHeading, textViewChatText, txtViewChatTime, txtViewName,
                txtviewChatCount;
        private ImageView imageViewCar;

    }
}
