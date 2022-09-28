package com.example.avfisica;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.UUID;

public class BlueToothBLE {
    private static final int TIMEOUT_SEMAFARO_CONNECT = 10;
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB");
    public final static UUID DESCRIPTOR_CCC_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public final static String ACTION_DATA_AVAILABLE =
            "com.autonomo.bluetooh.ACTION_DATA_AVAILABLE";


    public Monitorado.Device cintaDevice = null;
    public Context context = null;
    int countSemafaroConnect = TIMEOUT_SEMAFARO_CONNECT;
    BluetoothGattCharacteristic characteristic_bkp;
    BluetoothGattDescriptor descriptor_bkp;


    //construtor
    public BlueToothBLE(Context context)
    {
        this.context = context;
        this.cintaDevice = new Monitorado.Device();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void notify_blue()
    {
        try {
            if ((this.cintaDevice.characteristic != null) &&
                    (this.cintaDevice.descriptor != null)) {
                this.cintaDevice.gatt.setCharacteristicNotification(this.cintaDevice.characteristic, true);
                this.cintaDevice.gatt.writeDescriptor(this.cintaDevice.descriptor);
            }
        } catch (Exception e) {
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connectToDevice()
    {
        try {
            if ((this.cintaDevice.device != null) &
                    (!this.cintaDevice.flagConect)) {
                this.cintaDevice.gatt = this.cintaDevice.device.connectGatt(context, false, gattCallback);
            }
        } catch (Exception e) {
        }
        ;
    }



    //###################### GATT SERVICE#######################################
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            try {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        countSemafaroConnect = 0; //LIGA O SEMÁFARO
                        Thread.sleep(2000); // DELAY -> POLAR
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        break;
                    default:
                }
            } catch (Exception e) {
            }
            ;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            boolean flag = false;

            for (BluetoothGattService service : services) {
                countSemafaroConnect = 0; //liga semafaro (sinal vermelho)

                if (flag) { //apenas serviÃ§o de heart rate
                    break;
                }
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    //evita que de timeout
                    cintaDevice.timeout = 0;

                    // Set notifiable
                    gatt.setCharacteristicNotification(characteristic, true);
                    characteristic_bkp = characteristic;

                    // Enable notification descriptor
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_CCC_UUID);
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                        descriptor_bkp = descriptor;
                        if (characteristic.getUuid().equals(UUID_HEART_RATE_MEASUREMENT)) {
                            if (cintaDevice.getdevice(gatt.getDevice(), cintaDevice)) {
                                flag = true;

                                cintaDevice.characteristic = characteristic_bkp;
                                cintaDevice.descriptor = descriptor_bkp;
                                notify_blue();
                            }
                        }
                    }
                    // Read characteristic
                    if (!gatt.readCharacteristic(characteristic)) {
                        // Log.e(TAG, "Failed to read characteristic: " + characteristic.toString());
                    }
                }
            }
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        private void broadcastUpdate(final String action,
                                     final BluetoothGattCharacteristic characteristic) {
            if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
                int flag = characteristic.getProperties();
                int format = -1;
                if ((flag & 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16;
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                }
                final int heartRate = characteristic.getIntValue(format, 1);

                //verifica qual cinta cadastrada recebeu o valor do batimento

                if ((cintaDevice.characteristic != null) && (cintaDevice.characteristic.equals(characteristic))) {
                    cintaDevice.heartRate = heartRate;
                    cintaDevice.timeout = 0; //reseta o timeout
                    cintaDevice.flagConect = true; //conectado
                    cintaDevice.flagLastConnect = true;
                    cintaDevice.indexBufferHeartRate++;
                    cintaDevice.bufferheartrate[cintaDevice.indexBufferHeartRate] = heartRate;
                    notify_blue();
                }
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        // Characteristic notification
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

    };
}