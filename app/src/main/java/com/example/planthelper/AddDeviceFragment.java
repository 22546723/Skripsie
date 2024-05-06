package com.example.planthelper;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.planthelper.databinding.FragmentAddDeviceBinding;

public class AddDeviceFragment extends Fragment {

    private FragmentAddDeviceBinding binding;
    private Button btnContinue;
    private Button btnScan;
    private TextView tvDevice;
    private ProgressBar spinner;
    private static BluetoothManager bluetoothManager;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddDeviceBinding.inflate(inflater, container, false);

        requireActivity().setTitle("Connect to device");

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnContinue = binding.btnAddContinue;
        btnScan = binding.btnAddScan;
        tvDevice = binding.tvDetectedDevice;
        spinner = binding.progressBar2;

        spinner.setVisibility(View.GONE);

        tvDevice.setText("No device connected.");

        btnContinue.setEnabled(false);
        btnScan.setEnabled(true);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i("MINE", "pre nav");
                SettingsFragment.setBluetoothManager(bluetoothManager);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_addDeviceFragment_to_settingsFragment);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the bt scan
                Context c = getContext();
                Activity a = getActivity();

                if (a == null)
                    Log.i("MINE", "empty activity in frag");
                if (c == null)
                    Log.i("MINE", "empty context in frag");
                // TODO: remove this ^

                spinner.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    bluetoothManager = new BluetoothManager(c, a);
//                    bluetoothManager.disconnect();
//                    Log.i("MINE", "pre scan");
                    bluetoothManager.scanForDevice();
//                    Log.i("MINE", "post scan");
                    btnScan.setEnabled(false);

                    // check if the scan is done every checkDelay ms
                    Handler scanHandler = new Handler();
                    int checkDelay = 100; // 100ms
                    scanHandler.postDelayed(new Runnable() {
                        public void run() {
//                            System.out.println("myHandler: here!"); // Do your work here
                            if (!(bluetoothManager.checkTimeout() || bluetoothManager.checkConnected())) {
                                scanHandler.postDelayed(this, checkDelay);
//                                Log.i("MINE", ".");
                            } else {
                                btnScan.setEnabled(true);
                                if (bluetoothManager.checkConnected()) {
//                                    bluetoothManager.setupChars();
                                    tvDevice.setText(bluetoothManager.readName());
                                    spinner.setVisibility(View.GONE);
                                    btnContinue.setEnabled(true);
//                                    Log.i("MINE", "meep");
                                }
                            }
                        }
                    }, checkDelay);
                }
            }
        });

        Log.i("MINE", "great snakes batman!");

        // TODO: display scan results?
        // TODO: toggle buttons
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    public static BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

}