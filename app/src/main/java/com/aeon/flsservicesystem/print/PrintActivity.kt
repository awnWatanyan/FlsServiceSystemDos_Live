package com.aeon.flsservicesystem.print

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.registerReceiver
import com.aeon.flsservicesystem.PREFS_KEY_IS_LOGIN
import com.aeon.flsservicesystem.R
import com.aeon.flsservicesystem.callurl
import com.aeon.flsservicesystem.pathSeqment
import com.aeon.flsservicesystem.scheme
import com.pixplicity.easyprefs.library.Prefs
import com.zebra.isv.tapbluetoothwifi.BluetoothDeviceArrayAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL
import kotlin.system.exitProcess
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.SGD
import com.zebra.sdk.printer.ZebraPrinterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import kotlin.concurrent.thread

class PrintActivity : AppCompatActivity() {

    companion object{
        private val bluetoothList : ArrayList<BluetoothDevice> = ArrayList();
        private var bluetoothDeviceName = "";
        private var currentPage = 0;
        private var totalPage = 0;
        private var printSuccess = 200;
        private var printMessage = "";
        fun  getCurrentPageValue(): Int{

            return currentPage

        }
        fun  setCurrentPageValue(value : Int) {

            currentPage = value

        }

        fun  getTotalPageValue(): Int{

            return totalPage

        }
        fun  setTotalPageValue(value : Int) {

            totalPage = value

        }

    }

    lateinit var textPriter: TextView
    lateinit var textProgress: TextView
    lateinit var textCountPrint: TextView
    lateinit var btnBack : Button
    lateinit var btnPrint : Button
    lateinit var progressBar: ProgressBar
    lateinit var textPictureUrl: TextView



    private var uri: Uri? = null

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    // private val desiredDeviceName = "YourDeviceName"

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Get the device name
                val deviceName = device?.name
                // Compare device name
                if (deviceName != null && deviceName == bluetoothDeviceName) {
                    // Device found
                    println("Device found: $deviceName")
                    // You can connect to the device here or perform any other operation
                    if(bluetoothList.find { it.name == deviceName } == null)
                    {
                        bluetoothList.add(device)
                    }
                    bluetoothAdapter?.cancelDiscovery()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_print)

        // Register for broadcasts when a device is discovered
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        // Start discovery
        bluetoothAdapter?.startDiscovery()
        textPriter = findViewById(R.id.idTVPrinterStatus)
        textProgress = findViewById(R.id.iTVMessage)
        textCountPrint = findViewById(R.id.iTVCountPrint)
        btnBack = findViewById(R.id.btn_back)
        btnPrint = findViewById(R.id.btn_print)
        progressBar = findViewById(R.id.progress_Bar)
        textPictureUrl = findViewById(R.id.iTVUrlPicture)

        intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.android.chrome"))
        btnBack.setOnClickListener{
            //Call Back
            /*moveTaskToBack(true);
            exitProcess(-1)*/


            finish()
        }
        uri = intent.data

        if(btnBack.visibility == View.VISIBLE)
        {
            btnBack.visibility = View.INVISIBLE
        }
        if(btnPrint.visibility == View.VISIBLE)
        {
            btnPrint.visibility = View.INVISIBLE
        }

        if(uri != null) {

            textCountPrint.text = ""
            setTotalPageValue(0)
            setCurrentPageValue(0)
            progressBar.visibility = View.VISIBLE
            RetrieveFileFromURL(this,uri!!.getQueryParameter("filePath"),textPriter
                ,textProgress,textCountPrint,btnBack,btnPrint
                ,uri!!.getQueryParameter("pntDeviceNm"),uri!!.getQueryParameter("pntToken")
                , getCurrentPageValue(),progressBar,textPictureUrl
            ).execute()


        }

        btnPrint.setOnClickListener{
            progressBar.visibility = View.VISIBLE
            btnPrint.visibility = View.INVISIBLE
            textProgress.text =this.getString(R.string.print_next_doc)
            RetrieveFileFromURL(this,uri!!.getQueryParameter("filePath"),textPriter
                ,textProgress,textCountPrint,btnBack,btnPrint
                ,uri!!.getQueryParameter("pntDeviceNm"),uri!!.getQueryParameter("pntToken")
                ,getCurrentPageValue(),progressBar,textPictureUrl
            ).execute()
        }


    }



    open class RetrieveFileFromURL(context: Context, contentUri: String?, textPrinter: TextView
                                   , textView: TextView, textCount: TextView, btnBack: Button
                                   , btnPrint: Button, printerName : String? , pntToken : String?
                                   , printPage : Int, progress : ProgressBar,textPicUrl : TextView
    ) :

        AsyncTask<Void, String, String>() {
        private val page = printPage
        private var uri = contentUri
        @SuppressLint("StaticFieldLeak")
        private val contextMain = context
        var adapter: BluetoothDeviceArrayAdapter? = null
        var printer: BluetoothDevice? = null
        private var logTag = "connect printer"
        private var textPrinterDisplay : TextView = textPrinter;
        private var textMessage : TextView = textView
        private var textCountPrint : TextView = textCount
        private var btnB : Button = btnBack
        private var btnP : Button = btnPrint
        private var pb : ProgressBar = progress
        private var textPictureUrl : TextView = textPicUrl;
        private var printToken  = pntToken;


        private var printerNameData = printerName



        @SuppressLint("SetTextI18n", "MissingPermission", "SuspiciousIndentation")
        override fun doInBackground(vararg params: Void?): String? {
            bluetoothDeviceName = printerNameData.toString()
            //  Initialize the list view and its adapter
            // inch = 13 cm = 33
            var labelLength = 13
            val numberOfLabelLength = 2
            // 1 inch = 203 dot
            // 1 cm = 80 dot
            val labelDot = 203

            val bluetoothListForConnect = getPairedPrinters(bluetoothDeviceName)
            if(bluetoothListForConnect.size > 0)
            {
                adapter = BluetoothDeviceArrayAdapter(contextMain, getPairedPrinters(bluetoothDeviceName))
            }
            else
            {
                var countTime = 0;
                while (bluetoothList.size <= 0)
                {
                    Thread.sleep(1000)
                    countTime++
                    if(countTime == 10)
                    {
                        break
                    }
                }
                adapter = BluetoothDeviceArrayAdapter(contextMain, bluetoothList)
            }



            //adapter = BluetoothDeviceArrayAdapter(contextMain, getPairedPrinters())

            var conn: Connection? = null
            try {
                if(adapter!!.values.isNotEmpty())
                {
                    val printerList = adapter!!.values;
                    for(device in printerList)
                    {
                        if(device.name.equals(printerNameData))
                        {
                            printer = device;
                            break
                        }

                    }

                }
                else
                {
                    textPrinterDisplay.text = contextMain.getString(R.string.cannot_connect_printer)
                    return  contextMain.getString(R.string.printer_not_found)
                }
                if(printer == null)
                {
                    textPrinterDisplay.text = contextMain.getString(R.string.cannot_connect_printer)
                        return  contextMain.getString(R.string.printer_not_match)
                }

                val printerAddress = printer!!.address


                conn = BluetoothConnection(
                    printerAddress
                ) //Input text is considered Bluetooth MAC address

                if (!isPrinterPaired(printerAddress)){
                    //connectDevice(conn)
                    try {
                        conn.open()
                        textPrinterDisplay.text = contextMain.getString(R.string.printer_connected)

                    } catch (e: ConnectionException) {
                        //Log.e(tag, "Connection Failed: " + e.message)
                        textPrinterDisplay.text = contextMain.getString(R.string.cannot_connect_printer)
                        return e.message
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                }


                val printer = ZebraPrinterFactory.getInstance(conn)
                var printerLanguage = SGD.GET("device.languages",conn)
                if(printerLanguage != "zpl")
                {
                    conn.write("! U1 setvar \"device.languages\" \"zpl\"\r\n".toByteArray())
                }
                var printMode = SGD.GET("ezpl.print_mode",conn)
                if(printMode != "rewind")
                {
                    val zplCommand = "^XA\n^MMR\n^XZ"
                    conn.write(zplCommand.toByteArray())
                    Thread.sleep(3000)
                }



                var isReady: Boolean = false
                val printerStatus = printer.currentStatus
                if (printerStatus.isReadyToPrint) {
                    isReady = true
                }
                else
                {
                    textPrinterDisplay.text = contextMain.getString(R.string.print_error)

                    if(printerStatus.isHeadOpen)
                    {
                    return  contextMain.getString(R.string.printer_head_open)
                    }
                    else if(printerStatus.isPaperOut)
                    {
                        return  contextMain.getString(R.string.out_of_paper)
                    }
                    else if(printerStatus.isRibbonOut)
                    {
                        return  contextMain.getString(R.string.out_of_ribbon)
                    }
                    else if(printerStatus.isHeadTooHot)
                    {
                        return  contextMain.getString(R.string.priner_too_hot)
                    }
                    else if(printerStatus.isHeadCold)
                    {
                        return  contextMain.getString(R.string.printer_too_cold)
                    }
                    else
                    {
                        return  contextMain.getString(R.string.printer_not_ready)
                    }
                }
                val scale: String = scalePrint(conn)

                SGD.SET("apl.settings", scale, conn)
                val printOrientation = SGD.GET("zpl.print_orientation",conn)
                if(printOrientation != "inv")
                {
                        SGD.SET("zpl.print_orientation","inv",conn)
                }
                // 13 inch = 2639 , 1 inch = 203

                val urlData = uri?.split("|")
                var url : URL? = null
                var testRawUrl = ""

                if (urlData != null) {
                    if(getTotalPageValue() == 0)
                    {
                        setTotalPageValue(urlData.size)
                    }


                    if(urlData.isNotEmpty()) {
                        if (page <= urlData.size) {
                            val rawUrl = urlData[page]
                            testRawUrl = rawUrl
                            url =  URL(rawUrl.take(rawUrl.length-numberOfLabelLength))
                            labelLength = rawUrl.takeLast(numberOfLabelLength).toInt()


                        }

                    }
                }
                val mainHandler = Handler(Looper.getMainLooper())

                if(url == null)
                {
                    textPrinterDisplay.text = contextMain.getString(R.string.print_error)
                    return contextMain.getString(R.string.file_not_found)
                }
               /* else{
                    mainHandler.post{
                        textPictureUrl.text =testRawUrl
                    }

                }*/
                // 1 inch = 203 dot
                // 1 cm = 80 dot

                val labelLengthSet = labelLength *(labelDot)
                var labelTop = SGD.GET("zpl.label_top",conn)
                SGD.SET("zpl.label_length",labelLengthSet,conn)
                //val printFile :ByteArray = url.readBytes()

                val c  =  url.openConnection();
                c.connect();
                textMessage.text =contextMain.getString(R.string.download_file)
                textCountPrint.text = "(${getCurrentPageValue()}/${getTotalPageValue()})"

                //lenghtOfFile is used for calculating download progress
                val lengthOfFile: Int = c.getContentLength()

                //this is where the file will be seen after the download
                //file input is from the url
                val input: InputStream = c.getInputStream()

                val output: ByteArrayOutputStream = ByteArrayOutputStream(1024)
                //here’s the download code
                val buffer = ByteArray(1024)
                var total = 0
                while (true) {
                    val length = input.read(buffer)
                    total += length
                    publishProgress(Integer.valueOf((total * 100) / lengthOfFile));

                    if (length <= 0)
                        break

                    output.write(buffer, 0, length)
                }
                output.flush()
                publishProgress(100);
                val printFile :ByteArray = output.toByteArray()


                if (isReady) {
                    //textMessage.text ="Start Printing"

                    textMessage.text =contextMain.getString(R.string.printting_text)
                    val tempPrintFile = File.createTempFile("temp"
                        , ".jpg", contextMain.cacheDir);

                    tempPrintFile.writeBytes(printFile)

                    printer.printImage(tempPrintFile.absolutePath,0,0)


                    val labelLengthClear = (203)
                    SGD.SET("zpl.label_length",labelLengthClear,conn)
                    Thread.sleep(3000)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                //Log.e(logTag, e.printStackTrace().toString())
                //textPrinterDisplay.text = "Error"
                textPrinterDisplay.text = contextMain.getString(R.string.print_error)
                //return "Print Error: File Not Found"
                return contextMain.getString(R.string.file_not_found)
            }
            catch (e: Exception) {
                e.printStackTrace()
                Log.e(logTag, e.printStackTrace().toString())
                textPrinterDisplay.text = contextMain.getString(R.string.error)
                return contextMain.getString(R.string.print_error)  + " : $e.message"

            } finally {
                disconnectDevice(conn)
            }

            setCurrentPageValue(page+1)
            textCountPrint.text = "(${getCurrentPageValue()}/${getTotalPageValue()})"

            return contextMain.getString(R.string.print_complete)
        }

        private fun publishProgress(vararg progress: Int) {
            pb.progress = progress[0]
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            pb.visibility= View.INVISIBLE;
            textMessage.text = result
            printMessage = result.toString()
            if(result.equals(contextMain.getString(R.string.print_complete)))
            {
                printSuccess = 200
                if(getCurrentPageValue() < getTotalPageValue())
                {
                    btnP.visibility = View.VISIBLE;
                }
                else{
                    //btnB.visibility = View.VISIBLE;
                    sendPrintResult(true)

                }
            }
            else{
                printSuccess = 400
                //btnB.visibility = View.VISIBLE;
                sendPrintResult(false)
            }



        }

        private fun sendPrintResult(printResult: Boolean)
        {
            val client = OkHttpClient()
            val httpUrl = HttpUrl.Builder().scheme(scheme).host(callurl)
                .addPathSegment(pathSeqment)
                .addPathSegment("SubmitCollectorResult")

            val requestBody = "{\"status\":\"$printSuccess\",\"msg\":\"$printMessage\",\"pntToken\":\"$printToken\"}"
            val body = requestBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(httpUrl.build())
                .post(body)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                }

                override fun onResponse(call: Call, response: Response) {


                    val result = response.body?.string() ?: ""

                    var status = ""
                    var msg = ""
                    var resultJson: JSONObject? = null
                    try {
                        resultJson = JSONObject(result)
                    } catch (e: Exception) {
                        msg = e.cause.toString()
                    }

                    if (resultJson != null) {
                        if (resultJson.has("status")) {
                            status = resultJson.get("status").toString()
                        }
                        if (resultJson.has("msg")) {
                            msg = resultJson.get("msg").toString()
                        }
                    }
                    val mainHandler = Handler(Looper.getMainLooper())
                    mainHandler.post {
                        if (status == "200"&& printResult) {
                            textMessage.text = contextMain.getString(R.string.update_print_result_success)

                        } else {
                            textMessage.text = contextMain.getString(R.string.update_print_result_fail)
                        }
                        btnB.visibility = View.VISIBLE;
                    }



                }
            })
        }



        @SuppressLint("MissingPermission")
        private fun getPairedPrinters(deviceName: String): ArrayList<BluetoothDevice> {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val pairedDevices = mBluetoothAdapter.getBondedDevices()
            val pairedDevicesList = ArrayList<BluetoothDevice>()
            for (device in pairedDevices) {
                if (isBluetoothPrinter(device))
                {
                    if(device.name.equals(deviceName))
                        pairedDevicesList.add(device)
                }

            }
            return pairedDevicesList
        }

        @SuppressLint("MissingPermission")
        private fun isBluetoothPrinter(bluetoothDevice: BluetoothDevice): Boolean {
            return (bluetoothDevice.getBluetoothClass().majorDeviceClass == BluetoothClass.Device.Major.IMAGING
                    || bluetoothDevice.getBluetoothClass().majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED)
        }

        @SuppressLint("MissingPermission")
        private fun isPrinterPaired(address: String): Boolean {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val pairedDevices = mBluetoothAdapter.getBondedDevices()
            for (device in pairedDevices) {
                if (device.getAddress().replace("[\\p{P}\\p{S}]".toRegex(), "")
                        .equals(address, ignoreCase = true)
                ) {

                    return true
                }
            }
            return false
        }

      /*  @SuppressLint("SetTextI18n")
        private fun connectDevice(conn: Connection) {


            val tag = "CONNECT_PRINTER"
            try {
                conn.open()
                textPrinterDisplay.text = "Printer Connected"
                Thread.sleep(500)

            } catch (e: ConnectionException) {
                Log.e(tag, "Connection Failed: " + e.message)
                textPrinterDisplay.text = "Cannot Connect To Printer"
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
*/
        private fun disconnectDevice(conn: Connection?) {

            val tag = "CONNECT_PRINTER"
            try {

                if (conn != null) {
                    if (conn.isConnected)
                        conn.close()
                }
            } catch (e: ConnectionException) {
                Log.e(tag, "Connection Failed: " + e.message)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        @Throws(ConnectionException::class)
        private fun scalePrint(connection: Connection): String {
            val fileWidth: Int = 1;
            var scale = "dither scale-to-fit"
            var printerModel = SGD.GET("device.host_identification", connection)
            //var printerDevice = SGD.GET("device.", connection)
            if(printerModel.length >=5 )
            {
                printerModel = printerModel.substring(0, 5)
            }
            val scaleFactor: Double = if (printerModel == "iMZ22" || printerModel == "QLn22" || printerModel == "ZD410") {
                2.0 / fileWidth * 100
            } else if (printerModel == "iMZ32" || printerModel == "QLn32" || printerModel == "ZQ510") {
                3.0 / fileWidth * 100
            } else if (printerModel == "QLn42" || printerModel == "ZQ520" || printerModel == "ZD420" || printerModel == "ZD500" || printerModel == "ZT220" || printerModel == "ZT230" || printerModel == "ZT410") {
                4.0 / fileWidth * 100
            } else if (printerModel == "ZT420") {
                6.5 / fileWidth * 100
            } else {
                100.0
            }
            scale = "dither scale=" + scaleFactor.toInt() + "x" + scaleFactor.toInt()

            return scale
        }



    }


}