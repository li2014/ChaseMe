package org.chaseme.fragments.mission;

import org.chaseme.drone.variables.mission.waypoints.Land;
import org.chaseme.widgets.SeekBarWithText.SeekBarWithText.OnTextSeekBarChangedListener;

import android.view.View;

import org.chaseme.R;

public class MissionLandFragment extends MissionDetailFragment implements
		OnTextSeekBarChangedListener {

	//private SeekBarWithText yawSeekBar;

	@Override
	protected int getResource() {
		return R.layout.fragment_editor_detail_land;
	}

	@Override
	protected void setupViews(View view) {
		super.setupViews(view);
		Land item = (Land) this.item;
		typeSpinner.setSelection(commandAdapter.getPosition(MissionItemTypes.LAND));
		//yawSeekBar = (SeekBarWithText) view.findViewById(R.id.waypointAngle);
		//yawSeekBar.setValue(item.getYawAngle());
		//yawSeekBar.setOnChangedListener(this);
	}

	@Override
	public void onSeekBarChanged() {
		Land item = (Land) this.item;
		//item.setYawAngle((float) yawSeekBar.getValue());
	}

}
