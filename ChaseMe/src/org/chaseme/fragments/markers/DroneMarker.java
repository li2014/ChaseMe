package org.chaseme.fragments.markers;

import org.chaseme.R;
import org.chaseme.drone.Drone;
import org.chaseme.drone.DroneInterfaces.DroneEventsType;
import org.chaseme.drone.DroneInterfaces.OnDroneListener;
import org.chaseme.fragments.FlightMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Drone marker class.
 */
public class DroneMarker implements OnDroneListener {

	private Marker droneMarker;
	private FlightMapFragment flightMapFragment;
	private Drone drone;

	/**
	 * Constructor that takes in FlightMapFragment.
	 * @param flightMapFragment FlightMapFragment variable.
	 */
	public DroneMarker(FlightMapFragment flightMapFragment) {
		this.flightMapFragment = flightMapFragment;
		this.drone = flightMapFragment.drone;
		addMarkerToMap();
		drone.events.addDroneListener(this);
	}

	/**
	 * Update position.
	 * @param yaw Float variable.
	 * @param coord LatLng variable.
	 */
	private void updatePosition(float yaw, LatLng coord) {
			droneMarker.setPosition(coord);
			droneMarker.setRotation(yaw);
			droneMarker.setVisible(true);
	}

	/** 
	 * Add Marker to Map.
	 */
	private void addMarkerToMap() {
		droneMarker = flightMapFragment.mMap.addMarker(new MarkerOptions()
				.anchor((float) 0.5, (float) 0.5).position(new LatLng(0, 0))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.quad)).visible(false)
				.flat(true));
	}

	/* (non-Javadoc)
	 * @see org.chaseme.drone.DroneInterfaces.OnDroneListener#onDroneEvent(org.chaseme.drone.DroneInterfaces.DroneEventsType, org.chaseme.drone.Drone)
	 */
	@Override
	public void onDroneEvent(DroneEventsType event, Drone drone) {
		switch (event) {
		case GPS:
			updatePosition((float)flightMapFragment.drone.orientation.getYaw(),
					flightMapFragment.drone.GPS.getPosition());
			break;
		default:
			break;
		}

	}
}
