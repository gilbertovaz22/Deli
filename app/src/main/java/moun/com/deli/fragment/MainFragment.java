package moun.com.deli.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;
import moun.com.deli.HotDealsActivity;
import moun.com.deli.MenuActivityWithTabs;
import moun.com.deli.R;
import moun.com.deli.adapter.HomeMenuCustomAdapter;
import moun.com.deli.model.MenuItems;
import moun.com.deli.util.AppUtils;

/**
 * Created by Mounzer on 12/1/2015.
 */
public class MainFragment extends Fragment implements HomeMenuCustomAdapter.ClickListener{

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private HomeMenuCustomAdapter homeMenuCustomAdapter;
    ArrayList<MenuItems> rowListItem;
    private boolean mLinearShown;
    LayoutInflater inflater;
    private TextView startOrder;
    private AlphaInAnimationAdapter alphaAdapter;
    View header;
    private TextView hotDealheaderText;
    private OnItemSelectedListener listener;



    public interface OnItemSelectedListener {
        public void onItemSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemSelectedListener");
        }
    }


    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        rowListItem = getMenuList();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.inflater = inflater;

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
    //    mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;

        startOrder = (TextView) rootView.findViewById(R.id.start_order_text);
        startOrder.setTypeface(AppUtils.getTypeface(getActivity(), AppUtils.FONT_BOLD));

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        // Inflate the layout header
        header = LayoutInflater.from(getActivity()).inflate(R.layout.home_menu_header, mRecyclerView, false);
        hotDealheaderText = (TextView) header.findViewById(R.id.hot_deal_header_title);
        hotDealheaderText.setTypeface(AppUtils.getTypeface(getActivity(), AppUtils.FONT_BOLD));
        header.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HotDealsActivity.class);
                startActivity(intent);
            }
        });

        homeMenuCustomAdapter = new HomeMenuCustomAdapter(getActivity(), header, rowListItem, inflater, R.layout.grid_layout_row);
        alphaAdapter = new AlphaInAnimationAdapter(homeMenuCustomAdapter);
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        homeMenuCustomAdapter.setClickListener(this);




        return rootView;

    }

    @Override
    public void itemClicked(View view, int position) {
        if(position == 0){
            Intent intent = new Intent(getActivity(), HotDealsActivity.class);
            startActivity(intent);
        } else {
            listener.onItemSelected(position);


        }

    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_transition, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // We change the look of the icon every time the user toggles between list and grid.
        switch (item.getItemId()) {
            case R.id.action_toggle: {
                mLinearShown = !mLinearShown;
                if (mLinearShown) {
                    setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
                    homeMenuCustomAdapter = new HomeMenuCustomAdapter(getActivity(), header, rowListItem, inflater, R.layout.linear_layout_row);
                    alphaAdapter = new AlphaInAnimationAdapter(homeMenuCustomAdapter);
                    mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
                    homeMenuCustomAdapter.setClickListener(this);
                    item.setIcon(R.mipmap.ic_grid_on_white_24dp);

                } else {
                    setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
                    homeMenuCustomAdapter = new HomeMenuCustomAdapter(getActivity(), header, rowListItem, inflater, R.layout.grid_layout_row);
                    alphaAdapter = new AlphaInAnimationAdapter(homeMenuCustomAdapter);
                    mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));
                    homeMenuCustomAdapter.setClickListener(this);
                    item.setIcon(R.mipmap.ic_view_list_white_24dp);

                }

                return true;
            }
        }
        return false;
    }



    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                final GridLayoutManager gridManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mLayoutManager = gridManager;
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                // Override setSpanSizeLookup in GridLayoutManager to return the span count as the span size for the header
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return homeMenuCustomAdapter.isHeader(position) ? gridManager.getSpanCount() : 1;
                    }
                });
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private ArrayList<MenuItems> getMenuList(){

        ArrayList<MenuItems> menuItems = new ArrayList<MenuItems>();
        menuItems.add(new MenuItems(getString(R.string.sandwich), R.drawable.items2));
        menuItems.add(new MenuItems(getString(R.string.burgers), R.drawable.items3));
        menuItems.add(new MenuItems(getString(R.string.pizza), R.drawable.items4));
        menuItems.add(new MenuItems(getString(R.string.salads), R.drawable.items5));
        menuItems.add(new MenuItems(getString(R.string.sweets), R.drawable.items7));
        menuItems.add(new MenuItems(getString(R.string.drinks), R.drawable.items6));


        return menuItems;
    }
}
