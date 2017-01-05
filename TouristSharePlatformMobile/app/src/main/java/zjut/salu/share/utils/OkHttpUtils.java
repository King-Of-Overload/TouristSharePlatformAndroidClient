package zjut.salu.share.utils;

import android.os.Handler;
import android.os.Looper;
import android.service.carrier.CarrierMessagingService;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import zjut.salu.share.exception.MyException;

/**网络访问工具类
 * Created by Alan on 2016/10/21.
 */

public class OkHttpUtils {
    private static final String TAG = "OkHttpUtils";
    private OkHttpClient client=null;

    public OkHttpUtils(){
        client=new OkHttpClient();
    }

    /**
     * post异步访问
     * @param params 参数
     * @param requestURL 请求地址
     * @param callback 回调
     * @throws MyException
     */
    public void startPostRequestWithParams(List<Map<String,Object>> params, String requestURL, Callback callback) throws MyException {
        if(client==null){
            client=new OkHttpClient();
        }
        RequestBody dataBody=null;
        if(null!=params&&!params.isEmpty()){
            FormBody.Builder builder=new FormBody.Builder();
            for(Map<String,Object> map:params){
                Iterator iterator=map.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry entry= (Map.Entry) iterator.next();
                    String key= (String) entry.getKey();
                    String value= (String) entry.getValue();
                    builder.add(key,value);
                }
            }
            dataBody=builder.build();
        }else{
            throw new MyException("参数不能为空");
        }
        Request request=new Request.Builder()
                .url(requestURL)
                .post(dataBody)
                .build();
        Call call=client.newCall(request);
        call.enqueue(callback);
    }

    /**
     *异步Get请求方法
     * @param requestURL
     * @param callback
     */
    public void startAsyncGetRequest(String requestURL,Callback callback){
        if(client==null){
            client=new OkHttpClient();
        }
        Request request=new Request.Builder().url(requestURL).build();
        Call call=client.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 异步的Get请求
     * @param requestURL 请求地址
     * @param  params 拼接参数
     * @return 观察者对象
     */
    public Observable<String> asyncGetRequest(String requestURL,Map<String,Object> params){
        StringBuilder builder=new StringBuilder(requestURL);
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    if(client!=null){
                        client=new OkHttpClient();
                    }
                    if(null!=params){
                        builder.append("?");
                        Iterator iterator=params.entrySet().iterator();
                        while(iterator.hasNext()){
                            Map.Entry entry= (Map.Entry) iterator.next();
                            String key= (String) entry.getKey();
                            String value= (String) entry.getValue();
                            builder.append(key+"="+value);
                            builder.append("&");
                        }
                        builder.deleteCharAt(builder.lastIndexOf("&"));
                    }
                    Request request=new Request.Builder().url(builder.toString()).build();
                    Response response=client.newCall(request).execute();
                    if(!response.isSuccessful()){
                        throw new IOException("Unexpected code " + response);
                    }
                    String result=response.body().string();
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 返回gson字符串
     * @param params
     * @param requestURL
     * @return
     */
    public Observable<String> asyncPostRequest(final List<Map<String,Object>> params, final String requestURL) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    if(client==null){
                        client=new OkHttpClient();
                    }
                    RequestBody dataBody=null;
                    if(null!=params&&!params.isEmpty()){
                        FormBody.Builder builder=new FormBody.Builder();
                        for(Map<String,Object> map:params){
                            Iterator iterator=map.entrySet().iterator();
                            while(iterator.hasNext()){
                                Map.Entry entry= (Map.Entry) iterator.next();
                                String key= (String) entry.getKey();
                                String value= (String) entry.getValue();
                                builder.add(key,value);
                            }
                        }
                        dataBody=builder.build();
                    }else{
                        throw new MyException("参数不能为空");
                    }
                    Request request=new Request.Builder()
                            .url(requestURL)
                            .post(dataBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    String result = response.body().string();
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 单图片上传
     */
    public Observable<String> uploadMutipleImages(final List<Map<String,Object>> params,File file,final String requestURL,String imgName){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                try {
                    if(client==null){
                        client=new OkHttpClient();
                    }
                    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                    RequestBody dataBody=null;
                    if(null!=params&&!params.isEmpty()){
                        MultipartBody.Builder builder=new MultipartBody.Builder();
                        builder.setType(MultipartBody.FORM);
                        for(Map<String,Object> map:params){
                            Iterator iterator=map.entrySet().iterator();
                            while(iterator.hasNext()){
                                Map.Entry entry= (Map.Entry) iterator.next();
                                String key= (String) entry.getKey();
                                String value= (String) entry.getValue();
                                builder.addPart(Headers.of(
                                        "Content-Disposition",
                                        "form-data; name=\""+key+"\""),
                                        RequestBody.create(null, value));
                            }
                        }
                        builder.addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\""+imgName+"\";filename=\""+file.getName()+"\""), fileBody);
                        dataBody=builder.build();
                    }else{
                        throw new MyException("参数不能为空");
                    }
                    Request request=new Request.Builder()
                            .url(requestURL)
                            .post(dataBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    String result = response.body().string();
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

}
