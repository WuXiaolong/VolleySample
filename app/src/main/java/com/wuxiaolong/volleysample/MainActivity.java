package com.wuxiaolong.volleysample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RequestQueue mRequestQueue;
    TextView showMsg;
    ProgressBar progressBar;
    ImageView showImage;
    NetworkImageView networkImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRequestQueue = Volley.newRequestQueue(this);
        showMsg = (TextView) findViewById(R.id.showMsg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        showImage = (ImageView) findViewById(R.id.showImage);
        networkImageView = (NetworkImageView) findViewById(R.id.networkImageView);
        findViewById(R.id.string_request).setOnClickListener(this);
        findViewById(R.id.json_request).setOnClickListener(this);
        findViewById(R.id.image_request).setOnClickListener(this);
        findViewById(R.id.image_loader).setOnClickListener(this);
        findViewById(R.id.post).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.string_request:
                stringRequest();
                break;
            case R.id.json_request:
                json_request();
                break;
            case R.id.image_request:
                iamgeRequest();
                break;
            case R.id.image_loader:
                imageLoader();
                break;
            case R.id.post:
                postRequest();
                break;
            default:
                break;
        }
    }


    private void stringRequest() {

        showMsg.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String url = "http://wuxiaolong.me/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showMsg.setText("stringRequest==" + response);
                progressBar.setVisibility(View.GONE);
                Log.d("wxl", "stringRequest==" + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("wxl", "StringRequest error==" + error);
                progressBar.setVisibility(View.GONE);
            }
        });
        stringRequest.setTag("stringRequest");
        //如果要取消stringRequest请求，只需简单的添加下面的一行代码：
        // mRequestQueue.cancelAll("stringRequest");
        mRequestQueue.add(stringRequest);
    }

    private void json_request() {
        showMsg.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String url = "http://httpbin.org/get?site=code&network=tutsplus";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showMsg.setText("JsonObjectRequest==" + response);
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMsg.setText("JsonObjectRequest error==" + error);
                progressBar.setVisibility(View.GONE);
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    /**
     * 第一个参数是图片的url，
     * 第二个是结果的listener，
     * 第三、第四个参数是maxWidth（最大宽度） 和 maxHeight（最大高度），你可以设置为0来忽略他们。
     * 第五个参数是用于计算图片所需大小的ScaleType，
     * 第六个参数是用于指定图片压缩方式的参数，建议总是使用 Bitmap.Config.ARGB_8888
     */
    private void iamgeRequest() {

        showMsg.setText("");
        progressBar.setVisibility(View.VISIBLE);
        showImage.setImageBitmap(null);
        String url = "http://p3.so.qhimg.com/t012befc69d1b6d8f88.jpg";
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                showImage.setImageBitmap(response);
                progressBar.setVisibility(View.GONE);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                showMsg.setText("ImageRequest error==" + error);
            }
        });
        mRequestQueue.add(imageRequest);
    }

    /**
     * 数量庞大的ImageRequests，比如生成一个带有图片的ListView
     */
    private void imageLoader() {

        showMsg.setText("");
        showImage.setImageBitmap(null);
        progressBar.setVisibility(View.VISIBLE);
        String url = "http://p0.so.qhimg.com/t0191efbe9e784617e5.jpg";
        int maxSize = 10 * 1024 * 1024;//设置缓存图片的大小为10M
        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };

//        ImageLoader imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
//            @Override
//            public Bitmap getBitmap(String url) {
//                return lruCache.get(url);
//            }
//
//            @Override
//            public void putBitmap(String url, Bitmap bitmap) {
//                lruCache.put(url, bitmap);
//            }
//        });
        ImageLoader imageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(getApplicationContext())));
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                showImage.setImageBitmap(response.getBitmap());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                showMsg.setText("ImageRequest error==" + error);
            }
        }, 0, 0, ImageView.ScaleType.FIT_XY);
        networkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
        networkImageView.setErrorImageResId(R.mipmap.ic_launcher);
        networkImageView.setImageUrl("http://p2.so.qhimg.com/t01ac4a335801991667.jpg", imageLoader);
    }

    private void postRequest() {
        showMsg.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String url = "http://httpbin.org/post";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response).getJSONObject("form");
                            String site = jsonResponse.getString("site"),
                                    network = jsonResponse.getString("network");
                            System.out.println("Site: " + site + "\nNetwork: " + network);
                            showMsg.setText("PostRequest==" + "Site: " + site + "\nNetwork: " + network);
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        showMsg.setText("PostRequest error==" + error);
                        progressBar.setVisibility(View.GONE);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("site", "code");
                params.put("network", "tutsplus");
                return params;
            }
        };
        mRequestQueue.add(postRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                // do I have to cancel this?
                return true; // -> always yes
            }
        });
    }
}
