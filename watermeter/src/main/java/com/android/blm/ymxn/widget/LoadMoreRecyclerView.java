package com.android.blm.ymxn.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.blm.ymxn.R;


public class LoadMoreRecyclerView extends RecyclerView {
    /**
     * item 类型
     */
    public final static int TYPE_HEADER = 1;// 头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more
    public final static int TYPE_LIST = 3;//代表item展示的模式是list模式
    private boolean mIsFooterEnable;//是否允许加载更多
    private boolean usePicture = false;//是否使用图片动画作为进度条
    LoadType loadType = LoadType.AUTO_LOAD;
    /**
     * 自定义实现了头部和底部加载更多的adapter
     */
    private AutoLoadAdapter mAutoLoadAdapter;
    /**
     * 标记是否正在加载更多，防止再次调用加载更多接口
     */
    private boolean mIsLoadingMore;
    /**
     * 标记加载更多的position
     */
    private int mLoadMorePosition;
    /**
     * 加载更多的监听-业务需要实现加载数据
     */
    private LoadMoreListener mListener;
    private TextView loadmore;
    private ProgressBar progressBar;
    private AutoLoadAdapter.FooterViewHolder footerViewHolder;
//    private WhorlView whorlView;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化-添加滚动监听
     * <p/>
     * 回调加载更多方法，前提是
     * <pre>
     *    1、有监听并且支持加载更多：null != mListener && mIsFooterEnable
     *    2、目前没有在加载，正在上拉（dy>0），当前最后一条可见的view是否是当前数据列表的最后一条--及加载更多
     * </pre>
     */
    private void init() {
        super.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (null != mListener && mIsFooterEnable && !mIsLoadingMore && dy >= 0) {
                    int lastVisiblePosition = getLastVisiblePosition();
                    if (lastVisiblePosition + 1 == mAutoLoadAdapter.getItemCount() && loadType == LoadType.AUTO_LOAD) {
                        setLoadingMore(true);
                        mLoadMorePosition = lastVisiblePosition;
                        mListener.onLoadMore();
                    }
                }
            }
        });
    }

    /**
     * 设置加载更多的监听
     *
     * @param listener
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        mListener = listener;
    }

    /**
     * 设置正在加载更多
     *
     * @param loadingMore
     */
    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
        if (progressBar != null)
            progressBar.setVisibility(VISIBLE);
        if (loadmore != null)
            loadmore.setVisibility(GONE);
        if (footerViewHolder != null) {
            footerViewHolder.txtLoadMore.setVisibility(GONE);
        }

    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mAutoLoadAdapter = new AutoLoadAdapter(adapter);
            adapter.registerAdapterDataObserver(new AdapterDataObserver() {
                @Override
                public void onChanged() {
                    mAutoLoadAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    mAutoLoadAdapter.notifyItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                    mAutoLoadAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    mAutoLoadAdapter.notifyItemRangeInserted(positionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    mAutoLoadAdapter.notifyItemRangeRemoved(positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    mAutoLoadAdapter.notifyItemRangeChanged(fromPosition, toPosition, itemCount);
                }
            });
        }
        super.swapAdapter(mAutoLoadAdapter, true);
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private int getLastVisiblePosition() {
//        return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }


    /**
     * 设置将加载方式，是自动加载还是手动加载
     */
    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }

    /**
     * 设置是否支持自动加载更多
     *
     * @param autoLoadMore
     */
    public void setAutoLoadMoreEnable(boolean autoLoadMore) {
        mIsFooterEnable = autoLoadMore;
    }

    /**
     * 通知更多的数据已经加载
     * <p/>
     * 每次加载完成之后添加了Data数据，用notifyItemRemoved来刷新列表展示，
     * 而不是用notifyDataSetChanged来刷新列表
     *
     * @param hasMore
     */
    public void notifyMoreFinish(boolean hasMore) {

        if (loadType == LoadType.AUTO_LOAD) {

            if (isVisiableBottom && !hasMore) {
                if (footerViewHolder != null) {
                    mIsLoadingMore = true;
                    footerViewHolder.txtLoadMore.setVisibility(VISIBLE);
                    loadmore.setVisibility(VISIBLE);
                    progressBar.setVisibility(GONE);
                }

            } else {
                setAutoLoadMoreEnable(hasMore);
                mIsLoadingMore = false;
                getAdapter().notifyItemRemoved(mLoadMorePosition);
            }

        } else {
            getAdapter().notifyItemChanged(mLoadMorePosition + 1);

        }

//        mIsLoadingMore = false;
    }

    public boolean isVisiableBottom;

    public void setVisiableBottom(boolean isVisiableBottom) {
        this.isVisiableBottom = isVisiableBottom;
    }

    public String canLoadNofity = "已经到底啦";

    public void setCanLoadNofity(String canLoadNofity) {
        this.canLoadNofity = canLoadNofity;
        if (footerViewHolder != null) {
            footerViewHolder.txtLoadMore.setText(canLoadNofity);
            footerViewHolder.txtLoadMore.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
        }
        if (loadmore != null)
            loadmore.setText(canLoadNofity);
    }

    /**
     * 加载更多监听
     */
    public interface LoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }

    public boolean mIsHeaderEnable;
    private int mHeaderResId;

    /**
     * 自动加载的适配器
     */
    public class AutoLoadAdapter extends Adapter<ViewHolder> {
        /**
         * 数据adapter
         */
        private Adapter mInternalAdapter;

        public AutoLoadAdapter(Adapter adapter) {
            mInternalAdapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            int footerPosition = getItemCount() - 1;
            int headerPosition = 0;
            if (headerPosition == position && mIsHeaderEnable && mHeaderResId > 0) {
                return TYPE_HEADER;
            }
            if (footerPosition == position && mIsFooterEnable) {
                return TYPE_FOOTER;
            } else {
                return TYPE_LIST;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == TYPE_HEADER) {
                return new HeaderViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(mHeaderResId, parent, false));
            }
            if (viewType == TYPE_FOOTER) {
                if (usePicture) {
                    return new FooterViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.xlistview_footer, parent, false));
                } else if (loadType == LoadType.AUTO_LOAD) {
                    footerViewHolder = new FooterViewHolder(
                            LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.xlistview_footer, parent, false));
                    return footerViewHolder;
                } else {
                    return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.xlistview_footer, parent, false));
                }
            } else {
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }


        }

        public void setHeaderEnable(boolean enable) {
            mIsHeaderEnable = enable;
        }

        public void addHeaderView(int resId) {
            mHeaderResId = resId;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type != TYPE_FOOTER && type != TYPE_HEADER) {
                mInternalAdapter.onBindViewHolder(holder, position);
            }
        }

        /**
         * 需要计算上加载更多
         *
         * @return
         */
        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (mIsFooterEnable) count++;
            if (mIsHeaderEnable) count++;
            return count;
        }

        public class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class FooterViewHolder extends ViewHolder {
            private TextView txtLoadMore;

            public FooterViewHolder(View itemView) {
                super(itemView);
                if (usePicture) {
                    //使用图片动画
                    ImageView image = (ImageView) itemView.findViewById(R.id.image);
//                    image.setBackgroundResource(R.drawable.anim_list);
                    AnimationDrawable animationDrawable = (AnimationDrawable) image.getBackground();
                    animationDrawable.start();
                } else {
                    //不是用图片动画
                    if (loadType == LoadType.AUTO_LOAD) {
                        loadmore = (TextView) itemView.findViewById(R.id.xlistview_footer_hint_textview);
                        loadmore.setVisibility(INVISIBLE);
                        txtLoadMore = (TextView) itemView.findViewById(R.id.xlistview_footer_hint_textview);
                        txtLoadMore.setVisibility(GONE);
                        progressBar = (ProgressBar) itemView.findViewById(R.id.xlistview_footer_progressbar);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        loadmore = (TextView) itemView.findViewById(R.id.xlistview_footer_hint_textview);
//
                        loadmore.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!mIsLoadingMore) {
                                    setLoadingMore(true);
//                                    whorlView.setVisibility(VISIBLE);
                                    loadmore.setVisibility(GONE);
                                    mListener.onLoadMore();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 添加头部view
     *
     * @param resId
     */
    public void addHeaderView(int resId) {
        mAutoLoadAdapter.addHeaderView(resId);
    }

    /**
     * 设置头部view是否展示 该属性需要在setAdapter之后进行设置,不然会有空指针错误
     *
     * @param enable
     */
    public void setHeaderEnable(boolean enable) {
        mAutoLoadAdapter.setHeaderEnable(enable);
    }

    /**
     * 处理回调，使loadmore显示和whorlView隐藏
     */
    public void handleCallback() {
//        whorlView.setVisibility(GONE);
        loadmore.setVisibility(VISIBLE);
    }

    public void removeLoadming() {
    }

    public void notifyRemove(int pos) {
        getAdapter().notifyItemRemoved(pos);
    }

    public void noitifyInsert(int pos) {
        getAdapter().notifyItemInserted(pos);
    }

    public void notifyChange(int pos) {
        getAdapter().notifyItemChanged(pos);
    }

    public void setUsePicture(boolean usePicture) {
        this.usePicture = usePicture;
    }
}
