package ch.rmy.android.statusbar_tacho;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class TachoService extends Service implements LocationListener {

	public static void setRunningState(Context context, boolean state) {
		Intent intent = new Intent(context, TachoService.class);
		if (state) {
			context.startService(intent);
		} else {
			context.stopService(intent);
		}
	}

	public static boolean isRunning(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREF, 0);
		return settings.getBoolean(PREF_SERVICE, false);
	}

	public static final String PREF = "pref";
	public static final String PREF_SERVICE = "service";
	public static final String PREF_UNIT = "unit";

	private static final int NOTIFICATION_ID = 1;

	private static final int[] ICONS = { R.drawable.icon000, R.drawable.icon001, R.drawable.icon002, R.drawable.icon003, R.drawable.icon004, R.drawable.icon005,
			R.drawable.icon006, R.drawable.icon007, R.drawable.icon008, R.drawable.icon009, R.drawable.icon010, R.drawable.icon011, R.drawable.icon012, R.drawable.icon013,
			R.drawable.icon014, R.drawable.icon015, R.drawable.icon016, R.drawable.icon017, R.drawable.icon018, R.drawable.icon019, R.drawable.icon020, R.drawable.icon021,
			R.drawable.icon022, R.drawable.icon023, R.drawable.icon024, R.drawable.icon025, R.drawable.icon026, R.drawable.icon027, R.drawable.icon028, R.drawable.icon029,
			R.drawable.icon030, R.drawable.icon031, R.drawable.icon032, R.drawable.icon033, R.drawable.icon034, R.drawable.icon035, R.drawable.icon036, R.drawable.icon037,
			R.drawable.icon038, R.drawable.icon039, R.drawable.icon040, R.drawable.icon041, R.drawable.icon042, R.drawable.icon043, R.drawable.icon044, R.drawable.icon045,
			R.drawable.icon046, R.drawable.icon047, R.drawable.icon048, R.drawable.icon049, R.drawable.icon050, R.drawable.icon051, R.drawable.icon052, R.drawable.icon053,
			R.drawable.icon054, R.drawable.icon055, R.drawable.icon056, R.drawable.icon057, R.drawable.icon058, R.drawable.icon059, R.drawable.icon060, R.drawable.icon061,
			R.drawable.icon062, R.drawable.icon063, R.drawable.icon064, R.drawable.icon065, R.drawable.icon066, R.drawable.icon067, R.drawable.icon068, R.drawable.icon069,
			R.drawable.icon070, R.drawable.icon071, R.drawable.icon072, R.drawable.icon073, R.drawable.icon074, R.drawable.icon075, R.drawable.icon076, R.drawable.icon077,
			R.drawable.icon078, R.drawable.icon079, R.drawable.icon080, R.drawable.icon081, R.drawable.icon082, R.drawable.icon083, R.drawable.icon084, R.drawable.icon085,
			R.drawable.icon086, R.drawable.icon087, R.drawable.icon088, R.drawable.icon089, R.drawable.icon090, R.drawable.icon091, R.drawable.icon092, R.drawable.icon093,
			R.drawable.icon094, R.drawable.icon095, R.drawable.icon096, R.drawable.icon097, R.drawable.icon098, R.drawable.icon099, R.drawable.icon100, R.drawable.icon101,
			R.drawable.icon102, R.drawable.icon103, R.drawable.icon104, R.drawable.icon105, R.drawable.icon106, R.drawable.icon107, R.drawable.icon108, R.drawable.icon109,
			R.drawable.icon110, R.drawable.icon111, R.drawable.icon112, R.drawable.icon113, R.drawable.icon114, R.drawable.icon115, R.drawable.icon116, R.drawable.icon117,
			R.drawable.icon118, R.drawable.icon119, R.drawable.icon120, R.drawable.icon121, R.drawable.icon122, R.drawable.icon123, R.drawable.icon124, R.drawable.icon125,
			R.drawable.icon126, R.drawable.icon127, R.drawable.icon128, R.drawable.icon129, R.drawable.icon130, R.drawable.icon131, R.drawable.icon132, R.drawable.icon133,
			R.drawable.icon134, R.drawable.icon135, R.drawable.icon136, R.drawable.icon137, R.drawable.icon138, R.drawable.icon139, R.drawable.icon140, R.drawable.icon141,
			R.drawable.icon142, R.drawable.icon143, R.drawable.icon144, R.drawable.icon145, R.drawable.icon146, R.drawable.icon147, R.drawable.icon148, R.drawable.icon149,
			R.drawable.icon150, R.drawable.icon151, R.drawable.icon152, R.drawable.icon153, R.drawable.icon154, R.drawable.icon155, R.drawable.icon156, R.drawable.icon157,
			R.drawable.icon158, R.drawable.icon159, R.drawable.icon160, R.drawable.icon161, R.drawable.icon162, R.drawable.icon163, R.drawable.icon164, R.drawable.icon165,
			R.drawable.icon166, R.drawable.icon167, R.drawable.icon168, R.drawable.icon169, R.drawable.icon170, R.drawable.icon171, R.drawable.icon172, R.drawable.icon173,
			R.drawable.icon174, R.drawable.icon175, R.drawable.icon176, R.drawable.icon177, R.drawable.icon178, R.drawable.icon179, R.drawable.icon180, R.drawable.icon181,
			R.drawable.icon182, R.drawable.icon183, R.drawable.icon184, R.drawable.icon185, R.drawable.icon186, R.drawable.icon187, R.drawable.icon188, R.drawable.icon189,
			R.drawable.icon190, R.drawable.icon191, R.drawable.icon192, R.drawable.icon193, R.drawable.icon194, R.drawable.icon195, R.drawable.icon196, R.drawable.icon197,
			R.drawable.icon198, R.drawable.icon199 };

	public static final int[] UNIT_NAMES = { R.string.unit_kmh, R.string.unit_mph, R.string.unit_ms, R.string.unit_fts };

	public static final float[] UNIT_CONVERSIONS = { 3.6f, 2.23694f, 1f, 3.28084f };

	private LocationManager locationManager;

	private NotificationManager notificationManager;
	private Notification.Builder notificationBuilder;

	private int unit = 0;
	private boolean locationProviderEnabled = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences settings = getSharedPreferences(PREF, 0);
		unit = settings.getInt(PREF_UNIT, 0);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setSpeedRequired(true);
		String provider = locationManager.getBestProvider(criteria, false);

		locationProviderEnabled = locationManager.isProviderEnabled(provider);
		locationManager.requestLocationUpdates(provider, 800, 0, this);

		Intent intent = new Intent(this, SettingsActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationBuilder = new Notification.Builder(this);
		CharSequence title = getText(R.string.current_speed);
		CharSequence message = getText(locationProviderEnabled ? R.string.unknown : R.string.gps_disabled);
		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		Notification notification = notificationBuilder.setSmallIcon(R.drawable.icon_unknown).setLargeIcon(largeIcon).setContentTitle(title).setContentText(message).setWhen(0)
				.setContentIntent(pendingIntent).build();

		startForeground(NOTIFICATION_ID, notification);

		Editor editor = settings.edit();
		editor.putBoolean(PREF_SERVICE, true);
		editor.commit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		SharedPreferences settings = getSharedPreferences(PREF, 0);
		Editor editor = settings.edit();
		editor.putBoolean(PREF_SERVICE, false);
		editor.commit();

		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Notification notification;
		if (locationProviderEnabled) {
			CharSequence message;
			int icon;

			if (location == null) {
				icon = R.drawable.icon_unknown;
				message = getText(R.string.unknown);
			} else {
				float conversion = UNIT_CONVERSIONS[unit];
				CharSequence unitName = getText(UNIT_NAMES[unit]);

				float speed = location.getSpeed() * conversion;

				int speedForIcon = (int) speed;
				if (speedForIcon >= ICONS.length) {
					speedForIcon = ICONS.length - 1;
				}
				icon = ICONS[speedForIcon];
				message = String.format("%.1f", speed) + " " + unitName + " (" + location.getAccuracy() + ")";
			}

			notification = notificationBuilder.setSmallIcon(icon).setContentText(message).build();
		} else {
			notification = notificationBuilder.setSmallIcon(R.drawable.icon_unknown).setContentText(getText(R.string.gps_disabled)).build();
		}
		notificationManager.notify(NOTIFICATION_ID, notification);
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
