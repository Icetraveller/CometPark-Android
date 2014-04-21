package com.icetraveller.android.apps.cometpark.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.provider.CometParkContract;
import com.icetraveller.android.apps.cometpark.utils.MapUtils;
import com.icetraveller.android.apps.cometpark.utils.UIUtils;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class RankFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private RankAdapter mAdapter;
	private View mEmptyView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_list_with_empty_container_inset, container,
				false);
		mEmptyView = rootView.findViewById(android.R.id.empty);
		inflater.inflate(R.layout.empty_waiting_for_sync,
				(ViewGroup) mEmptyView, true);
		return rootView;
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setBackgroundColor(Color.WHITE);

		final ListView listView = getListView();
		listView.setSelector(android.R.color.transparent);
		listView.setCacheColorHint(Color.WHITE);
		addMapHeaderView(); // yw, Add map

		mAdapter = new RankAdapter(getActivity(), false);
		setListAdapter(mAdapter);
		listView.setEmptyView(null);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String viewId = (String) view.getTag();
				Intent intent = new Intent(getActivity(), UIUtils
						.getMapActivityClass(getActivity()));
				intent.putExtra(MapUtils.SHOW_LOT, viewId);
				startActivity(intent);
			}
		});
		mEmptyView.setVisibility(View.VISIBLE);
		mAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				if (mAdapter.getCount() > 0) {
					mEmptyView.setVisibility(View.GONE);
					mAdapter.unregisterDataSetObserver(this);
				}
			}
		});
	}

	private void addMapHeaderView() {
		ListView listView = getListView();
		final Context context = listView.getContext();
		View mapHeaderContainerView = LayoutInflater.from(context).inflate(
				R.layout.list_item_track_map, listView, false);
		View mapButton = mapHeaderContainerView.findViewById(R.id.map_button);
		mapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), UIUtils
						.getMapActivityClass(context));
				intent.putExtra(MapUtils.SHOW_LOT, MapFragment.SHOW_ALL);
				startActivity(intent);
			}
		});

		listView.addHeaderView(mapHeaderContainerView);
		listView.setHeaderDividersEnabled(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(RankAdapter.LotsStatusQuery._TOKEN, null,
				this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri uri = CometParkContract.Lots.buildLotsLotStatus();
		return new CursorLoader(getActivity(), uri,
				RankAdapter.LotsStatusQuery.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}
		mAdapter.swapCursor(cursor);
		if (cursor.getCount() > 0) {
			mEmptyView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
