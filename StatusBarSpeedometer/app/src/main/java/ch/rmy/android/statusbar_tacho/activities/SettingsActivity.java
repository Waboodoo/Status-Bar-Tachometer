package ch.rmy.android.statusbar_tacho.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ch.rmy.android.statusbar_tacho.R;
import ch.rmy.android.statusbar_tacho.services.TachoService;
import ch.rmy.android.statusbar_tacho.units.Unit;
import ch.rmy.android.statusbar_tacho.units.Units;
import ch.rmy.android.statusbar_tacho.utils.Links;
import ch.rmy.android.statusbar_tacho.utils.Settings;
import ch.rmy.android.statusbar_tacho.utils.SimpleItemSelectedListener;

public class SettingsActivity extends BaseActivity {

    @Bind(R.id.speed)
    TextView speedView;
    @Bind(R.id.toggleButton)
    ToggleButton toggleButton;
    @Bind(R.id.unitSpinner)
    Spinner spinner;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settings = new Settings(getContext());

        TachoService.setRunningState(this, TachoService.isRunning(this));

        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TachoService.setRunningState(getContext(), isChecked);
            }
        });

        List<CharSequence> list = new ArrayList<>();
        for (Unit unit : Units.UNITS) {
            list.add(getText(unit.getNameRes()));
        }
        ArrayAdapter<CharSequence> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setUnit(Units.UNITS[position]);

                if (TachoService.isRunning(getContext())) {
                    TachoService.setRunningState(getContext(), false);
                    TachoService.setRunningState(getContext(), true);
                }
            }
        });
    }

    @Override
    int getNavigateUpIcon() {
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_github: {
                Links.openGithub(getContext());
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
