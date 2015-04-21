package ch.rmy.android.statusbar_tacho;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Main activitiy. Allows to toggle the service, select the unit and to preview the current speed.
 * 
 * @author Roland Meyer
 * 
 */
public class SettingsActivity extends Activity implements OnCheckedChangeListener, OnItemSelectedListener, LocationListener {

	private TextView speedView;
	private ToggleButton toggleButton;
	private Spinner spinner;

	private boolean locationProviderEnabled;

	private int unit = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		TachoService.setRunningState(this, TachoService.isRunning(this));

		toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
		toggleButton.setOnCheckedChangeListener(this);

		spinner = (Spinner) findViewById(R.id.unitSpinner);

		List<CharSequence> list = new ArrayList<CharSequence>();
		for (int stringId : TachoService.UNIT_NAMES) {
			list.add(getText(stringId));
		}
		ArrayAdapter<CharSequence> dataAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setOnItemSelectedListener(this);

		speedView = (TextView) findViewById(R.id.speed);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (TachoService.isRunning(this)) {
			registerSpeedListener();
			toggleButton.setChecked(true);
		} else {
			speedView.setText("-");
			toggleButton.setChecked(false);
		}

		spinner.setSelection(getUnitId());
	}

	private void registerSpeedListener() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setSpeedRequired(true);
		String provider = locationManager.getBestProvider(criteria, false);

		locationProviderEnabled = locationManager.isProviderEnabled(provider);
		locationManager.requestLocationUpdates(provider, 800, 0, this);
		onLocationChanged(null);
	}

	private void unregisterSpeedListener() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeUpdates(this);
		speedView.setText("-");
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterSpeedListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		TachoService.setRunningState(this, isChecked);
		if (isChecked) {
			registerSpeedListener();
		} else {
			unregisterSpeedListener();
		}
	}

	private int getUnitId() {
		SharedPreferences settings = getSharedPreferences(TachoService.PREF, 0);
		return settings.getInt(TachoService.PREF_UNIT, 0);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		unit = position;

		SharedPreferences settings = getSharedPreferences(TachoService.PREF, 0);
		Editor editor = settings.edit();
		editor.putInt(TachoService.PREF_UNIT, position);
		editor.commit();

		if (TachoService.isRunning(this)) {
			TachoService.setRunningState(this, false);
			TachoService.setRunningState(this, true);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onLocationChanged(Location location) {
		if (locationProviderEnabled) {
			if (location == null) {
				speedView.setText("-");
			} else {
				float speed = location.getSpeed() * TachoService.UNIT_CONVERSIONS[unit];
				String speedString = String.format("%.1f", speed);
				speedView.setText(speedString);
			}
		} else {
			speedView.setText(R.string.gps_disabled);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
		locationProviderEnabled = true;
		onLocationChanged(null);
	}

	@Override
	public void onProviderDisabled(String provider) {
		locationProviderEnabled = false;
		onLocationChanged(null);
	}
}
