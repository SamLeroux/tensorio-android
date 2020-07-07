package ai.doc.kotlinexample

import ai.doc.tensorio.TIOModel.TIOModelBundle
import ai.doc.tensorio.TIOModel.TIOModelBundleException
import ai.doc.tensorio.TIOModel.TIOModelException
import ai.doc.tensorio.TIOUtilities.TIOClassificationHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.io.IOException

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            // Load the Model

            val bundle = TIOModelBundle(applicationContext, "mobilenet_v2_1.4_224.tfbundle")
            val model = bundle.newModel()

            // Load the Image

            val bitmap = assets.open("picture2.jpg")
            val bMap = BitmapFactory.decodeStream(bitmap)
            val scaled = Bitmap.createScaledBitmap(bMap, 224, 224, false)

            // Create a Background Thread

            val mHandlerThread = HandlerThread("HandlerThread")
            mHandlerThread.start()
            val mHandler = Handler(mHandlerThread.looper)

            // Execute the Model

            mHandler.post {
                try {
                    val output = model.runOn(scaled)
                    val classification = output.get("classification") as MutableMap<String, Float>
                    val top5 = TIOClassificationHelper.topN(classification, 5)

                    for (entry in top5) {
                        Log.i(TAG, entry.key + ":" + entry.value)
                    }
                } catch (e: TIOModelException) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: TIOModelBundleException) {
            e.printStackTrace()
        }
    }
}
