package eu.inloop.easygcm

import groovyx.net.http.HTTPBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST

/**
 * Task for sending push notification to device.
 *
 * <p>Sample parameters:</p>
 *
 * <pre>
 * // Content of the notification
 * String data = '{"key1":"value1"}'
 * </pre>
 *
 * <pre>
 * //Server API key from http://developer.android.com/google/gcm/gs.html
 * String apiKey = 'apiKeyFromApiConsole'
 * </pre>
 *
 * <pre>
 * // registration ID received by application from server upon registration (find it in logcat)
 * def registrationIds = [
 *   'veryVeryLongStringThatCanBeFoundInLogcat'
 * ]
 * </pre>
 */
public class PushTask extends DefaultTask {

    String data
    String apiKey
    def registrationIds

    static final String TAG = '[easygcm]'

    @TaskAction
    def send() {
        
        if (!checkParams()) {
            println "$TAG Unable to send push notification. Please correct errors above."
            return
        }
        
        println "$TAG Sending GCM message ..."
            
        println "$TAG data: $data"
        println "$TAG registration IDs: $registrationIds"
        
        def http = new HTTPBuilder('https://android.googleapis.com/gcm/send')

        http.request(POST, JSON) { req ->
            headers.'Authorization' = 'key=' + apiKey
            body = [
                data: this.data,
                registration_ids: registrationIds
            ]
                 
            response.success = { resp, json ->
                // response handling here
                println "$TAG Done"
            }          
            
            response.failure = { resp ->
                println "$TAG Request failed with status ${resp.status}"
            }              
        }
    }
    
    boolean checkParams() {
        boolean valid = true
            
        if (!data) {
            println "$TAG Missing 'data' parameter (example: data = '{\"key1\":\"value1\"}')"
            valid = false
        }

        if (!apiKey) {
            println "$TAG Missing 'apiKey' parameter (see this how to get it: http://developer.android.com/google/gcm/gs.html)"
            valid = false
        }
        
        if (!registrationIds) {
            println "$TAG Missing 'registrationIds' parameter (example: registrationIds = ['DEVICE_REGISTRATION_ID_FROM_LOGCAT'])"
            valid = false
        }

        return valid
    }
}