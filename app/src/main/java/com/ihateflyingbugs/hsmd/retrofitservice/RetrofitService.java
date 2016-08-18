package com.ihateflyingbugs.hsmd.retrofitservice;

import com.ihateflyingbugs.hsmd.RetroAPI.AuthorizationRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.BoardRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.ManagerRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.NotificationRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.OfflineLessonRretro;
import com.ihateflyingbugs.hsmd.RetroAPI.OriginWordRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.PaymentRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.StudyInfoRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.TestRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.UseAppInfomationRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.WordRetro;
import com.ihateflyingbugs.hsmd.RetroAPI.WordUpdateRetro;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by 영철 on 2016-05-18.
 */
public class RetrofitService {

    public String URL_AUTHORIZATION = "http://52.79.87.3/rest/index.php/Authorization/";
    public AuthorizationRetro getAuthorizationService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_AUTHORIZATION)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .client(client)
                .build();
        AuthorizationRetro service =  retrofit.create(AuthorizationRetro.class);
        return service;
    }

    public String URL_BOARD = "http://52.79.87.3/rest/index.php/Board/";
    public BoardRetro getBoardService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BOARD)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        BoardRetro service =  retrofit.create(BoardRetro.class);
        return service;
    }

    public String URL_PAYMENT = "http://52.79.87.3/rest/index.php/Payment/";
    public PaymentRetro getPaymentService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_PAYMENT)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        PaymentRetro service =  retrofit.create(PaymentRetro.class);
        return service;
    }


    public String URL_STUDYINFO = "http://52.79.87.3/rest/index.php/StudyInfo/";
    public StudyInfoRetro getStudyInfoService(){


        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_STUDYINFO)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        StudyInfoRetro service =  retrofit.create(StudyInfoRetro.class);
        return service;
    }

    public String URL_MANAGER = "http://52.79.87.3/rest/index.php/Manager/";
    public ManagerRetro getManagerService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_MANAGER)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        ManagerRetro service =  retrofit.create(ManagerRetro.class);
        return service;
    }

    public String URL_USEAPPINFO= "http://52.79.87.3/rest/index.php/Manager/";
    public UseAppInfomationRetro getUseAppInfoService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_USEAPPINFO)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        UseAppInfomationRetro service =  retrofit.create(UseAppInfomationRetro.class);
        return service;
    }

    public String URL_WORD= "http://52.79.87.3/rest/index.php/Word/";
    public WordRetro getWordService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_WORD)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        WordRetro service =  retrofit.create(WordRetro.class);
        return service;
    }

    public String URL_WORDUPDATE= "http://52.79.87.3/admin/index.php/WorkBook/";
    public WordUpdateRetro getWordUpdateService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_WORDUPDATE)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        WordUpdateRetro service =  retrofit.create(WordUpdateRetro.class);
        return service;
    }

    public String URL_NOTIFICATION = "http://52.79.87.3/admin/index.php/Notification/";
    public NotificationRetro getNotificationService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_NOTIFICATION)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        NotificationRetro service =  retrofit.create(NotificationRetro.class);
        return service;
    }

    public String URL_OFFLINELESSON = "http://52.79.87.3/manager/index.php/Teaching/";
    public OfflineLessonRretro getOfflineLessonService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_OFFLINELESSON)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        OfflineLessonRretro service =  retrofit.create(OfflineLessonRretro.class);
        return service;
    }

    public String URL_ORIGINWORD = "http://52.79.87.3/manager/index.php/OriginWord/";
    public OriginWordRetro getOriginWordService(){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_ORIGINWORD)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(client)
                .build();
        OriginWordRetro service =  retrofit.create(OriginWordRetro.class);
        return service;
    }



    public TestRetro getTestService(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_PAYMENT)
                .addConverterFactory(JacksonConverterFactory.create())
//              .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
       TestRetro service =  retrofit.create(TestRetro.class);
        return service;
    }

//    public Retrofit getRetrofit() {
//        if (mRetrofit == null) {
//            File httpCacheDirectory = new File(getInstance().getCacheDir(), "responses");
//            int cacheSize = 10 * 1024 * 1024; // 10 MiB
//            Cache cache = new Cache(httpCacheDirectory, cacheSize);
//            OkHttpClient okClient = null;
//            okClient = new OkHttpClient.Builder()
//                    .cache(cache)
//                    .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//                    .build();
//            ObjectMapper objectMapper = new ObjectMapper();
//            // register joda module, to parse DateTime field
//            objectMapper.registerModule(new JodaModule());
//            // http://stackoverflow.com/questions/10519265/jackson-overcoming-underscores-in-favor-of-camel-case
//            objectMapper.setPropertyNamingStrategy(
//                    PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
//            JacksonConverterFactory jacksonConverterFactory = JacksonConverterFactory.create(objectMapper);
//            mRetrofit = new Retrofit.Builder()
//                    .baseUrl(AppController.getInstance().getLocalStore().getBaseUrl())
//                    .client(okClient)
//                    .addConverterFactory(jacksonConverterFactory)
//                    .build();
//        }
//        return mRetrofit;
//    }

}
