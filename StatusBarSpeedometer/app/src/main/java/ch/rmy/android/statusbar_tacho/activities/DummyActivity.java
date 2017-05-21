package ch.rmy.android.statusbar_tacho.activities;

import android.app.Activity;
import android.os.Bundle;

import ch.rmy.android.statusbar_tacho.services.TachoService;

/**
 * Receives broadcast intents from shortcuts to enable/disable/toggle the main service
 * 
 * @author Roland Meyer
 * 
 */
public class DummyActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String action = getIntent().getAction();

		if (ShortcutActivity.SHORTCUT_ENABLE.equals(action)) {
			TachoService.setRunningState(this, true);
		} else if (ShortcutActivity.SHORTCUT_DISABLE.equals(action)) {
			TachoService.setRunningState(this, false);
		} else if (ShortcutActivity.SHORTCUT_TOGGLE.equals(action)) {
			if (TachoService.isRunning(this)) {
				TachoService.setRunningState(this, false);
			} else {
				TachoService.setRunningState(this, true);
			}
		}

		finish();
	}

}
