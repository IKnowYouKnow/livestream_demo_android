package cn.ucai.live.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.LiveHelper;
import cn.ucai.live.data.model.Gift;

/**
 * Created by wei on 2016/7/25.
 */
public class GiftListDialog extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.rv_gift)
    RecyclerView mRvGift;
    @BindView(R.id.tv_my_bill)
    TextView mTvMyBill;
    @BindView(R.id.tv_recharge)
    TextView mTvRecharge;

    GiftAdapter mGiftAdapter;
    List<Gift> mGiftList;
    GridLayoutManager gm;

    private static final String TAG = "GiftListDialog";

    public static GiftListDialog newInstance() {
        GiftListDialog dialog = new GiftListDialog();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_gift_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        gm = new GridLayoutManager(getContext(), I.GIFT_COLUMN_COUNT);
        mRvGift.setLayoutManager(gm);
        mRvGift.setHasFixedSize(true);
        customDialog();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mGiftList = LiveHelper.getInstance().getGiftList();
        if (mGiftList.size() > 0) {
            if (mGiftAdapter == null) {
                mGiftAdapter = new GiftAdapter(getContext(), mGiftList);
                mRvGift.setAdapter(mGiftAdapter);
            } else {
                mGiftAdapter.notifyDataSetChanged();
            }
        }
    }

    private void customDialog() {
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }

    public View.OnClickListener eventListener;

    public void setGiftEventListener(View.OnClickListener eventListener) {
        this.eventListener = eventListener;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.GiftViewHolder> {
        Context mContext;

        List<Gift> mList;

        public GiftAdapter(Context context, List<Gift> list) {
            mContext = context;
            mList = list;
        }

        @Override
        public GiftAdapter.GiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            GiftViewHolder holder = new GiftViewHolder(View.inflate(mContext, R.layout.item_gift, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(GiftAdapter.GiftViewHolder holder, int position) {
            holder.bind(mList.get(position));
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }

        class GiftViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ivGiftThumb)
            ImageView mIvGiftThumb;
            @BindView(R.id.tvGiftName)
            TextView mTvGiftName;
            @BindView(R.id.tvGiftPrice)
            TextView mTvGiftPrice;
            @BindView(R.id.layout_gift)
            LinearLayout mLayoutGift;

            GiftViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bind(Gift gift) {
                EaseUserUtils.setUserAvatarPath(mContext, gift.getGurl(), mIvGiftThumb);
                mTvGiftName.setText(gift.getGname());
                mTvGiftPrice.setText(String.valueOf(gift.getGprice()));
                mLayoutGift.setTag(gift.getId());
                mLayoutGift.setOnClickListener(eventListener);
            }
        }
    }
}