package org.chaseme.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.chaseme.ChaseMeApp;
import org.chaseme.adapters.ParamsAdapter;
import org.chaseme.adapters.ParamsAdapterItem;
import org.chaseme.dialogs.openfile.OpenFileDialog;
import org.chaseme.dialogs.openfile.OpenParameterDialog;
import org.chaseme.dialogs.parameters.DialogParameterInfo;
import org.chaseme.drone.Drone;
import org.chaseme.drone.DroneInterfaces;
import org.chaseme.drone.DroneInterfaces.DroneEventsType;
import org.chaseme.drone.DroneInterfaces.OnDroneListener;
import org.chaseme.file.IO.ParameterWriter;
import org.chaseme.parameters.Parameter;
import org.chaseme.parameters.ParameterMetadata;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.chaseme.R;

public class ParamsFragment extends ListFragment implements DroneInterfaces
        .OnParameterManagerListener, OnDroneListener{

    public static final String ADAPTER_ITEMS = ParamsFragment.class.getName() + ".adapter.items";

    private ProgressDialog pd;

    private Drone drone;
    private ParamsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create adapter
        if(savedInstanceState != null) {
            // load adapter items
            final ArrayList<ParamsAdapterItem> pwms =
                    (ArrayList<ParamsAdapterItem>) savedInstanceState.getSerializable(ADAPTER_ITEMS);
            adapter = new ParamsAdapter(getActivity(), R.layout.row_params, pwms);

        } else {
            // empty adapter
            adapter = new ParamsAdapter(getActivity(), R.layout.row_params);
        }
        setListAdapter(adapter);

        // help handler
        adapter.setOnInfoListener(new ParamsAdapter.OnInfoListener() {
            @Override
            public void onHelp(int position, EditText valueView) {
                showInfo(position, valueView);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // bind & initialize UI
        final View view = inflater.inflate(R.layout.fragment_params, container, false);

        // listen for clicks on empty
        view.findViewById(android.R.id.empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshParameters();
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
	public void onStart() {
		super.onStart();
		drone = ((ChaseMeApp) getActivity().getApplication()).drone;
		drone.events.addDroneListener(this);
		drone.parameters.parameterListener = this;
	}

	@Override
	public void onStop() {
		super.onStop();
		drone.events.removeDroneListener(this);
		drone.parameters.parameterListener = null;
	}

	@Override
	public void onDroneEvent(DroneEventsType event, Drone drone) {
		if (event == DroneEventsType.TYPE) {
	        adapter.loadMetadata(drone);
		}
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save adapter items
        final ArrayList<ParamsAdapterItem> pwms = new ArrayList<ParamsAdapterItem>();
        for(int i = 0; i < adapter.getCount(); i++)
            pwms.add(adapter.getItem(i));
        outState.putSerializable(ADAPTER_ITEMS, pwms);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_parameters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        adapter.clearFocus();

        switch (item.getItemId()) {
            case R.id.menu_load_parameters:
                refreshParameters();
                break;

            case R.id.menu_write_parameters:
                writeModifiedParametersToDrone();
                break;

            case R.id.menu_open_parameters:
                openParametersFromFile();
                break;

            case R.id.menu_save_parameters:
                saveParametersToFile();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showInfo(int position, EditText valueView) {
        final ParamsAdapterItem item = adapter.getItem(position);
        final ParameterMetadata metadata = item.getMetadata();
        if(metadata == null || !metadata.hasInfo())
            return;

        DialogParameterInfo.build(item, valueView, getActivity()).show();
    }

    private void refreshParameters() {
        if (drone.MavClient.isConnected()) {
            drone.parameters.getAllParameters();
        } else {
            Toast.makeText(getActivity(), R.string.msg_connect_first, Toast.LENGTH_SHORT).show();
        }
    }

    private void writeModifiedParametersToDrone() {
        int written = 0;
        for(int i = 0; i < adapter.getCount(); i++) {
            final ParamsAdapterItem item = adapter.getItem(i);
            if(!item.isDirty())
                continue;

            drone.parameters.sendParameter(item.getParameter());
            item.commit();

            written++;
        }
        if(written > 0)
            adapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), written +" "+R.string.msg_parameters_written_to_drone, Toast.LENGTH_SHORT).show();
    }

    private void openParametersFromFile() {
        OpenFileDialog dialog = new OpenParameterDialog() {
            @Override
            public void parameterFileLoaded(List<Parameter> parameters) {
                Collections.sort(parameters, new Comparator<Parameter>() {
                    @Override
                    public int compare(Parameter p1, Parameter p2) {
                        return p1.name.compareTo(p2.name);
                    }
                });
                // load parameters from file
                adapter.loadParameters(drone, parameters);
            }
        };
        dialog.openDialog(getActivity());
    }

    private void saveParametersToFile() {
        final List<Parameter> parameters = new ArrayList<Parameter>();
        for(int i = 0; i < adapter.getCount(); i++)
            parameters.add(adapter.getItem(i).getParameter());

        if (parameters.size() > 0) {
            ParameterWriter parameterWriter = new ParameterWriter(parameters);
            if (parameterWriter.saveParametersToFile()) {
                Toast.makeText(getActivity(), R.string.parameters_saved,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBeginReceivingParameters() {
        pd = new ProgressDialog(getActivity());
        pd.setTitle(R.string.refreshing_parameters);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(true);

        pd.show();
    }

    @Override
    public void onParameterReceived(Parameter parameter, int index, int count) {
        if (pd != null) {
            if (pd.isIndeterminate()) {
                pd.setIndeterminate(false);
                pd.setMax(count);
            }
            pd.setProgress(index);
        }
    }

    @Override
    public void onEndReceivingParameters(List<Parameter> parameters) {
        Collections.sort(parameters, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter p1, Parameter p2) {
                return p1.name.compareTo(p2.name);
            }
        });
        adapter.loadParameters(drone, parameters);

        // dismiss progress dialog
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }
}
