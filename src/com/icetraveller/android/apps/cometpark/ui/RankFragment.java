package com.icetraveller.android.apps.cometpark.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.icetraveller.android.apps.cometpark.R;
import com.icetraveller.android.apps.cometpark.utils.MapUtils;
import com.icetraveller.android.apps.cometpark.utils.UIUtils;

import static com.icetraveller.android.apps.cometpark.utils.LogUtils.*;

public class RankFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	ArrayAdapter<String> adapter;
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
		String[] values = new String[] { "Lot C", "Lot T", "Lot B", "Lot V",
				"Lot A", "Lot S", "Lot D", "Lot F" };
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		setListAdapter(adapter);

		// Override default ListView empty-view handling
		listView.setEmptyView(null);
		mEmptyView.setVisibility(View.GONE);
		// adapter.registerDataSetObserver(new DataSetObserver() {
		// @Override
		// public void onChanged() {
		// if (adapter.getCount() > 0) {
		// mEmptyView.setVisibility(View.GONE);
		// adapter.unregisterDataSetObserver(this);
		// }
		// }
		// });
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
				int viewId = view.getId();
				Intent intent = new Intent(context, UIUtils.getMapActivityClass(getActivity()));
//				intent.putExtra(MapUtils.SHOW_LOT, viewId);
				intent.putExtra(MapUtils.SHOW_LOT, "0");
				startActivity(intent);
			}
		});

		listView.addHeaderView(mapHeaderContainerView);
		listView.setHeaderDividersEnabled(false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
