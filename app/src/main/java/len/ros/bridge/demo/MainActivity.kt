package len.ros.bridge.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import len.ros.bridge.ROSClient
import len.ros.bridge.Service
import len.ros.bridge.Topic
import len.ros.bridge.message.Clock
import len.ros.bridge.message.TimePrimitive
import len.ros.bridge.rosapi.message.Empty
import len.ros.bridge.rosapi.message.GetTime
import len.ros.bridge.rosbridge.ROSBridgeClient

class MainActivity : AppCompatActivity() {

    private val tag: String = "rosBridge"
    private lateinit var rosBridgeClient: ROSBridgeClient
    private val rosServerUrl: String = "ws://192.168.1.112:9090"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connect()
    }

    private fun topicDemo() {
        Thread(
            Runnable {
                Log.e(tag, "topicDemo")
                var count = 50
//        val clockTopic = Topic("/hello", Hello::class.java, rosBridgeClient)
                val advertiseClockTopic = Topic("/clock", Clock::class.java, rosBridgeClient)
                val subscribeClockTopic = Topic("/clock", Clock::class.java, rosBridgeClient)
                advertiseClockTopic.advertise()
                try {
                    Thread.sleep(5000)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
                subscribeClockTopic.subscribe { msg: Clock? ->
                    Log.e(tag, "rcv <- ")
                    msg!!.print()
                }
                while (count > 0) {
                    Log.e(tag, "count = $count")
                    publishTopicDemo(advertiseClockTopic, count)
                    try {
                        Thread.sleep(2000)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                    count--
                }
                advertiseClockTopic.unadvertise()
                subscribeClockTopic.unsubscribe()
            }
        ).start()
    }

    private fun topicAdvertise() {
        Thread(
            Runnable {
                Log.e(tag, "advertise")
                var count = 50
                val advertiseClockTopic = Topic("/clock", Clock::class.java, rosBridgeClient)
                advertiseClockTopic.advertise()
                while (count > 0) {
                    Log.e(tag, "send count = $count")
                    publishTopicDemo(advertiseClockTopic, count)
                    try {
                        Thread.sleep(2000)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                    count--
                }
                advertiseClockTopic.unadvertise()
            }
        ).start()
    }

    private fun topicSubscribe() {
        Thread(
            Runnable {
                Log.e(tag, "subscribe")
                var count = 50
                val subscribeClockTopic = Topic("/clock", Clock::class.java, rosBridgeClient)
                subscribeClockTopic.subscribe { msg: Clock? ->
                    Log.e(tag, "rcv <- ")
                    msg!!.print()
                }
                while (count > 0) {
                    Log.e(tag, "rcv count = $count")
                    try {
                        Thread.sleep(2000)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                    count--
                }
                subscribeClockTopic.unsubscribe()
            }
        ).start()
    }

    private fun publishTopicDemo(clockTopic: Topic<Clock>, secs: Int) {
        Thread(
            Runnable {
                val clock: Clock = Clock()
                clock.clock = TimePrimitive()
                clock.clock.secs = secs
                clock.clock.nsecs = secs * 1000
                //{发布主题消息
                clockTopic.publish(clock)
                Log.e(tag, "send -> ")
                clock.print()
                //}发布主题消息
            }
        ).start()
    }

    private fun serviceDemo() {
        Thread(
            Runnable {
                val timeService =
                    Service(
                        "/rosapi/get_time",
                        Empty::class.java,
                        GetTime::class.java,
                        rosBridgeClient
                    )
                timeService.callWithHandler(Empty()) { msg: GetTime ->
                    Log.e(tag, "service <- rcv")
                    msg.print()
                }
            }
        ).start()

    }

    private fun connect() {
        Log.e(tag, "connect")

        Thread(
            Runnable {
                rosBridgeClient = ROSBridgeClient(rosServerUrl)
                rosBridgeClient.connect(object : ROSClient.ConnectionStatusListener {
                    override fun onConnect() {
                        Log.e(tag, "onConnect")
                        rosBridgeClient.setDebug(true)
                        //1.topic demo
                        //topicDemo()
                        //2.topic demo
                        topicAdvertise()
                        topicSubscribe()
                        //3.service demo
                        //serviceDemo()
                    }

                    override fun onDisconnect(normal: Boolean, reason: String?, code: Int) {
                        Log.e(tag, "onDisconnect->$reason - $code")
                    }

                    override fun onError(ex: Exception?) {
                        Log.e(tag, "onError", ex)
                    }

                })
            }
        ).start()
    }
}
