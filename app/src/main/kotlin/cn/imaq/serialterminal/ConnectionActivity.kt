package cn.imaq.serialterminal

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import cn.wch.ch34xuartdriver.CH34xUARTDriver
import kotlinx.android.synthetic.main.activity_connection.*

class ConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        spinnerBaudrate.setSelection(5) // 9600
        spinnerDataBits.setSelection(3) // 8

        Driver.driver = CH34xUARTDriver(
                getSystemService(Context.USB_SERVICE) as UsbManager,
                this, "${BuildConfig.APPLICATION_ID}.USB_PERMISSION"
        )
    }

    fun onOpenButtonClicked(v: View) {
        with (Driver.driver!!) {
            if (UsbFeatureSupported() && ResumeUsbList() == 0 && UartInit()) {
                val baudRate = (spinnerBaudrate.selectedItem as String).toInt()
                val dataBits = (spinnerDataBits.selectedItem as String).toByte()
                val stopBits = spinnerStopBits.selectedItemPosition.toByte()
                val parity = spinnerParity.selectedItemPosition.toByte()
                val flowControl = spinnerFlowControl.selectedItemPosition.toByte()
                if (SetConfig(baudRate, dataBits, stopBits, parity, flowControl)) {
                    Snackbar.make(v, "Open port succeeded", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(v, "Set config failed", Snackbar.LENGTH_LONG).show()
                }
                return
            }
        }
        Snackbar.make(v, "Open port failed", Snackbar.LENGTH_LONG).show()
    }

}
