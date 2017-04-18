package cn.imaq.serialterminal

import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.felhr.usbserial.CH34xSerialDevice
import kotlinx.android.synthetic.main.activity_connection.*

class ConnectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        spinnerBaudrate.setSelection(5) // 9600
        spinnerDataBits.setSelection(3) // 8
    }

    fun onOpenClicked(v: View) {
        var conn: Connection? = null
        when (v) {
            buttonOpenSerial -> {
                val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
                for (entry in usbManager.deviceList.entries) {
                    val device = entry.value
                    if (device.vendorId == 6790) {
                        SerialConnection.device = CH34xSerialDevice(device, usbManager.openDevice(device), 0)
                        if (SerialConnection.device!!.open()) {
                            val baudRate = (spinnerBaudrate.selectedItem as String).toInt()
                            // val dataBits = (spinnerDataBits.selectedItem as String).toInt()
                            // val stopBits = (spinnerStopBits.selectedItem as String).toInt()
                            val parity = spinnerParity.selectedItemPosition
                            val flowControl = spinnerFlowControl.selectedItemPosition
                            with(SerialConnection.device!!) {
                                setBaudRate(baudRate)
                                // setDataBits(dataBits)
                                // setStopBits(stopBits)
                                setParity(parity)
                                setFlowControl(flowControl)
                            }
                            conn = SerialConnection()
                            break
                        }
                    }
                }
            }
            buttonOpenSocket -> {
                conn = SocketConnection(12345)
            }
        }
        if (conn != null) {
            TerminalActivity.connection = conn
            startActivity(Intent(this, TerminalActivity::class.java))
        } else {
            Snackbar.make(v, "Open failed, no supported device found", Snackbar.LENGTH_LONG).show()
        }
    }

}
